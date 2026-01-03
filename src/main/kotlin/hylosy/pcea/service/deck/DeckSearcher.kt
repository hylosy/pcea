package hylosy.pcea.service.deck

import hylosy.pcea.service.PCEAClient
import hylosy.pcea.utils.Urls
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class DeckSearcher {
    private val pceaClient: PCEAClient
    constructor() {
        pceaClient = PCEAClient()
    }

    suspend fun fetchDeckInfo(deckId: String): Document =
        try {
            val response: HttpResponse = pceaClient.get(Urls.deckEndpoint(deckId))
            if (response.status.isSuccess()) {
                Jsoup.parse(response.bodyAsText())
            } else {
                throw Exception("Failed to fetch event $deckId")
            }
        } catch (e: Exception) {
            throw e
        }
}
