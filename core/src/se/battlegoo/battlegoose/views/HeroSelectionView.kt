package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.GameStateManager
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import kotlin.math.max
import kotlin.math.min

class HeroSelectionView(
    heroSelection: HeroSelection,
    onClickHeroCard: (hero: Hero) -> Unit
) : ViewBase() {

    companion object {
        private const val MAX_WINDOW_WIDTH = Game.WIDTH * 0.90f
        private const val MAX_WINDOW_HEIGHT = Game.HEIGHT * 0.90f
        private const val MAX_WINDOW_TOP = Game.HEIGHT * 0.95f
        private const val MIN_PADDING = 5f
        private const val MAX_HERO_WIDTH = 500f

        private const val CARD_PADDING_BETWEEN = 0.04f
        private const val FONT_BUTTON_SCALE = 2f
    }

    private val stage: Stage = Stage(Game.viewPort)
    private val skin: Skin = Skin(Gdx.files.internal("skins/star-soldier/star-soldier-ui.json"))

    private val backgroundTexture = Texture("menuBackground.jpg")
    private val heroCardViews: Array<HeroCardView>
    private var heroDetailsView: HeroDetailsView? = null

    private val backButton = TextButton("Back", skin)
    private val continueButton = TextButton("Continue", skin)

    init {
        // ###
        // # Calculate sizes, padding etc.
        // ###

        // Cards size
        val cardWidthIncludingPadding =
            min(MAX_WINDOW_WIDTH / heroSelection.heroCount, MAX_HERO_WIDTH)
        val cardPadding = max(CARD_PADDING_BETWEEN * cardWidthIncludingPadding, MIN_PADDING)
        val cardWidth = cardWidthIncludingPadding - 2 * cardPadding
        val cardHeight = min(cardWidth / HeroCardView.CARD_ASPECT_RATIO, MAX_WINDOW_HEIGHT)

        // Button size
        val buttonWidth = Game.WIDTH / 6f
        val buttonsHeight = Game.HEIGHT / 10f

        // Cards position
        var baselineVertical = (Game.HEIGHT / 2) - (cardHeight / 2) + buttonsHeight
        baselineVertical -=
            max((baselineVertical + cardHeight) - Game.HEIGHT * MAX_WINDOW_TOP, 0f)
        val totalWidth = heroSelection.heroCount * cardWidthIncludingPadding
        val baselineHorizontal = (Game.WIDTH / 2) - (totalWidth / 2)

        // Button position
        val buttonBaselineX = Game.WIDTH / 2 - buttonWidth
        val buttonsBaselineY = baselineVertical / 2 - buttonsHeight / 2

        // Init the views
        heroCardViews = Array(heroSelection.heroCount) { i ->
            HeroCardView(
                baselineHorizontal + (i * cardWidthIncludingPadding) + cardPadding,
                baselineVertical,
                cardWidth,
                cardHeight,
                stage,
                heroSelection,
                heroSelection.getHero(i),
                onClickHeroCard
            ) {
                // TODO: Move this!
                heroDetailsView = HeroDetailsView(
                    (Game.WIDTH / 2) - (MAX_WINDOW_WIDTH / 2),
                    (Game.HEIGHT / 2) - (MAX_WINDOW_HEIGHT / 2),
                    MAX_WINDOW_WIDTH,
                    MAX_WINDOW_HEIGHT,
                    it,
                    this::exitDetailsView
                )
                Gdx.input.inputProcessor = null
            }
        }

        backButton.label.setFontScale(FONT_BUTTON_SCALE)
        backButton.setPosition(buttonBaselineX, buttonsBaselineY)
        backButton.setSize(buttonWidth, buttonsHeight)

        continueButton.label.setFontScale(FONT_BUTTON_SCALE)
        continueButton.setPosition(Game.WIDTH - buttonBaselineX - buttonWidth, buttonsBaselineY)
        continueButton.setSize(buttonWidth, buttonsHeight)

        Gdx.input.inputProcessor = stage
        stage.addActor(backButton)
        stage.addActor(continueButton)
    }

    private fun exitDetailsView() {
        heroDetailsView?.dispose()
        heroDetailsView = null
        Gdx.input.inputProcessor = stage
    }

    override fun registerInput() {
        when {
            heroDetailsView != null ->
                heroDetailsView?.registerInput()
            Gdx.input.justTouched() && backButton.isPressed ->
                GameStateManager.goBack()
            Gdx.input.justTouched() && continueButton.isPressed ->
                // TODO: Implement next step
                Gdx.app.log("#INFO", "Should be proceeding!")
            else -> {
                for (heroCardView in heroCardViews)
                    heroCardView.registerInput()
            }
        }
    }

    override fun render(sb: SpriteBatch) {
        // Draw the background
        sb.draw(backgroundTexture, 0f, 0f, Game.WIDTH.toFloat(), Game.HEIGHT.toFloat())

        stage.draw()
        for (view in heroCardViews)
            view.render(sb)
        heroDetailsView?.render(sb)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        for (view in heroCardViews)
            view.dispose()
        heroDetailsView?.dispose()
        skin.dispose()
    }
}
