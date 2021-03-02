package bot

import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage

typealias ListenerFn = (c: IrcClient, m: IrcMessage) -> Unit
typealias ConsumerFn = (c: IrcClient, m: IrcMessage) -> Boolean

interface BotMod {
    fun listener() : ListenerFn
}

class Bot private constructor(private val client: IrcClient) {
    companion object {
        fun connect(client: IrcClient): Bot {
            return Bot(client).also { it.client.handShake() }
        }
    }

    private var listeners: Map<IrcCommand, List<ListenerFn>> = mapOf()
    private var consumers: List<ConsumerFn> = listOf()
    private var registeredModules: List<String> = listOf()

    fun register(id: String, fn: (b: Bot) -> Unit) {
        fn(this).also { registeredModules = registeredModules.plus(id) }
    }

    // For such as a NAMES command that shouldn't percolate down to "user space."
    fun addConsumers(vararg newConsumers: ConsumerFn) {
        consumers = consumers.plus(newConsumers)
    }

    fun addListeners(type: IrcCommand, vararg newListeners: ListenerFn) {
        val currentListeners = listeners[type] ?: listOf()
        listeners = listeners.plus(mapOf(type to currentListeners.plus(newListeners)))
    }

    private fun checkConsumers(msg: IrcMessage): Boolean =
        consumers.map { c -> checkConsume(msg, c) }.any { it }

    private fun checkConsume(
        msg: IrcMessage,
        fn: ConsumerFn
    ): Boolean = fn.invoke(client, msg).also { println("consuming: $msg ? $it") }

    fun loopOnce() {
        client.nextMessage().let { msg ->
            if (checkConsumers(msg)) {
                return
            }
            listeners[msg.type]?.forEach { l -> l.invoke(client, msg) }
        }
    }

    fun loopForever() {
        while (true) {
            loopOnce()
        }
    }
}

