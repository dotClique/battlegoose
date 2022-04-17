package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import kotlin.math.max
import kotlin.math.min

class HeroSelectionView(
    heroSelection: HeroSelection,
    onClickHeroCard: (hero: Hero) -> Unit
) : ViewBase() {

    companion object {
        const val MAX_WINDOW_WIDTH = Game.WIDTH * 0.9f
        const val MIN_PADDING = 5f
        const val MAX_HERO_WIDTH = 500f
    }

    private val backgroundTexture = Texture("menuBackground.jpg")
    private val heroCardViews: Array<HeroCardView>

    init {
        // Calculate sizes, padding etc.
        val cardWidthIncludingPadding = min(MAX_WINDOW_WIDTH / heroSelection.heroCount, MAX_HERO_WIDTH)
        val cardPadding = max(0.04f * cardWidthIncludingPadding, MIN_PADDING)
        val cardWidth = cardWidthIncludingPadding - 2 * cardPadding
        val cardHeight = 2f * cardWidth
        val baselineVertical = (Game.HEIGHT / 2) - (cardHeight / 2)
        val totalWidth = heroSelection.heroCount * cardWidthIncludingPadding
        val baselineHorizontal = (Game.WIDTH / 2) - (totalWidth / 2)

        // Init the views
        heroCardViews = Array(heroSelection.heroCount) { i ->
            HeroCardView(
                baselineHorizontal + (i * cardWidthIncludingPadding) + cardPadding,
                baselineVertical,
                cardWidth,
                cardHeight,
                heroSelection,
                heroSelection.getHero(i),
                onClickHeroCard
            )
        }
    }

    override fun registerInput() {
        for (heroCardView in heroCardViews)
            heroCardView.registerInput()
    }

    override fun render(sb: SpriteBatch) {
        // Draw the background
        sb.draw(backgroundTexture, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        for (view in heroCardViews)
            view.render(sb)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        for (view in heroCardViews)
            view.dispose()
    }
}
