package se.battlegoo.battlegoose.models.tutorial

class Tutorial(tutorialSteps: List<TutorialStep>) {

    private val steps: List<TutorialStep> = tutorialSteps.mapIndexed { index, step ->
        step.copy(
            first = tutorialSteps.size == 1 || index == 0,
            last = tutorialSteps.size == 1 || index == tutorialSteps.size - 1
        )
    }

    val size: Int by steps::size

    operator fun get(i: Int): TutorialStep = steps[i]
}
