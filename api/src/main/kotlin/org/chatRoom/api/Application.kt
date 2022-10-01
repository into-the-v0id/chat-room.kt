package org.chatRoom.api

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.chatRoom.api.plugins.*

fun main() {
    embeddedServer(Netty, host = "0.0.0.0", port = 8080) {
        configureHTTP()
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
