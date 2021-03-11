package roll

data class Dice(val qty: Int, val size: Int) {
    val valid: Boolean
        get() = qty in 1..100 && size in 2..200

    companion object {
        private val theEmptyDice = Dice(0, 0)
        fun empty(): Dice = theEmptyDice
    }
}