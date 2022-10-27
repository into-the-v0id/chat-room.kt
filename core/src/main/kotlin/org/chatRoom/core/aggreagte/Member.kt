package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.member.CreateMember
import org.chatRoom.core.event.member.DeleteMember
import org.chatRoom.core.event.member.MemberEvent
import org.chatRoom.core.valueObject.Id
import java.time.Instant

class Member protected constructor(
    modelId: Id,
    userId: Id,
    roomId: Id,
    dateCreated: Instant = Instant.now(),
    dateUpdated: Instant = dateCreated,
) : Aggregate<MemberEvent>(modelId = modelId) {
    var userId: Id = userId
        protected set

    var roomId: Id = roomId
        protected set

    var dateCreated: Instant = dateCreated
        protected set

    var dateUpdated: Instant = dateUpdated
        protected set

    companion object {
        fun create(user: User, room: Room): Member {
            val event = CreateMember(
                modelId = Id(),
                userId = user.modelId,
                roomId = room.modelId,
            )

            return applyEvent(null, event) ?: error("Expected member")
        }

        fun applyEvent(member: Member?, event: MemberEvent): Member? = applyEvent(member, event, Companion::applyEventInternal)

        fun applyAllEvents(member: Member?, events: List<MemberEvent>): Member? = applyAllEvents(member, events, Companion::applyEventInternal)

        private fun applyEventInternal(member: Member?, event: MemberEvent): Member? {
            var member = member

            when (event) {
                is CreateMember -> {
                    if (member != null) error("Unexpected member")
                    member = Member(
                        modelId = event.modelId,
                        userId = event.userId,
                        roomId = event.roomId,
                        dateCreated = event.dateIssued,
                    )
                }
                is DeleteMember -> {
                    if (member == null) error("Expected member")
                    member = null
                }
                else -> error("Unknown event")
            }

            return member
        }
    }
}
