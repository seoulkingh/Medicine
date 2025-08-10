// data/model/MedicineCategory.kt
package com.intel.medicine.data.model

enum class MedicineCategory(val displayName: String) {
    ALL("전체"),
    MEDICINE("약"),
    SUPPLEMENT("영양제");

    fun matches(categoryString: String): Boolean {
        return when (this) {
            ALL -> true
            MEDICINE -> categoryString == "약"
            SUPPLEMENT -> categoryString == "영양제"
        }
    }
}