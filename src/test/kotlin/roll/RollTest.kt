package roll

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import roll.Roll.Companion.checkDice
import roll.RollParser.Companion.parse
import roll.RollParser.Companion.tryHardMatch

class RollTest {


    @Test
    fun roll_1d6_ok() {
        val roll = Roll.toss(1, 6).first()
        assertThat(roll).isAtLeast(1)
        assertThat(roll).isAtMost(6)
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
    fun checkRollCommand_simpleCommand_parsedOk() {
        assertThat(parse("XdY").isEmpty).isTrue()
        assertThat(parse("Xd2").isEmpty).isTrue()
        assertThat(parse("1d2").isEmpty).isFalse()
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
    fun tryHardMatch_bonus_correctRoll() {
        val roll = tryHardMatch("1d2+1")
        assertThat(roll.isSimple).isFalse()
        assertThat(roll.bonus).isEqualTo(1)
        assertThat(roll.malus).isEqualTo(0)
    }

    @Test
    fun tryHardMatch_malus_correctRoll() {
        val roll = tryHardMatch("1d2-1")
        assertThat(roll.isSimple).isFalse()
        assertThat(roll.malus).isEqualTo(1)
        assertThat(roll.bonus).isEqualTo(0)
    }
}