package se.battlegoo.battlegoose.datamodels

import se.battlegoo.battlegoose.GridVector
import se.battlegoo.battlegoose.models.spells.Spell

sealed class ActionData {

    val actionType: String = this::class.java.name

    abstract val actionPointCost: Int
    abstract val playerID: String

    data class MoveUnit(
        override val playerID: String,
        val fromPosition: GridVector,
        val toPosition: GridVector,
        override val actionPointCost: Int = 1
    ) : ActionData()

    data class AttackUnit(
        override val playerID: String,
        val attackerPosition: GridVector,
        val targetPosition: GridVector,
        override val actionPointCost: Int = 1
    ) : ActionData()

    data class CastSpell<T : Spell>(
        override val playerID: String,
        val spell: SpellData<T>,
        override val actionPointCost: Int = 1
    ) : ActionData()

    data class Pass(
        override val playerID: String,
        override val actionPointCost: Int
    ) : ActionData()

    data class Forfeit(
        override val playerID: String
    ) : ActionData() {
        override val actionPointCost: Int = 0
    }
}
