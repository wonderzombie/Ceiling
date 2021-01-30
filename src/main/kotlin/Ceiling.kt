import bot.Bot
import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage
import irc.PlainTextConn
import kotlinx.coroutines.runBlocking

val replies = listOf(
    "i like fire swords",
    "i like bees",
    "where is thumbkin no really where is he",
    "beth is trouble",
    "where is thumbkin, where is thumbkin"
)

fun main() = runBlocking {
    val bot = Bot.connect(IrcClient(PlainTextConn()))

    bot.addListeners(::nameListener)
}

fun nameListener(cli: IrcClient, msg: IrcMessage) {
    if (msg.type == IrcCommand.PRIVMSG) {
        if (msg.body.contains("thumbkin")) cli.privmsg(replies.random())
    }
}