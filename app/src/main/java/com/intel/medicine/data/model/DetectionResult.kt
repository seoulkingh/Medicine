// data/model/DetectionResult.kt
package com.intel.medicine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetectionResult(
    val medicineName: String,
    val confidence: Float,
    val category: String,
    val manufacturer: String,
    val mainIngredient: String,
    val description: String
) : Parcelable
