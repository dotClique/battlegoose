package se.battlegoo.battlegoose.views.utils

import com.badlogic.gdx.Gdx
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.datamodels.ScreenVector

interface ClickableView {
    fun subscribe(observer: ClickObserver)
    fun registerInput()
}

interface ClickObserver {
    fun onClick()
}

class ClickableImpl(
    private val isPointWithinBounds: (ScreenVector) -> Boolean
) : ClickableView {

    private var observer: ClickObserver? = null

    override fun subscribe(observer: ClickObserver) {
        this.observer = observer
    }

    override fun registerInput() {
        if (Gdx.input.justTouched()) {
            val clickPos = Game.unproject(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            if (isPointWithinBounds(ScreenVector(clickPos.x, clickPos.y))) {
                observer?.onClick()
            }
        }
    }
}
