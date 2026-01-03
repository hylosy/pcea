package hylosy.pcea.task

import hylosy.pcea.config.ConfigLoader
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.model.event.result.HoldingEventResult
import hylosy.pcea.service.FetchEventResultException
import hylosy.pcea.service.PokemonCardOfficialSiteClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
    val taskConfig = ConfigLoader.loadFetchHoldingEventResultTaskConfig()
    runCatching {
        val holdingEventIds =
            if (taskConfig.startDate != null && taskConfig.endDate != null) {
                val from = taskConfig.startDate
                val to = taskConfig.endDate
                holdingEventService
                    .getEvents(from, to)
                    .map { it.id }
            } else if (taskConfig.holdingEventIds.isNotEmpty()) {
                taskConfig.holdingEventIds
            } else {
                emptyList()
            }
        println(holdingEventIds)
        holdingEventIds.forEach { holdingEventId ->
            val result =
                runCatching {
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
