package se.battlegoo.battlegoose.models.heroes

class HeroSelection(private val heroes: List<Hero>) {
    var selected: Int = 0
    val heroCount: Int by heroes::size
    val selectedHero: Hero
        get() = heroes[selected]

    init {
        if (heroCount == 0)
            throw IllegalArgumentException("At least 1 hero must be provided!")
    }

    fun getHero(i: Int) = heroes[i]

    fun selectHero(hero: Hero) {
        val new = heroes.indexOf(hero)
        if (new < 0)
            throw IllegalArgumentException("Hero not found!")
        selected = new
    }
}
