package gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.lang.Error
import java.util.*
// The singleton
object GameStateManager {
    private val states: Stack<GameState> = Stack()


    fun push(state: GameState) {
        states.push(state)
    }

    fun goBack(): GameState {
        states.peek().dispose()
        return states.pop()
    }

    // What about props? Doing it like this t avoid coupling between the states
    fun set(state: GameState) {
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