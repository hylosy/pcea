package hylosy.pcea.script

import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.model.Shop
import hylosy.pcea.model.event.Event
import hylosy.pcea.model.event.EventType
import hylosy.pcea.model.event.HoldingEventRecord
import hylosy.pcea.service.PokemonCardOfficialSiteClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("RunEventScript")

fun main(args: Array<String>) {
    DatabaseManager.initialize(
        hylosy.pcea.config.ConfigLoader
            .loadDatabaseConfig(),
    )
    runBlocking {
        runEventTask()
    }
}

suspend fun runEventTask() {
    val officialSiteClient = PokemonCardOfficialSiteClient()
    val holdingEventService = ServiceModule.holdingEventService
    val shopService = ServiceModule.shopService
    val eventService = ServiceModule.eventService

    var offset = 0
    var isSuccess = true
    while (isSuccess) {
        val page = offset / 20 + 1
        val result =
            runCatching {
                val eventResponse = officialSiteClient.fetchEvent(offset).getOrThrow()
                val holdingEvents = eventResponse.event.map { HoldingEventRecord.from(it) }
                val events = eventResponse.event.map { Event(it.id, it.event_title, EventType(it.event_type)) }.distinctBy { it.id }
                eventService.createEvents(events)
                holdingEventService.createEvents(holdingEvents)
                val shops =
                    eventResponse.event
                        .filter { it.shop_id != null && it.shop_name != null }
                        .map { Shop(it.shop_id!!, it.shop_name!!) }
                        .distinctBy { it.id }
                shopService.createShops(shops)
                logger.info("page=$page saved ${events.size} events, ${shops.size} shops")
            }
        result.onFailure {
            logger.error("page=$page failed: $it")
            isSuccess = false
        }
        delay(1000)
        offset += 20
    }
}
