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
import se.battlegoo.battlegoose.views.Skins

class ModalClass(
    private val title: String,
    private val text: String? = null,
    type: ModalType,
    private var stage: Stage,
    private val scale: Float = 1f,
    private val contentActors: List<Actor>? = null,
    private val minWidth: Float? = null,
    private val minHeight: Float? = null
) {

    private var skin: Skin = Skin(Gdx.files.internal(Skins.STAR_SOLDIER.filepath))
    val clicks = HashMap<String, (() -> Unit)?>()

    private val dialog = object : Dialog("\t$title", skin) {

        override fun getPrefHeight(): Float = this@ModalClass.minHeight ?: Game.HEIGHT / 2
        override fun getPrefWidth(): Float = this@ModalClass.minWidth ?: Game.WIDTH / 2

        override fun result(`object`: Any?) {
            if (`object` == null || `object` !is String)
                throw IllegalArgumentException(
                    "Object passed to Dialog result method is not a String."
                )
            if (!clicks.containsKey(`object`))
                throw IllegalArgumentException(
                    "Object passed to Dialog result function is not in possible clicks."
                )
            hide()
            cancel() // Cancel hide() call in onClick in Dialog
            skin.dispose()
            remove() // Remove this dialog from the stage
            clicks[`object`]?.let { it() }
        }
    }

    private fun <T : Actor> addActors(actors: List<T>?) {
        actors?.forEach { dialog.contentTable.add(it) }
    }

    init {
        dialog.setScale(scale)
        val dialogText = Label(text, skin)
        if (text != null) dialog.text(dialogText)
        addActors(contentActors)
        dialog.isMovable = false
//        if (minWidth != null) dialog.background.minWidth= minWidth
//        if (minHeight != null) dialog.background.minHeight = minHeight
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
        this.stage = newStage
        dialog.hide()
        show()
    }

}
