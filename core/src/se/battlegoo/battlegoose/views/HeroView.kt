package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.models.heroes.Hero
import se.battlegoo.battlegoose.utilities.fitScale

class HeroView(val x: Float, val y: Float, val width: Float, val height: Float, val hero: Hero) : ViewBase() {

    private val backgroundTexture = Texture("heroSelection/cutout.png")
    private val heroTexture = Texture(hero.texturePath)

    override fun render(sb: SpriteBatch) {
        val (backgroundWidth, backgroundHeight, backgroundX, backgroundY) = fitScale(backgroundTexture, width, height)
        sb.draw(backgroundTexture, x + backgroundX, y + backgroundY, backgroundWidth, backgroundHeight)

        // Define position and size based on the background-image
        val relX = backgroundX + backgroundWidth * 0.07f
        val relY = backgroundY + backgroundHeight * 0.39f
        val relWidth = backgroundWidth * 0.85f
        val relHeight = backgroundHeight * 0.55f

        // Ignore heroY as we want this image to render from the baseline
        val (heroWidth, heroHeight, heroX, _) = fitScale(heroTexture, relWidth, relHeight)
        sb.draw(heroTexture, x + heroX + relX, y + relY, heroWidth, heroHeight)
    }

    override fun dispose() {
        backgroundTexture.dispose()
        heroTexture.dispose()
    }
}
