package se.battlegoo.battlegoose.datamodels

sealed class SpellData : DataModel {

    val spellType: String = this::class.java.name

    object AdrenalineShot : SpellData()
}
