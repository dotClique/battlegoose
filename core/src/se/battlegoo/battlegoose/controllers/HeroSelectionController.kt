package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.models.heroes.HeroSelection
import se.battlegoo.battlegoose.views.HeroSelectionView

class HeroSelectionController(heroSelection: HeroSelection) : ControllerBase(HeroSelectionView(heroSelection)) {
    override fun update(dt: Float) {
    }
}
