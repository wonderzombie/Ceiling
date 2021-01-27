package irc

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

interface Connection {
    val isConnected: Boolean

    fun sendMsg(message: String)
    fun readLine(): String
}

class IrcClient(private val conn: Connection, private val nick: String = "thumbkin") {
    private fun s(msg: String) =
        conn.sendMsg(msg)

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

        val ircMessage = IrcMessage.from(rawMessage)

        if (ircMessage.type == IrcCommand.PING) {
            pong(ircMessage)
        }

        return ircMessage
    }

    private infix fun send(msg: String) =
        conn.sendMsg(msg)

    private fun pong(ircMessage: IrcMessage) {
        val pingParts = ircMessage.rawMessage.split(":", limit = 2).drop(1)
        println("| ping parts $pingParts -- sending pong")
        return s("PONG :${pingParts[0]}")
    }

    fun join(channel: String) =
        s("JOIN $channel $nick").also { this.activeChannel = channel }

    fun chat(message: String) = runBlocking {
        delay(message.length * (10L * Random.nextInt() % 3))
        s("PRIVMSG $activeChannel :$message")
    }

    val isConnected: Boolean
        get() = this.conn.isConnected

    fun action(emote: String) = s("PRIVMSG $activeChannel : ACTION $emote")

    fun handShake() {
        this send "NICK $nick"
        this send "USER $nick bot localhost :$nick"
    }
}