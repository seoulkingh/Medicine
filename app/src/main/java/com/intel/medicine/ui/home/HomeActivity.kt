// ui/home/HomeActivity.kt
package com.intel.medicine.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.intel.medicine.ui.camera.CameraActivity
import com.intel.medicine.ui.list.MyListActivity
import com.intel.medicine.ui.alarm.AlarmListActivity
import com.intel.medicine.ui.settings.SettingsActivity
import com.intel.medicine.util.showToast

class HomeActivity : ComponentActivity() {

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCameraActivity()
        } else {
            showToast("카메라 권한이 필요합니다")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                HomeScreen(
                    onCameraClick = {
                        checkCameraPermissionAndStart()
                    },
                    onMyListClick = { startMyListActivity() },
                    onAlarmClick = { startAlarmActivity() },
                    onSettingsClick = { startSettingsActivity() }
                )
            }
        }
    }

    private fun checkCameraPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCameraActivity()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCameraActivity() {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    private fun startMyListActivity() {
        startActivity(Intent(this, MyListActivity::class.java))
    }

    private fun startAlarmActivity() {
        startActivity(Intent(this, AlarmListActivity::class.java))
    }

    private fun startSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCameraClick: () -> Unit = {},
    onMyListClick: () -> Unit = {},
    onAlarmClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            "바른약 길잡이",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            "내 손안의 약국",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 메인 카메라 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                onClick = onCameraClick,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 카메라 아이콘 배경
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50),
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "카메라",
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "사진으로 약물 찾기",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "약이나 영양제를 촬영하면\nAI가 자동으로 정보를 알려드려요",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 하단 메뉴 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeBottomButton(
                    icon = Icons.Default.List,
                    text = "내 목록",
                    description = "저장된\n약물보기",
                    onClick = onMyListClick
                )

                HomeBottomButton(
                    icon = Icons.Default.Alarm,
                    text = "알람",
                    description = "복용시간\n알림설정",
                    onClick = onAlarmClick
                )

                HomeBottomButton(
                    icon = Icons.Default.Settings,
                    text = "설정",
                    description = "앱 환경\n설정하기",
                    onClick = onSettingsClick
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 하단 정보
            Text(
                "안전한 복용을 위해 의사나 약사와 상담하세요",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeBottomButton(
    icon: ImageVector,
    text: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            onClick = onClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = text,
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF4CAF50)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )

        Text(
            text = description,
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}