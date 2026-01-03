package hylosy.pcea.service.event

import hylosy.pcea.dao.HoldingEventRecordDao
import hylosy.pcea.dao.HoldingEventRecordTable
import hylosy.pcea.dao.HoldingEventResultDao
import hylosy.pcea.model.event.HoldingEventRecord
import hylosy.pcea.model.event.result.HoldingEventResult
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class HoldingEventService(
    val holdingEventRecordDao: HoldingEventRecordDao,
    val holdingEventResultDao: HoldingEventResultDao,
) {
    fun createEvents(events: List<HoldingEventRecord>) {
        transaction {
            HoldingEventRecordTable.batchUpsert(events) { event ->
                this[HoldingEventRecordTable.id] = event.id
                this[HoldingEventRecordTable.eventId] = event.eventId
                this[HoldingEventRecordTable.eventDate] = event.eventDate
                this[HoldingEventRecordTable.shopId] = event.shopId
                this[HoldingEventRecordTable.prefectureName] = event.prefectureName
                this[HoldingEventRecordTable.leagueName] = event.leagueName.value
            }
        }
    }

    fun getEvents(
        from: LocalDate,
        to: LocalDate,
    ): List<HoldingEventRecord> =
        transaction {
            holdingEventRecordDao.getByDates(from, to)
        }

    fun getDeckCodes(): List<String> =
        transaction {
            holdingEventResultDao.getAllDeckIds()
        }

    fun getDeckCodesByHoldingEventIds(holdingEventIds: List<Long>): List<String> =
        transaction {
            holdingEventResultDao.getDeckIdsByHoldingEventIds(holdingEventIds)
        }

    fun createHoldingEventResults(eventResults: List<HoldingEventResult>) {
        try {
            transaction {
                holdingEventResultDao.createMultiple(eventResults)
            }
        } catch (e: Exception) {
            println(e.message)
            throw e
        }
    }
}
