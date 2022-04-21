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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.utilities.fitScale

class HeroCardView(
    position: ScreenVector,
    maxSize: ScreenVector,
    private val parentStage: Stage,
    private val heroCardViewModel: HeroCardViewModel,
    var selected: Boolean,
    private val onClickCard: (heroId: String) -> Unit,
    private val onClickInfo: (heroId: String) -> Unit
) : ViewBase() {

    companion object {
        // The WIDTH / HEIGHT ratio
        const val CARD_ASPECT_RATIO = 0.58f

        // Relative values in percentage (0f - 1f) to place elements on the background
        private const val CARD_IMAGE_MARGIN_LEFT = 0.07f
        private const val CARD_IMAGE_MARGIN_DOWN = 0.39f
        private const val CARD_IMAGE_WIDTH = 0.85f
        private const val CARD_IMAGE_HEIGHT = 0.55f
        private const val CARD_TEXT_LINE_HEIGHT = 0.04f
        private const val CARD_TEXT_BOX_HEIGHT = 0.38f

        // Font scale multiplier (multiplied with lineHeight)
        private const val FONT_SCALE_MULTIPLIER = 0.033f

        // Specific multipliers
        private const val FONT_HEADER_SCALE = 2f // Used for hero name
        private const val FONT_BUTTON_SCALE = 2f // Used for info button
        private const val BUTTON_HEIGHT_MULTIPLIER = 3f // Used for info button

        // Color selection
        private val COLOR_FONT_HEADER = Color.BLACK
        private val COLOR_CARD_DEFAULT = Color.WHITE
        private val COLOR_CARD_HIGHLIGHTED = Color.ORANGE
    }

    private val stage: Stage = parentStage

    private val backgroundTexture: Texture = Texture("heroSelection/heroCard.png")
    private val heroTexture: Texture = Texture(
        when (heroCardViewModel.heroSprite) {
            HeroSprite.SERGEANT_SWAN -> "heroes/sergeantSwan.png"
            HeroSprite.MAJOR_MALLARD -> "heroes/majorMallard.png"
            HeroSprite.ADMIRAL_ALBATROSS -> "heroes/admiralAlbatross.png"
        }
    )

    private val backgroundSprite: Sprite = Sprite(backgroundTexture)
    private val heroSprite: Sprite = Sprite(heroTexture)

    private val mainSkin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    private val headerLabelStyle: Label.LabelStyle = Label.LabelStyle(
        mainSkin.getFont(Fonts.STAR_SOLDIER.identifier), Color.BLACK
    )
    private val textSkin: Skin = Skin(Gdx.files.internal(Skins.PLAIN_JAMES.filepath))
    private val bodyLabelStyle: Label.LabelStyle = Label.LabelStyle(
        textSkin.getFont(Fonts.PLAIN_JAMES.identifier), Color.BLACK
    )

    private val textTable: Table = Table(mainSkin)
    private val nameLabel: Label = Label(heroCardViewModel.name, headerLabelStyle)
    private val descriptionLabel: Label = Label(heroCardViewModel.description, bodyLabelStyle)

    private val infoButton: TextButton = TextButton("Info", mainSkin)

    init {
        val (backgroundSize, backgroundOffset) = fitScale(backgroundTexture, maxSize)
        backgroundSprite.setPosition(
            position.x + backgroundOffset.x,
            position.y + backgroundOffset.y
        )
        backgroundSprite.setSize(backgroundSize.x, backgroundSize.y)

        // Define position and size based on the background-image
        val heroBaseline = ScreenVector(
            position.x + backgroundOffset.x + backgroundSize.x * CARD_IMAGE_MARGIN_LEFT,
            position.y + backgroundOffset.y + backgroundSize.y * CARD_IMAGE_MARGIN_DOWN
        )
        val heroImage = ScreenVector(
            backgroundSize.x * CARD_IMAGE_WIDTH,
            backgroundSize.y * CARD_IMAGE_HEIGHT
        )

        val (heroSize, heroOffset) = fitScale(heroTexture, heroImage)
        heroSprite.setPosition(heroBaseline.x + heroOffset.x, heroBaseline.y)
        heroSprite.setSize(heroSize.x, heroSize.y)

        // Define size-values for text
        val lineHeight = backgroundSize.y * CARD_TEXT_LINE_HEIGHT
        val textHeight = backgroundSize.y * CARD_TEXT_BOX_HEIGHT - (lineHeight / 2)
        val fontScale = lineHeight * FONT_SCALE_MULTIPLIER

        textTable.setSize(heroImage.x, textHeight)
        textTable.setPosition(heroBaseline.x, heroBaseline.y - textHeight - lineHeight)
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(fontScale * FONT_HEADER_SCALE)
        nameLabel.color = COLOR_FONT_HEADER
        nameLabel.wrap = true

        descriptionLabel.setFontScale(fontScale)
        descriptionLabel.wrap = true

        // Add and position all text elements
        textTable.row().width(heroImage.x)
        textTable.add(nameLabel)
        textTable.row().width(heroImage.x)
        textTable.add(descriptionLabel)
        textTable.row().width(heroImage.x).spaceTop(lineHeight / 2)

        infoButton.label.setFontScale(fontScale * FONT_BUTTON_SCALE)
        infoButton.setSize(heroImage.x, lineHeight * BUTTON_HEIGHT_MULTIPLIER)
        infoButton.setPosition(heroBaseline.x, heroBaseline.y - textHeight)

        stage.addActor(infoButton)
    }

    private fun cardIsPressed(): Boolean {
        return backgroundSprite.boundingRectangle.contains(
            Game.unproject(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
        )
    }

    override fun registerInput() {
        if (Gdx.input.justTouched()) {
            if (infoButton.isPressed) {
                onClickInfo(heroCardViewModel.id)
            } else if (cardIsPressed()) {
                onClickCard(heroCardViewModel.id)
            }
        }
    }

    override fun render(sb: SpriteBatch) {
        backgroundSprite.color =
            if (selected) COLOR_CARD_HIGHLIGHTED else COLOR_CARD_DEFAULT
        backgroundSprite.draw(sb)
        heroSprite.draw(sb)
        textTable.draw(sb, 1f)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroTexture.dispose()
        mainSkin.dispose()
        textSkin.dispose()
    }
}

data class HeroCardViewModel(
    val id: String,
    val name: String,
    val description: String,
    val heroSprite: HeroSprite
)
