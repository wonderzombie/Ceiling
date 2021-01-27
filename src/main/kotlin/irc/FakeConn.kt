package irc

class FakeConn() : Connection {
    override val isConnected: Boolean
        get() = true

    var received: List<String> = emptyList()
    var toSend: List<String> = emptyList()

    fun addToSendList(message: String) {
        toSend = toSend + message
    }

    override fun sendMsg(message: String) {
        received = received.plus(message)
    }

    override fun readLine(): String {
        val ln = toSend.first()
        toSend = toSend.minus(ln)
        return ln
    }
}