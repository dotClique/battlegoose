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
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.utilities.fitScale

class HeroDetailsView(
    x: Float,
    y: Float,
    maxWidth: Float,
    maxHeight: Float,
    hero: Hero,
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
    }

    private val stage: Stage = Stage(Game.viewPort)

    private val backgroundTexture: Texture = Texture("heroSelection/heroDetails.png")
    private val heroTexture: Texture = Texture(hero.texturePath)

    private val backgroundSprite: Sprite = Sprite(backgroundTexture)
    private val heroSprite: Sprite = Sprite(heroTexture)

    private val mainSkin: Skin = Skin(Gdx.files.internal("skins/star-soldier/star-soldier-ui.json"))
    private val textSkin: Skin = Skin(Gdx.files.internal("skins/plain-james/plain-james-ui.json"))

    private val textTable: Table = Table(mainSkin)
    private val nameLabel = Label(hero.name, mainSkin)
    private val descriptionLabel = Label(hero.description, textSkin)
    private val spellHeaderLabel = Label("Spell:", mainSkin)
    private val spellNameLabel = Label(hero.spell.title, textSkin)
    private val spellDescriptionLabel = Label(
        "${hero.spell.description}\n${hero.spell.cooldown} turns cooldown.", textSkin
    )

    init {
        val (backgroundScaledWidth, backgroundScaledHeight, backgroundOffsetX, backgroundOffsetY) =
            fitScale(backgroundTexture, maxWidth, maxHeight)
        backgroundSprite.setPosition(x + backgroundOffsetX, y + backgroundOffsetY)
        backgroundSprite.setSize(backgroundScaledWidth, backgroundScaledHeight)

        // Define position and size based on the background-image
        val heroBaselineX = x + backgroundOffsetX + backgroundScaledWidth * CARD_IMAGE_MARGIN_LEFT
        val heroBaselineY = y + backgroundOffsetY + backgroundScaledHeight * CARD_IMAGE_MARGIN_DOWN
        val heroImageWidth = backgroundScaledWidth * CARD_IMAGE_WIDTH
        val heroImageHeight = backgroundScaledHeight * CARD_IMAGE_HEIGHT

        // Ignore heroOffsetY as we want this image to render from the baseline
        val (heroScaledWidth, heroScaledHeight, heroOffsetX, _) =
            fitScale(heroTexture, heroImageWidth, heroImageHeight)
        heroSprite.setPosition(heroBaselineX + heroOffsetX, heroBaselineY)
        heroSprite.setSize(heroScaledWidth, heroScaledHeight)

        // Define size-values for text
        val lineHeight = backgroundScaledHeight * CARD_TEXT_LINE_HEIGHT
        val textWidth = backgroundScaledWidth * CARD_TEXT_BOX_WIDTH
        val textHeight = backgroundScaledHeight * CARD_TEXT_BOX_HEIGHT - lineHeight
        // Use heroImageWidth to find midline, then use textWidth to find left starting point
        val textBaselineX = heroBaselineX + (heroImageWidth / 2) - (textWidth / 2)

        val fontScale = lineHeight * FONT_SCALE_MULTIPLIER
        val headerFontScale = fontScale * FONT_MAIN_SCALE
        val bodyFontScale = fontScale * FONT_BODY_SCALE

        textTable.setSize(textWidth, textHeight)
        textTable.setPosition(textBaselineX, heroBaselineY - textHeight - (lineHeight / 2))
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(headerFontScale)
        nameLabel.color = Color.BLACK
        nameLabel.wrap = true

        descriptionLabel.setFontScale(bodyFontScale)
        descriptionLabel.wrap = true

        spellHeaderLabel.setFontScale(headerFontScale * FONT_MAIN_SPELL_HEADER_SCALE)
        spellHeaderLabel.color = Color.BLACK
        spellHeaderLabel.wrap = true

        spellNameLabel.setFontScale(bodyFontScale * FONT_BODY_SPELL_NAME_SCALE)
        spellNameLabel.wrap = true

        spellDescriptionLabel.setFontScale(bodyFontScale)
        spellDescriptionLabel.wrap = true

        // Set default values for table cells
        textTable.defaults().fill().left().colspan(2)
        // Add and position all text elements
        textTable.add(nameLabel).width(textWidth)
        textTable.row()
        textTable.add(descriptionLabel).width(textWidth)
        textTable.row().spaceTop(lineHeight / 2).colspan(1)
        textTable.add(spellHeaderLabel).width(textWidth * TABLE_COLUMN_SPELL_TITLE_WIDTH)
        textTable.add(spellNameLabel).expandX()
        textTable.row().width(textWidth)
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
