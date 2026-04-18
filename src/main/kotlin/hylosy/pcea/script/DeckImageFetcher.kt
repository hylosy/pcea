package hylosy.pcea.script

import hylosy.pcea.config.ConfigLoader
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.service.EagleClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.LocalDate

private val logger = LoggerFactory.getLogger("DeckImageFetcher")

fun main(args: Array<String>) {
    val params = args.toList().chunked(2).associate { it[0] to it[1] }
    val from = params["--from"]?.let { LocalDate.parse(it) }
    val to = params["--to"]?.let { LocalDate.parse(it) }

    DatabaseManager.initialize(ConfigLoader.loadDatabaseConfig())
    runBlocking {
        fetchDeckImages(from, to)
    }
}

suspend fun fetchDeckImages(
    from: LocalDate? = null,
    to: LocalDate? = null,
) {
    val taskConfig = ConfigLoader.loadTaskConfig()
    val holdingEventService = ServiceModule.holdingEventService
    val eagleClient = EagleClient()

    val deckCodes =
        when {
            from != null && to != null -> holdingEventService.getDeckCodesByDateRange(from, to)
            taskConfig.holdingEventIds.isNotEmpty() -> holdingEventService.getDeckCodesByHoldingEventIds(taskConfig.holdingEventIds)
            else -> holdingEventService.getDeckCodes()
        }

    logger.info("Target deck codes: ${deckCodes.size}")
    deckCodes.forEach { deckCode ->
        if (eagleClient.exists(name = deckCode, folderId = taskConfig.eagleFolderId)) {
            logger.info("Already exists in Eagle, skipping: $deckCode")
            return@forEach
        }
        val url = "https://www.pokemon-card.com/deck/deckView.php/deckID/$deckCode.png"
        eagleClient
            .addFromURL(url = url, name = deckCode, folderId = taskConfig.eagleFolderId)
            .onSuccess { logger.info("Imported to Eagle: $deckCode") }
            .onFailure { logger.error("Failed to import to Eagle: $deckCode", it) }
        delay(1000)
    }
}
