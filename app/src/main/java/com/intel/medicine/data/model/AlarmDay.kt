// data/model/AlarmDay.kt
package com.intel.medicine.data.model

enum class AlarmDay(val value: Int, val displayName: String) {
    MONDAY(1, "월"),
    TUESDAY(2, "화"),
    WEDNESDAY(3, "수"),
    THURSDAY(4, "목"),
    FRIDAY(5, "금"),
    SATURDAY(6, "토"),
    SUNDAY(7, "일")
}