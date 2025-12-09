package com.breathing.app.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.breathing.app.models.AudioSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class AudioManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var soundPool: SoundPool? = null
    private var audioSettings: AudioSettings = AudioSettings()
    private var isTtsReady = false

    init {
        initializeTTS()
        initializeSoundPool()
    }

    private fun initializeTTS() {
        tts = TextToSpeech(context, this)
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.KOREAN
            isTtsReady = true
        }
    }

    fun updateSettings(settings: AudioSettings) {
        audioSettings = settings
    }

    fun speak(text: String) {
        if (!audioSettings.voiceGuide || !isTtsReady) return

        tts?.let {
            it.setSpeechRate(0.9f)
            it.setPitch(1.0f)
            val params = Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, audioSettings.volume / 100f)
            it.speak(text, TextToSpeech.QUEUE_FLUSH, params, null)
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun playTickSound() {
        if (!audioSettings.soundEffects) return
        // Generate a tick sound using SoundPool (simplified - you might want to add actual sound files)
        playTone(1000f, 50)
    }

    fun playTransitionSound(type: String) {
        if (!audioSettings.soundEffects) return
        when (type) {
            "inhale" -> {
                playTone(600f, 200)
                CoroutineScope(Dispatchers.Main).launch {
                    kotlinx.coroutines.delay(100)
                    playTone(800f, 200)
                }
            }
            "hold" -> playTone(700f, 150)
            "exhale" -> {
                playTone(800f, 250)
                CoroutineScope(Dispatchers.Main).launch {
                    kotlinx.coroutines.delay(100)
                    playTone(600f, 250)
                }
            }
        }
    }

    private fun playTone(frequency: Float, duration: Int) {
        // Note: This is a simplified implementation
        // For actual sound generation, you might want to use AudioTrack or include sound files
        // For now, we'll just trigger the SoundPool if it's available
        // In a real implementation, you'd load sound files and play them
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        soundPool?.release()
    }
}

