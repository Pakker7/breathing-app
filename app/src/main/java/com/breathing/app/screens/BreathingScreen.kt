package com.breathing.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.breathing.app.models.BreathingConfig
import com.breathing.app.models.AudioSettings
import com.breathing.app.utils.AudioManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class BreathState {
    IDLE, INHALE, HOLD, EXHALE
}

@Composable
fun BreathingScreen(
    config: BreathingConfig,
    audioSettings: AudioSettings,
    audioManager: AudioManager,
    onComplete: (Int, Int) -> Unit,
    onBack: () -> Unit,
    onAudioSettingsChange: (AudioSettings) -> Unit
) {
    var state by remember { mutableStateOf(BreathState.INHALE) }
    var currentSet by remember { mutableStateOf(1) }
    var counter by remember { mutableStateOf(config.inhale) }
    var isPaused by remember { mutableStateOf(false) }
    var showPauseModal by remember { mutableStateOf(false) }
    var showVolumeControl by remember { mutableStateOf(false) }
    val startTime = remember { System.currentTimeMillis() }
    val scope = rememberCoroutineScope()

    // Animation for breathing circle
    val scale = remember {
        Animatable(
            if (state == BreathState.INHALE || state == BreathState.HOLD) 1.5f else 1f
        )
    }

    LaunchedEffect(state) {
        if (!isPaused) {
            val targetScale = if (state == BreathState.INHALE || state == BreathState.HOLD) 1.5f else 1f
            scale.animateTo(
                targetValue = targetScale,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
        }
    }

    // Breathing cycle logic
    LaunchedEffect(state, counter, isPaused) {
        if (state != BreathState.IDLE && !isPaused) {
            delay(1000)
            if (counter > 0) {
                audioManager.playTickSound()
                counter--
            } else {
                when (state) {
                    BreathState.INHALE -> {
                        state = BreathState.HOLD
                        counter = config.hold
                        audioManager.playTransitionSound("hold")
                        audioManager.speak("멈추세요")
                    }
                    BreathState.HOLD -> {
                        state = BreathState.EXHALE
                        counter = config.exhale
                        audioManager.playTransitionSound("exhale")
                        audioManager.speak("내쉬세요")
                    }
                    BreathState.EXHALE -> {
                        if (currentSet < config.sets) {
                            currentSet++
                            state = BreathState.INHALE
                            counter = config.inhale
                            audioManager.playTransitionSound("inhale")
                            audioManager.speak("들이쉬세요")
                        } else {
                            audioManager.speak("수고하셨습니다")
                            val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                            onComplete(config.sets, duration)
                        }
                    }
                    BreathState.IDLE -> {}
                }
            }
        }
    }

    // Initial start
    LaunchedEffect(Unit) {
        audioManager.playTransitionSound("inhale")
        audioManager.speak("들이쉬세요")
    }

    val stateText = when {
        isPaused -> "정지되었습니다"
        state == BreathState.IDLE -> "시작 버튼을 눌러주세요"
        state == BreathState.INHALE -> "들이쉬세요"
        state == BreathState.HOLD -> "멈추세요"
        state == BreathState.EXHALE -> "내쉬세요"
        else -> ""
    }

    val circleColor = when (state) {
        BreathState.INHALE -> Color(0xFF4ade80)
        BreathState.HOLD -> Color(0xFFfbbf24)
        BreathState.EXHALE -> Color(0xFFf87171)
        else -> Color.White.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currentSet/${config.sets} 세트",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    IconButton(onClick = { showVolumeControl = !showVolumeControl }) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "음량 조절",
                            tint = Color.White
                        )
                    }
                    if (showVolumeControl) {
                        VolumeControl(
                            settings = audioSettings,
                            onSettingsChange = onAudioSettingsChange,
                            onDismiss = { showVolumeControl = false },
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }
            }

            // Breathing Circle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .scale(scale.value)
                            .border(
                                width = 4.dp,
                                color = circleColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty circle
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Status Text
                    Text(
                        text = stateText,
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )

                    // Counter
                    Text(
                        text = counter.toString(),
                        fontSize = 72.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Control Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        audioManager.stopSpeaking()
                        isPaused = true
                        showPauseModal = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("일시정지")
                }

                OutlinedButton(
                    onClick = {
                        val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                        onComplete(currentSet - 1, duration)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("종료하기", color = Color(0xFFFF6B6B))
                }
            }
        }

        // Pause Modal
        if (showPauseModal) {
            Dialog(onDismissRequest = {}) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF667eea)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "일시정지 중",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = {
                                isPaused = false
                                showPauseModal = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF4ade80),
                                                Color(0xFF22d3ee)
                                            )
                                        )
                                    )
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("계속하기", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                isPaused = false
                                showPauseModal = false
                                currentSet = 1
                                state = BreathState.INHALE
                                counter = config.inhale
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("처음부터")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                                onComplete(currentSet - 1, duration)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFF6B6B)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFFF6B6B).copy(alpha = 0.5f))
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("종료하기", color = Color(0xFFFF6B6B))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VolumeControl(
    settings: AudioSettings,
    onSettingsChange: (AudioSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${settings.volume}%",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Slider(
                value = settings.volume.toFloat(),
                onValueChange = { value ->
                    onSettingsChange(settings.copy(volume = value.toInt()))
                },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("음성", fontSize = 14.sp, color = Color.Gray)
                Switch(
                    checked = settings.voiceGuide,
                    onCheckedChange = { checked ->
                        onSettingsChange(settings.copy(voiceGuide = checked))
                    }
                )
            }
        }
    }
}

