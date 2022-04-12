package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.ScreenVector
import kotlin.math.sqrt

class BattleMapTileView(
    tileHexRadius: Float,
    pos: ScreenVector
) : ViewBase(), ClickableView {

    var focused: Boolean = false
        set(value) {
            sprite.texture = if (value) textureFocused else texture
            field = value
        }

    private val texture = Texture("tileDark.png")
    private val textureFocused = Texture("tileAccentLight.png")
    private val sprite = Sprite(if (focused) textureFocused else texture)

    private val clickHandler: ClickableView = ClickableImpl { sprite.boundingRectangle }

    init {
        sprite.setSize(tileHexRadius * sqrt(3f), tileHexRadius * 2)
        sprite.setPosition(pos.x, pos.y)
    }

    override fun subscribe(observer: ClickObserver) = clickHandler.subscribe(observer)

    override fun registerInput() = clickHandler.registerInput()

    override fun render(sb: SpriteBatch) {
        sprite.draw(sb)
    }

    override fun dispose() {
        texture.dispose()
        textureFocused.dispose()
    }
}
