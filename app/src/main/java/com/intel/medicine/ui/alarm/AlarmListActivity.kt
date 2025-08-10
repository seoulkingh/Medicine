// ui/alarm/AlarmListActivity.kt
package com.intel.medicine.ui.alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intel.medicine.data.model.Alarm
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.repository.MedicineRepository
import com.intel.medicine.util.toDisplayString
import java.util.*

class AlarmListActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedMedicine = intent.getParcelableExtra<Medicine>("medicine")

        // 샘플 알람 데이터 추가 (테스트용)
        if (repository.getAllAlarms().isEmpty()) {
            addSampleAlarms()
        }

        setContent {
            MaterialTheme {
                AlarmListScreen(
                    selectedMedicine = selectedMedicine,
                    onBackClick = { finish() },
                    onAddAlarmClick = { addNewAlarm(selectedMedicine) },
                    onAlarmToggle = { alarm, enabled -> toggleAlarm(alarm, enabled) },
                    onDeleteAlarm = { alarm -> deleteAlarm(alarm) },
                    repository = repository
                )
            }
        }
    }

    private fun addSampleAlarms() {
        val sampleAlarms = listOf(
            Alarm(
                medicineId = "1",
                medicineName = "타이레놀정 500mg",
                time = "08:00",
                days = listOf(1, 2, 3, 4, 5), // 평일
                soundEnabled = true,
                vibrationEnabled = true,
                isEnabled = true
            ),
            Alarm(
                medicineId = "2",
                medicineName = "센트롬 종합비타민",
                time = "19:30",
                days = listOf(1, 2, 3, 4, 5, 6, 7), // 매일
                soundEnabled = true,
                vibrationEnabled = false,
                isEnabled = false
            )
        )

        sampleAlarms.forEach { repository.addAlarm(it) }
    }

    private fun addNewAlarm(selectedMedicine: Medicine?) {
        val intent = Intent(this, AlarmSettingActivity::class.java).apply {
            selectedMedicine?.let { putExtra("medicine", it) }
        }
        startActivity(intent)
    }

    private fun toggleAlarm(alarm: Alarm, enabled: Boolean) {
        repository.updateAlarm(alarm.copy(isEnabled = enabled))
    }

    private fun deleteAlarm(alarm: Alarm) {
        repository.deleteAlarm(alarm.id)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmListScreen(
    selectedMedicine: Medicine? = null,
    onBackClick: () -> Unit = {},
    onAddAlarmClick: () -> Unit = {},
    onAlarmToggle: (Alarm, Boolean) -> Unit = { _, _ -> },
    onDeleteAlarm: (Alarm) -> Unit = {},
    repository: MedicineRepository
) {
    var alarms by remember { mutableStateOf<List<Alarm>>(emptyList()) }

    LaunchedEffect(Unit) {
        alarms = repository.getAllAlarms()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "알람 목록",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAlarmClick,
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "알람 추가",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (selectedMedicine != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "정보",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "${selectedMedicine.name}의 알람을 설정해보세요",
                            fontSize = 14.sp,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (alarms.isEmpty()) {
                EmptyAlarmState(onAddAlarmClick)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "등록된 알람 ${alarms.size}개",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(alarms) { alarm ->
                        AlarmListItem(
                            alarm = alarm,
                            onToggle = { enabled -> onAlarmToggle(alarm, enabled) },
                            onDelete = { onDeleteAlarm(alarm) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAlarmState(onAddAlarmClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⏰", fontSize = 80.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "설정된 알람이 없습니다",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            "복용 시간을 잊지 않도록 알람을 설정해보세요",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAddAlarmClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "알람 추가",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "첫 알람 만들기",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmListItem(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        alarm.time,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alarm.isEnabled) Color(0xFF333333) else Color.Gray
                    )

                    Text(
                        alarm.medicineName,
                        fontSize = 16.sp,
                        color = if (alarm.isEnabled) Color(0xFF666666) else Color.Gray
                    )

                    Text(
                        alarm.days.toDisplayString(),
                        fontSize = 14.sp,
                        color = if (alarm.isEnabled) Color(0xFF4CAF50) else Color.Gray
                    )
                }

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    if (alarm.soundEnabled) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "소리",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    if (alarm.vibrationEnabled) {
                        Icon(
                            Icons.Default.Vibration,
                            contentDescription = "진동",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("알람 삭제") },
            text = { Text("이 알람을 삭제하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336)
                    )
                ) {
                    Text("삭제", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}