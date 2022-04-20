package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import kotlin.math.max
import kotlin.math.min

class HeroSelectionView(
    heroSelection: HeroSelection
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

    private var controller: IHeroSelectionViewController? = null

    private val stage: Stage = Stage(Game.viewPort)
    private val skin: Skin = Skin(Gdx.files.internal("skins/star-soldier/star-soldier-ui.json"))

    private val backgroundTexture = Texture("menuBackground.jpg")
    private val heroCardViews: List<HeroCardView>
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

        val _cardWidth = cardWidthIncludingPadding - 2 * cardPadding
        val cardSize = ScreenVector(
            _cardWidth,
            min(_cardWidth / HeroCardView.CARD_ASPECT_RATIO, MAX_WINDOW_HEIGHT)
        )

        // Button size
        val buttonSize = ScreenVector(
            Game.WIDTH / 6f,
            Game.HEIGHT / 10f
        )

        // Cards position
        var _baselineVertical = (Game.HEIGHT / 2) - (cardSize.y / 2) + buttonSize.y
        _baselineVertical -=
            max((_baselineVertical + cardSize.y) - Game.HEIGHT * MAX_WINDOW_TOP, 0f)
        val totalWidth = heroSelection.heroCount * cardWidthIncludingPadding
        val cardBaseline = ScreenVector(
            (Game.WIDTH / 2) - (totalWidth / 2),
            _baselineVertical
        )

        // Button position
        val buttonBaseline = ScreenVector(
            Game.WIDTH / 2 - buttonSize.x,
            cardBaseline.y / 2 - buttonSize.y / 2
        )

        // Init the views
        heroCardViews = List(heroSelection.heroCount) { i ->
            HeroCardView(
                ScreenVector(
                    cardBaseline.x + (i * cardWidthIncludingPadding) + cardPadding,
                    cardBaseline.y
                ),
                ScreenVector(
                    cardSize.x,
                    cardSize.y
                ),
                stage,
                heroSelection,
                heroSelection.getHero(i),
                this::onClickHeroCard,
                this::onClickHeroInfoOpen
            )
        }

        backButton.label.setFontScale(FONT_BUTTON_SCALE)
        backButton.setPosition(buttonBaseline.x, buttonBaseline.y)
        backButton.setSize(buttonSize.x, buttonSize.y)

        continueButton.label.setFontScale(FONT_BUTTON_SCALE)
        continueButton.setPosition(Game.WIDTH - buttonBaseline.x - buttonSize.x, buttonBaseline.y)
        continueButton.setSize(buttonSize.x, buttonSize.y)

        Gdx.input.inputProcessor = stage
        stage.addActor(backButton)
        stage.addActor(continueButton)
    }

    fun showHeroDetails(hero: Hero?) {
        hero?.let {
            heroDetailsView = HeroDetailsView(
                ScreenVector(
                    (Game.WIDTH / 2) - (MAX_WINDOW_WIDTH / 2),
                    (Game.HEIGHT / 2) - (MAX_WINDOW_HEIGHT / 2),
                ),
                ScreenVector(
                    MAX_WINDOW_WIDTH,
                    MAX_WINDOW_HEIGHT,
                ),
                hero,
                this::onClickHeroInfoExit
            )
            Gdx.input.inputProcessor = null
        } ?: run {
            heroDetailsView?.dispose()
            heroDetailsView = null
            Gdx.input.inputProcessor = stage
        }
    }

    override fun registerInput() {
        when {
            heroDetailsView != null ->
                heroDetailsView?.registerInput()
            Gdx.input.justTouched() && backButton.isPressed ->
                onClickBack()
            Gdx.input.justTouched() && continueButton.isPressed ->
                onClickContinue()
            else -> heroCardViews.forEach { it.registerInput() }
        }
    }

    override fun render(sb: SpriteBatch) {
        // Draw the background
        sb.draw(backgroundTexture, 0f, 0f, Game.WIDTH, Game.HEIGHT)

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

    fun registerController(controller: IHeroSelectionViewController) {
        if (this.controller == null)
            this.controller = controller
        else
            Gdx.app.error("#PREREG", "Controller already registered")
    }

    private fun onClickHeroCard(hero: Hero) {
        controller?.onClickHeroSelectionCard(hero)
    }

    private fun onClickHeroInfoOpen(hero: Hero) {
        controller?.onClickHeroSelectionInfoOpen(hero)
    }

    private fun onClickHeroInfoExit() {
        controller?.onClickHeroSelectionInfoExit()
    }

    private fun onClickBack() {
        controller?.onClickHeroSelectionBack()
    }

    private fun onClickContinue() {
        controller?.onClickHeroSelectionContinue()
    }
}

interface IHeroSelectionViewController {
    fun onClickHeroSelectionCard(hero: Hero)
    fun onClickHeroSelectionInfoOpen(hero: Hero)
    fun onClickHeroSelectionInfoExit()
    fun onClickHeroSelectionBack()
    fun onClickHeroSelectionContinue()
}
