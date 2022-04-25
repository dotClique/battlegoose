package se.battlegoo.battlegoose.datamodels

sealed class ActionData : DataModel {

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

    data class CastSpell(
        override val playerID: String,
        val spell: SpellData,
        override val actionPointCost: Int = 1
    ) : ActionData()

    data class Pass(
        override val playerID: String
    ) : ActionData() {
        override val actionPointCost: Int = 0
    }

    data class Forfeit(
        override val playerID: String
    ) : ActionData() {
        override val actionPointCost: Int = 0
    }
}
