package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.views.screens.HeroSelectionView
import se.battlegoo.battlegoose.views.screens.IHeroSelectionViewController

class HeroSelectionController(
    private val view: HeroSelectionView,
    private val heroSelection: HeroSelection,
    private val onClickBack: () -> Unit,
    private val onClickContinue: () -> Unit
) : ControllerBase(view), IHeroSelectionViewController {

    init {
        view.registerController(this)
    }

    override fun update(dt: Float) {
        view.registerInput()
    }

    override fun onClickHeroSelectionCard(heroId: String) {
        heroSelection.selected = heroId
        view.selectHero(heroSelection.selected)
    }

    override fun onClickHeroSelectionInfoOpen(heroId: String) {
        view.showHeroDetails(heroId)
    }

    override fun onClickHeroSelectionBack() {
        onClickBack()
    }

    override fun onClickHeroSelectionContinue() {
        onClickContinue()
    }
}
