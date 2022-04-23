package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.views.BattleMapTileState
import se.battlegoo.battlegoose.views.BattleMapTileView
import se.battlegoo.battlegoose.views.ClickObserver

class BattleMapTileController(
    private val tileView: BattleMapTileView,
    private val onTileClick: (tileController: BattleMapTileController) -> Unit
) : ControllerBase(tileView) {

    var state: BattleMapTileState by tileView::state
    var showHint: Boolean by tileView::showHint

    init {
        tileView.subscribe(object : ClickObserver {
            override fun onClick() {
                onTileClick(this@BattleMapTileController)
            }
        })
    }

    override fun update(dt: Float) = tileView.registerInput()
}
