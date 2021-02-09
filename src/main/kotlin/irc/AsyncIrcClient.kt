package irc

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AsyncIrcClient(private val conn: Connection, val nick: String = "thumbkin") {
    private var writeJob: Job? = null
    private var readJob: Job? = null
    private val serverChannel: Channel<String> = Channel(10)
    private val clientChannel: Channel<String> = Channel(10)

    val serverFeed: ReceiveChannel<String> = serverChannel
    val clientInput: SendChannel<String> = clientChannel

    private var started: Boolean = false
//    lateinit var mainJob: Job

    suspend fun start() {
        started = true
        coroutineScope {
            readJob = launch { setupReceive() }
            writeJob = launch { setupSend() }
        }
    }

    private suspend fun setupReceive() = withTimeoutOrNull(1000L) {
        val ln = conn.readLine().also { if (it.isNotEmpty()) serverChannel.send(it) }
        println("<- $ln")
    }

    private suspend fun setupSend() = withTimeoutOrNull(1000L) {
        clientChannel.receive().let { msg ->
            println("-> $msg")
            conn.sendMsg(msg)
        }
    }

    suspend fun stop() {
        println("stop() called")
        readJob?.cancelAndJoin()
        writeJob?.cancelAndJoin()
        clientChannel.close()
        serverChannel.close()
    }
}