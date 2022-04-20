package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.Gdx
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.views.HeroSelectionView
import se.battlegoo.battlegoose.views.IHeroSelectionViewController

class HeroSelectionController(
    private val view: HeroSelectionView,
    private val heroSelection: HeroSelection,
    private val onClickBack: () -> Unit
) : ControllerBase(view), IHeroSelectionViewController {

    init {
        view.registerController(this)
    }

    override fun update(dt: Float) {
        view.registerInput()
    }

    override fun onClickHeroSelectionCard(hero: Hero) {
        heroSelection.selectHero(hero)
    }

    override fun onClickHeroSelectionInfoOpen(hero: Hero) {
        view.showHeroDetails(hero)
    }

    override fun onClickHeroSelectionInfoExit() {
        view.showHeroDetails(null)
    }

    override fun onClickHeroSelectionBack() {
        onClickBack()
    }

    override fun onClickHeroSelectionContinue() {
        Gdx.app.log(
            "#TODO",
            "Continue not implemented yet. " +
                "Currently selected: [${heroSelection.selectedHero.name}]"
        )
    }
}
