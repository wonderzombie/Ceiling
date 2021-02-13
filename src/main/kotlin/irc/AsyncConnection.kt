package irc

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.net.Socket

class AsyncConnection : Connection {
    private var socket: Socket = Socket()
    private var writeJob: Job? = null
    private var readJob: Job? = null
    private val serverChannel: Channel<String> = Channel(10)
    private val clientChannel: Channel<String> = Channel(10)

    val serverFeed: ReceiveChannel<String> = serverChannel
    val clientInput: SendChannel<String> = clientChannel

    private var started: Boolean = false

    suspend fun start() {
        started = true
        coroutineScope {
            readJob = launch { setupReceive() }
            writeJob = launch { setupSend() }
        }
    }

    private suspend fun setupReceive() = withTimeoutOrNull(1000L) {
        val ln =
            this@AsyncConnection.readLine().also { if (it.isNotEmpty()) serverChannel.send(it) }
        println("<- $ln")
    }

    override fun readLine(): String = runBlocking {
        serverFeed.receive()
    }

    private suspend fun setupSend() = withTimeoutOrNull(1000L) {
        clientChannel.receive().let { msg ->
            println("-> $msg")
            sendMsg(msg)
        }
    }

    override val isConnected: Boolean
        get() = socket.isConnected

    override fun sendMsg(message: String) = runBlocking {
        clientInput.send(message)
    }

    suspend fun stop() {
        println("stop() called")
        readJob?.cancelAndJoin()
        writeJob?.cancelAndJoin()
        clientChannel.close()
        serverChannel.close()
    }
}