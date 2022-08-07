package bot

import com.google.common.truth.Truth.assertThat
import irc.FakeConn
import irc.IrcMessage
import irc.Irceiling
import org.junit.Test
import roll.Roll

class CombatModTest {
    private val fakeConn = FakeConn()
    private val fakeCli = Irceiling(fakeConn)
    private val combat = CombatMod(weapon = Roll.from(1, 6), startHp = 10, missChance = 0.0F)

    private val attackMsg = IrcMessage.from(":foo!~foo@localhost PRIVMSG #bucket : ACTION attacks bar")

    @Test
    fun testTarget() {
        val t = combat.target(attackMsg)
        assertThat(t).isEqualTo("bar")
    }

    @Test
    fun testActor() {
        val a = combat.actor(attackMsg)
        assertThat(a).isEqualTo("foo")
    }

    @Test
    fun testCombatListener() {
        combat.combatListener(fakeCli, attackMsg)
        val recv = fakeConn.recv.acquire
        assertThat(recv).isNotEmpty()
        assertThat(recv.last()).contains("damage")
    }
}
