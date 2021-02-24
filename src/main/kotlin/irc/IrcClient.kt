package irc

import java.time.Instant

interface IrcClient {
    val now: Instant
    val nick: String
    val isConnected: Boolean

    fun nextMessage(): IrcMessage
    fun join(channel: String)
    fun privmsg(message: String)
    fun privmsg(channel: String, message: String)
    fun action(emote: String)
    fun handShake()

    infix fun say(msg: String)
}