package se.battlegoo.battlegoose.views.utils

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
    val width: Int,
    val onClick: () -> Unit
) : ViewBase() {

    var texturePath: String = texturePath
        set(value) {
            field = value
            buttonTexture = Texture(texturePath)
        }
    private var buttonTexture = Texture(texturePath)
        set(value) {
            field.dispose()
            field = value
            buttonSprite.texture = value
            buttonTextureRegion.setRegion(buttonTexture)
            buttonSprite.setRegion(buttonTextureRegion)
        }
    private val buttonTextureRegion = TextureRegion(buttonTexture)

    val height =
        (buttonTexture.height.toFloat() / buttonTexture.width.toFloat() * width.toFloat()).toInt()
    private val scale =
        if (width > buttonTexture.width) width.toFloat() / buttonTexture.width.toFloat() else 1f
    var hidden: Boolean = false
    var disabled: Boolean = false

    private val buttonSprite = Sprite(buttonTextureRegion)

    /**
     * Handle whether a button is pressed or not
     *
     * @return boolean
     */
    private fun isPressed(): Boolean {
        if (disabled || hidden) return false
        return buttonSprite.boundingRectangle.contains(
            Game.unproject(
                Gdx.input.x.toFloat(),
                Gdx.input.y.toFloat()
            )
        )
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() && isPressed()) {
            onClick()
        }
    }

    override fun render(sb: SpriteBatch) {
        if (hidden) return
        buttonSprite.setCenter(xPos + width / 2, yPos + height / 2)
        buttonSprite.setScale(scale)
        buttonSprite.draw(sb)
    }

    override fun dispose() {
        buttonTexture.dispose()
    }
}
