package com.caressa.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caressa.model.entity.MedicationEntity

@Dao
abstract class MedicationDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(records: List<MedicationEntity.Medication>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertMedicine(medication: MedicationEntity.Medication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMedicineList(records: List<MedicationEntity.Medication>)

    @Query("SELECT * FROM Medication")
    abstract suspend fun getMedication() : List<MedicationEntity.Medication>

    @Query("SELECT * FROM Medication WHERE medicationId = :medicationId")
    abstract suspend fun getMedicineDetailsByMedicationId(medicationId: Int) : MedicationEntity.Medication

    @Query("SELECT * FROM Medication WHERE EndDate = '' OR EndDate IS NULL OR EndDate IS null OR (julianday(date('now')) - julianday(EndDate)) <= 0 ORDER BY PrescribedDate ASC")
    abstract fun getOngoingMedicines(): List<MedicationEntity.Medication>

    @Query("SELECT * FROM Medication WHERE EndDate != '' AND EndDate IS NOT NULL AND EndDate IS NOT null AND (julianday(date('now')) - julianday(EndDate)) > 0 ORDER BY PrescribedDate ASC")
    abstract fun getCompletedMedicines(): List<MedicationEntity.Medication>

    @Query("UPDATE  Medication SET notification=:notification WHERE medicationId=:id")
    abstract fun updateNotificationAlert(id : String, notification:MedicationEntity.Notification)

    @Query("DELETE FROM Medication WHERE medicationId=:medicationId")
    abstract suspend fun deleteMedicineFromMedication( medicationId: String )

    @Query("DELETE FROM Medication")
    abstract suspend fun deleteMedicationTable()

}