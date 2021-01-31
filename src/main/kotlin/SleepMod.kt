import irc.IrcClient
import irc.IrcCommand
import irc.IrcMessage
import java.time.Instant

class SleepMod {
    private var asleep = false
    private var wakeUpTime = Instant.EPOCH

    fun sleepConsumer(cli: IrcClient, msg: IrcMessage): Boolean {
        if (msg.type != IrcCommand.PRIVMSG) return false

        if (!asleep && msg.body.contains("shut up, ${cli.nick}")) {
            asleep = true
            wakeUpTime = cli.now.plusSeconds(60.times(5))
            cli.privmsg("ok i'll go to sleep for a while")
            return true
        } else if (asleep && cli.now.isAfter(wakeUpTime)) {
            asleep = false
            cli.privmsg("huh? wha? what did i miss?")
            return true
        } else if (asleep) {
            return true
        }
        return false
    }
}