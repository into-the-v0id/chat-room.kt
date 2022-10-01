package org.chatRoom.api.plugin

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.defaultheaders.*
import java.util.Base64

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("Server", "Ktor")
    }
    install(ConditionalHeaders) {
        version { call, content ->
            when (content) {
                is OutgoingContent.ByteArrayContent -> listOf(
                    EntityTagVersion(Base64.getEncoder().encodeToString(content.bytes()).hashCode().toString()),
                )
                else -> emptyList()
            }
        }
    }
    install(CachingHeaders) {
        options { _, _ ->
            CachingOptions(CacheControl.MaxAge(
                maxAgeSeconds = 0,
                mustRevalidate = true,
                visibility = CacheControl.Visibility.Private,
            ))
        }
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)

        anyHost()
    }
    install(Compression)
}
