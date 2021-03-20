package irc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class AsyncConnection(val conn: PlainTextConn) : Connection {
    private lateinit var coScope: CoroutineScope

    private var writeJob: Job? = null
    private var readJob: Job? = null

    private val connChannel: Channel<String> = Channel(10)

    private val socketIOChannel: Channel<String> = Channel(10)
    private val clientIOChannel: Channel<String> = Channel(10)

    override val isConnected: Boolean
        get() = conn.isConnected

    val toServer: SendChannel<String> = connChannel
    val fromServer: ReceiveChannel<String> = connChannel

    val toClient: ReceiveChannel<String> = clientIOChannel
    val fromClient: ReceiveChannel<String> = clientIOChannel

    private var started: Boolean = false
    override fun sendMsg(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun readLine(): String {
        TODO("Not yet implemented")
    }

//    fun start() {
//        started = true
//        GlobalScope.launch {
//            readJob = launch { socketLoop() }
//            writeJob = launch { clientLoop() }
//            coScope = this
//        }
//    }
//
//    private suspend fun socketLoop() {
//        while (true) {
//            connChannel.receive().let { ln ->
//                socketIOChannel.send(ln)
//                println("<- $ln")
//            }
//
//            delay(500L)
//        }
//    }
//
//    override suspend fun readLine(): String {
//        return ""
//    }
//
//    private suspend fun clientLoop() {
//        while (true) {
//            fromServer.receive().let { msg ->
//                println("-> $msg")
//                toClient.send(msg)
//            }
//            delay(500L)
//        }
//    }
//
//    override val isConnected: Boolean
//        get() = socket.isConnected
//
//    override fun sendMsg(message: String) = runBlocking {
//        toClient.send(message)
//    }
//
//    suspend fun stop() {
//        println("stop() called")
//        readJob?.cancelAndJoin()
//        writeJob?.cancelAndJoin()
//        clientChannel.close()
//        connClientChannel.close()
//    }
}