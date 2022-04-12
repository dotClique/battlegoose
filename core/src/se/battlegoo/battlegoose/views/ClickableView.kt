package se.battlegoo.battlegoose.views

interface ClickableView {
    fun subscribe(observer: ClickObserver)
    fun registerInput()
}
