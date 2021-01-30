package irc

interface Connection {
    val isConnected: Boolean

    fun sendMsg(message: String)
    fun readLine(): String
}