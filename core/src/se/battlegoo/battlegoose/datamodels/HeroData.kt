package se.battlegoo.battlegoose.datamodels

import se.battlegoo.battlegoose.models.heroes.AdmiralAlbatross
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.MajorMallard
import se.battlegoo.battlegoose.models.heroes.SergeantSwan

class HeroData<T : Hero>(hero: T) : DataModel {
    val heroType: String = hero::class.java.name

    fun toHero(): Hero {
        return when (heroType) {
            SergeantSwan::class.java.name -> SergeantSwan()
            MajorMallard::class.java.name -> MajorMallard()
            AdmiralAlbatross::class.java.name -> AdmiralAlbatross()
            else ->
                throw NotImplementedError("$heroType not implemented in HeroData")
        }
    }
}
