package se.battlegoo.battlegoose.utils

import kotlin.math.cos

class WavePulsator(min: Float, max: Float, private val length: Int, private val pulses: Int) {

    private var tick = 0
    private val waveStepSize = (2 * Math.PI.toFloat() / length)
    private var wave = (0 until length).map {
        ((max - min) * cos(it * waveStepSize + Math.PI.toFloat()) + (max + min)) / 2f
    }

    fun tick() {
        if (!isDone()) tick++
    }

    fun value(): Float = wave[tick % length]

    fun isDone(): Boolean = tick > pulses * length

    fun reset() {
        tick = 0
    }
}
