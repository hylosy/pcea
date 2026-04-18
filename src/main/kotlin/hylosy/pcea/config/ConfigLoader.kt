package hylosy.pcea.config

import com.typesafe.config.ConfigFactory
import hylosy.pcea.db.DatabaseConfig
import java.io.File
import java.time.LocalDate

object ConfigLoader {
    fun loadDatabaseConfig(): DatabaseConfig {
        val config =
            ConfigFactory
                .parseFile(File("src/main/resources/application.conf"))
                .resolve()

        return DatabaseConfig(
            url = config.getString("database.url"),
            driver = config.getString("database.driver"),
            user = config.getString("database.user"),
            password = config.getString("database.password"),
        )
    }

    fun loadDatabaseConfigFromClasspath(): DatabaseConfig {
        val config = ConfigFactory.load("application.conf")

        return DatabaseConfig(
            url = config.getString("database.url"),
            driver = config.getString("database.driver"),
            user = config.getString("database.user"),
            password = config.getString("database.password"),
        )
    }

    fun loadTaskConfig(): TaskConfig {
        val config =
            ConfigFactory
                .parseFile(File("src/main/resources/task.conf"))
                .resolve()

        val eagleFolderId = if (config.hasPath("image-fetcher.eagle-folder-id")) config.getString("image-fetcher.eagle-folder-id") else ""
        require(eagleFolderId.isNotBlank()) { "image-fetcher.eagle-folder-id is not set in task.conf" }

        return TaskConfig(
            eagleFolderId = eagleFolderId,
            holdingEventIds = config.getLongList("image-fetcher.holding-event-ids"),
        )
    }

    fun loadFetchHoldingEventResultTaskConfig(): FetchHoldingEventResultTaskTaskConfig {
        val config =
            ConfigFactory
                .parseFile(File("src/main/resources/task.conf"))
                .resolve()

        return FetchHoldingEventResultTaskTaskConfig(
            startDate =
                try {
                    LocalDate.parse(config.getString("event-result-fetcher.start-date"))
                } catch (_: Exception) {
                    null
                },
            endDate =
                try {
                    LocalDate.parse(config.getString("event-result-fetcher.end-date"))
                } catch (_: Exception) {
                    null
                },
            holdingEventIds = config.getLongList("event-result-fetcher.holding-event-ids"),
        )
    }
}

data class TaskConfig(
    val eagleFolderId: String,
    val holdingEventIds: List<Long>,
)

data class FetchHoldingEventResultTaskTaskConfig(
    val holdingEventIds: List<Long>,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
)
