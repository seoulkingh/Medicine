// ui/alarm/AlarmListActivity.kt
package com.intel.medicine.ui.alarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.intel.medicine.data.model.Alarm
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.repository.MedicineRepository

class AlarmListActivity : ComponentActivity() {

    private val repository = MedicineRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedMedicine = intent.getParcelableExtra<Medicine>("medicine")

        setContent {
            MaterialTheme {
                AlarmListScreen(
                    onBackClick = { finish() },
                    onAddAlarmClick = { addNewAlarm(selectedMedicine) },
                    onAlarmToggle = { alarm, enabled -> toggleAlarm(alarm, enabled) },
                    repository = repository
                )
            }
        }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmListScreen(
    onBackClick: () -> Unit = {},
    onAddAlarmClick: () -> Unit = {},
    onAlarmToggle: (Alarm, Boolean) -> Unit = { _, _ -> },
    repository: MedicineRepository
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알람 목록") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAlarmClick) {
                Icon(Icons.Default.Add, contentDescription = "알람 추가")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("등록된 알람을 여기에 표시합니다.")
            // 여기에 repository로부터 알람 리스트를 가져와서 표시하면 됩니다
        }
    }
}
