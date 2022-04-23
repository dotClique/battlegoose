package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.gridmath.isPointInsideHexagon
import se.battlegoo.battlegoose.utils.TextureAsset
import se.battlegoo.battlegoose.utils.WavePulsator
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

    private val texture = Game.getTexture(TextureAsset.TILE_DARK)
    private val textureFocused = Game.getTexture(TextureAsset.TILE_ACCENT_DARK)
    private val textureMoveTarget = Game.getTexture(TextureAsset.TILE_MOVE_DARK)
    private val textureAttackTarget = Game.getTexture(TextureAsset.TILE_ATTACK_DARK)

    private val textureHint = Game.getTexture(TextureAsset.TILE_HINT_DARK)

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

    private val tileSize = ScreenVector(tileHexRadius * sqrt(3f), tileHexRadius * 2)

    private val sprite = Sprite(textureByState(state))
        .apply { setSize(tileSize.x, tileSize.y) }
        .apply { setPosition(pos.x, pos.y) }
    private val hintSprite = Sprite(textureHint)
        .apply { setSize(tileSize.x, tileSize.y) }
        .apply { setPosition(pos.x, pos.y) }

    private val hintAlphaPulsator = WavePulsator(0f, 0.8f, 50, 2)

    var showHint: Boolean = false
        set(value) {
            hintAlphaPulsator.reset()
            field = value
        }

    private val clickHandler: ClickableView = ClickableImpl { clickPos ->
        isPointInsideHexagon(clickPos, pos, tileSize)
    }

    override fun render(sb: SpriteBatch) {
        sprite.draw(sb)
        if (showHint) {
            if (hintAlphaPulsator.isDone()) {
                showHint = false
                return
            }
            hintAlphaPulsator.tick()
            hintSprite.draw(sb, hintAlphaPulsator.value())
        }
    }

    override fun subscribe(observer: ClickObserver) = clickHandler.subscribe(observer)

    override fun registerInput() = clickHandler.registerInput()

    override fun dispose() {}
}
