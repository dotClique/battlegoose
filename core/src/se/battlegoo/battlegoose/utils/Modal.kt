package se.battlegoo.battlegoose.utils

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Logger
import java.util.UUID
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.GameStateManager

typealias CallBtnFunc = (((() -> Unit)?) -> Unit)?

object Modal {

    private fun createModal(modal: ModalClass): CallBtnFunc {
        val uuid = UUID.randomUUID().toString()
        GameStateManager.overlay += 1
        return { func -> callModalBtnFunc(uuid, func) }
    }

    private fun callModalBtnFunc(uuid: String, func: (() -> Unit)?) {
        func?.invoke()
        GameStateManager.overlay -= 1
    }

    fun error(title: String, text: String, onOk: (() -> Unit)? = null) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(ModalClass(title, text, ModalType.Error {
            callFuncCall?.invoke(onOk)
        }))
    }

    fun info(title: String, text: String, onOk: (() -> Unit)? = null) {
        Logger("ulrik").error("Info called")
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(ModalClass(title, text, ModalType.Info {
            callFuncCall?.invoke(onOk)
        }))
    }

    fun warning(
        title: String,
        text: String,
        onYes: (() -> Unit)? = null,
        onNo: (() -> Unit)? = null
    ) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(
            ModalClass(
                title, text, ModalType.Warning(
                    onYes = { callFuncCall?.invoke(onYes) },
                    onNo = { callFuncCall?.invoke(onNo) })
            )
        )
    }

    fun question(
        title: String,
        text: String,
        onYes: (() -> Unit)? = null,
        onNo: (() -> Unit)? = null
    ) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(
            ModalClass(
                title, text, ModalType.Question(
                    onYes = { callFuncCall?.invoke(onYes) },
                    onNo = { callFuncCall?.invoke(onNo) })
            )
        )
    }
}
