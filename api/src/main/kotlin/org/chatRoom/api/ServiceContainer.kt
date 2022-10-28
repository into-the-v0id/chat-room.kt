package org.chatRoom.api

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.chatRoom.api.configuration.DatabaseConfiguration
import org.chatRoom.api.configuration.ServerConfiguration
import org.chatRoom.api.controller.MemberController
import org.chatRoom.api.controller.MessageController
import org.chatRoom.api.controller.RoomController
import org.chatRoom.api.controller.UserController
import org.chatRoom.api.plugin.*
import org.chatRoom.api.route.MemberRoutes
import org.chatRoom.api.route.MessageRoutes
import org.chatRoom.api.route.RoomRoutes
import org.chatRoom.api.route.UserRoutes
import org.chatRoom.api.db.MigrationManager
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.read.MessageReadRepository
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.api.repository.read.event.MemberReadEventRepository
import org.chatRoom.api.repository.read.event.MessageReadEventRepository
import org.chatRoom.api.repository.read.event.RoomReadEventRepository
import org.chatRoom.api.repository.read.event.UserReadEventRepository
import org.chatRoom.api.repository.read.state.MemberReadStateRepository
import org.chatRoom.api.repository.read.state.MessageReadStateRepository
import org.chatRoom.api.repository.read.state.RoomReadStateRepository
import org.chatRoom.api.repository.read.state.UserReadStateRepository
import org.chatRoom.core.repository.write.MemberWriteRepository
import org.chatRoom.core.repository.write.MessageWriteRepository
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.chatRoom.api.repository.write.cascade.MemberWriteCascadeRepository
import org.chatRoom.api.repository.write.cascade.RoomWriteCascadeRepository
import org.chatRoom.api.repository.write.cascade.UserWriteCascadeRepository
import org.chatRoom.api.repository.write.chain.MemberWriteChainRepository
import org.chatRoom.api.repository.write.chain.MessageWriteChainRepository
import org.chatRoom.api.repository.write.chain.RoomWriteChainRepository
import org.chatRoom.api.repository.write.chain.UserWriteChainRepository
import org.chatRoom.api.repository.write.event.MemberWriteEventRepository
import org.chatRoom.api.repository.write.event.MessageWriteEventRepository
import org.chatRoom.api.repository.write.event.RoomWriteEventRepository
import org.chatRoom.api.repository.write.event.UserWriteEventRepository
import org.chatRoom.api.repository.write.guard.MemberWriteGuardRepository
import org.chatRoom.api.repository.write.guard.MessageWriteGuardRepository
import org.chatRoom.api.repository.write.guard.RoomWriteGuardRepository
import org.chatRoom.api.repository.write.guard.UserWriteGuardRepository
import org.chatRoom.api.repository.write.state.MemberWriteStateRepository
import org.chatRoom.api.repository.write.state.MessageWriteStateRepository
import org.chatRoom.api.repository.write.state.RoomWriteStateRepository
import org.chatRoom.api.repository.write.state.UserWriteStateRepository
import org.chatRoom.api.state.StateManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import javax.sql.DataSource

object ServiceContainer {
    val module = module {
        single<Config> { ConfigFactory.load() }

        single<ServerConfiguration> {
            @OptIn(ExperimentalSerializationApi::class)
            Hocon.decodeFromConfig(
                get<Config>().getConfig("server")
            )
        }
        single<ApplicationEngine> {
            val config = get<ServerConfiguration>()

            embeddedServer(Netty, host = config.host, port = config.port.toInt()) {
                configureHTTP()
                configureMonitoring()
                configureSerialization()
                get<Routing>().apply { configureRouting() }
            }
        }

        single<DatabaseConfiguration> {
            @OptIn(ExperimentalSerializationApi::class)
            Hocon.decodeFromConfig(
                get<Config>().getConfig("db")
            )
        }
        single<DataSource> {
            val config = get<DatabaseConfiguration>()

            HikariDataSource().apply {
                jdbcUrl = "jdbc:postgresql://${config.host}:${config.port}/${config.name}"
                username = config.user
                password = config.password
                schema = config.schema
            }
        }

        singleOf(::MigrationManager)
        single { StateManager(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        singleOf(::Routing)

        singleOf(::UserRoutes)
        singleOf(::UserController)
        singleOf(::UserReadEventRepository)
        singleOf(::UserWriteEventRepository)
        singleOf(::UserReadStateRepository) { bind<UserReadRepository>() }
        singleOf(::UserWriteStateRepository)
        single<UserWriteRepository> { UserWriteGuardRepository(
            UserWriteCascadeRepository(
                UserWriteChainRepository(listOf(
                    get<UserWriteEventRepository>(),
                    get<UserWriteStateRepository>(),
                )),
                get(),
                get(),
            ),
            get(),
        ) }

        singleOf(::RoomRoutes)
        singleOf(::RoomController)
        singleOf(::RoomReadEventRepository)
        singleOf(::RoomWriteEventRepository)
        singleOf(::RoomReadStateRepository) { bind<RoomReadRepository>() }
        singleOf(::RoomWriteStateRepository)
        single<RoomWriteRepository> { RoomWriteGuardRepository(
            RoomWriteCascadeRepository(
                RoomWriteChainRepository(listOf(
                    get<RoomWriteEventRepository>(),
                    get<RoomWriteStateRepository>(),
                )),
                get(),
                get()
            ),
            get(),
        ) }

        singleOf(::MemberRoutes)
        singleOf(::MemberController)
        singleOf(::MemberReadEventRepository)
        singleOf(::MemberWriteEventRepository)
        singleOf(::MemberReadStateRepository) { bind<MemberReadRepository>() }
        singleOf(::MemberWriteStateRepository)
        single<MemberWriteRepository> { MemberWriteGuardRepository(
            MemberWriteCascadeRepository(
                MemberWriteChainRepository(listOf(
                    get<MemberWriteEventRepository>(),
                    get<MemberWriteStateRepository>(),
                )),
                get(),
                get()
            ),
            get(),
        ) }

        singleOf(::MessageRoutes)
        singleOf(::MessageController)
        singleOf(::MessageReadEventRepository)
        singleOf(::MessageWriteEventRepository)
        singleOf(::MessageReadStateRepository) { bind<MessageReadRepository>() }
        singleOf(::MessageWriteStateRepository)
        single<MessageWriteRepository> { MessageWriteGuardRepository(
            MessageWriteChainRepository(listOf(
                get<MessageWriteEventRepository>(),
                get<MessageWriteStateRepository>(),
            )),
            get(),
        ) }
    }

    val koin = koinApplication {
        slf4jLogger()
        modules(module)
    }
}
