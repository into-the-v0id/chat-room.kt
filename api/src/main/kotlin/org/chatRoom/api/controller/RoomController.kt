package org.chatRoom.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.chatRoom.api.model.Room
import org.chatRoom.api.payload.room.CreateRoom
import org.chatRoom.core.repository.RoomRepository
import org.chatRoom.core.valueObject.Id
import org.chatRoom.core.aggreagte.Room as RoomAggregate

class RoomController(private val roomRepository: RoomRepository) {
    private fun fetchRoom(call: ApplicationCall) : RoomAggregate? {
        val rawId = call.parameters["id"] ?: return null

        val id = try {
            Id(rawId)
        } catch (e: Throwable) {
            return null
        }

        return roomRepository.getById(id)
    }

    suspend fun list(call: ApplicationCall) {
        val rooms = roomRepository.getAll()
            .map { roomAggregate -> Room(roomAggregate) }

        call.respond(rooms)
    }

    suspend fun detail(call: ApplicationCall) {
        val roomAggregate = fetchRoom(call) ?: throw NotFoundException()

        val roomModel = Room(roomAggregate)

        call.respond(roomModel)
    }

    suspend fun create(call: ApplicationCall) {
        val payload = call.receive<CreateRoom>()

        val room = RoomAggregate.create(handle = payload.handle)
        roomRepository.create(room)

        call.respond(HttpStatusCode.OK)
    }

    suspend fun delete(call: ApplicationCall) {
        val roomAggregate = fetchRoom(call) ?: throw NotFoundException()

        roomRepository.delete(roomAggregate)

        call.respond(HttpStatusCode.OK)
    }
}
