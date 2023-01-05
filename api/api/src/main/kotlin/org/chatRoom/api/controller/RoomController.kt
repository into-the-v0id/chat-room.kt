package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.core.model.Room
import org.chatRoom.core.payload.room.CreateRoom
import org.chatRoom.core.payload.room.UpdateRoom
import org.chatRoom.api.resource.Rooms
import org.chatRoom.core.repository.read.RoomReadRepository
import org.chatRoom.core.repository.write.RoomWriteRepository
import org.chatRoom.core.repository.write.create
import org.chatRoom.core.repository.write.delete
import org.chatRoom.core.repository.write.update
import org.chatRoom.core.aggreagte.Room as RoomAggregate

class RoomController(
    private val roomReadRepository: RoomReadRepository,
    private val roomWriteRepository: RoomWriteRepository,
) {
    suspend fun list(call: ApplicationCall, resource: Rooms) {
        val ids = resource.ids.ifEmpty { null }
        val handles = resource.handles.ifEmpty { null }

        val roomModels = roomReadRepository.getAll(
            ids = ids,
            handles = handles,
            offset = resource.offset,
            limit = resource.limit,
            sortCriteria = resource.sortCriteria,
        ).map { roomAggregate -> Room(roomAggregate) }

        call.respond(roomModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Rooms.Detail) {
        val roomAggregate = roomReadRepository.getById(resource.id) ?: throw NotFoundException()
        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateRoom>()

        val existingRooms = roomReadRepository.getAll(handles = listOf(payload.handle))
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

        val payload = call.receive<UpdateRoom>()

        if (payload.id != null && payload.id != roomAggregate.modelId) throw BadRequestException("Mismatching IDs")
        if (payload.handle != roomAggregate.handle) {
            val existingRooms = roomReadRepository.getAll(handles = listOf(payload.handle))
            if (existingRooms.isNotEmpty()) throw BadRequestException("Handle in use")

            roomAggregate = roomAggregate.changeHandle(payload.handle)
        }

        roomWriteRepository.update(roomAggregate)

        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Rooms.Detail) {
        val roomAggregate = roomReadRepository.getById(resource.id) ?: throw NotFoundException()

        roomWriteRepository.delete(roomAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
