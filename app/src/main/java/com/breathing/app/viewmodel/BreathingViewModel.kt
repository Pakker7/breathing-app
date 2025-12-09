package com.breathing.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breathing.app.data.DataStore
import com.breathing.app.models.BreathingConfig
import com.breathing.app.models.Preset
import com.breathing.app.models.SessionRecord
import com.breathing.app.models.AudioSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BreathingViewModel(private val dataStore: DataStore) : ViewModel() {

    val defaultPresets = listOf(
        Preset("4-7-8 호흡법", BreathingConfig(4, 7, 8, 4), true),
        Preset("5-5-5 균형 호흡", BreathingConfig(5, 5, 5, 5), true),
        Preset("6-6-6 깊은 호흡", BreathingConfig(6, 6, 6, 3), true)
    )

    private val _presets = MutableStateFlow<List<Preset>>(defaultPresets)
    val presets: StateFlow<List<Preset>> = _presets.asStateFlow()

    private val _currentConfig = MutableStateFlow<BreathingConfig>(BreathingConfig(4, 7, 8, 4))
    val currentConfig: StateFlow<BreathingConfig> = _currentConfig.asStateFlow()

    private val _audioSettings = MutableStateFlow<AudioSettings>(AudioSettings())
    val audioSettings: StateFlow<AudioSettings> = _audioSettings.asStateFlow()

    init {
        loadPresets()
        loadAudioSettings()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            _presets.value = dataStore.getPresets(defaultPresets)
        }
    }

    private fun loadAudioSettings() {
        viewModelScope.launch {
            _audioSettings.value = dataStore.getAudioSettings()
        }
    }

    fun updateConfig(config: BreathingConfig) {
        _currentConfig.value = config
    }

    fun savePreset(name: String, config: BreathingConfig) {
        viewModelScope.launch {
            val newPreset = Preset(name, config)
            val updated = _presets.value.filter { !it.isDefault } + newPreset
            _presets.value = defaultPresets + updated
            dataStore.savePresets(_presets.value)
        }
    }

    fun saveSessionRecord(record: SessionRecord) {
        viewModelScope.launch {
            dataStore.saveSessionRecord(record)
        }
    }

    fun updateAudioSettings(settings: AudioSettings) {
        viewModelScope.launch {
            _audioSettings.value = settings
            dataStore.saveAudioSettings(settings)
        }
    }
}

