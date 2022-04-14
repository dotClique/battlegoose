package se.battlegoo.battlegoose.views

import se.battlegoo.battlegoose.Game

class Menu {
    companion object {
        const val BOTTOM_SPACING = 80f // y-axis offset for menu screen options
        const val SPACER = 50f // spacer between menu screen option

        // width of main menu buttons
        const val BUTTON_WIDTH = ((Game.WIDTH - 5 * SPACER) / 4).toInt()
    }
}
