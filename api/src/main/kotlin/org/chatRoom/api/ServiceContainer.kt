package org.chatRoom.api

import io.ktor.server.engine.*
import io.ktor.server.netty.*
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
import org.postgresql.ds.PGPoolingDataSource
import java.sql.Connection
import javax.sql.DataSource

object ServiceContainer {
    val module = module {
        single<ApplicationEngine> {
            embeddedServer(Netty, host = "0.0.0.0", port = 8080) {
                configureHTTP()
                configureMonitoring()
                configureSerialization()
                get<Routing>().apply { configureRouting() }
            }
        }
        single<DataSource> {
            PGPoolingDataSource().apply {
                serverNames = arrayOf("localhost")
                databaseName = "app"
                user = "app"
                password = "app"
//                maxConnections = 10
            }
        }
        factory<Connection> { get<DataSource>().connection }

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
