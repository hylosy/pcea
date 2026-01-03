package hylosy.pcea.script

import hylosy.pcea.model.card.SimulationCard
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CheckTest {
    @Test
    fun `check should return true when all required cards are present`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Solrock", ""),
                SimulationCard("Lunatone", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return false when energy is missing`() {
        val hands =
            listOf(
                SimulationCard("Solrock", ""),
                SimulationCard("Lunatone", ""),
            )

        val result = check(hands)
        assertFalse(result)
    }

    @Test
    fun `check should return false when solrock is missing`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Lunatone", ""),
            )
        val result = check(hands)
        assertFalse(result)
    }

    @Test
    fun `check should return false when lunatone is missing`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Solrock", ""),
            )

        val result = check(hands)
        assertFalse(result)
    }

    // 提案2: 代替カードのテストケース

    @Test
    fun `check should return true when solrock is replaced by Studium`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Studium", ""),
                SimulationCard("Lunatone", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return true when solrock is replaced by Nested Ball`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Nested Ball", ""),
                SimulationCard("Lunatone", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return true when solrock is replaced by Fighting Gong`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Fighting Gong", ""),
                SimulationCard("Lunatone", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return true when lunatone is replaced by Studium`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Solrock", ""),
                SimulationCard("Studium", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return true when lunatone is replaced by Nested Ball`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Solrock", ""),
                SimulationCard("Nested Ball", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return true when lunatone is replaced by Fighting Gong`() {
        val hands =
            listOf(
                SimulationCard("Fight Energy", ""),
                SimulationCard("Solrock", ""),
                SimulationCard("Fighting Gong", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }

    @Test
    fun `check should return true when energy is replaced by Fighting Gong`() {
        val hands =
            listOf(
                SimulationCard("Fighting Gong", ""),
                SimulationCard("Solrock", ""),
                SimulationCard("Lunatone", ""),
            )

        val result = check(hands)
        assertTrue(result)
    }
}
