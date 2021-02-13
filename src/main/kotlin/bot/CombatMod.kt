package bot

import irc.Irceiling
import irc.IrcMessage

class CombatMod(private val b: Bot) {
    var hpMap: Map<String, Int> = mapOf()

    fun combatListener(cli: Irceiling, msg: IrcMessage) {

    }
}