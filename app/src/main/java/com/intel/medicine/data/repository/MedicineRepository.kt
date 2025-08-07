package com.intel.medicine.data.repository

import com.intel.medicine.data.model.Medicine
import com.intel.medicine.data.model.MedicineCategory
import com.intel.medicine.data.model.Alarm
import java.util.Date

class MedicineRepository {

    private val medicines = mutableListOf<Medicine>()
    private val alarms = mutableListOf<Alarm>()
    private var nextMedicineId = 1L
    private var nextAlarmId = 1L

    // Medicine 관련 메서드
    fun getAllMedicines(): List<Medicine> = medicines.toList()

    fun getMedicinesByCategory(category: MedicineCategory): List<Medicine> {
        return when (category) {
            MedicineCategory.ALL -> medicines.toList()
            else -> medicines.filter { it.category == category.displayName }
        }
    }

    fun getMedicineById(id: Long): Medicine? = medicines.find { it.id == id }

    fun addMedicine(medicine: Medicine): Medicine {
        val newMedicine = medicine.copy(id = nextMedicineId++)
        medicines.add(newMedicine)
        return newMedicine
    }

    fun updateMedicine(medicine: Medicine): Boolean {
        val index = medicines.indexOfFirst { it.id == medicine.id }
        return if (index != -1) {
            medicines[index] = medicine
            true
        } else {
            false
        }
    }

    fun deleteMedicine(id: Long): Boolean {
        val removed = medicines.removeAll { it.id == id }
        // 해당 약물의 알람도 함께 삭제
        alarms.removeAll { it.medicineId == id }
        return removed
    }

    // Alarm 관련 메서드
    fun getAllAlarms(): List<Alarm> = alarms.toList()

    fun getActiveAlarms(): List<Alarm> = alarms.filter { it.isEnabled }

    fun getAlarmsByMedicine(medicineId: Long): List<Alarm> =
        alarms.filter { it.medicineId == medicineId }

    fun addAlarm(alarm: Alarm): Alarm {
        val newAlarm = alarm.copy(id = nextAlarmId++)
        alarms.add(newAlarm)
        return newAlarm
    }

    fun updateAlarm(alarm: Alarm): Boolean {
        val index = alarms.indexOfFirst { it.id == alarm.id }
        return if (index != -1) {
            alarms[index] = alarm
            true
        } else {
            false
        }
    }

    fun deleteAlarm(id: Long): Boolean = alarms.removeAll { it.id == id }

    fun toggleAlarmEnabled(id: Long): Boolean {
        val alarm = alarms.find { it.id == id }
        return if (alarm != null) {
            val index = alarms.indexOf(alarm)
            alarms[index] = alarm.copy(isEnabled = !alarm.isEnabled)
            true
        } else {
            false
        }
    }

    // 샘플 데이터 추가 메서드 (테스트용)
    fun addSampleData() {
        if (medicines.isEmpty()) {
            addMedicine(
                Medicine(
                    name = "노바스크정 5mg",
                    category = "약",
                    manufacturer = "화이자제약",
                    mainIngredient = "암로디핀베실산염",
                    description = "고혈압 치료제",
                    createdAt = Date()
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MedicineRepository? = null

        fun getInstance(): MedicineRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MedicineRepository().also { INSTANCE = it }
            }
        }
    }
}