package se.battlegoo.battlegoose.views

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import se.battlegoo.battlegoose.Game

class ClickableImpl(private val getBoundingRectangle: () -> Rectangle) : ClickableView {

    private var observer: ClickObserver? = null

    override fun subscribe(observer: ClickObserver) {
        this.observer = observer
    }

    override fun registerInput() {
        if (Gdx.input.justTouched() &&
            getBoundingRectangle().contains(
                    Game.unproject(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
                )
        ) {
            observer?.onClick()
        }
    }
}
