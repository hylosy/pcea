package hylosy.pcea.dao

import hylosy.pcea.model.event.Event
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchUpsert

class EventDao {
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
