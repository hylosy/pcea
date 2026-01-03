package hylosy.pcea.db

import org.jetbrains.exposed.sql.Database

object DatabaseManager {
    private var initialized = false

    fun initialize(config: DatabaseConfig) {
        if (!initialized) {
            Database.connect(
                url = config.url,
                driver = config.driver,
                user = config.user,
                password = config.password
            )
            initialized = true
        }
    }

    fun initialize(
        url: String,
        driver: String,
        user: String,
        password: String
    ) {
        if (!initialized) {
            Database.connect(
                url = url,
                driver = driver,
                user = user,
                password = password
            )
            initialized = true
        }
    }
}

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
)