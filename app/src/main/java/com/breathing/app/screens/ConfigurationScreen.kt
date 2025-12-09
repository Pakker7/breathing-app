package com.breathing.app.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.breathing.app.models.BreathingConfig
import com.breathing.app.models.Preset
import com.breathing.app.models.AudioSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    presets: List<Preset>,
    currentConfig: BreathingConfig,
    audioSettings: AudioSettings,
    onConfigChange: (BreathingConfig) -> Unit,
    onStart: () -> Unit,
    onShowHistory: () -> Unit,
    onShowSettings: () -> Unit,
    onSavePreset: (String) -> Unit,
    onAudioSettingsChange: (AudioSettings) -> Unit
) {
    var config by remember { mutableStateOf(currentConfig) }
    var selectedPreset by remember { mutableStateOf("4-7-8 호흡법") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }

    LaunchedEffect(currentConfig) {
        config = currentConfig
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Settings Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 43.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showSettingsDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "설정",
                        tint = Color.White
                    )
                }
            }

            // Title
            Text(
                text = "호흡 설정",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Card Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Preset Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedPreset,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("프리셋 선택", color = Color.White) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                                focusedContainerColor = Color.White.copy(alpha = 0.2f),
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("커스텀") },
                                onClick = {
                                    selectedPreset = "커스텀"
                                    expanded = false
                                }
                            )
                            presets.forEach { preset ->
                                DropdownMenuItem(
                                    text = { Text(preset.name) },
                                    onClick = {
                                        selectedPreset = preset.name
                                        config = preset.config
                                        onConfigChange(config)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Number Inputs
                    NumberInput(
                        label = "들숨:",
                        value = config.inhale,
                        unit = "초",
                        onDecrement = {
                            config = config.copy(inhale = (config.inhale - 1).coerceAtLeast(1))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        },
                        onIncrement = {
                            config = config.copy(inhale = (config.inhale + 1).coerceAtMost(20))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        }
                    )

                    Spacer(modifier = Modifier.height(11.dp))

                    NumberInput(
                        label = "멈춤:",
                        value = config.hold,
                        unit = "초",
                        onDecrement = {
                            config = config.copy(hold = (config.hold - 1).coerceAtLeast(1))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        },
                        onIncrement = {
                            config = config.copy(hold = (config.hold + 1).coerceAtMost(20))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        }
                    )

                    Spacer(modifier = Modifier.height(11.dp))

                    NumberInput(
                        label = "날숨:",
                        value = config.exhale,
                        unit = "초",
                        onDecrement = {
                            config = config.copy(exhale = (config.exhale - 1).coerceAtLeast(1))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        },
                        onIncrement = {
                            config = config.copy(exhale = (config.exhale + 1).coerceAtMost(20))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        }
                    )

                    Spacer(modifier = Modifier.height(11.dp))

                    NumberInput(
                        label = "세트:",
                        value = config.sets,
                        unit = "회",
                        onDecrement = {
                            config = config.copy(sets = (config.sets - 1).coerceAtLeast(1))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        },
                        onIncrement = {
                            config = config.copy(sets = (config.sets + 1).coerceAtMost(20))
                            selectedPreset = "커스텀"
                            onConfigChange(config)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Select
                    Text(
                        text = "빠른 선택",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("4-7-8", "5-5-5", "6-6-6").forEach { pattern ->
                            val values = pattern.split("-").map { it.toInt() }
                            Button(
                                onClick = {
                                    config = config.copy(
                                        inhale = values[0],
                                        hold = values[1],
                                        exhale = values[2]
                                    )
                                    selectedPreset = "커스텀"
                                    onConfigChange(config)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Text(pattern, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(11.dp))

                    // Save Preset Button
                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(11.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Text("프리셋 저장하기", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(9.dp))

                    // Start Button
                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp),
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
                            Text("호흡 시작하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(9.dp))

                    // History Button
                    OutlinedButton(
                        onClick = onShowHistory,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("기록 보기")
                    }
                }
            }
        }

        // Save Preset Dialog
        if (showSaveDialog) {
            Dialog(onDismissRequest = { showSaveDialog = false }) {
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
                            .padding(22.dp)
                    ) {
                        Text(
                            text = "프리셋 저장",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        OutlinedTextField(
                            value = presetName,
                            onValueChange = { presetName = it },
                            placeholder = { Text("프리셋 이름을 입력하세요", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                                focusedContainerColor = Color.White.copy(alpha = 0.3f),
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showSaveDialog = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Text("취소")
                            }
                            OutlinedButton(
                                onClick = {
                                    if (presetName.isNotBlank()) {
                                        onSavePreset(presetName)
                                        presetName = ""
                                        showSaveDialog = false
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = presetName.isNotBlank(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Text("저장")
                            }
                        }
                    }
                }
            }
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsModal(
                audioSettings = audioSettings,
                onSettingsChange = onAudioSettingsChange,
                onClose = { showSettingsDialog = false }
            )
        }
    }
}

@Composable
fun NumberInput(
    label: String,
    value: Int,
    unit: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.width(54.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(43.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value.toString(),
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
        IconButton(
            onClick = onDecrement,
            modifier = Modifier
                .size(36.dp)
                .background(
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(18.dp)
                )
        ) {
            Text("-", color = Color.White, fontSize = 18.sp)
        }
        IconButton(
            onClick = onIncrement,
            modifier = Modifier
                .size(36.dp)
                .background(
                    Color.White.copy(alpha = 0.15f),
                    RoundedCornerShape(18.dp)
                )
        ) {
            Text("+", color = Color.White, fontSize = 18.sp)
        }
    }
}

