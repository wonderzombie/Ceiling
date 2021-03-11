package roll

class RollParser {
    companion object {
        val rollRegex = Regex("(\\d+)d(\\d+)")

        fun parse(command: String): Roll {
            val bonusOrMalus = '+' in command || '-' in command
            return if (bonusOrMalus) tryIncrementalMatch(command) else trySimpleMatch(command)
        }

        private fun toInt(s: String?): Int = s?.let {
            if (it.all(Char::isDigit)) (it.toIntOrNull() ?: 0) else 0
        } ?: 0

        private fun trySimpleMatch(command: String): Roll = rollRegex.matchEntire(command)?.let {
            val (diceNum, size) = it.destructured
            Roll.from(toInt(diceNum), toInt(size))
        } ?: Roll.empty

        private fun tryIncrementalMatch(command: String): Roll {
            val qty = toInt(command.takeDigits())
            if (qty == 0) Roll.empty

            val maybeTheRest = command.dropDigits()
            if (!maybeTheRest.startsWith('d')) Roll.empty

            val size = toInt(maybeTheRest.drop(1).takeDigits())
            if (size == 0) Roll.empty

            val maybeModifier = maybeTheRest.drop(1).dropDigits()
            if (!maybeModifier.startsWith(listOf('-', '+'))) Roll.from(qty, size)

            val modifier = toInt(maybeModifier.drop(1).takeDigits())
            if (modifier == 0) Roll.from(qty, size)

            val bonus = if (maybeModifier.first() == '+') modifier else 0
            val malus = if (maybeModifier.first() == '-') modifier else 0

            return Roll(Dice(qty, size), bonus, malus)
        }

        private fun String.takeDigits(): String = this.takeWhile { it.isDigit() }

        private fun String.dropDigits(): String = this.dropWhile { it.isDigit() }

        private fun String.startsWith(prefixes: List<Char>): Boolean =
            prefixes.any { this.startsWith(it) }
    }

}