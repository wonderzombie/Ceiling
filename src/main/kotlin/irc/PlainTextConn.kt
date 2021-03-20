package irc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlainTextConn(
    private val reader: ReceiveChannel<String>,
    private val writer: SendChannel<String>
) :
    Connection {

    private val clientChannel: Channel<String> = Channel(capacity = 10)
    private val serverChannel: Channel<String> = Channel(capacity = 10)

    // This accepts input from the user.
    val sendChannel: SendChannel<String> = clientChannel

    override val isConnected: Boolean
        get() = !writer.isClosedForSend && !reader.isClosedForReceive

    override fun sendMsg(message: String) {
        runBlocking {
            serverChannel.send(message)
            println("-> $message")
        }
    }

    override suspend fun readLine(): String = coroutineScope {
        launch(Dispatchers.IO) {
            serverChannel.send(reader.receive())
        }
        return@coroutineScope serverChannel.receive()
    }
}