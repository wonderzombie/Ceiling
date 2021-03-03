package irc

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.random.Random

class Irceiling(private val conn: Connection, override val nick: String = "thumbkin") : IrcClient {
    // send
    private fun s(msg: String) =
        conn.sendMsg(msg)

    // type
    private fun t(msg: String) = runBlocking {
        val msgParts = msg.split(":", limit = 2)

        val typingOut = if (msgParts.size == 2) msgParts[1] else msg

        val delayMillis = randomInterval * typingOut.length
        delay(delayMillis).apply { s(msg) }
    }

    private val randomInterval: Long
        get() = 10L * Random.nextInt() % 3

    private var activeChannel = ""

    private var channelList: List<String> = listOf()
        set(value) {
            field = channelList.plus(value)
        }

    override fun nextMessage(): IrcMessage {
        var rawMessage = ""
        while (rawMessage.isEmpty()) {
            rawMessage = conn.readLine()
        }
        val message = IrcMessage.from(rawMessage)
        if (message.type == IrcCommand.PING) pong(message)

        return message
    }

    override infix fun say(msg: String) = t(msg)

    // the client handles pong without user intervention
    private fun pong(ircMessage: IrcMessage) {
        ircMessage.rawMessage.split(":", limit = 2).drop(1).let {
            return@pong s("PONG :${it[0]}")
        }
    }

    override fun join(channel: String) =
        s("JOIN $channel $nick").also { this.activeChannel = channel }

    override fun privmsg(message: String) = runBlocking {
        // Nobody types the first part out, but who cares.
        t("PRIVMSG $activeChannel :$message")
    }

    override fun privmsg(channel: String, message: String) = runBlocking {
        activeChannel = channel
        privmsg(message)
    }

    override val isConnected: Boolean
        get() = this.conn.isConnected

    override val now: Instant
        get() = Instant.now()

    override fun action(emote: String) = s("PRIVMSG $activeChannel : ACTION $emote")

    override fun handShake() {
        this say "NICK $nick"
        this say "USER $nick bot localhost :$nick"
    }

    fun names() {
        this say "NAMES"
    }
}