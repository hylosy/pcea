package hylosy.pcea.script

import hylosy.pcea.model.card.Card
import hylosy.pcea.model.card.Deck
import hylosy.pcea.model.card.DeckCard
import hylosy.pcea.model.card.SimulationCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

private enum class TargetCardType(val label: String) {
    FightEnergy("Fight Energy"),
    Solrock("Solrock"),
    Lunatone("Lunatone"),
    Studium("Studium"),
    FlightGong("Fighting Gong"),
    NestedBall("Nested Ball"),
}


fun check(hands: List<Card>): Boolean {
    val energyCount = hands.count { it.name == TargetCardType.FightEnergy.label }
    val solrockCount = hands.count { it.name == TargetCardType.Solrock.label }
    val lunatoneCount = hands.count { it.name == TargetCardType.Lunatone.label }
    var studiumCount = hands.count { it.name == TargetCardType.Studium.label }
    var fightingGongCount = hands.count { it.name == TargetCardType.FlightGong.label }
    var nestedBallCount = hands.count { it.name == TargetCardType.NestedBall.label }

    var hasSolrock = false
    if (solrockCount > 0) {
        hasSolrock = true
    } else {
        if (studiumCount > 0) {
            studiumCount = 0 // use studium
            hasSolrock = true
        } else {
            if (nestedBallCount > 0) {
                nestedBallCount -= 1 // use nested ball
                hasSolrock = true
            } else if (fightingGongCount > 0) {
                fightingGongCount -= 1
                hasSolrock = true
            }
        }
    }

    var hasLunatone = false
    if (lunatoneCount > 0) {
        hasLunatone = true
    } else {
        if (studiumCount > 0) {
            studiumCount = 0 // use studium
            hasLunatone = true
        } else {
            if (nestedBallCount > 0) {
                nestedBallCount -= 1 // use nested ball
                hasLunatone = true
            } else if (fightingGongCount > 0) {
                fightingGongCount -= 1
                hasLunatone = true
            }
        }
    }

    var hasEnergy = false
    if (energyCount > 0) {
        hasEnergy = true
    } else {
        if (fightingGongCount > 0) {
            fightingGongCount -= 1
            hasEnergy = true;
        }
    }
    return hasEnergy && hasLunatone && hasSolrock
}

private fun simulate(deck: Deck, trials: Int = 200000, draws: Int = 8): Double {
    val success = (0..trials).filter {
        val shuffledDeckCards = deck.shuffle()
        val hands = shuffledDeckCards.take(draws)
        check(hands)
    }.size

    return (success + 0.0) / trials
}

private data class CombinationParams(
    val sorlockCount: Int,
    val lunatoneCount: Int,
    val energyCount: Int,
    val nestedBallCount: Int,
    val flightGongCount: Int,
    val studiumCount: Int
) {
    companion object {
        fun fromList(list: List<Int>) = CombinationParams(
            sorlockCount = list[0],
            lunatoneCount = list[1],
            energyCount = list[2],
            nestedBallCount = list[3],
            flightGongCount = list[4],
            studiumCount = list[5]
        )
    }
}

private fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> {
    return lists.fold(listOf(emptyList())) { acc, list ->
        acc.flatMap { accItem ->
            list.map { listItem -> accItem + listItem }
        }
    }
}

fun generateCombinations(): List<List<DeckCard>> {
    val sorlockCounts = 1..4
    val lunatoneCounts = 1..4
    val studiumCounts = 0..4
    val nestedBallCounts = 0..4
    val flightGongCounts = 0..4
    val energyCounts = 8..12

    return cartesianProduct(
        listOf(
            sorlockCounts.toList(),
            lunatoneCounts.toList(),
            energyCounts.toList(),
            nestedBallCounts.toList(),
            flightGongCounts.toList(),
            studiumCounts.toList(),
        )
    ).map(CombinationParams::fromList)
        .map { params ->
            listOf(
                DeckCard(SimulationCard(TargetCardType.FightEnergy.label, ""), params.energyCount),
                DeckCard(SimulationCard(TargetCardType.Solrock.label, ""), params.sorlockCount),
                DeckCard(SimulationCard(TargetCardType.Lunatone.label, ""), params.lunatoneCount),
                DeckCard(SimulationCard(TargetCardType.NestedBall.label, ""), params.nestedBallCount),
                DeckCard(SimulationCard(TargetCardType.FlightGong.label, ""), params.flightGongCount),
                DeckCard(SimulationCard(TargetCardType.Studium.label, ""), params.studiumCount),
            )
        }
}

fun main() = runBlocking {
    val combinations = generateCombinations()
    val total = combinations.size
    val completed = AtomicInteger(0)

    combinations.map { deckCards ->
        val deck = Deck.generate(deckCards)
        async(Dispatchers.Default) {
            val ratio = simulate(deck)
            val current = completed.incrementAndGet()
            if (current % 10 == 0) {
                println("Progress: $current/$total (${current * 100 / total}%)")
            }
            deckCards.joinToString("\t") { it -> it.card.name + "\t" + it.count } + "\t" + ratio
        }
    }.awaitAll().forEach { result ->
        println(result)
    }
}