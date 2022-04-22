package se.battlegoo.battlegoose.views

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector
import se.battlegoo.battlegoose.models.BattleMapBackground
import se.battlegoo.battlegoose.utils.TextureAsset

class BattleMapView(
    background: BattleMapBackground,
    val pos: ScreenVector,
    val size: ScreenVector
) :
    ViewBase() {

    private val textureAsset = when (background) {
        BattleMapBackground.DUNES -> TextureAsset.MAP_DUNES
        BattleMapBackground.ICE_RINK -> TextureAsset.MAP_ICE_RINK
        BattleMapBackground.DIRT_ROAD -> TextureAsset.MAP_DIRT_ROAD
    }
    private val backgroundTexture = Game.getTexture(textureAsset)
    var backgroundTextureRegion = TextureRegion(backgroundTexture)

    override fun render(sb: SpriteBatch) {
        sb.draw(
            backgroundTextureRegion,
            0f, 0f,
            Game.WIDTH, Game.HEIGHT
        )
    }

    override fun dispose() {}
}
