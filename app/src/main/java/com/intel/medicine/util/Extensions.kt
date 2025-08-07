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

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.startActivitySafely(intent: Intent): Boolean {
    return try {
        startActivity(intent)
        true
    } catch (e: Exception) {
        showToast("앱을 열 수 없습니다.")
        false
    }
}

// Date Extensions
fun Date.toFormattedString(pattern: String = "yyyy.MM.dd HH:mm"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}

fun Date.toTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)
}

fun Date.toDateString(): String {
    return SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(this)
}

// String Extensions
fun String.toDate(pattern: String = "yyyy.MM.dd HH:mm"): Date? {
    return try {
        SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String.isValidTime(): Boolean {
    return try {
        val parts = this.split(":")
        if (parts.size != 2) return false
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        hour in 0..23 && minute in 0..59
    } catch (e: Exception) {
        false
    }
}

// Compose Extensions
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

fun Modifier.clickableWithRipple(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = rememberRipple(),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}

// Collections Extensions
fun <T> List<T>.toggle(item: T): List<T> {
    return if (contains(item)) {
        this - item
    } else {
        this + item
    }
}

// Validation Extensions
fun String.isNotEmptyOrBlank(): Boolean = isNotEmpty() && isNotBlank()

fun String.truncate(maxLength: Int): String {
    return if (length <= maxLength) this else "${take(maxLength)}..."
}