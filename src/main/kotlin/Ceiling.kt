import bot.Bot
import bot.ReplyMod
import bot.SleepMod
import irc.AsyncIrcClient
import irc.IrcCommand
import irc.PlainTextConn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import java.net.InetAddress
import java.net.Socket

fun main() = runBlocking {
    val socket = Socket(InetAddress.getLocalHost(), 6667)
//    val reader = socket.getInputStream().bufferedReader()
//    val writer = PrintWriter(socket.getOutputStream())
    val readChannel: Channel<String> = Channel(capacity = 10)
    val writeChannel: Channel<String> = Channel(capacity = 10)

    Bot.connect(AsyncIrcClient(PlainTextConn(reader = readChannel, writer = writeChannel)))
        .let { bot ->
            bot.addListeners(IrcCommand.PRIVMSG, ReplyMod()::replyListener)
            bot.addConsumers(SleepMod()::sleepConsumer)
            bot.loopForever()
        }
}

