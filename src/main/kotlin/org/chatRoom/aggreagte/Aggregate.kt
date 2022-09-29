package org.chatRoom.aggreagte

import org.chatRoom.events.Event
import org.chatRoom.valueObject.Id

abstract class Aggregate<E>(
    modelId: Id,
    events: List<E> = listOf(),
): Cloneable {
    var modelId: Id = modelId
        protected set

    var events: List<E> = events
        protected set

    companion object {
        @JvmStatic
        protected fun <A: Aggregate<E>, E: Event> applyEvent(
            aggregate: A?,
            event: E,
            handler: (aggregate: A?, event: E) -> A?,
        ): A? {
            if (aggregate != null && event.modelId != aggregate.modelId) error("Model ID mismatch (Aggregate / Event)")

            var newAggregate = aggregate?.clone() as A?
            newAggregate = handler(newAggregate, event)

            if (newAggregate != null) {
                newAggregate.events = newAggregate.events
                    .toMutableList()
                    .also { it.add(event) }
                    .toList()
            }

            return newAggregate
        }
    }

    override fun equals(other: Any?): Boolean = other is Aggregate<*> && events == other.events

    override fun hashCode(): Int = events.hashCode()
}
