import irc.IrcClient
import irc.IrcCommand
import irc.PlainTextConn

val replies = listOf(
    "i like fire swords",
    "i like bees",
    "where is thumbkin no really where is he",
    "beth is trouble",
    "where is thumbkin, where is thumbkin"
)

fun main() {
    val irc = IrcClient(PlainTextConn())

    println("begin ceiling")

    with(irc) {
        handShake()

        join("#blobby")
        chat("hi")

        while (true) {
            val message = nextMessage()
            if (message.type == IrcCommand.PRIVMSG && message.rawMessage.contains("thumbkin")) {
                irc.chat(replies.random())
            }
        }
    }
}
