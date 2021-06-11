package com.caressa.medication_tracker.common

import android.content.Intent
import com.caressa.model.medication.MedicineListByDayModel

class MedicationSingleton {

    private var medicineListByDay : MutableList<MedicineListByDayModel.Medication> = mutableListOf()
    private var notificationIntent = Intent()

    fun geMedicineListByDay() : MutableList<MedicineListByDayModel.Medication> {
        return medicineListByDay
    }

    fun setMedicineListByDay(medicineListByDay: MutableList<MedicineListByDayModel.Medication>) {
        this.medicineListByDay = medicineListByDay
    }

    fun getNotificationIntent(): Intent {
        return notificationIntent
    }

    fun setNotificationIntent(notificationIntent: Intent) {
        this.notificationIntent = notificationIntent
    }

    companion object {
        private var instance: MedicationSingleton? = null
        fun getInstance(): MedicationSingleton? {
            if (instance == null) {
                instance = MedicationSingleton()
            }
            return instance
        }
    }

}