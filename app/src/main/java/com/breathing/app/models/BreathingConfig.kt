package com.breathing.app.models

data class BreathingConfig(
    val inhale: Int = 4,
    val hold: Int = 7,
    val exhale: Int = 8,
    val sets: Int = 4
) {
    fun getPattern(): String = "$inhale-$hold-$exhale"
}

