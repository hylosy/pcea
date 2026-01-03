package hylosy.pcea.script

import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.model.Shop
import hylosy.pcea.model.event.Event
import hylosy.pcea.model.event.HoldingEventRecord
import hylosy.pcea.service.PokemonCardOfficialSiteClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    System.out.printf("Start event task\n")
    DatabaseManager.initialize(
        hylosy.pcea.config.ConfigLoader.loadDatabaseConfig()
    )
    runBlocking {
        runEventTask()
    }
}

suspend fun runEventTask() {
    val officialSiteClient = PokemonCardOfficialSiteClient()
    val holdingEventService = ServiceModule.holdingEventService
    val shopService =  ServiceModule.shopService
    val eventService = ServiceModule.eventService

    var offset = 0
    var isSuccess = true
    while (isSuccess) {
        val result = runCatching {
            val eventResponse = officialSiteClient.fetchEvent(offset).getOrThrow()
            val holdingEvents = eventResponse.event.map { HoldingEventRecord.from(it) }
            val events = eventResponse.event.map { Event(it.id, it.event_title) }.distinctBy { it.id }
            eventService.createEvents(events)
            holdingEventService.createEvents(holdingEvents)
            val shops = eventResponse.event
                .filter { it.shop_id != null && it.shop_name != null}
                .map { Shop(it.shop_id!!, it.shop_name!!) }
                .distinctBy { it.id }
            shopService.createShops(shops)
        }
        result.onFailure {
            System.out.printf("Error: $it")
            isSuccess = false
        }
        delay(1000)
        offset += 20
    }
}
