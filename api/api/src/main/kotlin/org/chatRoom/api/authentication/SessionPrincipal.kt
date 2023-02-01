package org.chatRoom.api.authentication

import io.ktor.server.auth.*
import org.chatRoom.core.aggreagte.Session

data class SessionPrincipal(val session: Session): Principal
