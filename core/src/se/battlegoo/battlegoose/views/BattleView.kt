package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.utils.Modal
import se.battlegoo.battlegoose.utils.ModalType

interface BattleViewObserver {
    fun onCastSpell()
    fun onPass()
    fun onForfeit()
}

class BattleView(
    background: BattleMapBackground,
    val position: ScreenVector,
    val maxSize: ScreenVector,
    val hero: Hero,
    enemyHero: Hero,
    val stage: Stage
) : ViewBase() {

    companion object {
        // Relative values in percentage (0f - 1f) to place elements atop the background
        private const val HERO_ICON_WIDTH_RATIO = 0.16f // used to determine height to
        private const val HERO_ICON_MARGIN_UP_LEFT_RATIO = 0.04f // based on Height

        private const val INFO_BACKGROUND_WIDTH_RATIO = 0.205f
        private const val INFO_BACKGROUND_HEIGHT_RATIO = 1f

        private const val STANDARD_BUTTON_WIDTH_RATIO = 0.14f
        private const val STANDARD_BUTTON_MARGIN_RATIO = 0.025f

        private const val PREF_MODAL_HEIGHT = Game.HEIGHT * 0.6f
        private const val PREF_MODAL_WIDTH = Game.WIDTH * 0.4f
    }

    private val heroIconWidth = HERO_ICON_WIDTH_RATIO * maxSize.y
    private val heroIconMarginUpLeft = HERO_ICON_MARGIN_UP_LEFT_RATIO * maxSize.y
    private val infoBackgroundWidth = INFO_BACKGROUND_WIDTH_RATIO * maxSize.x
    private val infoBackgroundHeight = INFO_BACKGROUND_HEIGHT_RATIO * maxSize.y

    private val standardButtonWidth = STANDARD_BUTTON_WIDTH_RATIO * maxSize.x
    private val standardButtonMargin = STANDARD_BUTTON_MARGIN_RATIO * maxSize.y

    private val backgroundTexture = Texture(
        when (background) {
            BattleMapBackground.DUNES -> "maps/sidebar/beachTowelCropped.png"
            BattleMapBackground.DIRT_ROAD -> "maps/sidebar/grassPatchCropped.png"
            BattleMapBackground.ICE_RINK -> "maps/sidebar/snowPatchCropped.png"
        }
    )
    private val backgroundSprite = Sprite(backgroundTexture)

    private val skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))

    private val turnTimerLabel: Label = Label("", skin).also {
        it.setPosition(
            position.x + heroIconMarginUpLeft,
            position.y + maxSize.y - heroIconWidth - heroIconMarginUpLeft * 2
        )
        it.setScale(2f)
        it.color = Color.BLACK
        stage.addActor(it)
    }
    var turnTimer: Int? = null
        set(value) {
            field = value
            turnTimerLabel.setText(turnTimer?.toString() ?: "")
        }

    private val heroIconView = HeroIconView(
        ScreenVector(
            position.x + heroIconMarginUpLeft,
            position.y + maxSize.y - heroIconWidth - heroIconMarginUpLeft
        ),
        ScreenVector(
            heroIconWidth,
            heroIconWidth
        ),
        hero.heroSprite
    )

    private val enemyIconView = HeroIconView(
        ScreenVector(
            position.x + maxSize.x - heroIconWidth - heroIconMarginUpLeft,
            position.y + maxSize.y - heroIconWidth - heroIconMarginUpLeft
        ),
        ScreenVector(
            heroIconWidth,
            heroIconWidth
        ),
        enemyHero.heroSprite
    )

    private val spellButtonView: ButtonView = ButtonView(
        "spellBtn.png",
        position.x + (infoBackgroundWidth - standardButtonWidth) / 2,
        position.y + standardButtonMargin * 5,
        standardButtonWidth.toInt(), ::onClickSpellButton
    )

    private val endTurnButtonView: ButtonView = ButtonView(
        "endTurnBtn.png",
        position.x + (infoBackgroundWidth - standardButtonWidth) / 2,
        position.y + standardButtonMargin,
        standardButtonWidth.toInt(), ::onClickPassButton
    )

    private val surrenderButtonView: ButtonView = ButtonView(
        "surrenderBtn.png",
        position.x + maxSize.x - standardButtonWidth -
            (infoBackgroundWidth - standardButtonWidth) / 2,
        position.y + standardButtonMargin,
        standardButtonWidth.toInt(), ::onClickSurrenderButton
    )

    private val mainSkin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private val textTable: Table = Table(mainSkin)

    private lateinit var spellModal: Modal
    private lateinit var surrenderModal: Modal

    private var observer: BattleViewObserver? = null

    var yourTurn: Boolean = false
        set(yourTurn) {
            heroIconView.showMyTurn = yourTurn
            enemyIconView.showMyTurn = !yourTurn
            field = yourTurn
        }

    init {
        backgroundSprite.setPosition(position.x, position.y)
        backgroundSprite.setSize(infoBackgroundWidth, infoBackgroundHeight)
    }

    fun subscribe(observer: BattleViewObserver) {
        this.observer = observer
    }

    private fun newSpellModal(): Modal {
        return Modal(
            hero.spell.title,
            "${hero.spell.description}\nCooldown: ${hero.spell.cooldown}",
            // TODO: Fix auto linebreak
            ModalType.Question(
                onYes = { observer?.onCastSpell() }
            ),
            stage,
            contentActors = listOf(textTable),
            prefHeight = PREF_MODAL_HEIGHT,
            prefWidth = PREF_MODAL_WIDTH
        )
    }

    private fun newSurrenderModal(): Modal {
        return Modal(
            "Surrender",
            "Are you sure you want to surrender?",
            ModalType.Question(
                onYes = { observer?.onForfeit() }
            ),
            stage,
            contentActors = listOf(textTable),
            prefHeight = PREF_MODAL_HEIGHT,
            prefWidth = PREF_MODAL_WIDTH
        )
    }

    private fun onClickSpellButton() {
        if (yourTurn) {
            spellModal = newSpellModal()
            spellModal.show()
        }
    }

    private fun onClickPassButton() {
        if (yourTurn) {
            observer?.onPass()
        }
    }

    private fun onClickSurrenderButton() {
        if (yourTurn) {
            surrenderModal = newSurrenderModal()
            surrenderModal.show()
        }
    }

    override fun registerInput() {
        spellButtonView.registerInput()
        surrenderButtonView.registerInput()
        endTurnButtonView.registerInput()
    }

    override fun render(sb: SpriteBatch) {
        backgroundSprite.draw(sb)
        heroIconView.render(sb)
        enemyIconView.render(sb)
        spellButtonView.render(sb)
        endTurnButtonView.render(sb)
        surrenderButtonView.render(sb)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroIconView.dispose()
        enemyIconView.dispose()
        spellButtonView.dispose()
        endTurnButtonView.dispose()
        surrenderButtonView.dispose()
    }
}
