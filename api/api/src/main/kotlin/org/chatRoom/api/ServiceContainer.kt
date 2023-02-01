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
import org.chatRoom.api.controller.*
import org.chatRoom.api.plugin.*
import org.chatRoom.api.db.MigrationManager
import org.chatRoom.api.repository.read.event.*
import org.chatRoom.api.repository.read.state.*
import org.chatRoom.api.repository.write.cascade.MemberWriteCascadeRepository
import org.chatRoom.api.repository.write.cascade.RoomWriteCascadeRepository
import org.chatRoom.api.repository.write.cascade.UserWriteCascadeRepository
import org.chatRoom.api.repository.write.concurrent.*
import org.chatRoom.api.repository.write.event.*
import org.chatRoom.api.repository.write.guard.*
import org.chatRoom.api.repository.write.state.*
import org.chatRoom.api.route.*
import org.chatRoom.api.state.StateManager
import org.chatRoom.core.repository.read.*
import org.chatRoom.core.repository.write.*
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

            embeddedServer(
                Netty,
                host = config.host,
                port = config.port.toInt(),
                watchPaths = listOf(),
            ) {
                configureMonitoring()
                configureHTTP()
                configureSerialization()
                configureErrorPage()
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

        singleOf(::AuthenticationRoutes)
        singleOf(::AuthenticationController)

        singleOf(::UserRoutes)
        singleOf(::UserController)
        singleOf(::UserReadEventRepository)
        singleOf(::UserWriteEventRepository)
        singleOf(::UserReadStateRepository) { bind<UserReadRepository>() }
        singleOf(::UserWriteStateRepository)
        single<UserWriteRepository> { UserWriteGuardRepository(
            UserWriteCascadeRepository(
                UserWriteConcurrentRepository(listOf(
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
                RoomWriteConcurrentRepository(listOf(
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
                MemberWriteConcurrentRepository(listOf(
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
            MessageWriteConcurrentRepository(listOf(
                get<MessageWriteEventRepository>(),
                get<MessageWriteStateRepository>(),
            )),
            get(),
        ) }

        singleOf(::SessionRoutes)
        singleOf(::SessionController)
        singleOf(::SessionReadEventRepository)
        singleOf(::SessionWriteEventRepository)
        singleOf(::SessionReadStateRepository) { bind<SessionReadRepository>() }
        singleOf(::SessionWriteStateRepository)
        single<SessionWriteRepository> { SessionWriteGuardRepository(
            SessionWriteConcurrentRepository(listOf(
                get<SessionWriteEventRepository>(),
                get<SessionWriteStateRepository>(),
            )),
            get(),
        ) }
    }

    val koin = koinApplication {
        slf4jLogger()
        modules(module)
    }
}
