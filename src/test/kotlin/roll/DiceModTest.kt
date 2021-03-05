package roll

import bot.DiceMod
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DiceModTest {

    private val diceMod = DiceMod()

    @Test
    fun roll_1d6_ok() {
        val roll = diceMod.roll(1, 6).first()
        assertThat(roll).isAtLeast(1)
        assertThat(roll).isAtMost(6)
    }

    @Test
    fun readRoll_simpleRolls_succeed() {
        listOf("2d12", "1d100", "10d6").forEach {
            assertThat(diceMod.readRoll(it)).startsWith(it)
        }
    }

    @Test
    fun rollRegex_simpleData_matches() {
        val match = diceMod.rollRegex.find("2d12")
        assertThat(match).isNotNull()

        val (nDice, die) = match!!.destructured

        assertThat(nDice.toInt()).isEqualTo(2)
        assertThat(die.toInt()).isEqualTo(12)
    }

    @Test
    fun checkRollCommand_simpleCommand_parsedOk() {
        assertThat(diceMod.parseRoll("XdY").isEmpty).isTrue()
        assertThat(diceMod.parseRoll("Xd2").isEmpty).isTrue()
        assertThat(diceMod.parseRoll("1d2").isEmpty).isFalse()
    }

    @Test
    fun checkDice_diceOk_valid() {
        assertThat(diceMod.checkDice(Dice(qty = 1, size = 2))).isTrue()
        assertThat(diceMod.checkDice(Dice(qty = 1, size = 6))).isTrue()
        assertThat(diceMod.checkDice(Dice(qty = 2, size = 12))).isTrue()
        assertThat(diceMod.checkDice(Dice(qty = 100, size = 200))).isTrue()
    }

    @Test
    fun checkDice_diceNotOk_invalid() {
        assertThat(diceMod.checkDice(Dice(qty = 101, size = 6))).isFalse()
        assertThat(diceMod.checkDice(Dice(qty = 100, size = 201))).isFalse()
        assertThat(diceMod.checkDice(Dice(qty = 101, size = 201))).isFalse()
    }

    @Test
    fun tryHardMatch_bonus_correctRoll() {
        val roll = diceMod.tryHardMatch("1d2+1")
        assertThat(roll.isSimple).isFalse()
        assertThat(roll.bonus).isEqualTo(1);
        assertThat(roll.malus).isEqualTo(0);
    }

    @Test
    fun tryHardMatch_malus_correctRoll() {
        val roll = diceMod.tryHardMatch("1d2-1")
        assertThat(roll.isSimple).isFalse()
        assertThat(roll.malus).isEqualTo(1);
        assertThat(roll.bonus).isEqualTo(0);
    }
}