package hylosy.pcea.task

import hylosy.pcea.config.ConfigLoader
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.model.event.result.HoldingEventResult
import hylosy.pcea.service.FetchEventResultException
import hylosy.pcea.service.PokemonCardOfficialSiteClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.onFailure


fun main() {
    DatabaseManager.initialize(ConfigLoader.loadDatabaseConfig())
    runBlocking {
        runEventResultTask()
    }
}

suspend fun runEventResultTask() {
    val holdingEventService = ServiceModule.holdingEventService
    val officialSiteClient = PokemonCardOfficialSiteClient()
    runCatching {
        val from = LocalDate.of(2025, 1, 1)
        val to = LocalDate.of(2025, 4, 30)
        val events = holdingEventService.getEvents(from, to)

        events.map { it.id }.forEach { holdingEventId ->
            val result = runCatching {
                val eventResultResponse = officialSiteClient.fetchEventResult(holdingEventId).getOrThrow()
                val holdingEventResults =
                    eventResultResponse.results.map { HoldingEventResult.from(holdingEventId, it) }
            holdingEventService.createHoldingEventResults(holdingEventResults)
                delay(1000)
            }
            result.onFailure { exception ->
                if (exception is FetchEventResultException) {
                    // noop
                } else {
                    System.out.printf("Error: $exception")
                    return
                }
            }
        }
    }
}