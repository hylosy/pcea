package hylosy.pcea.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

data class Expansion(
    val id: String,
    val name: String,
    val regulation: Regulation,
    val releaseDate: LocalDate,
)

@JvmInline
value class Regulation(val value: String) {
    init {
        require(regulations.contains(value)) {
            "Regulation is invalid. value: $value."
        }
    }
}

val regulations = setOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")

val expansionList = listOf(
    Expansion("m3","ムニキスゼロ", Regulation("J"),LocalDate.of(2026, 1, 23)),
    Expansion("mC","スタートデッキ100 バトルコレクション", Regulation("J"),LocalDate.of(2025, 12, 19)),
    Expansion("m2a","MEGAドリームex", Regulation("J"),LocalDate.of(2025, 11, 28)),
    Expansion("m2","インフェルノX", Regulation("J"),LocalDate.of(2025, 9, 26)),

    Expansion("mBG","スターターセットMEGA メガゲンガーex", Regulation("I"),LocalDate.of(2025, 9, 5)),
    Expansion("mBD","スターターセットMEGA メガディアンシーex", Regulation("I"),LocalDate.of(2025, 9, 5)),

    Expansion("m1L","メガブレイブ", Regulation("I"),LocalDate.of(2025, 8, 1)),
    Expansion("m1S","メガシンフォニア", Regulation("I"),LocalDate.of(2025, 8, 1)),

    Expansion("sv11W","ホワイトフレア", Regulation("I"),LocalDate.of(2025, 6, 6)),
    Expansion("sv11B","ブラックボルト", Regulation("I"),LocalDate.of(2025, 6, 6)),

    Expansion("sv10","ロケット団の栄光", Regulation("I"),LocalDate.of(2025, 4, 18)),
    Expansion("sv9a","熱風のアリーナ", Regulation("I"),LocalDate.of(2025, 3, 14)),
    Expansion("svOM","スターターセットex マリィのモルペコ＆オーロンゲex", Regulation("I"),LocalDate.of(2025, 2, 21)),
    Expansion("svOD","スターターセットex ダイゴのダンバル＆メタグロスex", Regulation("I"),LocalDate.of(2025, 2, 21)),
    Expansion("sv9","バトルパートナーズ", Regulation("I"),LocalDate.of(2025, 1, 24)),
)

object ExpansionTable : Table("expansions") {
    val id = varchar("id", 50)
    val name = varchar("name", 100)
    val regulation = varchar("regulation", 50)
    val releaseDate = date("releaseDate")

    override val primaryKey = PrimaryKey(id)
}