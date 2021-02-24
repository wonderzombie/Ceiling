import bot.Bot
import bot.ReplyMod
import bot.SleepMod
import irc.AsyncIrcClient
import irc.IrcCommand
import irc.PlainTextConn
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    Bot.connect(AsyncIrcClient(PlainTextConn())).let { bot ->
        bot.addListeners(IrcCommand.PRIVMSG, ReplyMod()::replyListener)
        bot.addConsumers(SleepMod()::sleepConsumer)
        bot.loopForever()
    }
}

