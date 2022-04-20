package se.battlegoo.battlegoose.models.heroes

class HeroSelection(heroes: Collection<Hero>) {

    private val heroesMap: Map<Int, Hero>

    var selected: Int = heroes.first().heroId
        set(value) {
            if (heroesMap.containsKey(value))
                field = value
            else
                throw IllegalArgumentException("Tried to select non-existent hero")
        }

    val selectedHero: Hero
        get() = heroesMap[selected]!!

    init {
        if (heroes.isEmpty())
            throw IllegalArgumentException("At least 1 hero must be provided!")

        val heroesTempMap = HashMap<Int, Hero>(heroes.size)
        heroes.forEach {
            heroesTempMap[it.heroId] = it
        }
        heroesMap = heroesTempMap.toMap()
    }
}
