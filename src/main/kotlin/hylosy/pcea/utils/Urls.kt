package hylosy.pcea.utils

object Urls {
    private const val DOMAIN = "https://players.pokemon-card.com"

    fun holdingEventResultSearchEndpoint(holdingEventId: Int) =
        "$DOMAIN/event_result_detail_search?event_holding_id=$holdingEventId&offset=0"

    fun deckEndpoint(deckId: String) = "https://www.pokemon-card.com/deck/confirm.html/deckID/$deckId"

    fun cardSearchEndpoint(
        page: Int,
        expansionNumericId: Int,
    ) = "https://www.pokemon-card.com/card-search/resultAPI.php?keyword=&se_ta=&regulation_sidebar_form=XY&pg=$expansionNumericId&illust=&sm_and_keyword=true&page=$page"

    fun cardDetailEndpoint(
        cardId: Long,
        regulation: String,
    ) = "https://www.pokemon-card.com/card-search/details.php/card/$cardId/regu/$regulation"
}
