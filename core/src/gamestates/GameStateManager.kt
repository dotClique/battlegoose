package gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque

// The singleton
object GameStateManager {
    private val states: Deque<GameState> = ConcurrentLinkedDeque()


    fun push(state: GameState) {
        states.push(state)
    }

    fun goBack(): GameState {
        states.peek().dispose()
        return states.pop()
    }

    fun replace(state: GameState) {
        goBack()
        push(state)
    }

    fun update(dt: Float) {
        states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }
}