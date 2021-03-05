package roll

import kotlin.random.Random

class RollParser {
    companion object {
        val rollRegex = Regex("(\\d+)d(\\d+)")

        fun parse(command: String): Roll {
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
    }
}