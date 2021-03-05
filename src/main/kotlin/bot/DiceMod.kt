package bot

import irc.IrcClient
import irc.IrcMessage
import roll.Dice
import roll.Roll
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
        println("| command $command")
        val theRoll = parseRoll(command)

        if (theRoll.isEmpty) {
            return null
        }

        val rolls = roll(theRoll)
        val simpleSum = rolls.sum()
        val totalSum = simpleSum + theRoll.bonus - theRoll.malus

        val outResults = rolls.joinToString(", ")

        val rollSummary = if (theRoll.dice.qty == 1) outResults else "$outResults -> $totalSum"

        val outMessage = "${theRoll.dice.qty}d${theRoll.dice.size} => $rollSummary"

        println("| out message: $outMessage")

        return outMessage
    }

    fun parseRoll(command: String): Roll {
        val theDice = tryEasyMatch(command) ?: Dice.empty()
        if (theDice.valid) {
            return Roll(theDice)
        }
        return tryHardMatch(command)
    }

    fun toInt(s: String): Int =
        if (s.all(Char::isDigit)) (s.toIntOrNull() ?: 0) else 0

    fun tryHardMatch(command: String): Roll {
        val commandParts = command.split("d", limit = 2)
        if (commandParts.size != 2) return Roll.empty()

        val qty: Int = commandParts.firstOrNull()?.toIntOrNull() ?: 0

        // example: "2d12+6" -> "12+6"
        val maybeTheRest: String = commandParts.drop(1).firstOrNull() ?: ""
        val stringDigits = maybeTheRest.takeWhile { c -> c.isDigit() }
        val size = stringDigits.toIntOrNull() ?: 0

        val maybeBonus = maybeTheRest.split("+", limit = 2)
        val maybeMalus = maybeTheRest.split("-", limit = 2)

        if (maybeBonus.size != 2 && maybeMalus.size != 2) return Roll.from(0, 0)

        val bonus = maybeBonus.drop(1).firstOrNull()?.toIntOrNull() ?: 0
        val malus = maybeMalus.drop(1).firstOrNull()?.toIntOrNull() ?: 0

        return Roll(Dice(qty, size), bonus, malus)
    }

    private fun tryEasyMatch(command: String): Dice? = rollRegex.matchEntire(command)?.let {
        val (diceNum, die) = it.destructured
        Dice(diceNum.toInt(), die.toInt())
    }

    fun checkDice(roll: Dice): Boolean {
        val nDiceValid = roll.qty <= 100
        val dieValid = roll.size in 2..200
        return nDiceValid.and(dieValid)
    }

    fun roll(theRoll: Roll): List<Int> {
        return roll(theRoll.dice.qty, theRoll.dice.size)
    }

    fun roll(qty: Int, size: Int) =
        qty.downTo(1).map { Random.nextInt(1, size) }

    private fun isBotCommand(msg: IrcMessage) =
        msg.body.startsWith("!")
}