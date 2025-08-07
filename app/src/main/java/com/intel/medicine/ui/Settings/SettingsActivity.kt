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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Notifications
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 회원 설정 섹션
            SettingSectionHeader("회원 설정")

            // 글자 크기 설정 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(32.dp),
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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            "글자 크기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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

                    Text(
                        "미리보기: 노바스크정 5mg",
                        fontSize = when(selectedTextSize) {
                            "보통" -> 14.sp
                            "크게" -> 16.sp
                            "아주 크게" -> 18.sp
                            else -> 16.sp
                        },
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 알람 설정 섹션
            SettingSectionHeader("알람 설정")

            // 기본 알람 소리 설정
            SettingsCard(
                icon = Icons.Default.VolumeUp,
                title = "기본 알람 소리",
                subtitle = "약 복용 시간에 알람으로",
                iconColor = Color(0xFF4CAF50)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 알람 진동 설정
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "진동",
                            tint = Color.White,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "알람 진동",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "알람과 함께 진동으로 알려드립니다",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = alarmVibrationEnabled,
                        onCheckedChange = {
                            alarmVibrationEnabled = it
                            preferences?.edit()?.putBoolean("alarm_vibration", it)?.apply()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingSectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TextSizeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        } else {
            ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(36.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = iconColor
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

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
fun PreviewSettingsScreen() {
    MaterialTheme {
        SettingsScreen()
    }
}
