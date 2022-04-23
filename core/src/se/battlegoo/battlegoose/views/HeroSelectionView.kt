package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.models.units.UnitModel
import se.battlegoo.battlegoose.utils.TextureAsset
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

class HeroSelectionView(
    private val heroes: Collection<HeroSelectionViewModel>,
    private var selectedHeroId: String,
    val stage: Stage
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

    private val skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private val backgroundTexture = Game.getTexture(TextureAsset.MENU_BACKGROUND)
    private val heroCardViews: Map<String, HeroCardView>
    private var heroDetailsView: HeroDetailsView? = null

    private val backButton = TextButton("Back", skin)
    private val continueButton = TextButton("Continue", skin)

    init {
        // ###
        // # Calculate sizes, padding etc.
        // ###

        // Cards size
        val heroCount = heroes.size
        val cardWidthIncludingPadding =
            min(MAX_WINDOW_WIDTH / heroCount, MAX_HERO_WIDTH)
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
        val totalWidth = heroCount * cardWidthIncludingPadding
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
        var counter = 0
        val heroCardViewsMap = HashMap<String, HeroCardView>(heroCount)
        heroes.forEach {
            heroCardViewsMap[it.id] = HeroCardView(
                ScreenVector(
                    cardBaseline.x + (counter++ * cardWidthIncludingPadding) + cardPadding,
                    cardBaseline.y
                ),
                ScreenVector(
                    cardSize.x,
                    cardSize.y
                ),
                stage,
                HeroCardViewModel(it.id, it.name, it.description, it.heroSprite),
                it.id == selectedHeroId,
                this::onClickHeroCard,
                this::onClickHeroInfoOpen
            )
        }
        heroCardViews = heroCardViewsMap.toMap()

        backButton.label.setFontScale(FONT_BUTTON_SCALE)
        backButton.setPosition(buttonBaseline.x, buttonBaseline.y)
        backButton.setSize(buttonSize.x, buttonSize.y)

        continueButton.label.setFontScale(FONT_BUTTON_SCALE)
        continueButton.setPosition(Game.WIDTH - buttonBaseline.x - buttonSize.x, buttonBaseline.y)
        continueButton.setSize(buttonSize.x, buttonSize.y)

        stage.addActor(backButton)
        stage.addActor(continueButton)
    }

    fun selectHero(heroId: String) {
        this.selectedHeroId = heroId
        heroCardViews.forEach {
            it.value.selected = it.key == selectedHeroId
        }
    }

    fun showHeroDetails(heroId: String?) {
        val heroData = heroes.firstOrNull { it.id == heroId }
        heroData?.let {
            heroDetailsView = HeroDetailsView(
                HeroDetailsViewModel(
                    it.id, it.name, it.description, it.heroSprite,
                    it.army, it.spellName, it.spellDescription, it.spellCooldown
                ),
                stage = stage
            )
        } ?: run {
            heroDetailsView = null
        }
        heroDetailsView?.show()
    }

    override fun registerInput() {
        when {
            Gdx.input.justTouched() && backButton.isPressed ->
                onClickBack()
            Gdx.input.justTouched() && continueButton.isPressed ->
                onClickContinue()
            else -> heroCardViews.forEach { it.value.registerInput() }
        }
    }

    override fun render(sb: SpriteBatch) {
        // Draw the background
        sb.draw(backgroundTexture, 0f, 0f, Game.WIDTH, Game.HEIGHT)

        heroCardViews.forEach {
            it.value.render(sb)
        }
    }

    override fun dispose() {
        heroCardViews.forEach {
            it.value.dispose()
        }
        skin.dispose()
    }

    fun registerController(controller: IHeroSelectionViewController) {
        if (this.controller == null)
            this.controller = controller
        else
            Gdx.app.error("#PREREG", "Controller already registered")
    }

    private fun onClickHeroCard(heroId: String) {
        controller?.onClickHeroSelectionCard(heroId)
    }

    private fun onClickHeroInfoOpen(heroId: String) {
        controller?.onClickHeroSelectionInfoOpen(heroId)
    }

    private fun onClickBack() {
        controller?.onClickHeroSelectionBack()
    }

    private fun onClickContinue() {
        controller?.onClickHeroSelectionContinue()
    }
}

interface IHeroSelectionViewController {
    fun onClickHeroSelectionCard(heroId: String)
    fun onClickHeroSelectionInfoOpen(heroId: String)
    fun onClickHeroSelectionBack()
    fun onClickHeroSelectionContinue()
}

data class HeroSelectionViewModel(
    val id: String,
    val name: String,
    val description: String,
    val heroSprite: HeroSprite,
    val army: List<KClass<out UnitModel>>,
    val spellName: String,
    val spellDescription: String,
    val spellCooldown: Int
)
