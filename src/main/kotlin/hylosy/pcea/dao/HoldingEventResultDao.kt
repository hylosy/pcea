package hylosy.pcea.dao

import hylosy.pcea.model.event.result.HoldingEventResult
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class HoldingEventResultDao {
    fun getHoldingEventResults(holdingEventIds: List<Long>): List<HoldingEventResult> =
        selectForHoldingEventResult {
            HoldingEventResultTable.holdingEventId inList holdingEventIds
        }.map { it.toHoldingEventResult() }

    fun getAllDeckIds(): List<String> =
        HoldingEventResultTable
            .slice(HoldingEventResultTable.holdingEventId, HoldingEventResultTable.deckId)
            .selectAll()
            .orderBy(HoldingEventResultTable.holdingEventId to SortOrder.DESC)
            .map { it[HoldingEventResultTable.deckId] }
            .distinct()

    fun getDeckIdsByHoldingEventIds(holdingEventIds: List<Long>): List<String> =
        HoldingEventResultTable
            .slice(HoldingEventResultTable.holdingEventId, HoldingEventResultTable.deckId)
            .select { HoldingEventResultTable.holdingEventId inList holdingEventIds }
            .orderBy(HoldingEventResultTable.holdingEventId to SortOrder.DESC)
            .map { it[HoldingEventResultTable.deckId] }
            .distinct()

    private fun selectForHoldingEventResult(
        where: org.jetbrains.exposed.sql.SqlExpressionBuilder.() -> org.jetbrains.exposed.sql.Op<Boolean>,
    ) = HoldingEventResultTable
        .slice(
            HoldingEventResultTable.holdingEventId,
            HoldingEventResultTable.rank,
            HoldingEventResultTable.point,
            HoldingEventResultTable.deckId,
            HoldingEventResultTable.playerId,
        ).select(where)

    fun createMultiple(eventResults: List<HoldingEventResult>) {
        HoldingEventResultTable.batchUpsert(eventResults) { eventResult ->
            this[HoldingEventResultTable.holdingEventId] = eventResult.holdingEventId
            this[HoldingEventResultTable.rank] = eventResult.rank
            this[HoldingEventResultTable.deckId] = eventResult.deckId
            this[HoldingEventResultTable.playerId] = eventResult.playerId
            this[HoldingEventResultTable.point] = eventResult.point
        }
    }
}

fun ResultRow.toHoldingEventResult(): HoldingEventResult =
    HoldingEventResult(
        holdingEventId = this[HoldingEventResultTable.holdingEventId],
        rank = this[HoldingEventResultTable.rank],
        point = this[HoldingEventResultTable.point],
        deckId = this[HoldingEventResultTable.deckId],
        playerId = this[HoldingEventResultTable.playerId],
    )

object HoldingEventResultTable : Table("holding_event_results") {
    // NOTE: Set default value because of batch insert.
    val holdingEventId = long("holdingEventId")
    val rank = integer("rank")
    val point = integer("point")
    val deckId = varchar("deckId", 50)
    val playerId = varchar("playerId", 100)

    override val primaryKey = PrimaryKey(holdingEventId, playerId)
}
