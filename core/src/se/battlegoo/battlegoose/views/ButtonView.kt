package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import se.battlegoo.battlegoose.Game

class ButtonView(
    texturePath: String,
    private val xPos: Float,
    private val yPos: Float,
    val width: Float = 1f
) : ViewBase() {

    private val buttonTexture = Texture(texturePath)
    private val buttonTextureRegion = TextureRegion(buttonTexture)

    val height: Float = buttonTextureRegion.regionHeight.toFloat() / buttonTextureRegion.regionWidth
        .toFloat() * width

    private val button = Rectangle(xPos, yPos, width, height)

    /**
     * Handle whether a button is pressed or not
     *
     * @return boolean
     */
    fun isPressed(): Boolean {
        return button.contains(Game.unproject(Gdx.input.x.toFloat(), Gdx.input.y.toFloat()))
    }

    override fun render(sb: SpriteBatch) {
        sb.draw(buttonTextureRegion, xPos, yPos, width, height)
    }

    override fun dispose() {
        buttonTexture.dispose()
    }
}
