// ui/result/ResultActivity.kt
package com.intel.medicine.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
import com.intel.medicine.data.model.DetectionResult
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.repository.MedicineRepository
import com.intel.medicine.ui.alarm.AlarmListActivity
import com.intel.medicine.util.showToast
import java.util.Date

class ResultActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인텐트에서 인식 결과 가져오기 (실제 구현에서는 DetectionResult를 직렬화)
        val sampleDetectionResult = DetectionResult(
            medicineName = "타이레놀정 500mg",
            confidence = 0.95f,
            category = "약",
            manufacturer = "한국얀센",
            mainIngredient = "아세트아미노펜",
            description = "해열진통제"
        )

        val medicine = Medicine(
            name = sampleDetectionResult.medicineName,
            category = sampleDetectionResult.category,
            manufacturer = sampleDetectionResult.manufacturer,
            mainIngredient = sampleDetectionResult.mainIngredient,
            description = sampleDetectionResult.description,
            createdAt = Date()
        )

        setContent {
            MaterialTheme {
                ResultScreen(
                    detectionResult = sampleDetectionResult,
                    medicine = medicine,
                    onBackClick = { finish() },
                    onAddToListClick = { addToMyList(medicine) },
                    onSetAlarmClick = { setAlarmForMedicine(medicine) },
                    onRetakeClick = { retakePhoto() }
                )
            }
        }
    }

    private fun addToMyList(medicine: Medicine) {
        repository.addMedicine(medicine)
        showToast("내 목록에 추가되었습니다.")
    }

    private fun setAlarmForMedicine(medicine: Medicine) {
        val intent = Intent(this, AlarmListActivity::class.java).apply {
            putExtra("medicine", medicine)
        }
        startActivity(intent)
    }

    private fun retakePhoto() {
        finish() // 이전 화면(카메라)로 돌아가기
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    detectionResult: DetectionResult,
    medicine: Medicine,
    onBackClick: () -> Unit = {},
    onAddToListClick: () -> Unit = {},
    onSetAlarmClick: () -> Unit = {},
    onRetakeClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "확인",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("약물 인식 완료")
                    }
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
            // 인식 신뢰도 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (detectionResult.confidence > 0.8f)
                        Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (detectionResult.confidence > 0.8f) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "신뢰도",
                        tint = if (detectionResult.confidence > 0.8f) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "인식 신뢰도: ${(detectionResult.confidence * 100).toInt()}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (detectionResult.confidence > 0.8f) "높은 신뢰도로 인식되었습니다" else "재촬영을 권장합니다",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 약물 이미지 카드
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💊", fontSize = 40.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 약물 정보 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        medicine.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "(${medicine.description})",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MedicineInfoRow(
                        label = "제조사",
                        value = medicine.manufacturer,
                        icon = Icons.Default.Business,
                        iconColor = Color(0xFF4CAF50)
                    )

                    MedicineInfoRow(
                        label = "주요성분",
                        value = medicine.mainIngredient,
                        icon = Icons.Default.Science,
                        iconColor = Color(0xFF4CAF50)
                    )

                    MedicineInfoRow(
                        label = "분류",
                        value = medicine.category,
                        icon = Icons.Default.Category,
                        iconColor = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 다시 촬영 버튼
                OutlinedButton(
                    onClick = onRetakeClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "다시촬영",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("다시촬영")
                }

                // 내 목록에 추가 버튼
                Button(
                    onClick = onAddToListClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "추가",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("목록추가", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 알람 설정 버튼
            Button(
                onClick = onSetAlarmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Alarm,
                    contentDescription = "알람",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("이 약으로 알람 설정", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MedicineInfoRow(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            color = iconColor
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Text(
            value,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    val sampleResult = DetectionResult(
        medicineName = "타이레놀정 500mg",
        confidence = 0.95f,
        category = "약",
        manufacturer = "한국얀센",
        mainIngredient = "아세트아미노펜",
        description = "해열진통제"
    )

    val sampleMedicine = Medicine(
        name = sampleResult.medicineName,
        category = sampleResult.category,
        manufacturer = sampleResult.manufacturer,
        mainIngredient = sampleResult.mainIngredient,
        description = sampleResult.description
    )

    MaterialTheme {
        ResultScreen(
            detectionResult = sampleResult,
            medicine = sampleMedicine
        )
    }
}