package se.battlegoo.battlegoose.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import java.util.UUID
import kotlin.math.roundToInt
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.GameStateManager

class ModalClass(
    private val title: String,
    private val text: String,
    type: ModalType,
    val stage: Stage,
    val skin: Skin,
    private val scale: Float = 2f
) {

    val clicks = HashMap<String, (() -> Unit)?>()

    private val dialog = object : Dialog("\t $title", skin) {

        init {
            setScale(scale)
        }

        override fun getPrefHeight(): Float = Game.HEIGHT / 4
        override fun getPrefWidth(): Float = Game.WIDTH / 4

        override fun result(`object`: Any?) {
            if (`object` == null || `object` !is String)
                throw IllegalArgumentException(
                    "Object passed to Dialog result method is not a String."
                )
            if (!clicks.containsKey(`object`))
                throw IllegalArgumentException(
                    "Object passed to Dialog result function is not in possible clicks."
                )
            clicks[`object`]?.let { it() }
            GameStateManager.removeOverlay()
        }
    }


    init {
        dialog.text(text)
        dialog.isMovable = false
        when (type) {
            is ModalType.Error -> {
                dialog.color = Color.RED
                addButton("Ok", type.onOk)
            }
            is ModalType.Info -> {
                dialog.color = Color.SKY
                addButton("Ok", type.onOk)
            }
            is ModalType.Warning -> {
                dialog.color = Color.ORANGE
                addButton("Yes", type.onYes)
                addButton("No", type.onNo)
            }
            is ModalType.Question -> {
                addButton("Yes", type.onYes)
                addButton("No", type.onNo)
            }
        }
    }

    fun show() {
        dialog.show(stage)
        // Centre the dialog on the screen with respect to the scale
        dialog.setPosition(
            ((stage.width - dialog.width * scale) / 2).roundToInt().toFloat(),
            ((stage.height - dialog.height * scale) / 2).roundToInt().toFloat()
        )
        GameStateManager.addOverlay()
    }

    private fun addButton(buttonText: String, onClick: (() -> Unit)?) {
        val uuid = UUID.randomUUID().toString()
        val button = TextButton(buttonText, skin)
        dialog.buttonTable.add(button).width(dialog.prefWidth / 3).height(dialog.prefHeight / 4)
        dialog.setObject(button, uuid)
        clicks[uuid] = onClick
    }

}
