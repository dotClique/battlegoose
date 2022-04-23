package se.battlegoo.battlegoose.controllers

import com.badlogic.gdx.Gdx
import se.battlegoo.battlegoose.models.tutorial.TutorialStep
import se.battlegoo.battlegoose.models.tutorial.Tutorial
import se.battlegoo.battlegoose.views.ITutorialViewController
import se.battlegoo.battlegoose.views.TutorialView
import se.battlegoo.battlegoose.views.TutorialViewModel

class TutorialController(
    private val view: TutorialView,
    private val tutorial: Tutorial,
    private val goBack: () -> Unit,
    private val goForward: () -> Unit
) : ControllerBase(view), ITutorialViewController {

    private var currentStep: Int = 0

    init {
        view.registerController(this)
        showTutorialStep(tutorial[currentStep])
    }

    private fun showTutorialStep(step: TutorialStep) {
        view.showTutorialPage(
            TutorialViewModel(step.tutorialSprite, step.headerText, step.tutorialText, step.extraText, step.first, step.last)
        )
    }

    override fun update(dt: Float) {
        view.registerInput()
    }

    override fun onClickBack() {
        if (currentStep > 0)
            showTutorialStep(tutorial[--currentStep])
        else
            goBack() // TODO: Not accessible currently as the button is hidden when at first step... Is it needed?
    }

    override fun onClickForward() {
        if (currentStep < tutorial.size - 1)
            showTutorialStep(tutorial[++currentStep])
        else
            goForward()
    }
}
