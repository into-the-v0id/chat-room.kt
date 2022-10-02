package org.chatRoom.api.plugin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import org.chatRoom.core.valueObject.Id
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.DEBUG
    }
    install(CallId) {
        header(HttpHeaders.XCorrelationId)
        generate { Id().toString() }
    }
}
