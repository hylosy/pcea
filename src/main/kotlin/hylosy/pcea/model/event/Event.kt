package hylosy.pcea.model.event

data class Event(
    val id: Int,
    val name: String,
    val eventType: EventType,
)

@JvmInline
value class EventType(
    val id: Int,
)

enum class EventTypeCategory(
    val value: Int,
) {
    ChampionShip(1),
    CityLeague(2),
    SealedBattle(7), // Part of limited deck battle
    ;

    companion object {
        fun constructedDeck(): List<EventTypeCategory> = listOf(ChampionShip, CityLeague)
    }
}
