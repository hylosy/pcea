package hylosy.pcea.service.event

import hylosy.pcea.dao.EventDao
import hylosy.pcea.model.event.Event
import org.jetbrains.exposed.sql.transactions.transaction

class EventService(
    val eventDao: EventDao,
) {
    fun createEvents(events: List<Event>) {
        try {
            transaction {
                eventDao.createMultiple(events)
            }
        } catch (e: Exception) {
            println("Failed to create events: ${e.message}")
            throw e
        }
    }
}
