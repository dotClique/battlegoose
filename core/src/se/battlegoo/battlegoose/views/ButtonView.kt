package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.Game

class ButtonView(
    texturePath: String,
    private val xPos: Float,
    private val yPos: Float,
    val width: Int = 1
) : ViewBase() {

    private val buttonTexture = Texture(texturePath)
    val height =
        (buttonTexture.height.toFloat() / buttonTexture.width.toFloat() * width.toFloat()).toInt()
    private val scale =
        if (width > buttonTexture.width) width.toFloat() / buttonTexture.width.toFloat() else 1f
    private val buttonTextureRegion = TextureRegion(
        buttonTexture,
        0,
        0,
        buttonTexture.width,
        buttonTexture.height
    )

    private val buttonSprite = Sprite(buttonTextureRegion)

    /**
     * Handle whether a button is pressed or not
     *
     * @return boolean
     */
    fun isPressed(): Boolean {
        return buttonSprite.boundingRectangle.contains(
            Game.unproject(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
        )
    }

    override fun render(sb: SpriteBatch) {
        buttonSprite.setCenter(xPos + width / 2, yPos + height / 2)
        buttonSprite.setScale(scale)
        buttonSprite.draw(sb)
    }

    override fun dispose() {
        buttonTexture.dispose()
    }
}
