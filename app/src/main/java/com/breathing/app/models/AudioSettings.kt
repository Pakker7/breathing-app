package com.breathing.app.models

data class AudioSettings(
    val volume: Int = 60,
    val voiceGuide: Boolean = true,
    val soundEffects: Boolean = true,
    val backgroundSound: String = "none"
)

