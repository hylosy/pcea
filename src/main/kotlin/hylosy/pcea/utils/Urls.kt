package hylosy.pcea.utils

object Urls {
    private const val DOMAIN = "https://players.pokemon-card.com"
    fun HoldingEventResultSearchEndpoint(holdingEventId: Int) =
        "$DOMAIN/event_result_detail_search?event_holding_id=${holdingEventId}&offset=0"

    fun DeckEndpoint(deckId: String) =
        "https://www.pokemon-card.com/deck/confirm.html/deckID/${deckId}"

    fun CardSearchEndpoint(page: Int, expansion: Int) =
        // sc_tr_tr=1 goods
        // sc_tr_goods=1 tools
        // sc_tr_sp=1 support
        // sc_tr_st=1 studium
        // sc_energy_basic=1
        // sc_energy_special=1
        // se_ta=pokemon
        "https://www.pokemon-card.com/card-search/resultAPI.php?keyword=&se_ta=&regulation_sidebar_form=XY&pg=${expansion}&illust=&sm_and_keyword=true&page=${page}"

}
