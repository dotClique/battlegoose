package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

abstract class UnitViewBase(
    texturePath: String,
    protected val textureStartFacingDirection: FacingDirection = FacingDirection.RIGHT
) : ViewBase() {

    private val texture = Texture(texturePath)
    protected var textureRegion = TextureRegion(texture)
        get
    protected val sprite = Sprite(textureRegion)
    protected var facingDirection = textureStartFacingDirection

    fun clicked(): Boolean {
        if (!Gdx.input.justTouched()) return false
        val touchPoint = Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
        val thisRect = sprite.boundingRectangle
        return thisRect.contains(touchPoint)
    }

    fun overlaps(other: UnitViewBase): Boolean {
        val thisRect = this.sprite.boundingRectangle
        val otherRect = other.sprite.boundingRectangle
        return thisRect.overlaps(otherRect)
    }

    fun setDirection(newDirection: FacingDirection) {
        if (newDirection == this.facingDirection) return
        facingDirection = newDirection
        sprite.flip(true, false)
    }

    override fun render(sb: SpriteBatch) {
        sprite.draw(sb)
    }

    override fun dispose() {
        texture.dispose()
    }
}
