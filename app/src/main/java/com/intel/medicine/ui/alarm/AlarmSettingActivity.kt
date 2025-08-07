// ui/alarm/AlarmSettingActivity.kt
package com.intel.medicine.ui.alarm

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intel.medicine.data.model.Alarm
import com.intel.medicine.data.model.AlarmDay
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.repository.MedicineRepository
import com.intel.medicine.util.showToast
import com.intel.medicine.util.toggle
import java.util.Calendar

class AlarmSettingActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedMedicine = intent.getParcelableExtra<Medicine>("medicine")

        setContent {
            AlarmSettingScreen(
                selectedMedicine = selectedMedicine,
                onBackClick = { finish() },
                onSaveAlarm = { alarm -> saveAlarm(alarm) },
                repository = repository
            )
        }
    }

    private fun saveAlarm(alarm: Alarm) {
        repository.addAlarm(alarm)
        showToast("알람이 설정되었습니다.")
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingScreen(
    selectedMedicine: Medicine? = null,
    onBackClick: () -> Unit = {},
    onSaveAlarm: (Alarm) -> Unit = {},
    repository: MedicineRepository = MedicineRepository.getInstance()
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedTime by remember { mutableStateOf("08:00") }
    var selectedMedicineState by remember { mutableStateOf(selectedMedicine) }
    var selectedDays by remember { mutableStateOf<List<Int>>(emptyList()) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    val medicines by remember { mutableStateOf(repository.getAllMedicines()) }
    val daysList = AlarmDay.values().toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알람 설정") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            selectedMedicineState?.let { medicine ->
                                val alarm = Alarm(
                                    medicineId = medicine.id,
                                    medicineName = medicine.name,
                                    time = selectedTime,
                                    days = selectedDays,
                                    soundEnabled = soundEnabled,
                                    vibrationEnabled = vibrationEnabled
                                )
                                onSaveAlarm(alarm)
                            }
                        },
                        enabled = selectedMedicineState != null && selectedDays.isNotEmpty()
                    ) {
                        Text(
                            "저장",
                            color = if (selectedMedicineState != null && selectedDays.isNotEmpty())
                                Color(0xFF4CAF50) else Color.Gray
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
            // 시간 설정
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    val timeParts = selectedTime.split(":")
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()

                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                        },
                        hour,
                        minute,
                        true
                    ).show()
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = "시간",
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "복용 시간",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            selectedTime,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 약물 선택
            Text(
                "약물 선택",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (medicines.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(medicines) { medicine ->
                        MedicineSelectionChip(
                            medicine = medicine,
                            isSelected = selectedMedicineState?.id == medicine.id,
                            onClick = { selectedMedicineState = medicine }
                        )
                    }
                }
            } else {
                Text(
                    "등록된 약물이 없습니다. 먼저 약물을 등록해주세요.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 요일 선택
            Text(
                "반복 요일",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysList) { day ->
                    DaySelectionChip(
                        day = day,
                        isSelected = selectedDays.contains(day.value),
                        onClick = { selectedDays = selectedDays.toggle(day.value) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 알람 옵션
            Text(
                "알람 옵션",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 소리 설정
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "소리",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "알람 소리",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4CAF50)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 진동 설정
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Vibration,
                            contentDescription = "진동",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "진동",
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it },
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineSelectionChip(
    medicine: Medicine,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(medicine.name) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF4CAF50),
            selectedLabelColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelectionChip(
    day: AlarmDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = { Text(day.displayName) },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF4CAF50),
            selectedLabelColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AlarmSettingScreenPreview() {
    AlarmSettingScreen()
}