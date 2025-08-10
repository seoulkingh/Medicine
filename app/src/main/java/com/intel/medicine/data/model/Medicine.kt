// data/model/Medicine.kt
package com.intel.medicine.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String,
    val manufacturer: String,
    val mainIngredient: String,
    val description: String,
    val imageUri: String? = null,
    val createdAt: Date = Date()
) : Parcelable
