package bot

import irc.IrcMessage

data class ChatMsg(val msg: IrcMessage) {
//    internal fun target(msg: IrcMessage): String =
//        actions.keys.find { it.containsMatchIn(msg.body) }?.split(msg.body, 2)?.first() ?: ""

    internal fun actor(msg: IrcMessage): String = msg.header.split(Regex("!"), 2).first()

    companion object {
        val actions = listOf(
            "attacks",
            "heals",
            "revives"
        )

        fun from(msg: IrcMessage) {

        }
    }

}
