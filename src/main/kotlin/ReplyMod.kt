import irc.IrcClient
import irc.Irceiling
import irc.IrcCommand
import irc.IrcMessage

class ReplyMod {
    private val replies = listOf(
        "i like fire swords",
        "i like bees",
        "where is thumbkin no really where is he",
        "beth is trouble",
        "where is thumbkin, where is thumbkin"
    )

    fun replyListener(cli: IrcClient, msg: IrcMessage) {
        with(msg) {
            if (type == IrcCommand.PRIVMSG && body.contains(cli.nick)) cli.privmsg(replies.random())
        }
    }
}