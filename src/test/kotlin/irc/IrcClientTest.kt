package irc

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IrcClientTest {

    @Test
    fun handshake_writesCorrectly() {
        val toSend = listOf(":foo.bar.net 001 thumbkin :hello there: you're great")

        val fakeConn = FakeConn()
        fakeConn.addToSendList(":foo.bar.net 001 thumbkin :hello there: you're great")

        val cli = IrcClient(fakeConn, nick = "grumkin")

        cli.handShake()

        assertThat(fakeConn.received).isNotEmpty()
        assertThat(fakeConn.received).containsExactly(
            "NICK grumkin",
            "USER grumkin bot localhost :grumkin"
        )
    }
}