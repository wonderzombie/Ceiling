package roll

import kotlin.random.Random

data class Roll(val dice: Dice = Dice.empty(), val bonus: Int = 0, val malus: Int = 0) {
    val isSimple: Boolean get() = bonus == 0 && malus == 0
    val isEmpty: Boolean get() = this == theEmptyRoll
    val valid: Boolean get() = dice.valid

    companion object {
        private val theEmptyRoll = Roll(Dice.empty(), 0, 0)
        fun empty(): Roll = theEmptyRoll
        fun from(qty: Int, size: Int): Roll = Roll(Dice(qty, size), 0, 0)

        fun checkDice(roll: Dice): Boolean {
            val nDiceValid = roll.qty <= 100
            val dieValid = roll.size in 2..200
            return nDiceValid.and(dieValid)
        }

        fun toss(theRoll: Roll): List<Int> {
            return toss(theRoll.dice.qty, theRoll.dice.size)
        }

        fun toss(qty: Int, size: Int) =
            qty.downTo(1).map { Random.nextInt(1, size) }

    }
}

