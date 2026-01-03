package hylosy.pcea.dao

import hylosy.pcea.model.event.HoldingEventRecord
import hylosy.pcea.model.event.LeagueName
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.select
import java.time.LocalDate

class HoldingEventRecordDao {
    fun getByIdsAndDates(
        ids: List<Int>,
        from: LocalDate,
        to: LocalDate?,
        offset: Long,
        limit: Int,
    ): List<HoldingEventRecord> =
        getByIdsAndDatesQuery(ids, from, to)
            .orderBy(HoldingEventRecordTable.eventDate to SortOrder.DESC)
            .limit(limit, offset)
            .map { it.toEventRecord() }

    fun getByDates(
        from: LocalDate,
        to: LocalDate,
    ): List<HoldingEventRecord> =
        getByDatesQuery(from, to)
            .sortedBy { HoldingEventRecordTable.eventDate }
            .reversed()
            .map { it.toEventRecord() }

    fun getCountByIdsAndDates(
        ids: List<Int>,
        from: LocalDate,
        to: LocalDate?,
    ): Long =
        getByIdsAndDatesQuery(ids, from, to)
            .count()

    private fun getByDatesQuery(
        from: LocalDate,
        to: LocalDate?,
    ): Query =
        HoldingEventRecordTable
            .select { HoldingEventRecordTable.eventDate.between(from, to) }

    private fun getByIdsAndDatesQuery(
        ids: List<Int>,
        from: LocalDate,
        to: LocalDate?,
    ): Query =
        HoldingEventRecordTable
            .select { HoldingEventRecordTable.eventId inList ids and HoldingEventRecordTable.eventDate.between(from, to) }

    private fun ResultRow.toEventRecord(): HoldingEventRecord =
        HoldingEventRecord(
            id = this[HoldingEventRecordTable.id],
            eventId = this[HoldingEventRecordTable.eventId],
            eventDate = this[HoldingEventRecordTable.eventDate],
            shopId = this[HoldingEventRecordTable.shopId],
            prefectureName = this[HoldingEventRecordTable.prefectureName],
            leagueName = LeagueName(this[HoldingEventRecordTable.leagueName]),
        )
}

object HoldingEventRecordTable : Table("holding_events") {
    // NOTE: Set default value because of batch insert.
    val id = long("id").default(0)
    val eventId = integer("eventId")
    val eventDate = date("eventDate")
    val shopId = integer("shopId").nullable()
    val prefectureName = varchar("prefectureName", 50)
    val leagueName = varchar("leagueName", 100)

    override val primaryKey = PrimaryKey(id)
}
