package hylosy.pcea.service.deck

import hylosy.pcea.model.deck.CardInDeck
import hylosy.pcea.model.deck.CardInDeckTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.nodes.Document
import kotlin.collections.flatten

class DeckService {
    private fun connect() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/pcea?useSSL=false&serverTimezone=Asia/Tokyo&rewriteBatchedStatements=true",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "root",
        )
    }

    fun createCardsInDeck(
        deckId: String,
        cards: List<CardInDeck>,
    ) {
        connect()
        transaction {
            CardInDeckTable.batchUpsert(cards) { card ->
                this[CardInDeckTable.deckId] = deckId
                this[CardInDeckTable.cardId] = card.cardId
                this[CardInDeckTable.count] = card.count
            }
        }
    }

    private fun parse(
        doc: Document,
        elementType: HTMLDeckElementType,
    ): List<CardInDeck> =
        doc
            .getElementById("inputArea")
            ?.getElementById(elementType.value)
            ?.`val`()
            ?.split("-")
            ?.map { data ->
                val (id, count) = data.split("_")
                CardInDeck(id.toInt(), count.toInt())
            } ?: emptyList()

    fun parseAll(doc: Document): List<CardInDeck> =
        HTMLDeckElementType.entries
            .map { elementType ->
                parse(doc, elementType)
            }.flatten()
}

enum class HTMLDeckElementType(
    val value: String,
) {
    Pokemon("deck_pke"),
    Goods("deck_gds"),
    Tool("deck_tool"),
    Support("deck_sup"),
    Studium("deck_sta"),
    Energy("deck_ene"),
}
