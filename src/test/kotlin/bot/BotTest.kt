package bot

import com.google.common.truth.Truth.assertThat
import irc.FakeConn
import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage
import org.junit.Test


class BotTest {

    private val fakeConn = FakeConn()
    private val bot = Bot.connect(IrcClient(fakeConn))

    @Test
    fun bot_connectsAndSends_ok() {
        val chatFn: ListenerFn = { c: IrcClient, m: IrcMessage ->
            if (m.type == IrcCommand.PRIVMSG) {
                c.privmsg("#bouquet", "hello world")
            }

            if (m.type == IrcCommand.JOIN) {
                c.privmsg("#uhoh", "this shouldn't happen")
            }
        }

        bot.addListeners(IrcCommand.PRIVMSG, chatFn)
        fakeConn.addToSendList(":foo.bar.baz 001 foo :welcome to the server")
        fakeConn.addToSendList(":foo!~foo@localhost PRIVMSG #bucket :hello bucket")

        bot.loopOnce()
        bot.loopOnce()

        assertThat(fakeConn.received).isNotEmpty()
        assertThat(fakeConn.received).hasSize(3)

        val sentByBot = fakeConn.received
        assertThat(sentByBot).contains("PRIVMSG #bouquet :hello world")
        assertThat(sentByBot).doesNotContain("this shouldn't happen")
    }
}