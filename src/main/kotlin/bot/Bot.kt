package bot

import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage
import kotlinx.coroutines.withTimeoutOrNull

class Bot private constructor(private val client: IrcClient) {
    companion object {
        fun connect(client: IrcClient): Bot {
            return Bot(client).also { it.client.handShake() }
        }
    }

    private var listeners: Map<IrcCommand, List<ListenerFn>> = mapOf()

    fun addListeners(type: IrcCommand, vararg newListeners: ListenerFn) {
        val currentListeners = listeners[type].orEmpty()
        listeners = listeners.plus(mapOf(type to currentListeners.plus(newListeners)))
    }

    suspend fun loopOnce() = withTimeoutOrNull(500L) {
        println("| loop")
        client.nextMessage().run {
            listeners[type].orEmpty().onEach { l -> l.invoke(client, this) }
        }
    }

    suspend fun loopForever() {
        while (true) {
            loopOnce()
        }
    }
}

typealias ListenerFn = (c: IrcClient, m: IrcMessage) -> Unit
typealias ConsumerFn = (c: IrcClient, m: IrcMessage) -> Boolean

interface BotMod {
    fun listener(): ListenerFn
}

