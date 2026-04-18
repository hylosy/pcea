package hylosy.pcea.script

import hylosy.pcea.di.ServiceModule
import hylosy.pcea.service.deck.DeckSearcher
import hylosy.pcea.service.deck.DeckService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.time.LocalDate

private val logger = LoggerFactory.getLogger("RunCardInDeckScript")

fun main(args: Array<String>) {
    val from = if (args.size >= 1) LocalDate.parse(args[0]) else LocalDate.now()
    val to = if (args.size >= 2) LocalDate.parse(args[1]) else from
    runBlocking {
        runStoreCardInDeckScript(from, to)
    }
}

suspend fun runStoreCardInDeckScript(
    from: LocalDate,
    to: LocalDate,
) {
    val deckSearcher = DeckSearcher()
    val deckService = DeckService()
    val eventResultService = ServiceModule.eventResultService

    logger.info("Fetching event results from=$from to=$to")
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
