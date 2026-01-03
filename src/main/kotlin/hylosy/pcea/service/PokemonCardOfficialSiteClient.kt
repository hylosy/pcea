package hylosy.pcea.service

import hylosy.pcea.model.event.OfficialSiteHoldingEventSearchResponse
import hylosy.pcea.model.event.result.SearchEventResultOfficialSiteResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.isSuccess

class PokemonCardOfficialSiteClient {
    private val officialSiteDomain = "https://players.pokemon-card.com"
    private val pceaClient: PCEAClient
    constructor() {
        pceaClient = PCEAClient()
    }
    /**
     * Fetch card event from official site.
     */
    suspend fun fetchEvent(offset: Int): Result<OfficialSiteHoldingEventSearchResponse> {
        val endpoint =
            "$officialSiteDomain/event_search?offset=${offset}&order=4&result_resist=1&event_type[]=3:1&event_type[]=3:2&event_type[]=3:7"

        return try {
            val response: HttpResponse = pceaClient.get(endpoint)
            if (response.status.isSuccess()) {
                val data: OfficialSiteHoldingEventSearchResponse = response.body()
                Result.success(data)
            } else {
                Result.failure(Exception("HTTP status is not 200: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchEventResult(holdingEventId: Long): Result<SearchEventResultOfficialSiteResponse> {
        val endpoint = "$officialSiteDomain/event_result_detail_search?event_holding_id=${holdingEventId}&offset=0"
        return try {
            val response: HttpResponse = pceaClient.get(endpoint)
            if (response.status.isSuccess()) {
                println(response.bodyAsText())
                val data: SearchEventResultOfficialSiteResponse = response.body()
                Result.success(data)
            } else {
                Result.failure(FetchEventResultException("HTTP status is not 200: ${response.status} holdingEventId: $holdingEventId"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchImage(deckCode: String): Result<ByteArray> {
        val endpoint = "https://www.pokemon-card.com/deck/deckView.php/deckID/${deckCode}.png"
        return try {
            val response: HttpResponse = pceaClient.get(endpoint)
            if (response.status.isSuccess()) {
                val imageBytes = response.readRawBytes()
                Result.success(imageBytes)
            } else {
                Result.failure(FetchEventResultException("HTTP status is not 200: ${response.status} deckCode: $deckCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class FetchEventResultException(message: String): Exception(message)
