package irc

import java.util.concurrent.atomic.AtomicReference

class FakeConn : Connection {
    override val isConnected: Boolean
        get() = true

    var received = AtomicReference<List<String>>()
    var toSend = AtomicReference<List<String>>()
    var sentTo: Boolean = false

    fun addToSendList(message: String) {
        toSend.getAndUpdate { it.plus(message) }
    }

    override fun sendMsg(message: String) {
        sentTo = true
        received.getAndUpdate { it.plus(message) }
        println("$this sendmsg called: $message")
        println("$this remaining received $received")
    }

    override fun readLine(): String {
        val line = toSend.get().first()
        toSend.getAndUpdate { it.minus(line) }
        println("readline called: $line")
        return line
    }
}