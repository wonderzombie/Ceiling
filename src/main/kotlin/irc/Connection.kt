package irc

interface Connection {
    val isConnected: Boolean

    fun sendMsg(message: String)
    suspend fun readLine(): String
}