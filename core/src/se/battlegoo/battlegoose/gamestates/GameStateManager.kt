package se.battlegoo.battlegoose.gamestates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.utils.Modal
import java.util.Deque
import java.util.concurrent.ConcurrentLinkedDeque

object GameStateManager {
    private val states: Deque<GameState> = ConcurrentLinkedDeque()
    private val modalList = mutableListOf<Modal>()

    private fun initializeStage(state: GameState) {
        Game.setGlobalStage(state.stage)
        changeStage(state.stage)
    }

    fun push(state: GameState) {
        states.push(state)
        initializeStage(state)
    }

    fun goBack(): GameState? {
        if (states.size <= 1) {
            return null
        }
        states.peek().disposeState()
        val popped = states.pop()
        initializeStage(states.peek())
        return popped
    }

    fun replace(state: GameState) {
        if (states.size <= 1) return
        states.peek().disposeState()
        states.pop()
        states.push(state)
        initializeStage(state)
    }

    fun update(dt: Float) {
        if (modalList.size == 0)
            states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }

    fun dispose() {
        states.forEach { it.disposeState() }
    }

    fun addOverlay(modal: Modal) {
        modalList.add(modal)
    }

    fun removeOverlay(modal: Modal) {
        modalList.remove(modal)
    }

    private fun changeStage(newStage: Stage) {
        modalList.forEach { it.updateStage(newStage) }
    }
}
