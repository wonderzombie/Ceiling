package bot

import irc.IrcClient
import irc.IrcMessage
import kotlin.random.Random

class DiceMod : BotMod {
    override fun listener(): ListenerFn {
        return this::receive
    }

    val rollRegex = Regex("(\\d+)d(\\d+)")

    fun receive(cli: IrcClient, msg: IrcMessage) {
        if (!isBotCommand(msg)) return

        val commandMsg = msg.body.trimStart('!')
        val commandTok = commandMsg.split(' ')
        if (commandTok.size < 2) {
            return
        }

        val verb = commandTok.first().toLowerCase()
        if (verb == "r") {
            cli say handleRoll(commandTok)
        }
    }

    fun handleRoll(commandTok: List<String>): String {
        val results = commandTok.map { readRoll(it) }
        // then do more formatting than this
        return results.joinToString(" ")
    }

    fun readRoll(command: String): String? {
        print("| command $command")
        val match = rollRegex.matchEntire(command) ?: return null
        val (maybeDiceNum, maybeDie) = match.destructured
        val numDice = maybeDiceNum.toIntOrNull() ?: return null
        val die = maybeDie.toIntOrNull() ?: return null

        if (numDice > 100 || die > 200) return null

        val rolls = roll(numDice, die)
        val sum = rolls.sum()

        val outResults = rolls.joinToString(", ")

        val outMessage = "${numDice}d${die} => $outResults -> $sum"
        println("| outmessage: $outMessage")

        return outMessage
    }

    fun diceOk(numDice: Int, die: Int): Boolean {
        val nDiceValid = numDice <= 100
        val dieValid = die <= 200 && die != 0
        return nDiceValid.and(dieValid)
    }

    fun roll(numDice: Int, die: Int) =
        numDice.downTo(1).map { Random.nextInt(1, die) }

    private fun isBotCommand(msg: IrcMessage) =
        msg.body.startsWith("!")
}