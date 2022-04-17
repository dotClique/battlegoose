package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.HeroSelectionController
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.views.HeroSelectionView

class HeroSelectionState(heroes: Array<Hero>) : GameState() {

    private val heroSelection = HeroSelection(heroes)
    private val heroSelectionView = HeroSelectionView(heroSelection) {
        Gdx.app.log("#INFO", "Selecting hero '${it.name}'")
        heroSelection.selectHero(it)
    }
    private val controller = HeroSelectionController(heroSelectionView, heroSelection)

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
