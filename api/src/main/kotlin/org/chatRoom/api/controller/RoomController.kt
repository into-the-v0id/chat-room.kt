package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Room
import org.chatRoom.api.payload.room.CreateRoom
import org.chatRoom.api.payload.room.UpdateRoom
import org.chatRoom.api.resource.Rooms
import org.chatRoom.core.repository.RoomRepository
import org.chatRoom.core.aggreagte.Room as RoomAggregate

class RoomController(private val roomRepository: RoomRepository) {
    suspend fun list(call: ApplicationCall, resource: Rooms) {
        val handles = resource.handles.ifEmpty { null }

        val roomModels = roomRepository.getAll(handles = handles)
            .map { roomAggregate -> Room(roomAggregate) }

        call.respond(roomModels)
    }

    suspend fun detail(call: ApplicationCall, resource: Rooms.Detail) {
        val roomAggregate = roomRepository.getById(resource.id) ?: throw NotFoundException()
        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateRoom>()

        val existingRooms = roomRepository.getAll(handles = listOf(payload.handle))
        if (existingRooms.isNotEmpty()) throw BadRequestException("Handle in use")

        val roomAggregate = RoomAggregate.create(handle = payload.handle)
        roomRepository.create(roomAggregate)

        val roomModel = Room(roomAggregate)

        call.response.header(
            HttpHeaders.Location,
            call.application.href(Rooms.Detail(roomAggregate.modelId))
        )
        call.respond(HttpStatusCode.Created, roomModel)
    }

    suspend fun update(call: ApplicationCall, resource: Rooms.Detail) {
        var roomAggregate = roomRepository.getById(resource.id) ?: throw NotFoundException()

        val payload = call.receive<UpdateRoom>()

        if (payload.handle != roomAggregate.handle) {
            val existingRooms = roomRepository.getAll(handles = listOf(payload.handle))
            if (existingRooms.isNotEmpty()) throw BadRequestException("Handle in use")

            roomAggregate = roomAggregate.changeHandle(payload.handle)
        }

        roomRepository.update(roomAggregate)

        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun delete(call: ApplicationCall, resource: Rooms.Detail) {
        val roomAggregate = roomRepository.getById(resource.id) ?: throw NotFoundException()

        roomRepository.delete(roomAggregate)

        call.respond(HttpStatusCode.NoContent)
    }
}
