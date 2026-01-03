package hylosy.pcea.model.card

data class PhysicalCard(
    val id: Int,
    val name: String,
)



data class CardRelations(
    val cardId: Int,
    val physicalCardId: Int,
    val name: String,
)


enum class CardType(val value: Int) {
    Pokemon(1),
    Goods(2),
    Tool(3),
    Support(4),
    Studium(5),
    Energy(6),
}
