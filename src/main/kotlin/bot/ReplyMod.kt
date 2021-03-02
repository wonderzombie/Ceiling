package bot

import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage

class ReplyMod : BotMod {
    private val replies = listOf(
        "i like fire swords",
        "i like bees",
        "where is thumbkin no really where is he",
        "beth is trouble",
        "where is thumbkin, where is thumbkin"
    )

    fun replyListener(cli: IrcClient, msg: IrcMessage) {
        with(msg) {
            if (type == IrcCommand.PRIVMSG && body.contains(cli.nick)) cli say replies.random()
        }
    }

    override fun listener(): ListenerFn {
        return this::replyListener
    }
}