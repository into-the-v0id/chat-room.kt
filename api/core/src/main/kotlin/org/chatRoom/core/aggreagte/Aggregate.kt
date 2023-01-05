package org.chatRoom.core.aggreagte

import org.chatRoom.core.event.Event
import org.chatRoom.core.valueObject.Id
import org.slf4j.LoggerFactory

abstract class Aggregate<E>(
    modelId: Id,
    events: List<E> = listOf(),
): Cloneable {
    var modelId: Id = modelId
        protected set

    var events: List<E> = events
        protected set

    companion object {
        private val logger = LoggerFactory.getLogger(Aggregate::class.java)

        @JvmStatic
        protected fun <A: Aggregate<E>, E: Event> applyAllEvents(
            aggregate: A?,
            events: List<E>,
            handler: (aggregate: A?, event: E) -> A?,
        ): A? {
            logger.debug("Applying ${events.size} ${if (events.size == 1) "event" else "events" } to ${if (aggregate == null) "null" else aggregate::class.java.name}")

            var newAggregate = aggregate?.clone() as A?
            var newEvents = newAggregate?.events?.toMutableList() ?: mutableListOf()

            for (event in events) {
                logger.trace("Event: ${event.eventType}")

                if (aggregate != null && event.modelId != aggregate.modelId) error("Model ID mismatch (Aggregate / Event)")

                newAggregate = handler(newAggregate, event)

                if (newAggregate != null) {
                    if (newAggregate.events != newEvents) {
                        newEvents = newAggregate.events.toMutableList()
                    }

                    newEvents.add(event)
                    newAggregate.events = newEvents
                }
            }

            if (newAggregate != null) {
                newAggregate.events = newAggregate.events.toList()
            }

            return newAggregate
        }
    }

    override fun equals(other: Any?): Boolean = other is Aggregate<*> && modelId == other.modelId

    override fun hashCode(): Int = modelId.hashCode()
}
