package bot

import irc.IrcClient
import irc.IrcMessage

typealias ListenerFn = (c: IrcClient, m: IrcMessage) -> Unit
typealias ReplyFn = (s: String) -> Unit

class Bot private constructor(private val client: IrcClient) {
    companion object {
        fun connect(client: IrcClient): Bot {
            return Bot(client)
        }
    }

    var listeners: List<ListenerFn> = listOf()

    fun addListeners(vararg newListeners: ListenerFn) {
        listeners = listeners.plus(newListeners)
    }

    fun loopOnce() {
        val msg = client.nextMessage()
        listeners.forEach { it.invoke(client, msg) }
    }

    fun loopForever() {
        while (true) {
            loopOnce()
        }
    }
}