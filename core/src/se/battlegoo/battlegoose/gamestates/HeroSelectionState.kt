package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.controllers.HeroSelectionController
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.models.heroes.Hero

class HeroSelectionState(heroes : Array<Hero>) : GameState() {

    private val controller = HeroSelectionController(HeroSelection(heroes))

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
