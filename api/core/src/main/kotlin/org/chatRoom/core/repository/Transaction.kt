package org.chatRoom.core.repository

import org.chatRoom.core.valueObject.Id
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Transaction::class.java)
    }

    fun subscribeToCommit(subscriber: Subscriber) = commitSubscribers.add(subscriber)
    fun subscribeToRollback(subscriber: Subscriber) = rollbackSubscribers.add(subscriber)
    fun subscribeToClose(subscriber: Subscriber) = closeSubscribers.add(subscriber)

    fun commit() {
        if (isClosed) error("Unable to commit transaction: Transaction is closed")

        logger.debug("Committing Transaction with ID $id")

        this.use {
            commitSubscribers.forEach { subscriber -> subscriber(this) }
        }
    }

    fun rollback() {
        if (isClosed) error("Unable to rollback transaction: Transaction is closed")

        logger.debug("Rolling back Transaction with ID $id")

        this.use {
            rollbackSubscribers.forEach { subscriber -> subscriber(this) }
        }
    }

    override fun close() {
        if (isClosed) error("Unable to close transaction: Transaction is already closed")

        logger.debug("Closing Transaction with ID $id")

        isClosed = true
        closeSubscribers.forEachCatching { subscriber -> subscriber(this) }
    }
}

inline fun <R> Transaction.execute(block: (Transaction) -> R): R {
    if (isClosed) error("Unable to use transaction: Transaction is closed")

    Transaction.logger.debug("Executing Transaction with ID $id")

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
