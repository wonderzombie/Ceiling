package irc

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IrceilingTest {

    @Test
    fun send_writesCorrectly() {
        val fakeConn = FakeConn()
        fakeConn.addToSendList(":foo.bar.net 001 thumbkin :hello there: you're great")

        val cli = Irceiling(fakeConn, nick = "grumkin")

        cli.join("#bucket")
        cli.privmsg("what is up my bucket")

        assertThat(fakeConn.recv.acquire).hasSize(2)
        assertThat(fakeConn.recv.acquire).containsExactly(
            "JOIN #bucket grumkin", "PRIVMSG #bucket :what is up my bucket"
        )

    }

    @Test
    fun handshake_writesCorrectly() {
        val fakeConn = FakeConn()
        val cli = Irceiling(fakeConn, nick = "grumkin")

        cli.handShake()

        assertThat(fakeConn.recv.acquire).isNotEmpty()
        assertThat(fakeConn.recv.acquire).containsExactly(
            "NICK grumkin",
            "USER grumkin bot localhost :grumkin"
        )
    }
}
