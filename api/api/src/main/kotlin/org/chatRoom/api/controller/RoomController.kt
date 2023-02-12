package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.authentication.SessionPrincipal
import org.chatRoom.api.exception.HttpException
import org.chatRoom.core.model.Room
import org.chatRoom.core.payload.room.CreateRoom
import org.chatRoom.core.payload.room.UpdateRoom
import org.chatRoom.api.resource.Rooms
import org.chatRoom.core.repository.read.MemberQuery
import org.chatRoom.core.repository.read.MemberReadRepository
import org.chatRoom.core.repository.read.RoomQuery
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.repository.write.update
import org.chatRoom.core.response.ListResponse
import org.chatRoom.core.valueObject.Limit
import org.chatRoom.core.valueObject.Offset
import org.chatRoom.core.aggreagte.Room as RoomAggregate

class RoomController(
    private val roomReadRepository: RoomReadRepository,
    private val roomWriteRepository: RoomWriteRepository,
    private val memberReadRepository: MemberReadRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Rooms) {
        val query = RoomQuery(
            ids = resource.ids.ifEmpty { null },
            handles = resource.handles.ifEmpty { null },
            offset = resource.offset ?: Offset(0),
            limit = resource.limit ?: Limit(100),
            sortCriteria = resource.sortCriteria,
        )

        val roomModels = roomReadRepository.getAll(query)
            .map { roomAggregate -> Room(roomAggregate) }

        val listResponse = ListResponse(
            data = roomModels,
            list = ListResponse.ListInfo(
                offset = query.offset,
                limit = query.limit,
                currentItemCount = roomModels.size,
                totalItemCount = roomReadRepository.count(query.copy(offset = null, limit = null)),
            )
        )

        call.respond(listResponse)
    }

    suspend fun detail(call: ApplicationCall, resource: Rooms.Detail) {
        val roomAggregate = roomReadRepository.getById(resource.id) ?: throw NotFoundException()
        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateRoom>()

        val existingRooms = roomReadRepository.getAll(RoomQuery(handles = listOf(payload.handle)))
        if (existingRooms.isNotEmpty()) throw BadRequestException("Handle in use")

        val roomAggregate = RoomAggregate.create(handle = payload.handle)
        roomWriteRepository.create(roomAggregate)

        val roomModel = Room(roomAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Rooms.Detail(roomAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, roomModel)
    }

    suspend fun update(call: ApplicationCall, resource: Rooms.Detail) {
        var roomAggregate = roomReadRepository.getById(resource.id) ?: throw NotFoundException()

        val session = call.principal<SessionPrincipal>()!!.session
        val memberAggregate = memberReadRepository.getAll(MemberQuery(
            roomIds = listOf(roomAggregate.modelId),
            userIds = listOf(session.userId),
        )).firstOrNull()
        if (memberAggregate == null) throw HttpException(HttpStatusCode.Forbidden)

        val payload = call.receive<UpdateRoom>()

        if (payload.id != null && payload.id != roomAggregate.modelId) throw BadRequestException("Mismatching IDs")
        if (payload.handle != roomAggregate.handle) {
            val existingRooms = roomReadRepository.getAll(RoomQuery(handles = listOf(payload.handle)))
            if (existingRooms.isNotEmpty()) throw BadRequestException("Handle in use")

            roomAggregate = roomAggregate.changeHandle(payload.handle)
        }

        roomWriteRepository.update(roomAggregate)

        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Rooms.Detail) {
        val roomAggregate = roomReadRepository.getById(resource.id) ?: throw NotFoundException()

        val session = call.principal<SessionPrincipal>()!!.session
        val memberAggregate = memberReadRepository.getAll(MemberQuery(
            roomIds = listOf(roomAggregate.modelId),
            userIds = listOf(session.userId),
        )).firstOrNull()
        if (memberAggregate == null) throw HttpException(HttpStatusCode.Forbidden)

        roomWriteRepository.delete(roomAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
