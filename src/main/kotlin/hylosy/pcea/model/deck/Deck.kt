package hylosy.pcea.model.deck

import org.jetbrains.exposed.sql.Table

data class CardInDeck(
    val cardId: Int,
    val count: Int
)

object CardInDeckTable: Table("cards_in_deck") {
    val deckId = varchar("deckId", 50)
    val cardId = integer("cardId")
    val count = integer("count")

    override val primaryKey = PrimaryKey(deckId, cardId)
}
