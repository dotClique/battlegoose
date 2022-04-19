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
    private val spellTitleLabel = Label(hero.spell.title, textSkin)
    private val spellDescriptionLabel = Label(
        "${hero.spell.description}\n${hero.spell.cooldown} turns cooldown.", textSkin
    )

    init {
        val (backgroundScaledWidth, backgroundScaledHeight, backgroundOffsetX, backgroundOffsetY) =
            fitScale(backgroundTexture, maxWidth, maxHeight)
        backgroundSprite.setPosition(x + backgroundOffsetX, y + backgroundOffsetY)
        backgroundSprite.setSize(backgroundScaledWidth, backgroundScaledHeight)

        // Define position and size based on the background-image
        val heroBaselineX = x + backgroundOffsetX + backgroundScaledWidth * 0.18f
        val heroBaselineY = y + backgroundOffsetY + backgroundScaledHeight * 0.65f
        val heroImageWidth = backgroundScaledWidth * 0.64f
        val heroImageHeight = backgroundScaledHeight * 0.30f

        // Ignore heroOffsetY as we want this image to render from the baseline
        val (heroScaledWidth, heroScaledHeight, heroOffsetX, _) =
            fitScale(heroTexture, heroImageWidth, heroImageHeight)
        heroSprite.setPosition(heroBaselineX + heroOffsetX, heroBaselineY)
        heroSprite.setSize(heroScaledWidth, heroScaledHeight)

        // Define size-values for text
        val lineHeight = backgroundScaledHeight * 0.04f
        val textWidth = backgroundScaledWidth * 0.85f
        val textHeight = backgroundScaledHeight * 0.60f - lineHeight
        val textBaselineX = heroBaselineX + (heroImageWidth / 2) - (textWidth / 2)

        val fontScale = lineHeight * 0.033f
        val skinFontScale = fontScale * 2f
        val labelFontScale = fontScale * 1.2f

        textTable.setSize(textWidth, textHeight)
        textTable.setPosition(textBaselineX, heroBaselineY - textHeight - (lineHeight / 2))
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(skinFontScale)
        nameLabel.color = Color.BLACK
        nameLabel.wrap = true

        descriptionLabel.setFontScale(labelFontScale)
        descriptionLabel.color = Color.BLACK
        descriptionLabel.wrap = true

        spellHeaderLabel.setFontScale(skinFontScale * 0.8f)
        spellHeaderLabel.color = Color.BLACK
        spellHeaderLabel.wrap = true

        spellTitleLabel.setFontScale(labelFontScale * 1.2f)
        spellTitleLabel.color = Color.BLACK
        spellTitleLabel.wrap = true

        spellDescriptionLabel.setFontScale(labelFontScale)
        spellDescriptionLabel.color = Color.BLACK
        spellDescriptionLabel.wrap = true

        // Set default values for table cells
        textTable.defaults().fill().left().colspan(2)
        // Add and position all text elements
        textTable.add(nameLabel).width(textWidth)
        textTable.row()
        textTable.add(descriptionLabel).width(textWidth)
        textTable.row().spaceTop(lineHeight / 2).colspan(1)
        textTable.add(spellHeaderLabel).width(textWidth * 0.3f)
        textTable.add(spellTitleLabel).expandX()
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
