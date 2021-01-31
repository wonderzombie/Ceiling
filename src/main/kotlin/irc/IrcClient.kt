package irc

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.random.Random

class IrcClient(private val conn: Connection, val nick: String = "thumbkin") {
    // send
    private fun s(msg: String) =
        conn.sendMsg(msg)

    // type
    private fun t(msg: String) = runBlocking {
        val delayMillis = randomInterval * msg.split(":", limit = 2)[1].length
        delay(delayMillis).also { s(msg) }
    }

    private val randomInterval: Long
        get() = 10L * Random.nextInt() % 3

    private var activeChannel = ""

    private var channelList: List<String> = listOf()
        set(value) {
            field = channelList.plus(value)
        }

    fun nextMessage(): IrcMessage {
        var rawMessage = ""
        while (rawMessage.isEmpty()) {
            rawMessage = conn.readLine()
        }

        return IrcMessage.from(rawMessage).also { if (it.type == IrcCommand.PING) pong(it) }
    }

    private infix fun send(msg: String) =
        conn.sendMsg(msg)

    // the client handles pong without user intervention
    private fun pong(ircMessage: IrcMessage) {
        ircMessage.rawMessage.split(":", limit = 2).drop(1).let {
            return@pong s("PONG :${it[0]}")
        }
    }

    fun join(channel: String) =
        s("JOIN $channel $nick").also { this.activeChannel = channel }

    fun privmsg(message: String) = runBlocking {
        // Nobody types the first part out, but who cares.
        t("PRIVMSG $activeChannel :$message")
    }

    fun privmsg(channel: String, message: String) = runBlocking {
        activeChannel = channel
        privmsg(message)
    }

    val isConnected: Boolean
        get() = this.conn.isConnected

    val now: Instant
        get() = Instant.now()

    fun action(emote: String) = s("PRIVMSG $activeChannel : ACTION $emote")

    fun handShake() {
        this send "NICK $nick"
        this send "USER $nick bot localhost :$nick"
    }
}