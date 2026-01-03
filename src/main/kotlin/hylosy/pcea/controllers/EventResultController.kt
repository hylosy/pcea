package hylosy.pcea.controllers

import hylosy.pcea.model.event.EventType
import hylosy.pcea.model.event.EventTypeCategory
import hylosy.pcea.model.event.result.SearchPaginatedEventResultResponse
import hylosy.pcea.service.event.result.EventResultService
import java.time.LocalDate

class EventResultController(
    private val eventResultService: EventResultService,
) {
    fun searchEventResults(
        from: LocalDate,
        to: LocalDate,
        page: Int,
        limit: Int,
    ): SearchPaginatedEventResultResponse =
        eventResultService.searchEventResults(
            from,
            to,
            EventTypeCategory.constructedDeck().map { EventType(it.value) },
            page,
            limit,
        )
}
