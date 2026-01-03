package hylosy.pcea.model.event

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class LeagueName(val value: String) {
    init {
        require(LeagueNameList.contains(value)) {
            "League Name not validated: $value"
        }
    }
}

private val LeagueNameList = listOf(
    "シニア",
    "ジュニア",
    "マスター",
    "オープン"
)
