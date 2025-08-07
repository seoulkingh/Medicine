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
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.repository.MedicineRepository
import com.intel.medicine.ui.alarm.AlarmListActivity
import com.intel.medicine.util.showToast
import java.util.Date

class ResultActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 샘플 약물 데이터 (실제로는 AI 분석 결과)
        val sampleMedicine = Medicine(
            name = "노바스크정 5mg",
            category = "약",
            manufacturer = "화이자제약",
            mainIngredient = "암로디핀베실산염",
            description = "고혈압 치료제",
            createdAt = Date()
        )

        setContent {
            ResultScreen(
                medicine = sampleMedicine,
                onBackClick = { finish() },
                onAddToListClick = { addToMyList(sampleMedicine) },
                onSetAlarmClick = { setAlarmForMedicine() }
            )
        }
    }

    private fun addToMyList(medicine: Medicine) {
        repository.addMedicine(medicine)
        showToast("내 목록에 추가되었습니다.")
    }

    private fun setAlarmForMedicine() {
        startActivity(Intent(this, AlarmListActivity::class.java))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    medicine: Medicine,
    onBackClick: () -> Unit = {},
    onAddToListClick: () -> Unit = {},
    onSetAlarmClick: () -> Unit = {}
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
                        Text("약 정보 확인")
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
            // 약 이미지 카드
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

            // 약 정보 카드
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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 내 목록에 추가 버튼
            Button(
                onClick = onAddToListClick,
                modifier = Modifier
                    .fillMaxWidth()
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
                Spacer(modifier = Modifier.width(8.dp))
                Text("내 목록에 추가", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 이 약으로 알람 맞추기 버튼
            OutlinedButton(
                onClick = onSetAlarmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4CAF50)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Alarm,
                    contentDescription = "알람",
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("이 약으로 알람 맞추기")
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
    val sampleMedicine = Medicine(
        name = "노바스크정 5mg",
        category = "약",
        manufacturer = "화이자제약",
        mainIngredient = "암로디핀베실산염",
        description = "고혈압 치료제"
    )
    ResultScreen(medicine = sampleMedicine)
}