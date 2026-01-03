package hylosy.pcea.model.card

data class Deck(
    private var cards: List<Card>,
) {
    companion object {
        const val CARD_COUNT = 60

        fun generate(cards: List<DeckCard>): Deck {
            val dummyCount = CARD_COUNT - cards.sumOf { it.count }
            return Deck(
                cards.flatMap { card -> (1..card.count).map { card.card } } +
                    (1..dummyCount).map { DummyCard() },
            )
        }
    }

    fun shuffle(): List<Card> {
        this.cards = cards.shuffled()
        return this.cards
    }
}

data class DeckCard(
    val card: Card,
    val count: Int,
)
