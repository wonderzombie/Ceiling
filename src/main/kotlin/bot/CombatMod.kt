package bot

import irc.IrcClient
import irc.IrcMessage

class CombatMod(private val b: Bot) {
    var hpMap: Map<String, Int> = mapOf()

    var keywords: List<Regex> = listOf(
        Regex("""attack[s]?"""),
        Regex("""heal[s]?"""),
        Regex("""res[s]?"""),
        Regex("""resurrect[s]?"""),
    )

    var actions = mapOf(
        Regex("""attack[s]?""") to ::attackListener,
        Regex("""heal[s]?""") to ::healListener,


    )

    fun attackListener(cli: IrcClient, msg: IrcMessage) {

    }

    fun healListener(cli: IrcClient, msg: IrcMessage) {

    }

    fun combatListener(cli: IrcClient, msg: IrcMessage) {

    }
}