package bot

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
}