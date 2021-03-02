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
    fun readRoll_simpleRoll_succeeds() {
        val rolled = diceMod.readRoll("2d12")
        assertThat(rolled).startsWith("2d12")
    }

    @Test
    fun rollRegex_simpleData_matches() {
        val match = diceMod.rollRegex.find("2d12")
        assertThat(match).isNotNull()

        val (nDice, die) = match!!.destructured

        assertThat(nDice.toInt()).isEqualTo(2)
        assertThat(die.toInt()).isEqualTo(12)
    }
}