import bot.Bot
import bot.ReplyMod
import bot.SleepMod
import irc.AsyncIrcClient
import irc.IrcCommand
import irc.PlainTextConn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

fun main() = runBlocking {
    val socket = Socket(InetAddress.getLocalHost(), 6667)
    val reader = socket.getInputStream().bufferedReader()
    val writer = PrintWriter(socket.getOutputStream())
    val socketRead: Channel<String> = Channel(capacity = 10)
    val socketWrite: Channel<String> = Channel(capacity = 10)

    val plainTextConn = PlainTextConn(socketRead, socketWrite)

    launch(Dispatchers.IO) {
        while (true) {
            reader.readLine().let { socketRead.send(it) }
            delay(1000)
        }
    }

    launch(Dispatchers.IO) {
        while (true) {
            writer.write(socketWrite.receive() + "\n\r")
            delay(1000)
        }
    }

    Bot.connect(AsyncIrcClient(plainTextConn))
        .let { bot ->
            bot.addListeners(IrcCommand.PRIVMSG, ReplyMod()::replyListener)
            bot.loopForever()
        }
}

