package hylosy.pcea.config

import com.typesafe.config.ConfigFactory
import hylosy.pcea.db.DatabaseConfig
import java.io.File

object ConfigLoader {
    fun loadDatabaseConfig(): DatabaseConfig {
        val config = ConfigFactory.parseFile(File("src/main/resources/application.conf"))
            .resolve()

        return DatabaseConfig(
            url = config.getString("database.url"),
            driver = config.getString("database.driver"),
            user = config.getString("database.user"),
            password = config.getString("database.password")
        )
    }

    fun loadDatabaseConfigFromClasspath(): DatabaseConfig {
        val config = ConfigFactory.load("application.conf")

        return DatabaseConfig(
            url = config.getString("database.url"),
            driver = config.getString("database.driver"),
            user = config.getString("database.user"),
            password = config.getString("database.password")
        )
    }

    fun loadTaskConfig(): TaskConfig {
        val config = ConfigFactory.parseFile(File("src/main/resources/task.conf"))
            .resolve()

        return TaskConfig(
            inputImagePath = config.getString("image-fetcher.input-image-directory-path"),
            outputImagePath = config.getString("image-fetcher.input-image-directory-path"),
            holdingEventIds = config.getLongList("image-fetcher.holding-event-ids")
        )
    }
}

data class TaskConfig(
    val inputImagePath: String,
    val outputImagePath: String,
    val holdingEventIds: List<Long>
)