package org.chatRoom.api.resource

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import org.chatRoom.core.valueObject.*

@Serializable
@Resource("auth")
class Authentication() {
    @Serializable
    @Resource("login")
    class Login() {
        val parent: Authentication = Authentication()
    }

    @Serializable
    @Resource("logout")
    class Logout() {
        val parent: Authentication = Authentication()
    }

    @Serializable
    @Resource("registration")
    class Registration() {
        val parent: Authentication = Authentication()
    }
}
