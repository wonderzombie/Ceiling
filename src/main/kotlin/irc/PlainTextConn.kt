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
    // This exposes output from the server.
    val receiveChannel: ReceiveChannel<String> = serverChannel

    override val isConnected: Boolean
        get() = !writer.isClosedForSend && !reader.isClosedForReceive

    override fun sendMsg(message: String) {
        runBlocking {
            // This suspends only when the channel is full.
            serverChannel.send(message)
            println("-> $message")
        }
    }

    override suspend fun readLine(): String = coroutineScope {
        // IO will actually block the *thread*, so we use an IO dispatcher
        // as the coroutine scope.
        launch(Dispatchers.IO) {
            val message = reader.receive()
            serverChannel.send(message)
            println("<- [conn] $message")
        }
        // This will suspend if the channel is empty.
        serverChannel.receive()
    }
}