package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.authentication.SessionPrincipal
import org.chatRoom.api.exception.HttpException
import org.chatRoom.core.payload.user.UpdateUser
import org.chatRoom.api.resource.Users
import org.chatRoom.core.model.user.PublicUser
import org.chatRoom.core.repository.read.UserQuery
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.repository.write.update
import org.chatRoom.core.response.ListResponse

class UserController(
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Users) {
        val query = UserQuery(
            ids = resource.ids.ifEmpty { null },
            handles = resource.handles.ifEmpty { null },
            offset = resource.offset,
            limit = resource.limit,
            sortCriteria = resource.sortCriteria,
        )

        val userModels = userReadRepository.getAll(query)
            .map { userAggregate -> PublicUser(userAggregate) }

        val listResponse = ListResponse(
            data = userModels,
            list = ListResponse.ListInfo(
                offset = resource.offset,
                limit = resource.limit,
                currentItemCount = userModels.size,
                totalItemCount = userReadRepository.count(query.copy(offset = null, limit = null)),
            )
        )

        call.respond(listResponse)
    }

    suspend fun detail(call: ApplicationCall, resource: Users.Detail) {
        val userAggregate = userReadRepository.getById(resource.id) ?: throw NotFoundException()
        val userModel = PublicUser(userAggregate)

        call.respond(userModel)
    }

    suspend fun update(call: ApplicationCall, resource: Users.Detail) {
        var userAggregate = userReadRepository.getById(resource.id) ?: throw NotFoundException()

        val session = call.principal<SessionPrincipal>()!!.session
        if (session.userId != userAggregate.modelId) throw HttpException(HttpStatusCode.Forbidden)

        val payload = call.receive<UpdateUser>()

        if (payload.id != null && payload.id != userAggregate.modelId) throw BadRequestException("Mismatching IDs")
        if (payload.handle != userAggregate.handle) {
            val existingUsers = userReadRepository.getAll(UserQuery(handles = listOf(payload.handle)))
            if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

            userAggregate = userAggregate.changeHandle(payload.handle)
        }
        if (payload.email != userAggregate.email) userAggregate = userAggregate.changeEmail(payload.email)

        userWriteRepository.update(userAggregate)

        val userModel = PublicUser(userAggregate)

        call.respond(userModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Users.Detail) {
        val userAggregate = userReadRepository.getById(resource.id) ?: throw NotFoundException()

        val session = call.principal<SessionPrincipal>()!!.session
        if (session.userId != userAggregate.modelId) throw HttpException(HttpStatusCode.Forbidden)

        userWriteRepository.delete(userAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
