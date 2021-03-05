package roll

data class Roll(val dice: Dice = Dice.empty(), val bonus: Int = 0, val malus: Int = 0) {
    val isSimple: Boolean get() = bonus == 0 && malus == 0
    val isEmpty: Boolean get() = this == theEmptyRoll
    val valid: Boolean get() = dice.valid

    companion object {
        private val theEmptyRoll = Roll(Dice.empty(), 0, 0)
        fun empty(): Roll = theEmptyRoll
        fun from(qty: Int, size: Int): Roll = Roll(Dice(qty, size), 0, 0)
    }
}

