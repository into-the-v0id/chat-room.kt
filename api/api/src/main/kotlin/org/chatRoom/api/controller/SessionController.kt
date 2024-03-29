package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import org.chatRoom.api.authentication.SessionPrincipal
import org.chatRoom.api.resource.Sessions
import org.chatRoom.core.model.session.PublicSession
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.repository.write.SessionWriteRepository
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.response.ListResponse
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import java.time.Instant

class SessionController(
    private val sessionReadRepository: SessionReadRepository,
    private val sessionWriteRepository: SessionWriteRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Sessions) {
        val session = call.principal<SessionPrincipal>()!!.session

        val query = SessionQuery(
            ids = resource.ids.ifEmpty { null },
            userIds = listOf(session.userId),
            isExpired = false,
            offset = resource.offset ?: Offset(0),
            limit = resource.limit ?: Limit(100),
            sortCriteria = resource.sortCriteria,
        )

        val sessionModels = sessionReadRepository.getAll(query)
            .map { sessionAggregate -> PublicSession(sessionAggregate) }

        val listResponse = ListResponse(
            data = sessionModels,
            list = ListResponse.ListInfo(
                offset = query.offset,
                limit = query.limit,
                currentItemCount = sessionModels.size,
                totalItemCount = sessionReadRepository.count(query.copy(offset = null, limit = null)),
            )
        )

        call.respond(listResponse)
    }

    suspend fun detail(call: ApplicationCall, resource: Sessions.Detail) {
        val sessionAggregate = sessionReadRepository.getById(resource.id) ?: throw NotFoundException()

        val session = call.principal<SessionPrincipal>()!!.session
        if (session.userId != sessionAggregate.userId) throw NotFoundException()

        if (sessionAggregate.isExpired()) throw NotFoundException()

        val sessionModel = PublicSession(sessionAggregate)

        call.respond(sessionModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Sessions.Detail) {
        val sessionAggregate = sessionReadRepository.getById(resource.id) ?: throw NotFoundException()

        val session = call.principal<SessionPrincipal>()!!.session
        if (session.userId != sessionAggregate.userId) throw NotFoundException()

        if (sessionAggregate.isExpired()) throw NotFoundException()

        sessionWriteRepository.delete(sessionAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
