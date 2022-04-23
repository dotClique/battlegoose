package se.battlegoo.battlegoose.models

import se.battlegoo.battlegoose.datamodels.ActionData
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.spells.ActiveSpell

class Battle(
    val hero1: Hero<*>,
    val hero2: Hero<*>,
    val battleMap: BattleMap,
    val battleId: String,
    val isHost: Boolean
) {

    var yourTurn: Boolean = false
    var turnNumber: Long = 0L
        private set
    var actions: MutableList<ActionData> = mutableListOf()
        private set
    var activeSpells: Pair<MutableList<ActiveSpell<*>>, MutableList<ActiveSpell<*>>> = Pair(
        mutableListOf(),
        mutableListOf()
    )
        private set

    fun nextTurn() {
        turnNumber++
        (if (yourTurn) hero1 else hero2).nextTurn()
        yourTurn = !yourTurn
    }

    fun getCurrentOutcome(): BattleOutcome? {
        var friendlyUnitsRemaining = false
        var opposingUnitsRemaining = false
        for (unit in battleMap.getUnits()) {
            if (friendlyUnitsRemaining && opposingUnitsRemaining) break
            when (unit.owner) {
                hero1 -> friendlyUnitsRemaining = true
                hero2 -> opposingUnitsRemaining = true
            }
        }
        return when {
            friendlyUnitsRemaining && opposingUnitsRemaining -> null
            friendlyUnitsRemaining -> BattleOutcome.VICTORY
            opposingUnitsRemaining -> BattleOutcome.DEFEAT
            else -> BattleOutcome.TIE
        }
    }
}

enum class BattleOutcome(val scoreChange: Long) {
    VICTORY(1),
    TIE(0),
    DEFEAT(-1)
}
