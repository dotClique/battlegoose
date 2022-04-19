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
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.utilities.fitScale

class HeroCardView(
    x: Float,
    y: Float,
    maxWidth: Float,
    maxHeight: Float,
    private val parentStage: Stage?,
    private val heroSelection: HeroSelection,
    private val hero: Hero,
    onClickCard: (hero: Hero) -> Unit,
    onClickInfo: (hero: Hero) -> Unit
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
    }

    private val stage: Stage = parentStage ?: Stage(Game.viewPort)

    private val backgroundTexture: Texture = Texture("heroSelection/heroCard.png")
    private val heroTexture: Texture = Texture(hero.texturePath)

    private val backgroundSprite: Sprite = Sprite(backgroundTexture)
    private val heroSprite: Sprite = Sprite(heroTexture)

    private val mainSkin: Skin = Skin(Gdx.files.internal("skins/star-soldier/star-soldier-ui.json"))
    private val textSkin: Skin = Skin(Gdx.files.internal("skins/plain-james/plain-james-ui.json"))

    private val textTable: Table = Table(mainSkin)
    private val nameLabel: Label = Label(hero.name, mainSkin)
    private val descriptionLabel: Label = Label(hero.description, textSkin)

    private val infoButton: TextButton = TextButton("Info", mainSkin)

    private val onClickCardListeners: MutableList<(hero: Hero) -> Unit> = arrayListOf(onClickCard)
    private val onClickInfoListeners: MutableList<(hero: Hero) -> Unit> = arrayListOf(onClickInfo)

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
        val textHeight = backgroundScaledHeight * CARD_TEXT_BOX_HEIGHT - (lineHeight / 2)
        val fontScale = lineHeight * FONT_SCALE_MULTIPLIER

        textTable.setSize(heroImageWidth, textHeight)
        textTable.setPosition(heroBaselineX, heroBaselineY - textHeight - lineHeight)
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(fontScale * FONT_HEADER_SCALE)
        nameLabel.color = Color.BLACK
        nameLabel.wrap = true

        descriptionLabel.setFontScale(fontScale)
        descriptionLabel.wrap = true

        // Add and position all text elements
        textTable.row().width(heroImageWidth)
        textTable.add(nameLabel)
        textTable.row().width(heroImageWidth)
        textTable.add(descriptionLabel)
        textTable.row().width(heroImageWidth).spaceTop(lineHeight / 2)

        infoButton.label.setFontScale(fontScale * FONT_BUTTON_SCALE)
        infoButton.setSize(heroImageWidth, lineHeight * BUTTON_HEIGHT_MULTIPLIER)
        infoButton.setPosition(heroBaselineX, heroBaselineY - textHeight)

        stage.addActor(infoButton)
        Gdx.input.inputProcessor = stage
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
        if (Gdx.input.justTouched() && infoButton.isPressed) {
            onClickInfoListeners.forEach { it(hero) }
        } else if (Gdx.input.justTouched() && cardIsPressed()) {
            onClickCardListeners.forEach { it(hero) }
        }
    }

    override fun render(sb: SpriteBatch) {
        if (heroSelection.selectedHero == hero) {
            backgroundSprite.color = Color.LIGHT_GRAY
        } else {
            backgroundSprite.color = Color.WHITE
        }
        backgroundSprite.draw(sb)
        heroSprite.draw(sb)
        textTable.draw(sb, 1f)
        stage.draw()
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroTexture.dispose()
        mainSkin.dispose()
        textSkin.dispose()
        parentStage ?: stage.dispose() // If no stage received from parent, dispose of local stage
    }
}
