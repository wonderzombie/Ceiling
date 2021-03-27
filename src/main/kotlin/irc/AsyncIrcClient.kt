package irc

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.time.Instant
import kotlin.random.Random

typealias ChannelList = Set<String>

/** Unused proof of concept of async IRC client. See AsyncConnection for the next iteration.  */
class AsyncIrcClient(private val conn: Connection, override val nick: String = "thumbkin") :
    IrcClient {
    private var writeJob: Job? = null
    private var readJob: Job? = null
    private val serverChannel: Channel<String> = Channel(10)
    private val clientChannel: Channel<String> = Channel(10)

    private val randomInterval: Long
        get() = 10L * (Random.nextInt() % 3)

    private var activeChannel = ""
    private var channelList: ChannelList = setOf()
        set(value) {
            field = field.plus(value)
        }

    val serverFeed: ReceiveChannel<String> = serverChannel
    val clientInput: SendChannel<String> = clientChannel

    private var started: Boolean = false

    suspend fun start() {
        println("start begin")
        coroutineScope {
            started = true
            readJob = launch { startReceiving() }
            writeJob = launch { startSending() }
        }
        println("start end")
    }

    private suspend fun startReceiving() = coroutineScope {
        println("| [irc] LAUNCH read")
        while (true) {
            println("| [irc] conn.Readline")
            conn.readLine().takeUnless(String::isEmpty)?.let {
                serverChannel.send(it)
                println("<- [irc] $it")
            }
            delay(500L)
        }
    }

    private suspend fun startSending() = coroutineScope {
        println("| [irc] launch conn WRITE job")
        while (true) {
            clientChannel.receive().apply {
                if (this.isEmpty()) println("received empty string from client")
                conn.sendMsg(this)
                println("-> [irc] $this")
            }
            delay(500L)
        }
    }

    suspend fun stop() {
        println("stop() called")
        readJob?.cancelAndJoin()
        writeJob?.cancelAndJoin()
        clientChannel.close()
        serverChannel.close()
    }

    // send
    private fun s(msg: String) =
        conn.sendMsg(msg)

    // type
    private fun t(msg: String) = runBlocking {
        val charsToType = msg.let {
            val parts = it.split(":", limit = 2)
            if (parts.size == 2) parts[1] else ""
        }.length
        if (charsToType == 0) return@runBlocking

        msg.apply {
            delay(randomInterval * charsToType)
            s(this)
        }
    }

    override suspend fun nextMessage(): IrcMessage = withTimeout(500L) {
        conn.readLine().let {
            IrcMessage.from(it)
        }
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