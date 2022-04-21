package se.battlegoo.battlegoose.utils

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import se.battlegoo.battlegoose.Game
import se.battlegoo.battlegoose.gamestates.GameStateManager

typealias CallBtnFunc = (((() -> Unit)?) -> Unit)?

object Modal {

    private val modalList = mutableListOf<ModalClass>()

    private fun createModal(modal: ModalClass): CallBtnFunc {
        GameStateManager.addOverlay()
        modalList.add(modal)
        modal.show()
        return { callModalBtnFunc(modal, it) }
    }

    private fun callModalBtnFunc(modal: ModalClass, func: (() -> Unit)?) {
        modalList.remove(modal)
        func?.invoke()
        GameStateManager.removeOverlay()
    }

    fun error(
        title: String,
        text: String,
        onOk: (() -> Unit)? = null,
        scale: Float = 1f,
        contentActors: List<Actor>? = null
    ) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(
            ModalClass(
                title,
                text,
                ModalType.Error { callFuncCall?.invoke(onOk) },
                Game.stage,
                scale,
                contentActors
            )
        )
    }

    fun info(
        title: String,
        text: String,
        onOk: (() -> Unit)? = null,
        scale: Float = 1f,
        contentActors: List<Actor>? = null
    ) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(
            ModalClass(
                title,
                text,
                ModalType.Info {
                    callFuncCall?.invoke(onOk)
                },
                Game.stage,
                scale
            )
        )
    }

    fun warning(
        title: String,
        text: String,
        onYes: (() -> Unit)? = null,
        onNo: (() -> Unit)? = null,
        scale: Float = 1f,
        contentActors: List<Actor>? = null

    ) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(
            ModalClass(
                title, text,
                scale = scale,
                stage = Game.stage,
                contentActors = contentActors,
                type = ModalType.Warning(
                    onYes = { callFuncCall?.invoke(onYes) },
                    onNo = { callFuncCall?.invoke(onNo) }
                ),
            )
        )
    }

    fun question(
        title: String,
        text: String,
        onYes: (() -> Unit)? = null,
        onNo: (() -> Unit)? = null,
        scale: Float = 1f,
        contentActors: List<Actor>? = null
    ) {
        var callFuncCall: CallBtnFunc = null
        callFuncCall = createModal(
            ModalClass(
                title, text,
                scale = scale,
                stage = Game.stage,
                contentActors = contentActors,
                type = ModalType.Question(
                    onYes = { callFuncCall?.invoke(onYes) },
                    onNo = { callFuncCall?.invoke(onNo) }
                ),
            )
        )
    }

    fun changeStage(newStage: Stage) {
        modalList.forEach { it.updateStage(newStage) }
    }
}
