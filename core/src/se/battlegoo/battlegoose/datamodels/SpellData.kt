package se.battlegoo.battlegoose.datamodels

sealed class SpellData : DataModel {

    val spellType: String = this::class.java.name

    object AdrenalineShot : SpellData()
    object Bird52 : SpellData()
    data class EphemeralAllegiance(val targetPosition: GridVector) : SpellData()
}
