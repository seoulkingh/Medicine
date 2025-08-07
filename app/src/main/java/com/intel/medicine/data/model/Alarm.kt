// data/model/Alarm.kt
package com.intel.medicine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Alarm(
    val id: Long = 0,
    val medicineId: Long,
    val medicineName: String,
    val time: String, // "HH:mm" 형식
    val days: List<Int>, // 0(일) ~ 6(토)
    val isEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val createdAt: Date = Date()
) : Parcelable

enum class AlarmDay(val value: Int, val displayName: String) {
    SUNDAY(0, "일"),
    MONDAY(1, "월"),
    TUESDAY(2, "화"),
    WEDNESDAY(3, "수"),
    THURSDAY(4, "목"),
    FRIDAY(5, "금"),
    SATURDAY(6, "토")
}