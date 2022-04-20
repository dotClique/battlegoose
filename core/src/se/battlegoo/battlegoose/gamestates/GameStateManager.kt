package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.Deque
import java.util.concurrent.ConcurrentLinkedDeque
import se.battlegoo.battlegoose.utils.ModalClass

object GameStateManager {
    private val states: Deque<GameState> = ConcurrentLinkedDeque()
    var overlay = 0

    fun push(state: GameState) {
        states.push(state)
    }

    fun goBack(): GameState? {
        if (states.size <= 1) {
            return null
        }
        states.peek().dispose()
        val popped = states.pop()
        states.peek().initialize()
        return popped
    }

    fun replace(state: GameState) {
        goBack()
        push(state)
    }

    fun update(dt: Float) {
        if (overlay == 0)
            states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }
}
