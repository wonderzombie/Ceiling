import irc.PlainTextConn
import irc.IrcClient
import irc.IrcCommand

val replies = listOf(
    "i like fire swords",
    "i like bees",
    "where is thumbkin no really where is he",
    "beth is trouble",
    "where is thumbkin, where is thumbkin"
)

fun main() {
    val conn = PlainTextConn()
    val irc = IrcClient(conn)

    println("begin ceiling")
    while (!conn.socket.isConnected) {
        println("not connected yet")
    }

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
