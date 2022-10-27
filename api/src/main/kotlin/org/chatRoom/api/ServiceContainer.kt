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
import org.chatRoom.core.db.MigrationManager
import org.chatRoom.core.repository.MemberRepository
import org.chatRoom.core.repository.MessageRepository
import org.chatRoom.core.repository.RoomRepository
import org.chatRoom.core.repository.UserRepository
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
        singleOf(::Routing)
        singleOf(::UserRoutes)
        singleOf(::UserController)
        singleOf(::UserRepository)
        singleOf(::RoomRoutes)
        singleOf(::RoomController)
        singleOf(::RoomRepository)
        singleOf(::MemberRoutes)
        singleOf(::MemberController)
        singleOf(::MemberRepository)
        singleOf(::MessageRoutes)
        singleOf(::MessageController)
        singleOf(::MessageRepository)
    }

    val koin = koinApplication {
        slf4jLogger()
        modules(module)
    }
}
