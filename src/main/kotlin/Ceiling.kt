import bot.Bot
import irc.IrcClient
import irc.IrcCommand
import irc.PlainTextConn
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    Bot.connect(IrcClient(PlainTextConn())).let { bot ->
        bot.addListeners(IrcCommand.PRIVMSG, ReplyMod()::replyListener)
        bot.addConsumers(SleepMod()::sleepConsumer)
        bot.loopForever()
    }
}

