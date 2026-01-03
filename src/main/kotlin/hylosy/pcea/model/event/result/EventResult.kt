package hylosy.pcea.model.event.result

import hylosy.pcea.model.event.LeagueName
import kotlinx.serialization.Serializable

@Serializable
data class SearchPaginatedEventResultResponse(
    val page: Int,
    val limit: Int,
    val total: Long,
    val results: List<EventResultSummary>
)

@Serializable
data class EventResultSummary(
    val holdingEventId: Long,
    val shopName: String,
    val prefectureName: String,
    val eventDate: String,
    val leagueName: LeagueName,
    val results: List<PrizedDeck>
)

@Serializable
data class PrizedDeck (
    val holdingEventId: Long,
    val rank: Int,
    val deckId: String,
    val imageUrl: String,
)

@Serializable
data class SearchEventResultOfficialSiteResponse(
    val code: Int,
    val event: EventResultResponse,
    val count: Int,
    val results: List<EventResultDetailResponse>
)
@Serializable
data class HoldingEventDate (
    val date: String,
    val timezone_type: Int,
    val timezone: String
)

@Serializable
data class EventResultResponse (
    val event_title: String,
    val eventTypeId: Int,
    val event_type_title: String,
    val event_kbn: String?,
    val csp_flg: Int?,
    val image: String,
    val eventDate: HoldingEventDate,
    val eventStartedAt: HoldingEventDate,
    val eventEndedAt: HoldingEventDate?,
    val eventDaySupply: String?,
    val prefecture_name: String?,
    val shopId: Int?,
    val shopName: String?,
    val zipCode: Int?,
    val address: String,
    val venue: String,
    val detail: String?,
    val league: String,
    val regulation: String,
    val entry_condition: String?,
    val capacity: Int,
    val remarks: String?,
    val access: String?,
    val eventAttrId: Int,
)


@Serializable
data class EventResultDetailResponse (
    val show_profile: Int,
    val player_id: String,
    val name: String,
    val rank: Int,
    val point: Int,
    val area: String?,
    val deck_id: String?,
)

@Serializable
data class HoldingEventResult (
    val holdingEventId: Long,
    val rank: Int,
    val point: Int,
    val deckId: String,
    val playerId: String
) {
    companion object {
        fun from(holdingEventId: Long, holdingEventResponse: EventResultDetailResponse): HoldingEventResult {
            return HoldingEventResult(
                holdingEventId = holdingEventId,
                rank = holdingEventResponse.rank,
                point = holdingEventResponse.point,
                deckId = holdingEventResponse.deck_id ?: "",
                playerId = holdingEventResponse.player_id,
            )
        }
    }
}
