package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.gridmath.isPointInsideHexagon
import kotlin.math.sqrt

enum class BattleMapTileState {
    NORMAL,
    FOCUSED,
    MOVE_TARGET,
    ATTACK_TARGET
}

class BattleMapTileView(
    tileHexRadius: Float,
    pos: ScreenVector
) : ViewBase(), ClickableView {

    private val texture = Texture("tileDark.png")
    private val textureFocused = Texture("tileAccentDark.png")
    private val textureMoveTarget = Texture("tileMoveDark.png")
    private val textureAttackTarget = Texture("tileAttackDark.png")

    private fun textureByState(state: BattleMapTileState): Texture = when (state) {
        BattleMapTileState.NORMAL -> texture
        BattleMapTileState.FOCUSED -> textureFocused
        BattleMapTileState.MOVE_TARGET -> textureMoveTarget
        BattleMapTileState.ATTACK_TARGET -> textureAttackTarget
    }

    var state: BattleMapTileState = BattleMapTileState.NORMAL
        set(value) {
            sprite.texture = textureByState(value)
            field = value
        }

    private val sprite = Sprite(textureByState(state))

    private val tileSize = ScreenVector(tileHexRadius * sqrt(3f), tileHexRadius * 2)

    private val clickHandler: ClickableView = ClickableImpl { clickPos ->
        isPointInsideHexagon(clickPos, pos, tileSize)
    }

    init {
        sprite.setSize(tileSize.x, tileSize.y)
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
