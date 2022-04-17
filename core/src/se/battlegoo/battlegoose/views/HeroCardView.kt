package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.utilities.Quad
import se.battlegoo.battlegoose.utilities.fitScale

class HeroCardView(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    hero: Hero
) : ViewBase() {

    private val backgroundPosition: Quad<Float, Float, Float, Float>
    private val heroPosition: Quad<Float, Float, Float, Float>

    private val backgroundTexture: Texture = Texture("heroSelection/cutout.png")
    private val heroTexture: Texture = Texture(hero.texturePath)

    private val skin: Skin = Skin(Gdx.files.internal("star-soldier-ui.json"))
    private val textTable: Table = Table(skin)
    private val nameLabel: Label = Label(hero.name, skin)
    private val descriptionLabel: Label = Label(hero.description, skin)
    private val statsLabel: Label = Label(
        "Action points: ${hero.baseStats.actionPoints}", skin)

    init {
        val (backgroundWidth, backgroundHeight, backgroundOffsetX, backgroundOffsetY) =
            fitScale(backgroundTexture, width, height)
        backgroundPosition = Quad(
            x + backgroundOffsetX, y + backgroundOffsetY, backgroundWidth, backgroundHeight)

        // Define position and size based on the background-image
        val heroBaselineX = x + backgroundOffsetX + backgroundWidth * 0.07f
        val heroBaselineY = y + backgroundOffsetY + backgroundHeight * 0.39f
        val heroImageWidth = backgroundWidth * 0.85f
        val heroImageHeight = backgroundHeight * 0.55f

        // Ignore heroOffsetY as we want this image to render from the baseline
        val (heroScaledWidth, heroScaledHeight, heroOffsetX, _) =
            fitScale(heroTexture, heroImageWidth, heroImageHeight)
        heroPosition = Quad(
            heroBaselineX + heroOffsetX, heroBaselineY, heroScaledWidth, heroScaledHeight)

        // Define size-values for text
        val lineHeight = backgroundHeight * 0.04f
        val textHeight = backgroundHeight * 0.38f - (lineHeight / 2)
        val fontScale = lineHeight * 0.033f

        textTable.setSize(heroImageWidth, textHeight)
        textTable.setPosition(heroBaselineX, heroBaselineY - textHeight - lineHeight)
        textTable.left().top() // Align content from top left

        nameLabel.setFontScale(fontScale * 2f)
        nameLabel.color = Color.BLACK
        nameLabel.wrap = true

        descriptionLabel.setFontScale(fontScale)
        descriptionLabel.color = Color.DARK_GRAY
        descriptionLabel.wrap = true

        statsLabel.setFontScale(fontScale)
        statsLabel.color = Color.DARK_GRAY
        statsLabel.wrap = true

        // Add and position all text elements
        textTable.row().width(heroImageWidth)
        textTable.add(nameLabel)
        textTable.row().width(heroImageWidth)
        textTable.add(descriptionLabel)
        textTable.row().width(heroImageWidth).spaceTop(lineHeight / 2)
        textTable.add(statsLabel)
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(backgroundTexture, backgroundPosition.a, backgroundPosition.b, backgroundPosition.c, backgroundPosition.d)
        sb.draw(heroTexture, heroPosition.a, heroPosition.b, heroPosition.c, heroPosition.d)
        textTable.draw(sb, 1f)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroTexture.dispose()
        skin.dispose()
    }
}
