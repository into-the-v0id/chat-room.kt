package org.chatRoom.core.db

import org.flywaydb.core.Flyway
import javax.sql.DataSource

class MigrationManager(dataSource: DataSource) {
    private val flyway: Flyway = Flyway.configure()
        .dataSource(dataSource)
        .load()

    fun migrate() {
        val migrationResult = flyway.migrate()
        if (! migrationResult.success) error("DB migration failed")
    }
}
