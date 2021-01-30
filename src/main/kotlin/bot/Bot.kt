package bot

import irc.IrcClient
import irc.IrcMessage

typealias Listener = (c: IrcClient, m: IrcMessage) -> Unit

class Bot private constructor(private val client: IrcClient) {

    var listeners: List<Listener> = listOf()

    fun addListener(listener: Listener) {
        listeners = listeners.plus(listener)
    }

    companion object {
        fun connect(client: IrcClient): Bot {
            return Bot(client)
        }
    }

    fun loopOnce() {
        with(client) {
            val msg = nextMessage()
            listeners.forEach { it.invoke(client, msg) }
        }
    }

    fun loopForever() {
        while (true) {
            loopOnce()
        }
    }
}