package roll

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test
import roll.Roll.Companion.checkDice
import roll.RollParser.Companion.parse

class RollTest {

    @Test
    fun roll_toss_diceOk() {
        val roll = Roll.toss(1, 6).first()
        assertThat(roll).isAtLeast(1)
        assertThat(roll).isAtMost(6)
    }

    @Test
    fun rollDice_rollsOk() {
        val (theRoll, results) = rollDice { 2 d 4 }
        assertThat(theRoll).isEqualTo(Roll.from(2, 4))
        assertThat(results).hasSize(2)
    }

    @Test
    fun rollCheck_rollsOk() {
        val (theRoll, results) = rollCheck { 2 d 4 plus 2 }
        val (theOtherRoll, otherResults) = rollCheck { 2 d 4 minus 2 }

        assertThat(theRoll).isEqualTo(Roll(Dice(2, 4), bonus = 2))
        assertThat(results).hasSize(2)

        assertThat(theOtherRoll).isEqualTo(Roll(Dice(2, 4), malus = 2))
        assertThat(otherResults).hasSize(2)
    }

    @Test
    fun infix_dNotation_withBonusMalus_correctRolls() {
        val diceOne = 4 d 6
        assertThat(diceOne).isEqualTo(Dice(4, 6))

        val diceTwo = 2 d 12 plus 2
        assertThat(diceTwo).isEqualTo(Roll(Dice(2, 12), bonus = 2))

        val diceThree = 1 d 12 minus 2
        assertThat(diceThree).isEqualTo(Roll(Dice(1, 12), malus = 2))
    }

    @Test
    fun rollRegex_simpleData_matches() {
        val match = RollParser.rollRegex.find("2d12")
        assertThat(match).isNotNull()

        val (nDice, die) = match!!.destructured

        assertThat(nDice.toInt()).isEqualTo(2)
        assertThat(die.toInt()).isEqualTo(12)
    }

    @Test
    fun checkRollCommand_badCommand_isEmptyRoll() {
        assertThat(parse("XdY").isEmpty).isTrue()
        assertThat(parse("Xd2").isEmpty).isTrue()
        assertThat(parse("Xdd2").isEmpty).isTrue()
        assertThat(parse("X0d2").isEmpty).isTrue()
        assertThat(parse("0xd2").isEmpty).isTrue()
        assertThat(parse("888888d").isEmpty).isTrue()
        assertThat(parse("10d10+2-1")).isEqualTo(Roll(Dice(10, 10), 2, 1))
    }

    @Test
    fun checkDice_diceOk_valid() {
        assertThat(checkDice(Dice(qty = 1, size = 2))).isTrue()
        assertThat(checkDice(Dice(qty = 1, size = 6))).isTrue()
        assertThat(checkDice(Dice(qty = 2, size = 12))).isTrue()
        assertThat(checkDice(Dice(qty = 100, size = 200))).isTrue()
    }

    @Test
    fun checkDice_diceNotOk_invalid() {
        assertThat(checkDice(Dice(qty = 101, size = 6))).isFalse()
        assertThat(checkDice(Dice(qty = 100, size = 201))).isFalse()
        assertThat(checkDice(Dice(qty = 101, size = 201))).isFalse()
    }

    @Test
    fun parse_justSomeRolls_resultsOk() {
        val rolls = mapOf(
            "4d6+4" to Roll(Dice(4, 6), bonus = 4),
            "2d2-1" to Roll(Dice(2, 2), malus = 1),
            "2d2" to Roll.from(2, 2),
            "1d12" to Roll.from(1, 12),
            "1d20+5" to Roll(Dice(1, 20), bonus = 5),
            "1d20-2" to Roll(Dice(1, 20), malus = 2),
        )

        val allRolls: Map<Roll, Roll> = rolls.map { parse(it.key) to it.value }.toMap()

        assertWithMessage("not all rolls were valid $rolls vs. $allRolls")
            .that(allRolls.all { it.key == it.value }).isTrue()
    }

    @Test
    fun parse_bonus_correctRoll() {
        val roll = parse("1d2+1")
        assertThat(roll.isEmpty).isFalse()
        assertThat(roll.isSimple).isFalse()
        assertThat(roll.valid).isTrue()
        assertThat(roll.bonus).isEqualTo(1)
        assertThat(roll.malus).isEqualTo(0)
    }

    @Test
    fun parse_malus_correctRoll() {
        val roll = parse("1d2-1")
        assertThat(roll.isEmpty).isFalse()
        assertThat(roll.isSimple).isFalse()
        assertThat(roll.valid).isTrue()
        assertThat(roll).isEqualTo(Roll(Dice(1, 2), malus = 1))
        assertThat(roll.bonus).isEqualTo(0)
    }

    @Test
    fun tossAndSum_Result_isCorrect() {
        val theRoll = Roll(Dice(2, 12), bonus = 3, malus = 1)

        val result = Roll.tossAndSum(theRoll)

        assertThat(result.results).hasSize(2)
        assertThat(result.sum).isIn(4..26)
    }

    @Test
    fun rollIt_usingDice_correctRoll() {
        val expectedDice = Dice(1, 8)

        val (theRoll, _) = rollIt { 1 d 8 }

        assertThat(theRoll.dice).isEqualTo(expectedDice)
    }


    @Test
    fun rollIt_usingRoll_correctRoll() {
        val expectedDice = Dice(1, 10)
        val expectedRoll = Roll(expectedDice, malus = 2)

        val (theRoll, _) = rollIt { 1 d 10 minus 2 }

        assertThat(theRoll).isEqualTo(expectedRoll)
    }
}

