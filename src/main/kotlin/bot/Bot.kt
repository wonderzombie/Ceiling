package bot

import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage

typealias ListenerFn = (c: IrcClient, m: IrcMessage) -> Unit
typealias ConsumerFn = (c: IrcClient, m: IrcMessage) -> Boolean

class Bot private constructor(private val client: IrcClient) {
    companion object {
        fun connect(client: IrcClient): Bot {
            return Bot(client)
        }
    }

    private var listeners: Map<IrcCommand, List<ListenerFn>> = mapOf()
    private var consumers: List<ConsumerFn> = listOf()
    private var registeredModules: List<String> = listOf()

    fun register(id: String, fn: (b: Bot) -> Unit) {
        fn(this)
        registeredModules = registeredModules.plus(id)
    }

    // For such as a NAMES command that shouldn't percolate down to "user space."
    fun addConsumers(vararg newConsumers: ConsumerFn) {
        consumers = consumers.plus(newConsumers)
    }

    fun addListeners(type: IrcCommand, vararg newListeners: ListenerFn) {
        val currentListeners = listeners[type] ?: listOf()
        val allListeners: List<ListenerFn> = currentListeners.plus(newListeners)
        listeners = listeners.plus(mapOf(type to allListeners))
    }

    private fun checkConsumers(msg: IrcMessage): Boolean =
        consumers.map { c -> checkConsume(msg, c).also { r -> println(r) } }.any { it }

    private fun checkConsume(
        msg: IrcMessage,
        fn: ConsumerFn
    ): Boolean = fn.invoke(client, msg).also { println("consuming: $msg ? $it") }

    fun loopOnce() {
        val msg = client.nextMessage()

        val consumed = checkConsumers(msg)
        if (!consumed) {
            val listenersForType = listeners[msg.type] ?: listOf()
            listenersForType.forEach { it.invoke(client, msg) }
        }
    }

    fun loopForever() {
        while (true) {
            loopOnce()
        }
    }
}

