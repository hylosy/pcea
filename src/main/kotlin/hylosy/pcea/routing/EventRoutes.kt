package hylosy.pcea.routing

import hylosy.pcea.controllers.EventResultController
import hylosy.pcea.service.event.result.EventResultService
import io.ktor.server.application.*
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

suspend fun Application.configureEventRoutes() {
    val eventResultService: EventResultService = dependencies.resolve()
    val controller = EventResultController(eventResultService)

    routing {
        route("/api/events/results") {
            get {
                val fromParam = call.request.queryParameters["from"]
                val toParam = call.request.queryParameters["to"]
                val pageParam = call.request.queryParameters["page"]
                val limitParam = call.request.queryParameters["limit"]

                val from = fromParam?.let { LocalDate.parse(it) } ?: LocalDate.of(2018, 12, 25)
                val to = toParam?.let { LocalDate.parse(it) } ?: LocalDate.of(2025, 12, 25)
                val page = pageParam?.toInt() ?: 1
                val limit = limitParam?.toInt() ?: 100

                val result = controller.searchEventResults(from, to, page, limit)
                call.respond(result)
            }
        }
    }
}