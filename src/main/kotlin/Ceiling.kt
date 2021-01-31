import bot.Bot
import irc.IrcClient
import irc.IrcCommand
import irc.PlainTextConn
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val bot = Bot.connect(IrcClient(PlainTextConn()))
    bot.addListeners(IrcCommand.PRIVMSG, ReplyMod()::replyListener)
    bot.addConsumers(SleepMod()::sleepConsumer)

    bot.loopForever()
}

