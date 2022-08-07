package bot

import irc.IrcClient
import irc.IrcMessage
import roll.Roll

class CombatMod(val weapon: Roll, val missChance: Float, private val startHp: Int = 10) {
    private val attacks: List<String> = listOf(
        "smashes",
        "murders",
        "discombobulates",
        "shames",
        "stabs",
        "censures",
        "invalidates",
        "ignores",
        "forgets",
        "shuns",
        "flips off",
        "disassembles",
        "damns",
        "yells at",
        "gives the shit-eye to",
        "is disappointed in",
        "expected better from",
        "looks just about done with",
        "fervently wishes a headache upon",
        "slashes",
        "impales",
        "disembowels",
        "disemvowels",
        "craters",
        "fillets",
        "maligns",
    )
    private var players = mapOf<String, Int>()

    private val actions = mapOf(
        Regex("""attacks""") to ::attackListener,
        Regex("""heals""") to ::healListener,
        Regex("""revives""") to ::resurrectListener,
    )

    internal fun target(msg: IrcMessage): String =
        actions.keys.find { it.containsMatchIn(msg.body) }?.let {
            val action = it.split(msg.body, 2)[1]
            println("${msg.body} has action $action")
            action.trim().split(Regex(" "), 2).first()
        } ?: ""

    internal fun actor(msg: IrcMessage): String = msg.header.split(Regex("!"), 2).first()

    fun attackListener(cli: IrcClient, msg: IrcMessage) {
        val victim = target(msg)
        val actor = actor(msg)
        if (Roll.justOne(10) == 1) {
            cli say "$actor misses $victim!"
            return
        }
        Roll.justOne(6).also {
            val newHp = players.getOrDefault(victim, 10) - it
            players = players.plus(victim to newHp)
            cli say "$actor ${attacks.random()} $victim for $it damage!"
        }
    }

    fun healListener(cli: IrcClient, msg: IrcMessage) {
        val patient = target(msg)
        val actor = actor(msg)
        val result = Roll.justOne(8).also { result ->
            val newHp = players.getOrDefault(patient, 10) + result
            players = players.plus(patient to newHp)
            cli say "$actor heals $patient for $result."
        }
    }

    fun resurrectListener(cli: IrcClient, msg: IrcMessage) {
        val patient = target(msg)
        val actor = actor(msg)
        players = players.plus(patient to startHp)
        cli say "$actor brings $patient back from death!"
    }

    fun combatListener(cli: IrcClient, msg: IrcMessage) {
        if (!msg.rawMessage.contains(" : ACTION")) return

        actions.forEach { (k, v) ->
            if (k.containsMatchIn(msg.body)) {
                println("found $k in <<< ${msg.rawMessage} >>>")
                v.call(cli, msg)
                return@forEach
            }
        }
    }
}
