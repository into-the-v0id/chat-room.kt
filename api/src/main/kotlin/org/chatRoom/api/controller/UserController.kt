package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.core.model.User
import org.chatRoom.core.payload.user.CreateUser
import org.chatRoom.core.payload.user.UpdateUser
import org.chatRoom.api.resource.Users
import org.chatRoom.core.repository.read.UserReadRepository
import org.chatRoom.core.repository.write.UserWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.repository.write.update
import org.chatRoom.core.aggreagte.User as UserAggregate

class UserController(
    private val userReadRepository: UserReadRepository,
    private val userWriteRepository: UserWriteRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Users) {
        val ids = resource.ids.ifEmpty { null }
        val handles = resource.handles.ifEmpty { null }

        val userModels = userReadRepository.getAll(
            ids = ids,
            handles = handles,
            offset = resource.offset,
            limit = resource.limit,
            sortCriteria = resource.sortCriteria,
        ).map { userAggregate -> User(userAggregate) }

        call.respond(userModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Users.Detail) {
        val userAggregate = userReadRepository.getById(resource.id) ?: throw NotFoundException()
        val userModel = User(userAggregate)

        call.respond(userModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateUser>()

        val existingUsers = userReadRepository.getAll(handles = listOf(payload.handle))
        if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

        val userAggregate = UserAggregate.create(email = payload.email, handle = payload.handle)
        userWriteRepository.create(userAggregate)

        val userModel = User(userAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Users.Detail(userAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, userModel)
    }

    suspend fun update(call: ApplicationCall, resource: Users.Detail) {
        var userAggregate = userReadRepository.getById(resource.id) ?: throw NotFoundException()

        val payload = call.receive<UpdateUser>()

        if (payload.id != userAggregate.modelId) throw BadRequestException("Mismatching IDs")
        if (payload.handle != userAggregate.handle) {
            val existingUsers = userReadRepository.getAll(handles = listOf(payload.handle))
            if (existingUsers.isNotEmpty()) throw BadRequestException("Handle in use")

            userAggregate = userAggregate.changeHandle(payload.handle)
        }
        if (payload.email != userAggregate.email) userAggregate = userAggregate.changeEmail(payload.email)

        userWriteRepository.update(userAggregate)

        val userModel = User(userAggregate)

        call.respond(userModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Users.Detail) {
        val userAggregate = userReadRepository.getById(resource.id) ?: throw NotFoundException()

        userWriteRepository.delete(userAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
