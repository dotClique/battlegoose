package se.battlegoo.battlegoose.utils

sealed class ModalType(
    onFirstButtonClick: (() -> Unit)? = null,
    onSecondButtonClick: (() -> Unit)? = null
) {
    data class Error(val onOk: (() -> Unit)? = null) : ModalType(onOk)
    data class Info(val onOk: (() -> Unit)? = null) : ModalType(onOk)
    data class Warning(val onYes: (() -> Unit)? = null, val onNo: (() -> Unit)? = null) :
        ModalType(onYes, onNo)
    data class Question(val onYes: (() -> Unit)? = null, val onNo: (() -> Unit)? = null) :
        ModalType(onYes, onNo)
}
