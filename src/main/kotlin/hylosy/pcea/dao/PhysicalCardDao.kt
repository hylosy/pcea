package hylosy.pcea.dao

import hylosy.pcea.model.card.CardType
import hylosy.pcea.model.card.PhysicalCard
import hylosy.pcea.model.card.PhysicalCardTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class PhysicalCardDao {
    fun existsById(id: Long): Boolean =
        transaction {
            PhysicalCardTable.select { PhysicalCardTable.id eq id }.count() > 0
        }

    fun upsertAll(cards: List<PhysicalCard>) {
        transaction {
            PhysicalCardTable.batchUpsert(cards) { card ->
                this[PhysicalCardTable.id] = card.id
                this[PhysicalCardTable.name] = card.name
                this[PhysicalCardTable.cardType] = card.cardType.value
                this[PhysicalCardTable.number] = card.number
                this[PhysicalCardTable.rarity] = card.rarity
                this[PhysicalCardTable.illustrator] = card.illustrator
                this[PhysicalCardTable.expansionId] = card.expansionId
                this[PhysicalCardTable.imageUrl] = card.imageUrl
            }
        }
    }
}

fun ResultRow.toPhysicalCard(): PhysicalCard =
    PhysicalCard(
        id = this[PhysicalCardTable.id],
        name = this[PhysicalCardTable.name],
        cardType = CardType.entries.first { it.value == this[PhysicalCardTable.cardType] },
        number = this[PhysicalCardTable.number],
        rarity = this[PhysicalCardTable.rarity],
        illustrator = this[PhysicalCardTable.illustrator],
        expansionId = this[PhysicalCardTable.expansionId],
        imageUrl = this[PhysicalCardTable.imageUrl],
    )
