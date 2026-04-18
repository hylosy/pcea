package hylosy.pcea.model.card

import kotlinx.serialization.Serializable

@Serializable
data class CardSearchApiResponse(
    val result: Int,
    val errMsg: String,
    val thisPage: Int,
    val maxPage: Int,
    val hitCnt: Int,
    val cardList: List<CardSearchItem>,
)

@Serializable
data class CardSearchItem(
    val cardID: String,
    val cardThumbFile: String,
    val cardNameAltText: String,
    val cardNameViewText: String,
)
