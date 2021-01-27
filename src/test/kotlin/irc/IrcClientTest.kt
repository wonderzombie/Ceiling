package irc

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IrcClientTest {

    @Test
    fun send_writesCorrectly() {
        val fakeConn = FakeConn()
        fakeConn.addToSendList(":foo.bar.net 001 thumbkin :hello there: you're great")

        val cli = IrcClient(fakeConn, nick = "grumkin")

        cli.join("#bucket")
        cli.chat("what is up my bucket")

        assertThat(fakeConn.received).hasSize(2)
        assertThat(fakeConn.received).containsExactly(
            "JOIN #bucket grumkin", "PRIVMSG #bucket :what is up my bucket"
        )

    }

    @Test
    fun handshake_writesCorrectly() {
        val fakeConn = FakeConn()
        val cli = IrcClient(fakeConn, nick = "grumkin")

        cli.handShake()

        assertThat(fakeConn.received).isNotEmpty()
        assertThat(fakeConn.received).containsExactly(
            "NICK grumkin",
            "USER grumkin bot localhost :grumkin"
        )
    }
}