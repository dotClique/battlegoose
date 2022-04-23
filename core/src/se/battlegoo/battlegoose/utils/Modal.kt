package se.battlegoo.battlegoose.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import java.util.UUID
import kotlin.math.roundToInt
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.GameStateManager
import se.battlegoo.battlegoose.views.Skins

class Modal(
    private val title: String,
    private val text: String? = null,
    type: ModalType,
    stage: Stage? = null,
    private val scale: Float = 1f,
    private val contentActors: List<Actor> = listOf(),
    private val prefWidth: Float = Game.WIDTH / 2,
    private val prefHeight: Float = Game.HEIGHT / 2
) {

    private var usedStage = stage ?: Game.stage
    private var skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    val clicks = HashMap<String, (() -> Unit)?>()

    private val dialog = object : Dialog("\t$title", skin) {

        override fun getPrefHeight(): Float = this@Modal.prefHeight
        override fun getPrefWidth(): Float = this@Modal.prefWidth

        override fun result(`object`: Any?) {
            if (`object` == null || `object` !is String)
                throw IllegalArgumentException(
                    "Object passed to Dialog result method is not a String."
                )
            if (!clicks.containsKey(`object`))
                throw IllegalArgumentException(
                    "Object passed to Dialog result function is not in possible clicks."
                )
            GameStateManager.removeOverlay(this@Modal)
            this@Modal.close()
            clicks[`object`]?.let { it() }
        }
    }

    private fun <T : Actor> addActors(actors: List<T>) {
        actors.forEach { dialog.contentTable.add(it) }
    }

    init {
        dialog.setScale(scale)
        val dialogText = Label(text, skin)
        dialogText.wrap = true
        dialog.contentTable.add(dialogText).center().width(dialog.prefWidth * 0.9f)
        addActors(contentActors)
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
        GameStateManager.addOverlay(this)
        dialog.show(usedStage)
        // Centre the dialog on the screen with respect to the scale
        dialog.setPosition(
            ((usedStage.width - dialog.width * scale) / 2).roundToInt().toFloat(),
            ((usedStage.height - dialog.height * scale) / 2).roundToInt().toFloat()
        )
    }

    /**
     * Hides the modal, disposes the assets the modal uses
     * and removes the modal from the stage.
     */
    fun close() {
        dialog.hide()
        dialog.cancel() // Cancel hide() call in onClick in Dialog
        skin.dispose()
        dialog.remove() // Remove this dialog from the stage
    }

    private fun addButton(buttonText: String, onClick: (() -> Unit)?) {
        val uuid = UUID.randomUUID().toString()
        val button = TextButton(buttonText, skin)
        dialog.buttonTable
            .add(button)
            .width(Game.WIDTH / 6)
            .height(Game.HEIGHT / 9)
        dialog.setObject(button, uuid)
        clicks[uuid] = onClick
    }

    fun updateStage(newStage: Stage) {
        this.usedStage = newStage
        dialog.hide()
        dialog.show(usedStage)
        // Centre the dialog on the screen with respect to the scale
        dialog.setPosition(
            ((usedStage.width - dialog.width * scale) / 2).roundToInt().toFloat(),
            ((usedStage.height - dialog.height * scale) / 2).roundToInt().toFloat()
        )
    }
}
