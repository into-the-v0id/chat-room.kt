package org.chatRoom.core.repository

import org.chatRoom.core.valueObject.Id
import java.sql.Connection

private typealias Subscriber = (Transaction) -> Unit

class Transaction(
    val id: Id,
) : AutoCloseable {
    private val commitSubscribers = mutableListOf<Subscriber>()
    private val rollbackSubscribers = mutableListOf<Subscriber>()
    private val closeSubscribers = mutableListOf<Subscriber>()
    var isClosed = false
        private set

    fun subscribeToCommit(subscriber: Subscriber) = commitSubscribers.add(subscriber)
    fun subscribeToRollback(subscriber: Subscriber) = rollbackSubscribers.add(subscriber)
    fun subscribeToClose(subscriber: Subscriber) = closeSubscribers.add(subscriber)

    fun commit() {
        if (isClosed) error("Unable to commit transaction: Transaction is closed")

        this.use {
            commitSubscribers.forEach { subscriber -> subscriber(this) }
        }
    }

    fun rollback() {
        if (isClosed) error("Unable to rollback transaction: Transaction is closed")

        this.use {
            rollbackSubscribers.forEach { subscriber -> subscriber(this) }
        }
    }

    override fun close() {
        if (isClosed) error("Unable to close transaction: Transaction is already closed")

        isClosed = true
        closeSubscribers.forEachCatching { subscriber -> subscriber(this) }
    }
}

inline fun <R> Transaction.execute(block: (Transaction) -> R): R {
    if (isClosed) error("Unable to use transaction: Transaction is closed")

    val response = try {
        block(this)
    } catch (blockException: Throwable) {
        try {
            rollback()
        } catch (rollbackException: Throwable) {
            blockException.addSuppressed(rollbackException)
        }

        throw blockException
    }

    commit()

    return response
}

fun Transaction.subscribeSqlConnection(connection: Connection) {
    connection.autoCommit = false
    subscribeToCommit { connection.commit() }
    subscribeToRollback { connection.rollback() }
    subscribeToClose { connection.close() }
}

private inline fun <T> Iterable<T>.forEachCatching(action: (T) -> Unit) {
    var capturedException: Throwable? = null

    for (element in this) {
        try {
            action(element)
        } catch (actionException: Throwable) {
            if (capturedException == null) {
                capturedException = actionException
            } else {
                capturedException.addSuppressed(actionException)
            }
        }
    }

    if (capturedException != null) throw capturedException
}
