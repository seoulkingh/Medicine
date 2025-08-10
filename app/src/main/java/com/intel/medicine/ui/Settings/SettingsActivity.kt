// ui/settings/SettingsActivity.kt
package com.intel.medicine.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

class SettingsActivity : ComponentActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("medicine_settings", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme {
                SettingsScreen(
                    onBackClick = { finish() },
                    preferences = preferences
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    preferences: SharedPreferences? = null
) {
    var selectedTextSize by remember {
        mutableStateOf(preferences?.getString("text_size", "크게") ?: "크게")
    }
    var alarmSoundEnabled by remember {
        mutableStateOf(preferences?.getBoolean("alarm_sound", true) ?: true)
    }
    var alarmVibrationEnabled by remember {
        mutableStateOf(preferences?.getBoolean("alarm_vibration", true) ?: true)
    }
    var notificationEnabled by remember {
        mutableStateOf(preferences?.getBoolean("notification", true) ?: true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "설정",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 앱 설정 섹션
            SettingSectionHeader("앱 설정")
            Spacer(modifier = Modifier.height(8.dp))

            // 글자 크기 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = CircleShape,
                            color = Color(0xFF4CAF50)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Aa",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                "글자 크기",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "앱 내 텍스트 크기를 조절합니다",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 글자 크기 옵션 버튼들
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextSizeButton(
                            text = "보통",
                            isSelected = selectedTextSize == "보통",
                            onClick = {
                                selectedTextSize = "보통"
                                preferences?.edit()?.putString("text_size", "보통")?.apply()
                            }
                        )
                        TextSizeButton(
                            text = "크게",
                            isSelected = selectedTextSize == "크게",
                            onClick = {
                                selectedTextSize = "크게"
                                preferences?.edit()?.putString("text_size", "크게")?.apply()
                            }
                        )
                        TextSizeButton(
                            text = "아주 크게",
                            isSelected = selectedTextSize == "아주 크게",
                            onClick = {
                                selectedTextSize = "아주 크게"
                                preferences?.edit()?.putString("text_size", "아주 크게")?.apply()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 미리보기
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF8F9FA),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "미리보기: 타이레놀정 500mg",
                            fontSize = when(selectedTextSize) {
                                "보통" -> 14.sp
                                "크게" -> 16.sp
                                "아주 크게" -> 18.sp
                                else -> 16.sp
                            },
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 알람 설정 섹션
            SettingSectionHeader("알람 설정")
            Spacer(modifier = Modifier.height(8.dp))

            // 알림 활성화 설정
            SettingsToggleCard(
                icon = Icons.Default.Notifications,
                title = "알림 받기",
                subtitle = "약 복용 시간에 알림을 받습니다",
                iconColor = Color(0xFF4CAF50),
                checked = notificationEnabled,
                onCheckedChange = {
                    notificationEnabled = it
                    preferences?.edit()?.putBoolean("notification", it)?.apply()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 알람 소리 설정
            SettingsToggleCard(
                icon = Icons.Default.VolumeUp,
                title = "알람 소리",
                subtitle = "알람과 함께 소리로 알려드립니다",
                iconColor = Color(0xFF2196F3),
                checked = alarmSoundEnabled,
                onCheckedChange = {
                    alarmSoundEnabled = it
                    preferences?.edit()?.putBoolean("alarm_sound", it)?.apply()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 알람 진동 설정
            SettingsToggleCard(
                icon = Icons.Default.Vibration,
                title = "알람 진동",
                subtitle = "알람과 함께 진동으로 알려드립니다",
                iconColor = Color(0xFFFF9800),
                checked = alarmVibrationEnabled,
                onCheckedChange = {
                    alarmVibrationEnabled = it
                    preferences?.edit()?.putBoolean("alarm_vibration", it)?.apply()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 정보 섹션
            SettingSectionHeader("앱 정보")
            Spacer(modifier = Modifier.height(8.dp))

            // 앱 버전 정보
            SettingsInfoCard(
                icon = Icons.Default.Info,
                title = "앱 버전",
                subtitle = "바른약 길잡이 v1.0.0",
                iconColor = Color(0xFF9C27B0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 개발자 정보
            SettingsInfoCard(
                icon = Icons.Default.Code,
                title = "개발자",
                subtitle = "Intel Medicine Team",
                iconColor = Color(0xFF607D8B)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 하단 안내 문구
            Text(
                "안전한 약물 복용을 위해 의사나 약사와 상담하세요",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingSectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSizeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            androidx.compose.material3.Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF4CAF50),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFF0F0F0),
            labelColor = Color(0xFF666666)
        )
    )
}

@Composable
fun SettingsToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = iconColor
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50)
                )
            )
        }
    }
}

@Composable
fun SettingsInfoCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = iconColor
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}