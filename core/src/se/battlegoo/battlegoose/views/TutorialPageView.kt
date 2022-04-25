package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.tutorial.TutorialSprite
import se.battlegoo.battlegoose.utils.fitScale
import se.battlegoo.battlegoose.views.utils.Fonts
import se.battlegoo.battlegoose.views.utils.Skins
import se.battlegoo.battlegoose.views.utils.ViewBase

class TutorialPageView(
    pos: ScreenVector,
    maxSize: ScreenVector,
    viewModel: TutorialPageViewModel
) : ViewBase() {

    companion object {
        private const val IMAGE_WIDTH = 0.8f
        private const val IMAGE_HEIGHT = 0.4f
        private const val IMAGE_MARGIN_ABOVE = 20f
        private const val TEXT_MARGIN_HORIZONTAL = 80f
        private const val TEXT_MARGIN_VERTICAL = 40f
        private const val TEXT_PADDING_INNER = 10f
        private const val TEXT_FONT_SCALE_HEADER = 2f
        private const val TEXT_FONT_SCALE = 1.5f
    }

    private val backgroundTexture: Texture = Texture("tutorial/tutorialPage.png")
    private val backgroundSprite: Sprite = Sprite(backgroundTexture)

    private val tutorialTexture: Texture = Texture(
        when (viewModel.sprite) {
            TutorialSprite.MAIN_MENU -> "tutorial/mainMenu.png"
            TutorialSprite.CREATE_LOBBY,
            TutorialSprite.JOIN_LOBBY,
            TutorialSprite.QUICK_JOIN,
            TutorialSprite.HERO_SELECT,
            TutorialSprite.BATTLE -> "tutorial/createLobby.png"
        }
    )
    private val tutorialSprite: Sprite = Sprite(tutorialTexture)

    private val mainSkin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private val labelStyleHeader = Label.LabelStyle(
        mainSkin.getFont(Fonts.STAR_SOLDIER.identifier), Color.RED
    )
    private val textSkin: Skin = Skin(Gdx.files.internal(Skins.PLAIN_JAMES.filepath))
    private val labelStyle: Label.LabelStyle = Label.LabelStyle(
        textSkin.getFont(Fonts.PLAIN_JAMES.identifier), Color.BLACK
    )
    private val headerText: Label = Label(viewModel.header, labelStyleHeader)
    private val tutorialTextMain: Label = Label(viewModel.text, labelStyle)
    private val tutorialTextExtra: Label = Label(viewModel.textExtra, labelStyle)
    private val textTable: Table = Table(mainSkin)

    init {
        val (backgroundSize, backgroundOffset) = fitScale(backgroundTexture, maxSize)
        backgroundSprite.setPosition(pos.x + backgroundOffset.x, pos.y + backgroundOffset.y)
        backgroundSprite.setSize(backgroundSize.x, backgroundSize.y)

        val imageMax = ScreenVector(
            backgroundSize.x * IMAGE_WIDTH,
            backgroundSize.y * IMAGE_HEIGHT
        )
        val (imageSize, imageOffset) =
            fitScale(tutorialTexture, ScreenVector(imageMax.x, imageMax.y))
        tutorialSprite.setPosition(
            backgroundSprite.x + backgroundSize.x * (1f - IMAGE_WIDTH) / 2f + imageOffset.x,
            backgroundSprite.y + backgroundSprite.height - imageMax.y + imageOffset.y -
                IMAGE_MARGIN_ABOVE
        )
        tutorialSprite.setSize(imageSize.x, imageSize.y)

        textTable.setPosition(
            backgroundSprite.x + TEXT_MARGIN_HORIZONTAL,
            backgroundSprite.y + TEXT_MARGIN_VERTICAL
        )
        textTable.setSize(
            backgroundSprite.width - 2 * TEXT_MARGIN_HORIZONTAL,
            tutorialSprite.y - textTable.y - TEXT_MARGIN_VERTICAL
        )
        textTable.left().top()

        headerText.wrap = true
        headerText.setFontScale(TEXT_FONT_SCALE_HEADER)

        tutorialTextMain.wrap = true
        tutorialTextMain.setFontScale(TEXT_FONT_SCALE)

        textTable.add(headerText).width(textTable.width).top().colspan(2)
        textTable.row()

        if (viewModel.textExtra == null) {
            textTable.defaults().width(textTable.width).top()
            textTable.add(tutorialTextMain)
        } else {
            tutorialTextExtra.wrap = true
            tutorialTextExtra.setFontScale(TEXT_FONT_SCALE)

            textTable.defaults().width(textTable.width / 2f).top()
            textTable.add(tutorialTextMain).padRight(TEXT_PADDING_INNER)
            textTable.add(tutorialTextExtra).padLeft(TEXT_PADDING_INNER)
        }
    }

    override fun render(sb: SpriteBatch) {
        backgroundSprite.draw(sb)
        tutorialSprite.draw(sb)
        textTable.draw(sb, 1f)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        tutorialTexture.dispose()
        mainSkin.dispose()
        textSkin.dispose()
    }
}

data class TutorialPageViewModel(
    val sprite: TutorialSprite,
    val header: String,
    val text: String,
    val textExtra: String? = null
)
