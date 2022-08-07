package roll

import kotlin.math.max
import kotlin.random.Random

data class Result(val theRoll: Roll, val results: List<Int> = emptyList()) {
    val sum: Int
        get() = max(results.sum() + theRoll.bonus - theRoll.malus, 1)

    companion object {
        val empty: Result
            get() = Result(Roll.empty, listOf())
    }
}

infix fun Int.d(size: Int): Dice = Dice(this, size)

infix fun Dice.plus(bonus: Int) = Roll(this, bonus, 0)
infix fun Dice.minus(minus: Int) = Roll(this, 0, minus)

fun rollThat(rollFn: () -> Roll): Result = with(rollFn()) { Result(this, Roll.many(this)) }

fun rollIt(diceFn: () -> Any): Result = rollOrEmpty(diceFn())

private fun rollOrEmpty(that: Any): Result = when (that) {
    is Roll -> rollThat { that }
    is Dice -> rollThat { Roll(that) }
    else -> Result.empty
}

fun rollDice(diceFn: () -> Dice): Result = rollThat { Roll(diceFn()) }

private fun rollIt_handlerWithMethodRefs(diceFn: () -> Any): Result = diceFn().let {
    val handler = when (it) {
        is Dice -> ::rollDice
        is Roll -> ::rollThat
        else -> Result::empty
    }
    handler.call(diceFn)
}

private fun rollIt_justWith(diceFn: () -> Any): Result = with(diceFn()) { rollOrEmpty(this) }

data class Roll(val dice: Dice = Dice.empty(), val bonus: Int = 0, val malus: Int = 0, val sub: Boolean = false) {
    val isSimple: Boolean get() = bonus == 0 && malus == 0
    val isEmpty: Boolean get() = this == empty
    val valid: Boolean get() = dice.valid && checkDice(dice)

    infix fun Dice.plus(bonus: Int) = Roll(Dice(qty, size), bonus = bonus)
    infix fun Dice.minus(malus: Int) = Roll(Dice(qty, size), malus = malus)

    companion object {
        val empty = Roll(Dice.empty(), 0, 0)

        fun from(qty: Int, size: Int): Roll = Roll(Dice(qty, size))

        fun checkDice(roll: Dice): Boolean = roll.qty <= 100 && roll.size in 2..200

        fun justOne(die: Int): Int = Random.nextInt(1, die)

        fun check(theRoll: Roll, target: Int): Boolean = many(theRoll).all { it >= target }

        fun many(theRoll: Roll): List<Int> = theRoll.dice.qty.downTo(1).map { justOne(theRoll.dice.size) }

        fun total(theRoll: Roll): Result = Result(theRoll, many(theRoll))

    }
}

