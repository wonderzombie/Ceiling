package irc

import java.util.concurrent.atomic.AtomicReference

class FakeConn : Connection {
    override val isConnected: Boolean
        get() = true

    val recv = AtomicReference<List<String>>(emptyList())
    val send = AtomicReference<List<String>>(emptyList())

    fun addToSendList(message: String) {
        send.getAndUpdate {
            it.plus(message).also {
                println("$this added to send list: $message")
            }

        }
    }

    override fun sendMsg(message: String) {
        recv.updateAndGet {
            println("$this received was $it")
            println("$this now sendMsg got $message")
            it.plus(message).also {itt ->
                println("$this messages so far: $itt")
            }
        }
    }

    override suspend fun readLine(): String {
        val line: String = send.acquire.firstOrNull() ?: ""
        send.getAndUpdate {
            it.minus(line)
        }
        return line
    }
}
