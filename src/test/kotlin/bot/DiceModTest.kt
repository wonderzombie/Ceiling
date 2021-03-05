package bot

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DiceModTest {

    private val diceMod = DiceMod()

    @Test
    fun readRoll_simpleRolls_succeed() {
        listOf("2d12", "1d100", "10d6").forEach {
            assertThat(diceMod.readRoll(it)).startsWith(it)
        }
    }
}