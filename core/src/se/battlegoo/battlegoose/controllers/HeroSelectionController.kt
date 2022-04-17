package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.views.HeroSelectionView

class HeroSelectionController(
    private val view: HeroSelectionView,
    heroSelection: HeroSelection
) : ControllerBase(
    view
) {
    override fun update(dt: Float) {
        view.registerInput()
    }
}
