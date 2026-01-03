package hylosy.pcea.dao

import hylosy.pcea.model.event.Event
import hylosy.pcea.model.event.EventType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.select

class EventDao {
    fun getEventByEventType(eventTypes: List<EventType>): List<Event> =
        EventTable
            .select {
                EventTable.eventType inList eventTypes.map { it.id }
            }.map { it.toEvent() }

    private fun ResultRow.toEvent(): Event =
        Event(
            id = this[EventTable.id],
            name = this[EventTable.name],
            eventType = EventType(this[EventTable.eventType]),
        )

    fun createMultiple(events: List<Event>) {
        EventTable.batchUpsert(events) { event ->
            this[EventTable.id] = event.id
            this[EventTable.name] = event.name
            this[EventTable.eventType] = event.eventType.id
        }
    }
}

// TODO: Exposed replace to DAO style
// The compiler doesn't catch it when a developer forgets to set a new property in the insert method.
object EventTable : Table("events") {
    // NOTE: Set default value because of batch insert.
    val id = integer("id").default(0)
    val name = varchar("name", 100)
    val eventType = integer("eventType")
    override val primaryKey = PrimaryKey(id)
}
