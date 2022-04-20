package se.battlegoo.battlegoose.controllers

import se.battlegoo.battlegoose.network.MultiplayerService
import se.battlegoo.battlegoose.views.ChangeUsernameView

class ChangeUsernameController(private val view: ChangeUsernameView) : ControllerBase(view) {

    companion object {
        private const val DEFAULT_USERNAME = "Anonymous"
    }

    override fun update(dt: Float) {
        view.registerInput()
    }

    init {
        view.usernameChangedListener = { new ->
            val old = view.username
            MultiplayerService.setUsername(new) { succeeded ->
                view.resolveUsernameChange(if (succeeded) new else old)
            }
        }

        // Load username, setting to default if none exists
        MultiplayerService.getUsername { username ->
            if (username != null) {
                view.resolveUsernameChange(username)
            } else {
                MultiplayerService.setUsername(DEFAULT_USERNAME) {
                    view.resolveUsernameChange(DEFAULT_USERNAME)
                }
            }
        }
    }
}
