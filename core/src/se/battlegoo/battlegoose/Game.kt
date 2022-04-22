package se.battlegoo.battlegoose

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import se.battlegoo.battlegoose.gamestates.GameStateManager
import se.battlegoo.battlegoose.gamestates.MainMenuState
import se.battlegoo.battlegoose.utils.TextureAsset

class Game : ApplicationAdapter() {

    companion object {

        private val assetManager = AssetManager()

        private fun loadTexture(asset: TextureAsset) =
            assetManager.load(asset.path, Texture::class.java)

        fun getTexture(asset: TextureAsset): Texture =
            assetManager.get(asset.path, Texture::class.java)

        lateinit var batch: SpriteBatch
        const val WIDTH = 2280f
        const val HEIGHT = 1080f
        const val TITLE = "BattleGoose"
        val viewPort = FitViewport(WIDTH, HEIGHT)
        lateinit var stage: Stage
            private set // use setStage

        fun setGlobalStage(newStage: Stage) {
            stage = newStage
            Gdx.input.inputProcessor = newStage
        }

        /**
         * Public function that scales Gdx values to Viewport values
         */
        fun unproject(x: Float, y: Float): Vector2 {
            val vector3d = viewPort.unproject(Vector3(x, y, 0f))
            return Vector2(vector3d.x, vector3d.y)
        }
    }

    override fun create() {
        TextureAsset.values().forEach(::loadTexture)
        assetManager.finishLoading()
        batch = SpriteBatch()
        stage = Stage(viewPort, batch)
        Gdx.input.inputProcessor = stage
        resize(WIDTH.toInt(), HEIGHT.toInt())
        GameStateManager.push(MainMenuState())
    }

    override fun render() {
        GameStateManager.update(Gdx.graphics.deltaTime)
        ScreenUtils.clear(Color.BLACK)
        viewPort.apply()
        batch.projectionMatrix = viewPort.camera.combined
        batch.color = Color.WHITE
        batch.begin()
        GameStateManager.render(batch)
        batch.end()
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        assetManager.dispose()
        batch.dispose()
        GameStateManager.dispose()
    }

    override fun resize(width: Int, height: Int) {
        viewPort.update(width, height, true)
        val total = viewPort.topGutterHeight + viewPort.bottomGutterHeight
        viewPort.screenY = total / 2
    }
}
