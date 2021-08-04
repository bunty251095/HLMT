package com.caressa.remote

import com.caressa.model.medication.*

class MedicationDatasource(
    private val defaultService: ApiService,
    private val encryptedService: ApiService
) {

    fun fetchDrugsListAsync(data: DrugsModel) = encryptedService.fetchDrugsList(data)

    fun fetchMedicationList(data: MedicationListModel) = encryptedService.fetchMedicationList(data)

    fun fetchMedicationInTakeByScheduleID(data: MedicineInTakeModel) =
        encryptedService.fetchMedicationInTakeByScheduleID(data)

    fun deleteMedicine(data: DeleteMedicineModel) = encryptedService.deleteMedicine(data)

    fun getMedicine(data: GetMedicineModel) = encryptedService.getMedicine(data)

    fun fetchMedicationListByDay(data: MedicineListByDayModel) =
        encryptedService.fetchMedicationListByDay(data)

    fun setAlert(data: SetAlertModel) = encryptedService.setAlert(data)

    fun saveMedicine(data: AddMedicineModel) = encryptedService.saveMedicine(data)

    fun updateMedicine(data: UpdateMedicineModel) = encryptedService.UpdateMedicine(data)

    fun addInTake(data: AddInTakeModel) = encryptedService.addInTake(data)
}