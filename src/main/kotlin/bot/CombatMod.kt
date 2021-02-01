package bot

import irc.IrcClient
import irc.IrcMessage

class CombatMod(private val b: Bot) {
    var hpMap: Map<String, Int> = mapOf()

    fun combatListener(cli: IrcClient, msg: IrcMessage) {

    }
}