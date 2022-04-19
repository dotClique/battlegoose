package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.Gdx
import se.battlegoo.battlegoose.gamestates.GameStateManager
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.views.*

class HeroSelectionController(
    private val view: HeroSelectionView,
    private val heroSelection: HeroSelection
) : ControllerBase(
    view
), OnClickHeroSelectionCardListener, OnClickHeroSelectionInfoListener, OnClickHeroSelectionInfoExitListener, OnClickHeroSelectionBackListener, OnClickHeroSelectionContinueListener {

    init {
        view.registerOnClickHeroSelectionCardListener(this)
        view.registerOnClickHeroSelectionInfoListener(this)
        view.registerOnClickHeroSelectionInfoExitListener(this)
        view.registerOnClickHeroSelectionBackListener(this)
        view.registerOnClickHeroSelectionContinueListener(this)
    }

    override fun update(dt: Float) {
        view.registerInput()
    }

    override fun onClickHeroSelectionCard(hero: Hero) {
        heroSelection.selectHero(hero)
    }

    override fun onClickHeroSelectionInfo(hero: Hero) {
        view.showHeroDetails(hero)
    }

    override fun onClickHeroSelectionInfoExit() {
        view.showHeroDetails(null)
    }

    override fun onClickHeroSelectionBack() {
        view.dispose()
        GameStateManager.goBack()
    }

    override fun onClickHeroSelectionContinue() {
        Gdx.app.log("#TODO", "Continue not implemented yet. " +
            "Currently selected: [${heroSelection.selectedHero.name}]")
    }
}
