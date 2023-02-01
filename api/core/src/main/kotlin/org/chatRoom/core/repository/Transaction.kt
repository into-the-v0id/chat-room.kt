package org.chatRoom.core.repository

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.chatRoom.core.valueObject.Id
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection

private typealias Subscriber = suspend (Transaction) -> Unit

class Transaction(val id: Id) : AutoCloseable {
    private val commitSubscribers = mutableListOf<Subscriber>()
    private val rollbackSubscribers = mutableListOf<Subscriber>()
    private val closeSubscribers = mutableListOf<Subscriber>()
    private var isClosed = false
    private val stateMutex: Mutex = Mutex()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Transaction::class.java)
    }

    suspend fun isClosed() = stateMutex.withLock {
        isClosed
    }

    suspend fun subscribeToCommit(subscriber: Subscriber) = stateMutex.withLock {
        commitSubscribers.add(subscriber)
    }

    suspend fun subscribeToRollback(subscriber: Subscriber) = stateMutex.withLock {
        rollbackSubscribers.add(subscriber)
    }

    suspend fun subscribeToClose(subscriber: Subscriber) = stateMutex.withLock {
        closeSubscribers.add(subscriber)
    }

    suspend fun commit() {
        if (isClosed()) error("Unable to commit transaction: Transaction is closed")

        logger.debug("Committing Transaction with ID $id")

        try {
            commitSubscribers.forEach { subscriber -> subscriber(this) }
        } catch (subscriberException: Throwable) {
            try {
                closeSuspending()
            } catch (closeException: Throwable) {
                subscriberException.addSuppressed(closeException)
            }

            throw subscriberException
        }

        closeSuspending()
    }

    suspend fun rollback() {
        if (isClosed()) error("Unable to rollback transaction: Transaction is closed")

        logger.debug("Rolling back Transaction with ID $id")

        try {
            rollbackSubscribers.forEach { subscriber -> subscriber(this) }
        } catch (subscriberException: Throwable) {
            try {
                closeSuspending()
            } catch (closeException: Throwable) {
                subscriberException.addSuppressed(closeException)
            }

            throw subscriberException
        }

        closeSuspending()
    }

    suspend fun closeSuspending() {
        if (isClosed()) error("Unable to close transaction: Transaction is already closed")

        logger.debug("Closing Transaction with ID $id")

        stateMutex.withLock { isClosed = true }
        closeSubscribers.forEachCatching { subscriber -> subscriber(this) }
    }

    override fun close() = runBlocking { closeSuspending() }
}

suspend inline fun <R> Transaction.execute(block: (Transaction) -> R): R {
    if (isClosed()) error("Unable to use transaction: Transaction is closed")

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

suspend fun Transaction.subscribeSqlConnection(connection: Connection) {
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
