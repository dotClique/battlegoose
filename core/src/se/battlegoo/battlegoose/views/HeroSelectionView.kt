package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import kotlin.math.min

class HeroSelectionView(heroSelection: HeroSelection) : ViewBase() {

    companion object {
        val BASELINE = 100f
        val MAX_WINDOW_WIDTH = Game.WIDTH * 0.9f
        val MARGIN = Game.WIDTH * 0.05f
        val PADDING = 20f
        val MAX_HERO_WIDTH = 500f
    }

    private val heroViews: Array<HeroView>
    private val backgroundTexture = Texture("menuBackground.jpg")

    init {
        val outerWidth = min(MAX_WINDOW_WIDTH / heroSelection.heroCount, MAX_HERO_WIDTH)
        val innerWidth = outerWidth - 2 * PADDING
        heroViews = Array(heroSelection.heroCount) { i ->
            HeroView(
                MARGIN + i * outerWidth + PADDING,
                BASELINE,
                innerWidth,
                heroSelection.getHero(i)
            )
        }
    }

    override fun render(sb: SpriteBatch) {
        // Draw the background
        sb.draw(backgroundTexture, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        for (view in heroViews)
            view.render(sb)
//        TODO("Not yet implemented")
    }

    override fun dispose() {
        backgroundTexture.dispose()
        for (view in heroViews)
            view.dispose()
//        TODO("Not yet implemented")
    }
}
