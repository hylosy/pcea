package hylosy.pcea.model.card

import java.util.UUID

open class Card(
    val ulid: CardId,
    val name: String,
    val cardType: String,
)

class SimulationCard(
    name: String,
    cardType: String,
) : Card(
        ulid = CardId(UUID.randomUUID().toString()),
        name = name,
        cardType = cardType,
    )

class DummyCard :
    Card(
        ulid = CardId(UUID.randomUUID().toString()),
        name = "--- DUMMY CARD ---",
        cardType = "--- DUMMY TYPE ---",
    )

@JvmInline
value class CardId(
    val ulid: String,
)
