package roll

import kotlin.math.max
import kotlin.random.Random

data class Result(val theRoll: Roll, val results: List<Int> = emptyList()) {
    val sum: Int
        get() = max(results.sum() + theRoll.bonus - theRoll.malus, 1)
}

data class Roll(val dice: Dice = Dice.empty(), val bonus: Int = 0, val malus: Int = 0) {
    val isSimple: Boolean get() = bonus == 0 && malus == 0
    val isEmpty: Boolean get() = this == empty
    val valid: Boolean get() = dice.valid && checkDice(dice)

    companion object {
        val empty = Roll(Dice.empty(), 0, 0)

        fun from(qty: Int, size: Int): Roll = Roll(Dice(qty, size), 0, 0)

        fun checkDice(roll: Dice): Boolean {
            return roll.qty <= 100 && roll.size in 2..200
        }

        fun toss(theRoll: Roll): List<Int> {
            return toss(theRoll.dice.qty, theRoll.dice.size)
        }

        fun toss(qty: Int, size: Int) =
            qty.downTo(1).map { Random.nextInt(1, size) }

        fun tossAndSum(theRoll: Roll): Result = Result(theRoll, toss(theRoll))

    }
}

