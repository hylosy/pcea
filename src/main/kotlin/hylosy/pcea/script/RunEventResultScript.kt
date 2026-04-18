package hylosy.pcea.script

import hylosy.pcea.config.ConfigLoader
import hylosy.pcea.db.DatabaseManager
import hylosy.pcea.di.ServiceModule
import hylosy.pcea.model.event.result.HoldingEventResult
import hylosy.pcea.service.FetchEventResultException
import hylosy.pcea.service.PokemonCardOfficialSiteClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.LocalDate
import kotlin.onFailure

private val logger = LoggerFactory.getLogger("RunEventResultScript")

fun main(args: Array<String>) {
    val params = args.toList().chunked(2).associate { it[0] to it[1] }
    val from = params["--from"]?.let { LocalDate.parse(it) }
    val to = params["--to"]?.let { LocalDate.parse(it) }

    DatabaseManager.initialize(ConfigLoader.loadDatabaseConfig())
    runBlocking {
        runEventResultScript(from, to)
    }
}

suspend fun runEventResultScript(
    from: LocalDate? = null,
    to: LocalDate? = null,
) {
    val holdingEventService = ServiceModule.holdingEventService
    val officialSiteClient = PokemonCardOfficialSiteClient()
    runCatching {
        val holdingEventIds =
            if (from != null && to != null) {
                holdingEventService
                    .getEvents(from, to)
                    .map { it.id }
            } else {
                val taskConfig = ConfigLoader.loadFetchHoldingEventResultTaskConfig()
                if (taskConfig.startDate != null && taskConfig.endDate != null) {
                    holdingEventService
                        .getEvents(taskConfig.startDate, taskConfig.endDate)
                        .map { it.id }
                } else if (taskConfig.holdingEventIds.isNotEmpty()) {
                    taskConfig.holdingEventIds
                } else {
                    emptyList()
                }
            }
        if (holdingEventIds.isEmpty()) {
            logger.warn("No holding events found for the specified condition")
        }
        logger.info("Target holdingEventIds: $holdingEventIds")
        holdingEventIds.forEach { holdingEventId ->
            val result =
                runCatching {
                    val eventResultResponse = officialSiteClient.fetchEventResult(holdingEventId).getOrThrow()
                    val holdingEventResults =
                        eventResultResponse.results.map { HoldingEventResult.from(holdingEventId, it) }
                    holdingEventService.createHoldingEventResults(holdingEventResults)
                    logger.info("Saved results for holdingEventId=$holdingEventId")
                    delay(1000)
                }
            result.onFailure { exception ->
                if (exception is FetchEventResultException) {
                    // noop
                } else {
                    logger.error("Failed to create events: $exception")
                    return
                }
            }
        }
    }
}
