package se.battlegoo.battlegoose.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Logger
import java.util.UUID
import se.battlegoo.battlegoose.Game

class ModalClass(
    private val title: String,
    private val text: String,
    type: ModalType
) {


    val clicks = HashMap<String, (() -> Unit)?>()

    private val dialog = object : Dialog(title, Game.skin) {
        override fun result(`object`: Any?) {
            if (`object` == null || `object` !is String)
                throw IllegalArgumentException(
                    "Object passed to Dialog result method is not a String."
                )
            if (!clicks.containsKey(`object`))
                throw IllegalArgumentException(
                    "Object passed to Dialog result function is not in possible clicks."
                )
            Logger("ulrik").error("Result called modal $`object`")
            clicks[`object`]?.let { it() }
        }
    }


    init {
        dialog.text(text)
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
        Logger("ulrik").error("Modal init")
        dialog.show(Game.stage)
    }

    private fun addButton(buttonText: String, onClick: (() -> Unit)?) {
        val uuid = UUID.randomUUID().toString()
        dialog.button(buttonText, uuid)
        clicks[uuid] = onClick
    }

}
