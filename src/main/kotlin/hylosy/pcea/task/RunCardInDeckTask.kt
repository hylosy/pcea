package hylosy.pcea.task

import hylosy.pcea.di.ServiceModule
import hylosy.pcea.service.deck.DeckSearcher
import hylosy.pcea.service.deck.DeckService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.time.LocalDate

private val logger = LoggerFactory.getLogger("RunCardInDeckTask")

fun main() {
    runBlocking {
        runStoreCardInDeckTask()
    }
}

suspend fun runStoreCardInDeckTask() {
    val deckSearcher = DeckSearcher()
    val deckService = DeckService()
    val eventResultService = ServiceModule.eventResultService

    val from = LocalDate.of(2026, 2, 16)
    val to = LocalDate.of(2026, 2, 19)
    val eventResults = eventResultService.getEventResults(from, to)
    val deckIds = eventResults.map { it.deckId }
    logger.info("Target deckIds: $deckIds")
    deckIds.map { deckId ->
        runCatching {
            val document = deckSearcher.fetchDeckInfo(deckId)
            val cardsInDeck = deckService.parseAll(document)
            deckService.createCardsInDeck(deckId, cardsInDeck)
            logger.info("Saved cards for deckId=$deckId")
            sleep(1000)
        }.onFailure { logger.error("Failed to process deckId=$deckId", it) }
    }
}
