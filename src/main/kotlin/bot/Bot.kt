package bot

import irc.IrcClient
import irc.IrcMessage

typealias ListenerFn = (c: IrcClient, m: IrcMessage) -> Unit
typealias ConsumerFn = (c: IrcClient, m: IrcMessage) -> Boolean

class Bot private constructor(private val client: IrcClient) {
    companion object {
        fun connect(client: IrcClient): Bot {
            return Bot(client)
        }
    }

    var listeners: List<ListenerFn> = listOf()
    var consumers: List<ConsumerFn> = listOf()
    var registeredModules: List<String> = listOf()

    fun register(id: String, fn: (b: Bot) -> Unit) {
        fn(this);
        registeredModules = registeredModules.plus(id)
    }

    // For such as a NAMES command that shouldn't percolate down to "user space."
    fun addConsumers(vararg newConsumers: ConsumerFn) {
        consumers = consumers.plus(newConsumers)
    }

    fun addListeners(vararg newListeners: ListenerFn) {
        listeners = listeners.plus(newListeners)
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
            listeners.forEach { it.invoke(client, msg) }
        }
    }

    fun loopForever() {
        while (true) {
            loopOnce()
        }
    }
}