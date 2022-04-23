package se.battlegoo.battlegoose.models.tutorial

class Tutorial(tutorialSteps: List<TutorialStep>) {

    private val steps: List<TutorialStep> = tutorialSteps.mapIndexed { index, step ->
        var first = false
        var last = false
        when {
            tutorialSteps.size == 1 -> {
                first = true
                last = true
            }
            index == 0 -> first = true
            index == tutorialSteps.size - 1 -> last = true
        }

        TutorialStep(step.tutorialSprite, step.headerText, step.tutorialText, step.extraText, first, last)
    }.toList()

    val size: Int
        get() = steps.size

    operator fun get(i: Int): TutorialStep {
        return steps[i]
    }
}
