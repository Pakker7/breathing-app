package com.breathing.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breathing.app.models.SessionRecord
import com.breathing.app.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    history: List<SessionRecord>,
    onBack: () -> Unit
) {
    val stats = remember(history) {
        val weekAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }
        
        val weeklyRecords = history.filter { record ->
            val recordDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                .parse(record.date)
            recordDate != null && recordDate.after(weekAgo.time)
        }
        
        val totalSessions = weeklyRecords.size
        val totalDuration = weeklyRecords.sumOf { it.duration }
        
        val patternCounts = weeklyRecords.groupingBy { it.pattern }.eachCount()
        val mostUsedPattern = patternCounts.maxByOrNull { it.value }?.key ?: "N/A"
        
        WeeklyStats(totalSessions, totalDuration, mostUsedPattern)
    }

    val groupedHistory = remember(history) {
        history.groupBy { record ->
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                .parse(record.date) ?: Date()
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        }.map { (dateKey, records) ->
            DateGroup(records.first().date, records)
        }
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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "기록",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Weekly Stats
                item {
                    Text(
                        text = "이번 주 통계",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "총 호흡 세션: ${stats.totalSessions}회",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "총 소요 시간: ${DateUtils.formatDurationShort(stats.totalDuration)}",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "가장 많이 사용: ${stats.mostUsedPattern} 호흡법",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Recent History
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "최근 기록",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (groupedHistory.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.1f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "아직 기록이 없습니다",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                } else {
                    items(groupedHistory) { group ->
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
                                    text = DateUtils.formatDate(group.date),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                group.records.forEach { record ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "• ${record.pattern}, ${record.sets}세트",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = DateUtils.formatTime(record.date),
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class WeeklyStats(
    val totalSessions: Int,
    val totalDuration: Int,
    val mostUsedPattern: String
)

data class DateGroup(
    val date: String,
    val records: List<SessionRecord>
)

