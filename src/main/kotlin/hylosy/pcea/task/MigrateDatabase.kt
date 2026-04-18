package hylosy.pcea.task
import org.flywaydb.core.Flyway

fun main() {
    // NOTE: In current, I use db from only local environment.
    // If you move database from local to cloud, you must store config in secret place.
    val flyway =
        Flyway
            .configure()
            .dataSource(
                "jdbc:mysql://127.0.0.1:3306/pcea?useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "root",
            ).locations("filesystem:src/main/resources/db/migration")
            .load()
    flyway.repair()
    flyway.migrate()
}
