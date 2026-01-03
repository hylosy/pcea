package hylosy.pcea.model.event

data class Event(
    val id: Int,
    val name: String,
)

@JvmInline
value class EventType(
    val id: Int,
)

enum class EventTypeId(
    val id: Int,
) {
    ChampionShip(1),
    CityLeague(2),
    SealedBattle(7),
}
