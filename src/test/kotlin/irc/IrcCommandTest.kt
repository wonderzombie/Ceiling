package irc

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class IrcCommandTest {
    @Test
    fun ircCommand_messages_yieldCorrectTypes() {
        assertThat(IrcCommand.from(":asdf!~ 123 asdf :welcome foo")).isEquivalentAccordingToCompareTo(
            IrcCommand.SERVER
        )
        assertThat(IrcCommand.from("JOIN #foo bar")).isEqualTo(IrcCommand.JOIN)
        assertThat(IrcCommand.from("PING :foo")).isEqualTo(IrcCommand.PING)
        assertThat(IrcCommand.from(":foo@bar PRIVMSG #foo :foo bar bar")).isEqualTo(
            IrcCommand.PRIVMSG
        )
    }

    @Test
    fun ircCommand_messageParts_capturedCorrectly() {
        val rawMessage = ":foo.bar.baz 001 thumbkin :hello"
        val actualCommand = IrcCommand.from(":foo.bar.baz 001 thumbkin :hello")
        assertThat(actualCommand).isEquivalentAccordingToCompareTo(IrcCommand.SERVER)

        val captures = actualCommand.captures(rawMessage)
            .also { println(it?.groupValues?.drop(1)?.joinToString(" | ")) }
        assertThat(captures?.groupValues?.drop(1)).hasSize(4)
    }

    @Test
    fun ircCommand_PRIVMSG_matches() {
        assertThat(IrcCommand.PRIVMSG.matches(":foo!~bar@localhost PRIVMSG #foo :hello world")).isTrue()
    }

    @Test
    fun ircCommand_PING_matches() {
        assertThat(IrcCommand.PING.matches("PING :foo.bar.com")).isTrue()
    }

    @Test
    fun ircCommand_JOIN_matches() {
        assertThat(IrcCommand.JOIN.matches("JOIN #bloo blah")).isTrue()
    }

    @Test
    fun ircCommand_begins_matches() {
        assertThat(IrcCommand.PING.begins("PING :foo.bar.com")).isTrue()
        assertThat(IrcCommand.PING.begins(":foo!boo@bah PRIVMSG #foo :hey there PING :foo.bar.com")).isFalse()
    }
}