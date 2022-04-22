package se.battlegoo.battlegoose.utils

sealed class ModalType(
) {
    data class Error(val onOk: (() -> Unit)? = null) : ModalType()
    data class Info(val onOk: (() -> Unit)? = null) : ModalType()
    data class Warning(val onYes: (() -> Unit)? = null, val onNo: (() -> Unit)? = null) :
        ModalType()
    data class Question(val onYes: (() -> Unit)? = null, val onNo: (() -> Unit)? = null) :
        ModalType()
}
