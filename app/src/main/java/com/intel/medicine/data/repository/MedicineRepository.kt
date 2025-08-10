// data/repository/MedicineRepository.kt
package com.intel.medicine.data.repository

import com.intel.medicine.data.model.Alarm
import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.model.MedicineCategory

class MedicineRepository private constructor() {

    private val medicines = mutableListOf<Medicine>()
    private val alarms = mutableListOf<Alarm>()

    companion object {
        @Volatile
        private var INSTANCE: MedicineRepository? = null

        fun getInstance(): MedicineRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicineRepository().also { INSTANCE = it }
            }
        }
    }

    // 약물 관련 메소드들
    fun addMedicine(medicine: Medicine) {
        medicines.removeIf { it.id == medicine.id }
        medicines.add(medicine)
    }

    fun getAllMedicines(): List<Medicine> {
        return medicines.toList()
    }

    fun getMedicineById(id: String): Medicine? {
        return medicines.find { it.id == id }
    }

    fun getMedicinesByCategory(category: MedicineCategory): List<Medicine> {
        return if (category == MedicineCategory.ALL) {
            medicines.toList()
        } else {
            medicines.filter { category.matches(it.category) }
        }
    }

    fun deleteMedicine(medicineId: String) {
        medicines.removeIf { it.id == medicineId }
        // 관련된 알람도 삭제
        alarms.removeIf { it.medicineId == medicineId }
    }

    // 알람 관련 메소드들
    fun addAlarm(alarm: Alarm) {
        alarms.removeIf { it.id == alarm.id }
        alarms.add(alarm)
    }

    fun getAllAlarms(): List<Alarm> {
        return alarms.toList()
    }

    fun getAlarmById(id: String): Alarm? {
        return alarms.find { it.id == id }
    }

    fun getAlarmsByMedicine(medicineId: String): List<Alarm> {
        return alarms.filter { it.medicineId == medicineId }
    }

    fun updateAlarm(alarm: Alarm) {
        val index = alarms.indexOfFirst { it.id == alarm.id }
        if (index != -1) {
            alarms[index] = alarm
        } else {
            alarms.add(alarm)
        }
    }

    fun deleteAlarm(alarmId: String) {
        alarms.removeIf { it.id == alarmId }
    }

    fun getEnabledAlarms(): List<Alarm> {
        return alarms.filter { it.isEnabled }
    }
}