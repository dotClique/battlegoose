package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import se.battlegoo.battlegoose.views.QuickJoinView

class QuickJoinState : GameState() {

    private val quickJoinView = QuickJoinView(
        this::goBack,
        stage
    )

    init {
    }

    private fun goBack() {
        GameStateManager.goBack()
    }

    private fun handleInput() {
        quickJoinView.registerInput()
    }

    override fun update(dt: Float) {
        handleInput()
    }

    override fun render(sb: SpriteBatch) {
        quickJoinView.render(sb)
    }

    override fun dispose() {
        quickJoinView.dispose()
    }
}
