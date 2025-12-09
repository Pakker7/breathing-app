package com.breathing.app.models

data class Preset(
    val name: String,
    val config: BreathingConfig,
    val isDefault: Boolean = false
)

