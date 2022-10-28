package org.chatRoom.core.db

import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import javax.sql.DataSource

class MigrationManager(dataSource: DataSource) {
    companion object {
        private val logger = LoggerFactory.getLogger(MigrationManager::class.java)
    }

    private val flyway: Flyway = Flyway.configure()
        .dataSource(dataSource)
        .load()

    fun migrate() {
        logger.info("Running pending database migrations ...")

        val migrationResult = flyway.migrate()
        if (! migrationResult.success) error("DB migration failed")

        logger.info("Successfully ran pending database migrations")
    }
}
