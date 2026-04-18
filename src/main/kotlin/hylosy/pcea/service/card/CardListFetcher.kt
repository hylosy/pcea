package hylosy.pcea.service.card

import hylosy.pcea.model.card.CardSearchApiResponse
import hylosy.pcea.model.card.CardSearchItem
import hylosy.pcea.service.PCEAClient
import hylosy.pcea.utils.Urls
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import org.slf4j.LoggerFactory

class CardListFetcher {
    private val client = PCEAClient()
    private val logger = LoggerFactory.getLogger(CardListFetcher::class.java)

    suspend fun fetchAllPages(expansionNumericId: Int): List<CardSearchItem> {
        val results = mutableListOf<CardSearchItem>()
        var page = 1
        while (true) {
            val url = Urls.cardSearchEndpoint(page, expansionNumericId)
            val response = client.get(url)
            if (!response.status.isSuccess()) {
                logger.warn("Non-success status ${response.status} on page=$page, stopping")
                break
            }
            val body: CardSearchApiResponse = response.body()
            if (body.cardList.isEmpty()) break
            results += body.cardList
            logger.info("Fetched page=$page count=${body.cardList.size} total=${results.size}/${body.hitCnt}")
            if (results.size >= body.hitCnt) break
            page++
        }
        return results
    }
}
