package irc

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IrcMessageTest {

    @Test
    fun ircMessage_serverWelcome_capturedCorrectly() {
        val rawMessage = ":foo.bar.net 001 thumbkin :Welcome to your demise"
        val ircMessage = IrcMessage.from(rawMessage)

        assertThat(ircMessage.kind).isEqualTo(IrcCommand.SERVER)
        assertThat(ircMessage.header).isEqualTo("foo.bar.net 001 thumbkin")

        val captures = ircMessage.captured()
        assertThat(captures?.groupValues).containsAtLeast(
            "foo.bar.net",
            "001",
            "thumbkin",
            "Welcome to your demise"
        )
    }

    @Test
    fun ircMessage_PRIVMSG_capturedCorrectly() {
        val rawMessage =
            ":foo!~bar@localhost PRIVMSG #blobby :hello there my friend: you're the best"
        val ircMessage = IrcMessage.from(rawMessage)
        assertThat(ircMessage.kind).isEquivalentAccordingToCompareTo(IrcCommand.PRIVMSG)
        assertThat(ircMessage.header).startsWith("foo!~bar@localhost")
        assertThat(ircMessage.header).contains("#blobby")
        assertThat(ircMessage.body).contains("hello there my friend: you're the best")
    }

    @Test
    fun ircMessage_JOIN_capturedCorrectly() {
        val rawMessage = "JOIN #blobby botname"
        val ircMessage = IrcMessage.from(rawMessage)
        assertThat(ircMessage.kind).isEquivalentAccordingToCompareTo(IrcCommand.JOIN)
        assertThat(ircMessage.header).contains(rawMessage)
    }

    @Test
    fun ircMessage_PING_capturedCorrectly() {
        val rawMessage = "PING :irc.example.net"
        val ircMessage = IrcMessage.from(rawMessage)
        assertThat(ircMessage.kind).isEquivalentAccordingToCompareTo(IrcCommand.PING)
    }
}
