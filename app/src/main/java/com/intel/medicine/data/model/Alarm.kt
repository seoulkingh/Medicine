// data/model/Alarm.kt
package com.intel.medicine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val medicineId: String,
    val medicineName: String,
    val time: String, // "HH:mm" 형식
    val days: List<Int>, // 요일 (1=월, 2=화, ..., 7=일)
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val isEnabled: Boolean = true,
    val createdAt: Date = Date()
) : Parcelable