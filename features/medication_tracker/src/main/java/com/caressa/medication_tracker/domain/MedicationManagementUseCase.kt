package com.caressa.medication_tracker.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.MedicationEntity
import com.caressa.model.entity.UserRelatives
import com.caressa.model.medication.*
import com.caressa.repository.HomeRepository
import com.caressa.repository.MedicationRepository
import com.caressa.repository.utils.Resource

class MedicationManagementUseCase(private val repository: MedicationRepository,private val homeRepository: HomeRepository){

    suspend fun invokeDrugsList(data: DrugsModel): LiveData<Resource<DrugsModel.DrugsResponse>> {
        return Transformations.map(
            repository.getDrugsListResponse(data = data)){
            it
        }
    }

    suspend fun fetchMedicationList(data: MedicationListModel, personId: String): LiveData<Resource<MedicationListModel.Response>>{
        return Transformations.map(
            repository.fetchMedicationList(data = data,personId = personId)){
            it
        }
    }

    suspend fun invokeGetMedicationListByDay(data: MedicineListByDayModel): LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>>{
        return Transformations.map(
            repository.getMedicationListByDay(data = data)){
            it
        }
    }

    suspend fun invokeSaveMedicine(data: AddMedicineModel): LiveData<Resource<AddMedicineModel.AddMedicineResponse>>{
        return Transformations.map(
            repository.saveMedicine(data = data)){
            it
        }
    }

    suspend fun invokeUpdateMedicine(data: UpdateMedicineModel): LiveData<Resource<UpdateMedicineModel.UpdateMedicineResponse>>{
        return Transformations.map(
            repository.updateMedicine(data = data)){
            it
        }
    }

    suspend fun invokeDeleteMedicine(data: DeleteMedicineModel,medicationID:String): LiveData<Resource<DeleteMedicineModel.DeleteMedicineResponse>>{
        return Transformations.map(
            repository.deleteMedicine(data = data, medicationID =  medicationID)){
            it
        }
    }

    suspend fun invokeSetAlert(data: SetAlertModel): LiveData<Resource<SetAlertModel.SetAlertResponse>>{
        return Transformations.map(
            repository.setAlert(data = data)){
            it
        }
    }

    suspend fun invokeGetMedicine(data: GetMedicineModel): LiveData<Resource<GetMedicineModel.GetMedicineResponse>>{
        return Transformations.map(
            repository.getMedicine(data = data)){
            it
        }
    }

    suspend fun invokeGetMedicationInTakeByScheduleID(data: MedicineInTakeModel, scheduleId : Int): LiveData<Resource<MedicineInTakeModel.MedicineDetailsResponse>>{
        return Transformations.map(
            repository.getMedicationInTakeByScheduleID(data = data,scheduleId = scheduleId)){
            it
        }
    }

    suspend fun invokeAddMedicineIntake(data: AddInTakeModel): LiveData<Resource<AddInTakeModel.AddInTakeResponse>>{
        return Transformations.map(
            repository.addMedicineIntake(data = data)){
            it
        }
    }

    suspend fun getOngoingMedicines(): List<MedicationEntity.Medication> {
        return repository.getOngoingMedicines()
    }

    suspend fun getCompletedMedicines(): List<MedicationEntity.Medication> {
        return repository.getCompletedMedicines()
    }

    suspend fun getAllMyMedicines(): List<MedicationEntity.Medication> {
        return repository.getAllMyMedicines()
    }

    suspend fun getPastMedicines(): List<MedicationEntity.Medication> {
        return repository.getPastMedicines()
    }

    suspend fun invokeUpdateNotificationAlert(id : String, isAlert : Boolean) {
        return repository.updateNotificationAlert(id,isAlert)
    }

    suspend fun getMedicineDetailsByMedicationId(medicationId: Int): MedicationEntity.Medication {
        return repository.getMedicineDetailsByMedicationId(medicationId)
    }

    suspend fun invokeGetUserRelativeDetailsByRelativeId( relativeId : String ) : UserRelatives {
        return homeRepository.getUserRelativeDetailsByRelativeId( relativeId )
    }

}