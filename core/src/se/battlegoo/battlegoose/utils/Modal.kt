package se.battlegoo.battlegoose.utils

import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.GameStateManager

typealias CallBtnFunc = (((() -> Unit)?) -> Unit)?

object Modal {

    private fun createModal(modal: ModalClass): CallBtnFunc {
        GameStateManager.addOverlay()
        modal.show()
        return { callModalBtnFunc(it) }
    }

    private fun callModalBtnFunc(func: (() -> Unit)?) {
        func?.invoke()
        GameStateManager.removeOverlay()
    }

    fun error(title: String, text: String, onOk: (() -> Unit)? = null) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(ModalClass(title, text, ModalType.Error {
            callFuncCall?.invoke(onOk)
        }, Game.stage))
    }

    fun info(title: String, text: String, onOk: (() -> Unit)? = null) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(ModalClass(title, text, ModalType.Info {
            callFuncCall?.invoke(onOk)
        }, Game.stage))
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
                title, text,
                stage = Game.stage,
                type = ModalType.Warning(
                    onYes = { callFuncCall?.invoke(onYes) },
                    onNo = { callFuncCall?.invoke(onNo) }),
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
                title, text,
                stage = Game.stage,
                type = ModalType.Question(
                    onYes = { callFuncCall?.invoke(onYes) },
                    onNo = { callFuncCall?.invoke(onNo) }),
            )
        )
    }
}
