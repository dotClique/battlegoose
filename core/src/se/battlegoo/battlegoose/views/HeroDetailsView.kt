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
import se.battlegoo.battlegoose.ScreenVector
import se.battlegoo.battlegoose.models.heroes.HeroSprite
import se.battlegoo.battlegoose.utilities.fitScale

class HeroDetailsView(
    position: ScreenVector,
    maxSize: ScreenVector,
    heroDetailsViewModel: HeroDetailsViewModel,
    private val onExit: () -> Unit
) : ViewBase() {

    companion object {
        // Relative values in percentage (0f - 1f) to place elements on the background
        private const val CARD_IMAGE_MARGIN_LEFT = 0.18f
        private const val CARD_IMAGE_MARGIN_DOWN = 0.65f
        private const val CARD_IMAGE_WIDTH = 0.64f
        private const val CARD_IMAGE_HEIGHT = 0.30f
        private const val CARD_TEXT_LINE_HEIGHT = 0.04f
        private const val CARD_TEXT_BOX_WIDTH = 0.85f
        private const val CARD_TEXT_BOX_HEIGHT = 0.60f

        // Font scale multipliers
        private const val FONT_SCALE_MULTIPLIER = 0.033f // Multiplied with lineHeight
        private const val FONT_MAIN_SCALE = 2f // Used for scaling main font
        private const val FONT_BODY_SCALE = 1.2f // Used for scaling body text font

        // Specific multipliers
        private const val FONT_MAIN_SPELL_HEADER_SCALE = 0.8f
        private const val FONT_BODY_SPELL_NAME_SCALE = 1.2f
        private const val TABLE_COLUMN_SPELL_TITLE_WIDTH = 0.3f

        // Color selection
        private val COLOR_FONT_HEADER = Color.BLACK
    }

    private val stage: Stage = Stage(Game.viewPort)

    private val backgroundTexture: Texture = Texture("heroSelection/heroDetails.png")
    private val heroTexture: Texture = Texture(
        when (heroDetailsViewModel.heroSprite) {
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
    private val nameLabel = Label(heroDetailsViewModel.name, headerLabelStyle)
    private val descriptionLabel = Label(heroDetailsViewModel.description, bodyLabelStyle)
    private val spellHeaderLabel = Label("Spell:", headerLabelStyle)
    private val spellNameLabel = Label(heroDetailsViewModel.spellName, bodyLabelStyle)
    private val spellDescriptionLabel = Label(
        "${heroDetailsViewModel.spellDescription}\n" +
            "${heroDetailsViewModel.spellCooldown} turns cooldown.",
        bodyLabelStyle
    )

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
        val textBoxSize = ScreenVector(
            backgroundSize.x * CARD_TEXT_BOX_WIDTH,
            backgroundSize.y * CARD_TEXT_BOX_HEIGHT - lineHeight
        )
        // Use heroImageWidth to find midline, then use textWidth to find left starting point
        val textBaselineX = heroBaseline.x + (heroImage.x / 2) - (textBoxSize.x / 2)

        val fontScale = lineHeight * FONT_SCALE_MULTIPLIER
        val headerFontScale = fontScale * FONT_MAIN_SCALE
        val bodyFontScale = fontScale * FONT_BODY_SCALE

        textTable.setSize(textBoxSize.x, textBoxSize.y)
        textTable.setPosition(textBaselineX, heroBaseline.y - textBoxSize.y - (lineHeight / 2))
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(headerFontScale)
        nameLabel.color = COLOR_FONT_HEADER
        nameLabel.wrap = true

        descriptionLabel.setFontScale(bodyFontScale)
        descriptionLabel.wrap = true

        spellHeaderLabel.setFontScale(headerFontScale * FONT_MAIN_SPELL_HEADER_SCALE)
        spellHeaderLabel.color = COLOR_FONT_HEADER
        spellHeaderLabel.wrap = true

        spellNameLabel.setFontScale(bodyFontScale * FONT_BODY_SPELL_NAME_SCALE)
        spellNameLabel.wrap = true

        spellDescriptionLabel.setFontScale(bodyFontScale)
        spellDescriptionLabel.wrap = true

        // Set default values for table cells
        textTable.defaults().fill().left().colspan(2)
        // Add and position all text elements
        textTable.add(nameLabel).width(textBoxSize.x)
        textTable.row()
        textTable.add(descriptionLabel).width(textBoxSize.x)
        textTable.row().spaceTop(lineHeight / 2).colspan(1)
        textTable.add(spellHeaderLabel).width(textBoxSize.x * TABLE_COLUMN_SPELL_TITLE_WIDTH)
        textTable.add(spellNameLabel).expandX()
        textTable.row().width(textBoxSize.x)
        textTable.add(spellDescriptionLabel)

        stage.addActor(textTable)
    }

    override fun registerInput() {
        if (Gdx.input.justTouched()) {
            onExit()
        }
    }

    override fun render(sb: SpriteBatch) {
        backgroundSprite.draw(sb)
        heroSprite.draw(sb)
        stage.draw()
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroTexture.dispose()
        mainSkin.dispose()
        textSkin.dispose()
        stage.dispose()
    }
}

data class HeroDetailsViewModel(
    val id: String,
    val name: String,
    val description: String,
    val heroSprite: HeroSprite,
    val spellName: String,
    val spellDescription: String,
    val spellCooldown: Int
)
