package se.battlegoo.battlegoose.models.heroes

class HeroSelection(private val heroes : Array<Hero>) {
    var selected : Int = 0
    val heroCount : Int
        get() = heroes.size
    val selectedHero : Hero
        get() = heroes[selected]

    init {
        if (heroCount == 0)
            throw IllegalArgumentException("At least 1 hero must be provided!")
    }

    fun getHero(i: Int) = heroes[i]
}
