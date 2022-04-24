package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.tutorial.TutorialSprite

class TutorialView(val stage: Stage) : ViewBase() {

    companion object {
        private const val BUTTON_WIDTH = 250f
        private const val BUTTON_HEIGHT = 200f
        private const val BUTTON_MARGIN = 20f
    }

    private val maxSize: ScreenVector = ScreenVector(Game.WIDTH * 0.8f, Game.HEIGHT * 0.9f)
    private val pos: ScreenVector = ScreenVector(
        Game.WIDTH / 2f - maxSize.x / 2f,
        Game.HEIGHT / 2f - maxSize.y / 2f
    )
    private var pageView: TutorialPageView? = null
        private set(value) {
            field?.dispose()
            field = value
        }
    private lateinit var controller: ITutorialViewController

    private val backgroundTexture: Texture = Texture("menuBackground.jpg")

    private val mainSkin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private val backButton = TextButton("Back", mainSkin)
    private val nextButton = TextButton("Next", mainSkin)
    private val closeButton = TextButton("Close", mainSkin)

    init {
        backButton.width = BUTTON_WIDTH
        backButton.height = BUTTON_HEIGHT
        nextButton.width = BUTTON_WIDTH
        nextButton.height = BUTTON_HEIGHT
        closeButton.width = BUTTON_WIDTH
        closeButton.height = BUTTON_HEIGHT

        backButton.setPosition(BUTTON_MARGIN, Game.HEIGHT / 2f - backButton.height / 2f)
        nextButton.setPosition(
            Game.WIDTH - nextButton.width - BUTTON_MARGIN,
            Game.HEIGHT / 2f - backButton.height / 2f
        )
        closeButton.setPosition(nextButton.x, pos.y + maxSize.y - BUTTON_HEIGHT)

        stage.addActor(backButton)
        stage.addActor(nextButton)
        stage.addActor(closeButton)
    }

    fun showTutorialPage(viewModel: TutorialViewModel) {
        pageView = TutorialPageView(
            pos, maxSize,
            TutorialPageViewModel(
                viewModel.tutorialSprite, viewModel.headerText,
                viewModel.tutorialText, viewModel.extraText
            )
        )
        backButton.isVisible = !viewModel.first
        nextButton.isVisible = !viewModel.last
    }

    override fun registerInput() {
        when {
            Gdx.input.justTouched() && backButton.isPressed -> controller.onClickBack()
            Gdx.input.justTouched() && nextButton.isPressed -> controller.onClickForward()
            Gdx.input.justTouched() && closeButton.isPressed -> controller.onClickClose()
        }
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(backgroundTexture, 0f, 0f, Game.WIDTH, Game.HEIGHT)
        pageView?.render(sb)
    }

    override fun dispose() {
        pageView?.dispose()
        backgroundTexture.dispose()
        mainSkin.dispose()
    }

    fun registerController(controller: ITutorialViewController) {
        if (!this::controller.isInitialized)
            this.controller = controller
        else
            Gdx.app.error("#PREREG", "Controller already registered")
    }
}

interface ITutorialViewController {
    fun onClickBack()
    fun onClickForward()
    fun onClickClose()
}

data class TutorialViewModel(
    val tutorialSprite: TutorialSprite,
    val headerText: String,
    val tutorialText: String,
    val extraText: String?,
    val first: Boolean,
    val last: Boolean
)
