// util/Extensions.kt
package com.intel.medicine.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.text.SimpleDateFormat
import java.util.*

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Date.toFormattedString(pattern: String = "yyyy.MM.dd HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

fun <T> List<T>.toggle(item: T): List<T> {
    return if (contains(item)) {
        this - item
    } else {
        this + item
    }
}

fun String.isValidTime(): Boolean {
    return try {
        val pattern = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$"
        this.matches(pattern.toRegex())
    } catch (e: Exception) {
        false
    }
}

fun List<Int>.toDisplayString(): String {
    if (isEmpty()) return "선택된 요일 없음"

    val dayNames = mapOf(
        1 to "월", 2 to "화", 3 to "수", 4 to "목",
        5 to "금", 6 to "토", 7 to "일"
    )

    return sorted().joinToString(", ") { dayNames[it] ?: "" }
}
