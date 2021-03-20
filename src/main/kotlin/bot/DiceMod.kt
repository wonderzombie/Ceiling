package bot

import irc.IrcClient
import irc.IrcMessage
import roll.Roll
import roll.RollParser

class DiceMod : BotMod {
    override fun listener(): ListenerFn {
        return this::receive
    }

    fun receive(cli: IrcClient, msg: IrcMessage) {
        if (!isBotCommand(msg)) return

        val commandMsg = msg.body.trimStart('!')
        val cmdTokens = commandMsg.split(' ')
        if (cmdTokens.size < 2) {
            return
        }

        val verb = cmdTokens.first().toLowerCase()
        if (verb == "r") {
            cli privmsg handleRoll(cmdTokens)
        }
    }

    fun handleRoll(commandTok: List<String>): String {
        val results = commandTok.map { readRoll(it) }
        // then do more formatting than this
        return results.joinToString(" ")
    }

    fun readRoll(command: String): String? {
        println("| command $command")
        val theRoll = RollParser.parse(command)

        if (theRoll.isEmpty) {
            return null
        }

        val rolls = Roll.toss(theRoll)
        val simpleSum = rolls.sum()
        val totalSum = simpleSum + theRoll.bonus - theRoll.malus

        val outResults = rolls.joinToString(", ")

        val rollSummary = if (theRoll.dice.qty == 1) outResults else "$outResults -> $totalSum"

        val outMessage = "${theRoll.dice.qty}d${theRoll.dice.size} => $rollSummary"

        println("| out message: $outMessage")

        return outMessage
    }

    private fun isBotCommand(msg: IrcMessage) =
        msg.body.startsWith("!")
}