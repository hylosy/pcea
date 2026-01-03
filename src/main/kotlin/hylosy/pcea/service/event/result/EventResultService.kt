package hylosy.pcea.service.event.result

import hylosy.pcea.dao.HoldingEventRecordDao
import hylosy.pcea.dao.HoldingEventRecordTable
import hylosy.pcea.dao.HoldingEventResultDao
import hylosy.pcea.dao.HoldingEventResultTable
import hylosy.pcea.dao.ShopsDao
import hylosy.pcea.model.event.result.EventResultSummary
import hylosy.pcea.model.event.result.HoldingEventResult
import hylosy.pcea.model.event.result.PrizedDeck
import hylosy.pcea.model.event.result.SearchPaginatedEventResultResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class EventResultService(
    val holdingEventRecordDao: HoldingEventRecordDao,
    val holdingEventResultDao: HoldingEventResultDao,
    val shopsDao: ShopsDao,
) {
    private fun connect() {
        Database.connect(
            url =
                "jdbc:mysql://localhost:3306/pcea?useSSL=false&serverTimezone=Asia/Tokyo&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "root",
        )
    }

    fun getEventResults(
        from: LocalDate,
        to: LocalDate,
    ): List<HoldingEventResult> {
        connect()
        return transaction {
            val holdingEventIdsQuery =
                HoldingEventRecordTable
                    .slice(HoldingEventRecordTable.id)
                    .select { HoldingEventRecordTable.eventDate.between(from, to) }

            HoldingEventResultTable
                .select { HoldingEventResultTable.holdingEventId inSubQuery holdingEventIdsQuery }
                .map { it.toHoldingEventResult() }
        }.toList()
    }

    fun searchEventResults(
        from: LocalDate,
        to: LocalDate,
        page: Int,
        limit: Int,
    ): SearchPaginatedEventResultResponse =
        transaction {
            val offset = (page - 1) * limit.toLong()
            val holdingEvents = holdingEventRecordDao.getByDates(from = from, to = to, offset = offset, limit = limit)
            val holdingEventTotal = holdingEventRecordDao.getCountByDates(from = from, to = to)
            val holdingEventResults = holdingEventResultDao.getHoldingEventResults(holdingEvents.map { it.id })
            val shops = shopsDao.getShops(holdingEvents.mapNotNull { it.shopId })

            val holdingEventResultMap = holdingEventResults.groupBy { it -> it.holdingEventId }
            val shopIdToNameMap = shops.associate { it.id to it.name }

            val summaries =
                holdingEvents
                    .sortedBy { it.eventDate }
                    .reversed()
                    .map { holdingEvent ->
                        val shopName = shopIdToNameMap[holdingEvent.shopId] ?: ""
                        val holdingEventResults = holdingEventResultMap[holdingEvent.id] ?: emptyList()
                        EventResultSummary(
                            holdingEvent.id,
                            shopName,
                            prefectureName = holdingEvent.prefectureName,
                            // TODO: Handle timezone
                            eventDate = holdingEvent.eventDate.toString(),
                            leagueName = holdingEvent.leagueName,
                            results =
                                holdingEventResults
                                    .sortedBy { it.rank }
                                    .map {
                                        // TODO: Set image URL path each environment.
                                        val imageUrl = "dev/images/${it.deckId}.png"
                                        PrizedDeck(it.holdingEventId, it.rank, it.deckId, imageUrl)
                                    },
                        )
                    }
            SearchPaginatedEventResultResponse(page = page, limit = limit, holdingEventTotal, summaries)
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
