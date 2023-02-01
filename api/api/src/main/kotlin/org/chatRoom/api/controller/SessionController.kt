package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.core.model.Session
import org.chatRoom.api.resource.Sessions
import org.chatRoom.core.repository.read.SessionQuery
import org.chatRoom.core.repository.read.SessionReadRepository
import org.chatRoom.core.repository.write.SessionWriteRepository
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.response.ListResponse

class SessionController(
    private val sessionReadRepository: SessionReadRepository,
    private val sessionWriteRepository: SessionWriteRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Sessions) {
        val query = SessionQuery(
            ids = resource.ids.ifEmpty { null },
            userIds = resource.userIds.ifEmpty { null },
            offset = resource.offset,
            limit = resource.limit,
            sortCriteria = resource.sortCriteria,
        )

        val sessionModels = sessionReadRepository.getAll(query)
            .map { sessionAggregate -> Session(sessionAggregate) }

        val listResponse = ListResponse(
            data = sessionModels,
            list = ListResponse.ListInfo(
                offset = resource.offset,
                limit = resource.limit,
                currentItemCount = sessionModels.size,
                totalItemCount = sessionReadRepository.count(query.copy(offset = null, limit = null)),
            )
        )

        call.respond(listResponse)
    }

    suspend fun detail(call: ApplicationCall, resource: Sessions.Detail) {
        val sessionAggregate = sessionReadRepository.getById(resource.id) ?: throw NotFoundException()
        val sessionModel = Session(sessionAggregate)

        call.respond(sessionModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Sessions.Detail) {
        val sessionAggregate = sessionReadRepository.getById(resource.id) ?: throw NotFoundException()

        sessionWriteRepository.delete(sessionAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
