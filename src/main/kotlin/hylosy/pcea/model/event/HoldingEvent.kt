package hylosy.pcea.model.event

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class OfficialSiteHoldingEventSearchResponse(
    val code: Int,
    val event: List<OfficialSiteHoldingEvent>,
    val eventCount: Int,
)

/**
 * id: indice event category(e.g: 2025 city league season 4)
 * date_id: unknown. this property may return null.(e.g. 2025 CL Miyagi seniour league)
 * shop_id: shop identifier. If Chanpionship league, this propety is null.
 * deck_count: is number of card in deck. this property may return null
 * zip_code: is area code. this property may return null.
 * csp_flg: indice that user can get csp flag. this property may return null.
 * entry_fee: is entry fee (e.g. "500円").  this property may return null.
 * shop_name: is shop name. this property may return null.
 * shop_term: is shop description. this property may return null.
 * regulation: is e.g. スタンダード
 */
@Serializable
data class OfficialSiteHoldingEvent(
    val id: Int,
    val date_id: Int?,
    val shop_id: Int?,
    val event_date_params: String,
    val event_date: String,
    val event_date_week: String,
    val event_started_at: String,
    val event_ended_at: String,
    val prefecture_name: String,
    val deck_count: String?,
    val zip_code: Int?,
    val address: String,
    val venue: String,
    val event_title: String,
    val event_holding_id: Long,
    val event_type: Int,
    val csp_flg: Int?,
    val event_league: Int,
    val regulation: String,
    val entry_fee: String?,
    val capacity: Int,
    val access: String?,
    val shop_name: String?,
    val shop_term: Int?,
    val leagueName: String,
    val event_attr_id: Int,
    val trainers_flg: Int,
    val discontinuance_flg: Int,
    val distance: String?,
    val holiday_flg: Int,
    val fullOccupiedFlg: Int,
    val cancelFlg: Int,
    val entryRestartFlg: Int,
    val recruitFlg: Int,
    val beginnerShopFlg: Int?,
    val strongShopFlg: Int?,
    val championShopFlg: Int?,
    val noOfMyGymReg: Int?,
    val entryStatus: String,
    val entryStatusCode: Int,
)

/**
 * id: is event identifier(eventResponse.event_holding_id)
 * eventId: is event category(eventResponse.id)
 */
data class HoldingEventRecord(
    val id: Long,
    val eventId: Int,
    val eventDate: LocalDate,
    val shopId: Int?,
    val prefectureName: String,
    val leagueName: LeagueName,
) {
    companion object {
        fun from(eventResponse: OfficialSiteHoldingEvent): HoldingEventRecord {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            try {
                return HoldingEventRecord(
                    id = eventResponse.event_holding_id,
                    eventId = eventResponse.id,
                    eventDate = LocalDate.parse(eventResponse.event_date_params, formatter),
                    shopId = eventResponse.shop_id,
                    prefectureName = eventResponse.prefecture_name,
                    leagueName = LeagueName(eventResponse.leagueName),
                )
            } catch (e: Exception) {
                println("Failed to parse event record: $eventResponse")
                throw e
            }
        }
    }
}
