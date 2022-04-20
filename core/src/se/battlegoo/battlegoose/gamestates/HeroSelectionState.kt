package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.HeroSelectionController
import se.battlegoo.battlegoose.models.heroes.AdmiralAlbatross
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.models.heroes.MajorMallard
import se.battlegoo.battlegoose.models.heroes.SergeantSwan
import se.battlegoo.battlegoose.views.HeroSelectionView

class HeroSelectionState : GameState() {

    private val heroes: List<Hero> = listOf(SergeantSwan(), MajorMallard(), AdmiralAlbatross())
    private val heroSelection = HeroSelection(heroes)
    private val heroSelectionView = HeroSelectionView(heroSelection)
    private val controller = HeroSelectionController(heroSelectionView, heroSelection, this::goBack)

    private fun goBack() {
        GameStateManager.goBack()
    }

    override fun update(dt: Float) {
        controller.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        controller.render(sb)
    }

    override fun dispose() {
        controller.dispose()
    }
}
