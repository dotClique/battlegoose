package gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.lang.Error
import java.util.*
// The singleton
object GameStateManager {
    private val states: Stack<GameState> = Stack()


    fun push(state: GameStates) {
//        when(state) {
//            GameStates.MAIN_MENU -> states.push(MainMenuState())
//            GameStates.BATTLE_STATE -> states.push(BattleState())
//            GameStates.LEADERBOARD_STATE -> states.push(LeaderboardState())
//            GameStates.MOVE_STATE -> states.push(MoveState())
//        }
    }

    fun goBack(): GameState {
        return states.pop()
    }

    // What about props? Doing it like this t avoid coupling between the states
    fun set(state: GameStates) {
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