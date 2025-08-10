// ui/list/DetailActivity.kt
package com.intel.medicine.ui.list

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.repository.MedicineRepository
import com.intel.medicine.ui.alarm.AlarmListActivity
import com.intel.medicine.ui.result.MedicineInfoRow
import com.intel.medicine.util.showToast
import com.intel.medicine.util.toFormattedString
import java.util.*

class DetailActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val medicine = intent.getParcelableExtra<Medicine>("medicine")

        if (medicine == null) {
            showToast("약물 정보를 불러올 수 없습니다.")
            finish()
            return
        }

        setContent {
            MaterialTheme {
                DetailScreen(
                    medicine = medicine,
                    onBackClick = { finish() },
                    onDeleteClick = { deleteMedicine(medicine) },
                    onSetAlarmClick = { setAlarmForMedicine(medicine) }
                )
            }
        }
    }

    private fun deleteMedicine(medicine: Medicine) {
        repository.deleteMedicine(medicine.id)
        showToast("${medicine.name}이(가) 삭제되었습니다.")
        finish()
    }

    private fun setAlarmForMedicine(medicine: Medicine) {
        val intent = Intent(this, AlarmListActivity::class.java).apply {
            putExtra("medicine", medicine)
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    medicine: Medicine,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSetAlarmClick: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "약물 정보",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "삭제",
                            tint = Color(0xFFF44336)
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
                .padding(16.dp)
        ) {
            // 약물 이미지 카드
            Card(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.CenterHorizontally),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (medicine.category == "약") "💊" else "🍃",
                            fontSize = 64.sp
                        )
                        Text(
                            medicine.category,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 약물 기본 정보 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        medicine.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        medicine.description,
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MedicineInfoRow(
                        label = "제조사",
                        value = medicine.manufacturer,
                        icon = Icons.Default.Business,
                        iconColor = Color(0xFF4CAF50)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MedicineInfoRow(
                        label = "주요성분",
                        value = medicine.mainIngredient,
                        icon = Icons.Default.Science,
                        iconColor = Color(0xFF2196F3)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MedicineInfoRow(
                        label = "분류",
                        value = medicine.category,
                        icon = Icons.Default.Category,
                        iconColor = Color(0xFFFF9800)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MedicineInfoRow(
                        label = "등록일",
                        value = medicine.createdAt.toFormattedString("yyyy.MM.dd"),
                        icon = Icons.Default.DateRange,
                        iconColor = Color(0xFF9C27B0)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 버튼들
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 알람 설정 버튼
                Button(
                    onClick = onSetAlarmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Alarm,
                        contentDescription = "알람",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "이 약으로 알람 설정",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 목록에서 제거 버튼
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color(0xFFF44336)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "목록에서 제거",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "약물 삭제",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "${medicine.name}을(를) 목록에서 제거하시겠습니까?\n\n관련된 알람도 함께 삭제됩니다.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("삭제", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    val sampleMedicine = Medicine(
        name = "타이레놀정 500mg",
        category = "약",
        manufacturer = "한국얀센",
        mainIngredient = "아세트아미노펜",
        description = "해열진통제",
        createdAt = Date()
    )

    MaterialTheme {
        DetailScreen(medicine = sampleMedicine)
    }
}