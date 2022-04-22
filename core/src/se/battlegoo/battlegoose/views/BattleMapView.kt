package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.BattleMapBackground

class BattleMapView(
    background: BattleMapBackground,
    val pos: ScreenVector,
    val size: ScreenVector
) :
    ViewBase() {

    private val texturePath = when (background) {
        BattleMapBackground.SAND -> "sand.png"
    }
    private val backgroundTexture = Texture(texturePath)
    var backgroundTextureRegion = TextureRegion(backgroundTexture)

    override fun render(sb: SpriteBatch) {
        sb.draw(
            backgroundTextureRegion,
            0f, 0f,
            Game.WIDTH, Game.HEIGHT
        )
    }

    override fun dispose() {
        backgroundTexture.dispose()
    }
}
