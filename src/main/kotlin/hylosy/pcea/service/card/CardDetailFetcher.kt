package hylosy.pcea.service.card

import hylosy.pcea.model.card.CardType
import hylosy.pcea.model.card.PhysicalCard
import hylosy.pcea.service.PCEAClient
import hylosy.pcea.utils.Urls
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class CardDetailFetcher {
    private val client = PCEAClient()

    suspend fun fetchCard(
        cardId: Long,
        expansionId: String,
        regulation: String = "XY",
    ): Result<PhysicalCard> =
        runCatching {
            val url = Urls.cardDetailEndpoint(cardId, regulation)
            val response = client.get(url)
            if (!response.status.isSuccess()) {
                error("HTTP ${response.status} for cardId=$cardId")
            }
            parseCard(cardId, expansionId, Jsoup.parse(response.bodyAsText()))
        }

    private fun parseCard(
        cardId: Long,
        expansionId: String,
        doc: Document,
    ): PhysicalCard {
        val name = doc.selectFirst("h1.Heading1")?.text() ?: error("name not found for cardId=$cardId")
        val imageUrl = doc.selectFirst("div.LeftBox img.fit")?.attr("src")
        val subtextEl = doc.selectFirst("div.subtext")
        val number = subtextEl?.ownText()?.replace(Regex("\\s+|&nbsp;"), "")?.trim('/') ?: ""
        val rarityImgSrc = subtextEl?.selectFirst("img")?.attr("src") ?: ""
        val rarity = extractRarity(rarityImgSrc)
        val illustrator = doc.selectFirst("div.author a")?.text()
        val cardType = detectCardType(doc)

        return PhysicalCard(
            id = cardId,
            name = name,
            cardType = cardType,
            number = number,
            rarity = rarity,
            illustrator = illustrator,
            expansionId = expansionId,
            imageUrl = imageUrl,
        )
    }

    private fun detectCardType(doc: Document): CardType {
        if (doc.selectFirst("span.hp-num") != null) return CardType.Pokemon
        val h2Text = doc.select("div.RightBox h2").firstOrNull()?.text() ?: return CardType.Goods
        return when {
            h2Text.contains("グッズ") -> CardType.Goods
            h2Text.contains("ポケモンのどうぐ") -> CardType.Tool
            h2Text.contains("サポート") -> CardType.Support
            h2Text.contains("スタジアム") -> CardType.Studium
            h2Text.contains("エネルギー") -> CardType.Energy
            else -> CardType.Goods
        }
    }

    private fun extractRarity(imgSrc: String): String? {
        val filename = imgSrc.substringAfterLast("/").substringBeforeLast(".")
        return when {
            filename.contains("_sar_") -> "SAR"
            filename.contains("_sr_") -> "SR"
            filename.contains("_hr_") -> "HR"
            filename.contains("_rr_") -> "RR"
            filename.contains("_r_") -> "R"
            filename.contains("_u_") -> "U"
            filename.contains("_c_") -> "C"
            else -> null
        }
    }
}
