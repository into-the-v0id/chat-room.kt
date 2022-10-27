/*
 * Copyright (C) Oliver Amann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.chatRoom.api

import io.ktor.server.engine.*
import org.chatRoom.core.db.MigrationManager
import org.chatRoom.core.state.StateManager
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        System.err.println("error: unexpected arguments")
        println("usage: chat-room-api")
        exitProcess(1)
    }

    val migrationManager = ServiceContainer.koin.koin.get<MigrationManager>()
    migrationManager.migrate()

    val stateManager = ServiceContainer.koin.koin.get<StateManager>()
    stateManager.replayAllEvents()

    val engine = ServiceContainer.koin.koin.get<ApplicationEngine>()
    engine.start(wait = true)
}
