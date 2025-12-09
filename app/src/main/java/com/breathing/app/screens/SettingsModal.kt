package com.breathing.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.breathing.app.models.AudioSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsModal(
    audioSettings: AudioSettings,
    onSettingsChange: (AudioSettings) -> Unit,
    onClose: () -> Unit
) {
    var settings by remember { mutableStateOf(audioSettings) }

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF667eea)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "설정",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sound Settings
                Text(
                    text = "🔊 소리 설정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Volume Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "효과음",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${settings.volume}%",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Slider(
                    value = settings.volume.toFloat(),
                    onValueChange = { value ->
                        settings = settings.copy(volume = value.toInt())
                        onSettingsChange(settings)
                    },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Voice Guide Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "음성가이드",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = settings.voiceGuide,
                        onCheckedChange = { checked ->
                            settings = settings.copy(voiceGuide = checked)
                            onSettingsChange(settings)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Effects Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "효과음",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = settings.soundEffects,
                        onCheckedChange = { checked ->
                            settings = settings.copy(soundEffects = checked)
                            onSettingsChange(settings)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Background Sound
                Text(
                    text = "배경음",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = when (settings.backgroundSound) {
                            "waves" -> "파도소리"
                            "rain" -> "빗소리"
                            "whitenoise" -> "백색소음"
                            else -> "없음"
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.15f),
                            focusedContainerColor = Color.White.copy(alpha = 0.2f),
                            unfocusedTextColor = Color.White
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
                            text = { Text("없음") },
                            onClick = {
                                settings = settings.copy(backgroundSound = "none")
                                onSettingsChange(settings)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("파도소리") },
                            onClick = {
                                settings = settings.copy(backgroundSound = "waves")
                                onSettingsChange(settings)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("빗소리") },
                            onClick = {
                                settings = settings.copy(backgroundSound = "rain")
                                onSettingsChange(settings)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("백색소음") },
                            onClick = {
                                settings = settings.copy(backgroundSound = "whitenoise")
                                onSettingsChange(settings)
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // App Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "ℹ️ 앱 정보",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "숨쉬기 v1.0\n명상과 이완을 위한 호흡 가이드 앱입니다.",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Close Button
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Text("닫기", color = Color.White)
                }
            }
        }
    }
}

