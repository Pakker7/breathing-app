package com.breathing.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.breathing.app.data.DataStore
import com.breathing.app.models.BreathingConfig
import com.breathing.app.models.SessionRecord
import com.breathing.app.screens.*
import com.breathing.app.ui.theme.BreathingAppTheme
import com.breathing.app.utils.AudioManager
import com.breathing.app.utils.DateUtils
import com.breathing.app.viewmodel.BreathingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Configuration : Screen("configuration")
    object Breathing : Screen("breathing")
    object Completion : Screen("completion")
    object History : Screen("history")
}

class MainActivity : ComponentActivity() {
    private lateinit var audioManager: AudioManager
    private lateinit var dataStore: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        audioManager = AudioManager(this)
        dataStore = DataStore(this)
        
        setContent {
            BreathingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
    }

    @androidx.compose.runtime.Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val viewModel: BreathingViewModel = viewModel { BreathingViewModel(dataStore) }
        
        val currentConfig by viewModel.currentConfig.collectAsState()
        val presets by viewModel.presets.collectAsState()
        val audioSettings by viewModel.audioSettings.collectAsState()
        
        // Update audio manager when settings change
        LaunchedEffect(audioSettings) {
            audioManager.updateSettings(audioSettings)
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Configuration.route
        ) {
            composable(Screen.Configuration.route) {
                ConfigurationScreen(
                    presets = presets,
                    currentConfig = currentConfig,
                    audioSettings = audioSettings,
                    onConfigChange = { config ->
                        viewModel.updateConfig(config)
                    },
                    onStart = {
                        navController.navigate(Screen.Breathing.route)
                    },
                    onShowHistory = {
                        navController.navigate(Screen.History.route)
                    },
                    onShowSettings = {
                        // Settings will be handled in ConfigurationScreen
                    },
                    onSavePreset = { name ->
                        viewModel.savePreset(name, currentConfig)
                    },
                    onAudioSettingsChange = { settings ->
                        viewModel.updateAudioSettings(settings)
                    }
                )
            }

            composable(Screen.Breathing.route) {
                BreathingScreen(
                    config = currentConfig,
                    audioSettings = audioSettings,
                    audioManager = audioManager,
                    onComplete = { completedSets, duration ->
                        val record = SessionRecord(
                            date = DateUtils.getTodayDateString(),
                            pattern = currentConfig.getPattern(),
                            sets = completedSets,
                            duration = duration
                        )
                        viewModel.saveSessionRecord(record)
                        
                        // Navigate to completion screen with data
                        navController.navigate("${Screen.Completion.route}/$completedSets/$duration") {
                            popUpTo(Screen.Breathing.route) { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    },
                    onAudioSettingsChange = { settings ->
                        viewModel.updateAudioSettings(settings)
                    }
                )
            }

            composable(
                route = "${Screen.Completion.route}/{completedSets}/{duration}",
                arguments = listOf(
                    navArgument("completedSets") { type = NavType.IntType },
                    navArgument("duration") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val completedSets = backStackEntry.arguments?.getInt("completedSets") ?: 0
                val duration = backStackEntry.arguments?.getInt("duration") ?: 0
                
                // Get today's session count
                var todaySessionCount by mutableStateOf(0)
                LaunchedEffect(Unit) {
                    val history = dataStore.getHistory()
                    val today = java.util.Date()
                    todaySessionCount = history.count { record ->
                        val recordDate = java.text.SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            java.util.Locale.getDefault()
                        ).parse(record.date)
                        recordDate != null && recordDate.date == today.date &&
                                recordDate.month == today.month &&
                                recordDate.year == today.year
                    } + 1 // +1 for current session
                }
                
                CompletionScreen(
                    config = currentConfig,
                    completedSets = completedSets,
                    duration = duration,
                    todaySessionCount = todaySessionCount,
                    onRestart = {
                        navController.navigate(Screen.Breathing.route) {
                            popUpTo(Screen.Configuration.route) { inclusive = false }
                        }
                    },
                    onBackToHome = {
                        navController.navigate(Screen.Configuration.route) {
                            popUpTo(Screen.Configuration.route) { inclusive = true }
                        }
                    },
                    onShowHistory = {
                        navController.navigate(Screen.History.route)
                    }
                )
            }

            composable(Screen.History.route) {
                var history by mutableStateOf<List<SessionRecord>>(emptyList())
                
                LaunchedEffect(Unit) {
                    history = dataStore.getHistory()
                }
                
                HistoryScreen(
                    history = history,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

