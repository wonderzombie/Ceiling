package irc

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class AsyncConnectionTest {
    private val sendChannel: Channel<String> = Channel(capacity = 10)
    private val receiveChannel: Channel<String> = Channel(capacity = 10)
    private val conn = AsyncConnection(PlainTextConn(receiveChannel, sendChannel))

    @Test
    fun start_sendStuff_seeWhatBreaks(): Unit = runBlocking {

        val theChannel: Channel<String> = Channel(capacity = 10)

        coroutineScope {
            launch {
                3.downTo(1).forEach {
                    theChannel.send("S $it sender sender")
                    delay(500)
                }
                theChannel.close()
            }

            launch {
                var open = true
                while (open) {
                    val recvFlow = theChannel.receiveAsFlow()
                    recvFlow.collect { msg -> println("got message $msg") }
                    delay(500)
                    open = !theChannel.isClosedForReceive
                    println("closed for receive? $open")
                }
                println("channel closed!")
            }
        }
    }

    @Test
    fun sendMsg_andReadLine_sendAndReceiveOk() {


    }
}