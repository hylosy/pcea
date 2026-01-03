package hylosy.pcea.task

import hylosy.pcea.di.ServiceModule
import hylosy.pcea.service.deck.DeckSearcher
import hylosy.pcea.service.deck.DeckService
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.time.LocalDate

fun main() {
    runBlocking {
        runStoreCardInDeckTask()
    }
}

suspend fun runStoreCardInDeckTask() {
    val deckSearcher = DeckSearcher()
    val deckService = DeckService()
    val eventResultService = ServiceModule.eventResultService

    val from = LocalDate.of(2025, 9, 6)
    val to = LocalDate.of(2025, 9, 7)
    val eventResults = eventResultService.getEventResults(from, to)
    val deckIds = eventResults.map { it.deckId }
    deckIds.map { deckId ->
        runCatching {
            val document = deckSearcher.fetchDeckInfo(deckId)
            val cardsInDeck = deckService.parseAll(document)
            deckService.createCardsInDeck(deckId, cardsInDeck)

            sleep(1000)
        }
    }
}