// ui/home/HomeActivity.kt
package com.intel.medicine.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intel.medicine.ui.camera.CameraActivity
import com.intel.medicine.ui.list.MyListActivity
import com.intel.medicine.ui.alarm.AlarmListActivity
import com.intel.medicine.ui.settings.SettingsActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(
                onCameraClick = { startCameraActivity() },
                onMyListClick = { startMyListActivity() },
                onAlarmClick = { startAlarmActivity() },
                onSettingsClick = { startSettingsActivity() }
            )
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "바른약 길잡이",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "내 손안의 약국",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 카메라 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                onClick = onCameraClick,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "카메라",
                            modifier = Modifier.padding(20.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "사진 촬영 찾기",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "약이나 영양제를 촬영하여 정보를 확인하세요",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 하단 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeBottomButton(
                    icon = Icons.Default.List,
                    text = "내 목록",
                    onClick = onMyListClick
                )
                HomeBottomButton(
                    icon = Icons.Default.Alarm,
                    text = "알람",
                    onClick = onAlarmClick
                )
                HomeBottomButton(
                    icon = Icons.Default.Settings,
                    text = "설정",
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Composable
fun HomeBottomButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }  // 클릭 처리 추가
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = Color(0xFF4CAF50),
            onClick = onClick
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.padding(16.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
@Composable
fun HomeScreenWithCameraPermission(
    onCameraClick: () -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                onCameraClick()
            } else {
                Toast.makeText(context, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // 버튼 누를 때 권한 요청
    Button(onClick = {
        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }) {
        Text("카메라 실행")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}