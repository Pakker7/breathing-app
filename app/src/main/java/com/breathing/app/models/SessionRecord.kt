package com.breathing.app.models

data class SessionRecord(
    val date: String,
    val pattern: String,
    val sets: Int,
    val duration: Int, // seconds
    val presetName: String? = null
)

