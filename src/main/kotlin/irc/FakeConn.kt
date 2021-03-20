package irc

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class FakeConn : Connection {
    override val isConnected: Boolean
        get() = true

    val recvReference = AtomicReference<List<String>>(emptyList())
    val toSendReference = AtomicReference<List<String>>(emptyList())
    val sendToRef = AtomicBoolean(false)

    var received: List<String> = recvReference.get() ?: emptyList()
    var toSend: List<String> = toSendReference.get() ?: emptyList()

    fun addToSendList(message: String) {
        toSend = toSend.plus(message)
    }

    override fun sendMsg(message: String) {
        received = received.plus(message)
        println("$this sendmsg called: $message")
        println("$this remaining received $received")
    }

    override suspend fun readLine(): String {
        val line = toSend.let { if (it.isEmpty()) "" else it.first() }
        toSend = toSend.minus(line)
        return line
    }
}