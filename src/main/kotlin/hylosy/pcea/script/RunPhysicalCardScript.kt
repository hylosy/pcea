package hylosy.pcea.script

import hylosy.pcea.config.ConfigLoader
import hylosy.pcea.dao.PhysicalCardDao
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.service.card.CardDetailFetcher
import hylosy.pcea.service.card.CardListFetcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RunPhysicalCardScript")

fun main() {
    val expansionNumericId = System.getProperty("expansion")?.toIntOrNull()
        ?: error("Pass -Dexpansion=<numeric expansion id>")
    val expansionId = System.getProperty("expansionId")
        ?: error("Pass -DexpansionId=<expansion string id>")

    DatabaseManager.initialize(ConfigLoader.loadDatabaseConfig())

    runBlocking {
        runPhysicalCardScript(expansionNumericId, expansionId)
    }
}

suspend fun runPhysicalCardScript(
    expansionNumericId: Int,
    expansionId: String,
) {
    val listFetcher = CardListFetcher()
    val detailFetcher = CardDetailFetcher()
    val dao = PhysicalCardDao()

    logger.info("Fetching card list for expansion=$expansionNumericId ($expansionId)")
    val cardItems = listFetcher.fetchAllPages(expansionNumericId)
    logger.info("Found ${cardItems.size} cards")

    cardItems.forEach { item ->
        val cardId = item.cardID.toLong()
        if (dao.existsById(cardId)) {
            logger.info("Already stored cardId=$cardId, skipping")
            return@forEach
        }
        delay(1000)
        detailFetcher
            .fetchCard(cardId, expansionId)
            .onSuccess { card ->
                dao.upsertAll(listOf(card))
                logger.info("Saved cardId=${card.id} name=${card.name}")
            }
            .onFailure { ex ->
                logger.error("Failed for cardId=$cardId", ex)
            }
    }
    logger.info("Done.")
}
