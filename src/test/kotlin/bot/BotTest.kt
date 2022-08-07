package bot

import com.google.common.truth.Truth.assertThat
import irc.FakeConn
import irc.IrcClient
import irc.Irceiling
import irc.IrcCommand
import irc.IrcMessage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test


class BotTest {

    private val fakeConn = FakeConn()
    private val bot = Bot.connect(Irceiling(fakeConn))

    @Test
    fun bot_connectsAndSends_ok() {
        val chatFn: ListenerFn = { c: IrcClient, m: IrcMessage ->
            if (m.kind == IrcCommand.PRIVMSG) {
                c.privmsg("#bouquet", "hello world")
            }

            if (m.kind == IrcCommand.JOIN) {
                c.privmsg("#uhoh", "this shouldn't happen")
            }
        }

        bot.addListeners(IrcCommand.PRIVMSG, chatFn)
        fakeConn.addToSendList(":foo.bar.baz 001 foo :welcome to the server")
        fakeConn.addToSendList(":foo!~foo@localhost PRIVMSG #bucket :hello bucket")

        runBlocking {
            withTimeout(1000L) {
                bot.loopOnce()
                println("loop once one")
                bot.loopOnce()
                println("loop once two")
            }
        }
        println("now what?")

        val sentByBot = fakeConn.recv.acquire
        assertThat(sentByBot).isNotEmpty()
        assertThat(sentByBot).hasSize(3)
        assertThat(sentByBot).contains("PRIVMSG #bouquet :hello world")
        assertThat(sentByBot).doesNotContain("this shouldn't happen")
    }
}
