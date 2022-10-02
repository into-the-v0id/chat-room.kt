package org.chatRoom.api

import io.ktor.server.engine.*

fun main() {
    val engine = ServiceContainer.koin.koin.get<ApplicationEngine>()

    engine.start(wait = true)
}
