// data/model/Medicine.kt
package com.intel.medicine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Medicine(
    val id: Long = 0,
    val name: String,
    val category: String, // "약" 또는 "영양제"
    val manufacturer: String,
    val mainIngredient: String,
    val description: String = "",
    val imagePath: String = "",
    val createdAt: Date = Date(),
    val isActive: Boolean = true
) : Parcelable

enum class MedicineCategory(val displayName: String) {
    MEDICINE("약"),
    SUPPLEMENT("영양제"),
    EXTERNAL("외용약"),
    ALL("전체")
}