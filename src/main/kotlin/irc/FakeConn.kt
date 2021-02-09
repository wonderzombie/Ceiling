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
        toSendReference.getAndUpdate { it.plus(message) }
    }

    override fun sendMsg(message: String) {
        sendToRef.compareAndExchange(false, true)
        recvReference.getAndUpdate { it.plus(message) }
        println("$this sendmsg called: $message")
        println("$this remaining received $received")
    }

    override fun readLine(): String {
        val line: String = toSendReference.get().let { if (it.isEmpty()) "" else it.first() }
        toSendReference.getAndUpdate {
            it.contains(line).let { b -> if (b) it.minus(line) else it }
        }
        return line
    }
}