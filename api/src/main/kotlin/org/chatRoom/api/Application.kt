package org.chatRoom.api

import io.ktor.server.engine.*
import org.chatRoom.core.db.MigrationManager

fun main() {
    val migrationManager = ServiceContainer.koin.koin.get<MigrationManager>()
    migrationManager.migrate()

    val engine = ServiceContainer.koin.koin.get<ApplicationEngine>()
    engine.start(wait = true)
}
