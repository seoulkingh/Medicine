// ui/list/MyListActivity.kt
package com.intel.medicine.ui.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.model.MedicineCategory
import com.intel.medicine.data.repository.MedicineRepository
import com.intel.medicine.ui.camera.CameraActivity

class MyListActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyListScreen(
                onBackClick = { finish() },
                onCameraClick = { startCameraActivity() },
                onMedicineClick = { medicine -> openMedicineDetail(medicine) },
                repository = repository
            )
        }
    }

    private fun startCameraActivity() {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    private fun openMedicineDetail(medicine: Medicine) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("medicine", medicine)
        }
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListScreen(
    onBackClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onMedicineClick: (Medicine) -> Unit = {},
    repository: MedicineRepository = MedicineRepository.getInstance()
) {
    var selectedCategory by remember { mutableStateOf(MedicineCategory.ALL) }
    var medicines by remember { mutableStateOf<List<Medicine>>(emptyList()) }

    // 데이터 로드
    LaunchedEffect(selectedCategory) {
        medicines = repository.getMedicinesByCategory(selectedCategory)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 목록") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = onCameraClick) {
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = CircleShape,
                            color = Color(0xFF4CAF50)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "추가",
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            // 탭 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                CategoryTabButton(
                    text = "전체",
                    isSelected = selectedCategory == MedicineCategory.ALL
                ) { selectedCategory = MedicineCategory.ALL }

                Spacer(modifier = Modifier.width(16.dp))

                CategoryTabButton(
                    text = "약",
                    isSelected = selectedCategory == MedicineCategory.MEDICINE
                ) { selectedCategory = MedicineCategory.MEDICINE }

                Spacer(modifier = Modifier.width(16.dp))

                CategoryTabButton(
                    text = "영양제",
                    isSelected = selectedCategory == MedicineCategory.SUPPLEMENT
                ) { selectedCategory = MedicineCategory.SUPPLEMENT }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 약물 목록 또는 빈 상태
            if (medicines.isEmpty()) {
                EmptyListState(onCameraClick = onCameraClick)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(medicines) { medicine ->
                        MedicineListItem(
                            medicine = medicine,
                            onClick = { onMedicineClick(medicine) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabButton(
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EmptyListState(
    onCameraClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📦", fontSize = 60.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "저장된 약이나 영양제가 없습니다",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            "카메라로 약을 촬영하여 추가해보세요",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCameraClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "촬영",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("사진 촬영 찾기", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListItem(
    medicine: Medicine,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
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
            // 약물 아이콘
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💊", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    medicine.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    medicine.manufacturer,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    medicine.category,
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "상세보기",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyListScreenPreview() {
    MyListScreen()
}