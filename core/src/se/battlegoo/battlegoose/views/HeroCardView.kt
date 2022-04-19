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
    stage: Stage?,
    private val heroSelection: HeroSelection,
    private val hero: Hero,
    private val onClickCard: (hero: Hero) -> Unit,
    private val onClickInfo: (hero: Hero) -> Unit
) : ViewBase() {

    private val stage: Stage = stage ?: Stage(Game.viewPort)

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

    init {
        val (backgroundScaledWidth, backgroundScaledHeight, backgroundOffsetX, backgroundOffsetY) =
            fitScale(backgroundTexture, maxWidth, maxHeight)
        backgroundSprite.setPosition(x + backgroundOffsetX, y + backgroundOffsetY)
        backgroundSprite.setSize(backgroundScaledWidth, backgroundScaledHeight)

        // Define position and size based on the background-image
        val heroBaselineX = x + backgroundOffsetX + backgroundScaledWidth * 0.07f
        val heroBaselineY = y + backgroundOffsetY + backgroundScaledHeight * 0.39f
        val heroImageWidth = backgroundScaledWidth * 0.85f
        val heroImageHeight = backgroundScaledHeight * 0.55f

        // Ignore heroOffsetY as we want this image to render from the baseline
        val (heroScaledWidth, heroScaledHeight, heroOffsetX, _) =
            fitScale(heroTexture, heroImageWidth, heroImageHeight)
        heroSprite.setPosition(heroBaselineX + heroOffsetX, heroBaselineY)
        heroSprite.setSize(heroScaledWidth, heroScaledHeight)

        // Define size-values for text
        val lineHeight = backgroundScaledHeight * 0.04f
        val textHeight = backgroundScaledHeight * 0.38f - (lineHeight / 2)
        val fontScale = lineHeight * 0.033f

        textTable.setSize(heroImageWidth, textHeight)
        textTable.setPosition(heroBaselineX, heroBaselineY - textHeight - lineHeight)
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(fontScale * 2f)
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

        infoButton.label.setFontScale(fontScale * 2f)
        infoButton.setSize(heroImageWidth, lineHeight * 3f)
        infoButton.setPosition(heroBaselineX, heroBaselineY - textHeight)

        this.stage.addActor(infoButton)
        Gdx.input.inputProcessor = this.stage
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
            onClickInfo(hero)
        } else if (Gdx.input.justTouched() && cardIsPressed()) {
            onClickCard(hero)
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
    }
}
