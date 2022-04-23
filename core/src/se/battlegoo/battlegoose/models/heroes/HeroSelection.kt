package se.battlegoo.battlegoose.models.heroes

class HeroSelection(heroes: Collection<Hero<*>>) {

    private val heroesMap: Map<String, Hero<*>>

    var selected: String = heroes.first()::class.java.name
        set(value) {
            if (heroesMap.containsKey(value))
                field = value
            else
                throw IllegalArgumentException("Tried to select non-existent hero")
        }

    val selectedHero: Hero<*>
        get() = heroesMap[selected]!!

    init {
        if (heroes.isEmpty())
            throw IllegalArgumentException("At least 1 hero must be provided!")

        heroesMap = heroes.associateBy { it::class.java.name }
    }
}
