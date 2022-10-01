package org.chatRoom.api

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.chatRoom.api.plugin.*
import org.chatRoom.core.db.MigrationManager
import org.chatRoom.core.repository.UserRepository
import org.postgresql.ds.PGPoolingDataSource

fun main() {
    val dataSource = PGPoolingDataSource().apply {
        serverNames = arrayOf("localhost")
        databaseName = "app"
        user = "app"
        password = "app"
//        maxConnections = 10
    }

    val migrationManager = MigrationManager(dataSource)
    migrationManager.migrate()

    val userRepository = UserRepository(dataSource.connection)

    embeddedServer(Netty, host = "0.0.0.0", port = 8080) {
        configureHTTP()
        configureSerialization()
        configureRouting(userRepository)
    }.start(wait = true)
}
