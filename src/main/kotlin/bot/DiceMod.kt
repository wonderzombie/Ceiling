package bot

import irc.IrcClient
import irc.IrcMessage
import kotlin.random.Random

data class Dice(val qty: Int, val size: Int) {
    fun empty() = qty == 0 && size == 0

    fun valid() = qty in 1..100 && size in 2..200

    companion object {
        fun empty(): Dice = Dice(0, 0)
    }
}

data class Roll(val dice: Dice = Dice.empty(), val bonus: Int = 0, val malus: Int = 0)

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
        println("| command $command")
        val theDice = parseRoll(command)

        if (theDice.empty()) {
            return null
        }

        val rolls = roll(theDice)
        val sum = rolls.sum()

        val outResults = rolls.joinToString(", ")

        val rollSummary = if (theDice.qty == 1) outResults else "$outResults -> $sum"

        val outMessage = "${theDice.qty}d${theDice.size} => $rollSummary"

        println("| out message: $outMessage")

        return outMessage
    }

    fun parseRoll(command: String): Dice {
        val match = rollRegex.matchEntire(command) ?: return Dice.empty()

        val theDice = match.let {
            val (diceNum, die) = it.destructured
            Dice(diceNum.toInt(), die.toInt())
        }
        if (!checkDice(theDice)) return Dice.empty()

        return theDice
    }

    fun checkDice(roll: Dice): Boolean {
        val nDiceValid = roll.qty <= 100
        val dieValid = roll.size in 2..200
        return nDiceValid.and(dieValid)
    }

    fun roll(theDice: Dice) = roll(theDice.qty, theDice.size)

    fun roll(qty: Int, size: Int) =
        qty.downTo(1).map { Random.nextInt(1, size) }

    private fun isBotCommand(msg: IrcMessage) =
        msg.body.startsWith("!")
}