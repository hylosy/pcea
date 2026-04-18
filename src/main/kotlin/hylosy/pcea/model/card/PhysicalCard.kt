package hylosy.pcea.model.card

import org.jetbrains.exposed.sql.Table

data class PhysicalCard(
    val id: Long,
    val name: String,
    val cardType: CardType,
    val number: String,
    val rarity: String?,
    val illustrator: String?,
    val expansionId: String,
    val imageUrl: String?,
)

object PhysicalCardTable : Table("physical_cards") {
    val id = long("id")
    val name = varchar("name", 100)
    val cardType = integer("cardType")
    val number = varchar("number", 20)
    val rarity = varchar("rarity", 50).nullable()
    val illustrator = varchar("illustrator", 100).nullable()
    val expansionId = varchar("expansionId", 50)
    val imageUrl = varchar("imageUrl", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

enum class CardType(
    val value: Int,
) {
    Pokemon(1),
    Goods(2),
    Tool(3),
    Support(4),
    Studium(5),
    Energy(6),
}
