package com.caressa.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.caressa.common.constants.ApiConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.local.dao.DataSyncMasterDao
import com.caressa.local.dao.MedicationDao
import com.caressa.model.entity.DataSyncMaster
import com.caressa.model.entity.MedicationEntity
import com.caressa.model.entity.MedicationEntity.Medication
import com.caressa.model.medication.*
import com.caressa.remote.MedicationDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.Resource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import timber.log.Timber

interface MedicationRepository {

    suspend fun getDrugsListResponse(forceRefresh: Boolean = false, data: DrugsModel): LiveData<Resource<DrugsModel.DrugsResponse>>

    suspend fun fetchMedicationList(forceRefresh: Boolean = false, data: MedicationListModel, personId: String): LiveData<Resource<MedicationListModel.Response>>

    suspend fun getMedicationListByDay(data: MedicineListByDayModel): LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>>
    suspend fun saveMedicine(data: AddMedicineModel): LiveData<Resource<AddMedicineModel.AddMedicineResponse>>
    suspend fun updateMedicine(data: UpdateMedicineModel): LiveData<Resource<UpdateMedicineModel.UpdateMedicineResponse>>
    suspend fun deleteMedicine(data: DeleteMedicineModel, medicationID: String): LiveData<Resource<DeleteMedicineModel.DeleteMedicineResponse>>

    suspend fun setAlert(data: SetAlertModel): LiveData<Resource<SetAlertModel.SetAlertResponse>>
    suspend fun getMedicine(data: GetMedicineModel): LiveData<Resource<GetMedicineModel.GetMedicineResponse>>
    suspend fun getMedicationInTakeByScheduleID(data: MedicineInTakeModel, scheduleId: Int): LiveData<Resource<MedicineInTakeModel.MedicineDetailsResponse>>

    suspend fun addMedicineIntake(data: AddInTakeModel): LiveData<Resource<AddInTakeModel.AddInTakeResponse>>

    suspend fun updateNotificationAlert(id: String, isAlert: Boolean)
    suspend fun getOngoingMedicines(): List<Medication>
    suspend fun getCompletedMedicines(): List<Medication>
    suspend fun getAllMyMedicines(): List<Medication>
    suspend fun getPastMedicines(): List<Medication>
    suspend fun getMedicineDetailsByMedicationId(medicationId: Int): Medication
    suspend fun logoutUser()
}

class MedicationRepositoryImpl(
    private val dataSource: MedicationDatasource,
    private val medicationDao: MedicationDao,
    private val dataSyncMasterDao: DataSyncMasterDao,
    private val context: Context) : MedicationRepository {

    override suspend fun fetchMedicationList(forceRefresh: Boolean, data: MedicationListModel, personId: String): LiveData<Resource<MedicationListModel.Response>> {

        return object : NetworkBoundResource<MedicationListModel.Response, BaseResponse<MedicationListModel.Response>>(context) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): MedicationListModel.Response {
                val resp = MedicationListModel.Response()
                resp.medication = medicationDao.getMedication()
                return resp
            }

            override suspend fun saveCallResults(items: MedicationListModel.Response) {
                try {
                    medicationDao.deleteMedicationTable()
                    if (items.medication.isNotEmpty()) {
                        for (medicine in items.medication) {
                            if (Utilities.isNullOrEmpty(medicine.DrugTypeCode)) {
                                medicine.DrugTypeCode = ""
                            }
                            if (!Utilities.isNullOrEmpty(medicine.PrescribedDate)) {
                                medicine.PrescribedDate =
                                    medicine.PrescribedDate!!.split("T").toTypedArray()[0]
                            }
                            if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
                                medicine.EndDate = medicine.EndDate!!.split("T").toTypedArray()[0]
                            }
                            medicine.scheduleList =
                                medicine.scheduleList.distinctBy { it.scheduleID }
                            if (medicine.notification == null) {
                                medicine.notification = MedicationEntity.Notification()
                                Timber.e("\nNotification is Null in MedicationId----->${medicine.medicationId}")
                            } else if (medicine.notification != null && medicine.notification!!.setAlert == null) {
                                medicine.notification = MedicationEntity.Notification()
                                Timber.e("\nSetAlert is Null in MedicationId----->${medicine.medicationId}")
                            }
                        }
                        medicationDao.insertMedicineList(items.medication)
                    }
                    val dataSyc = DataSyncMaster(
                        apiName = ApiConstants.MEDICATION_LIST,
                        syncDate = DateHelper.currentDateAsStringyyyyMMdd,
                        personId = personId
                    )
                    dataSyncMasterDao.insertApiSyncData(dataSyc)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun createCallAsync(): Deferred<BaseResponse<MedicationListModel.Response>> {
                return dataSource.fetchMedicationList(data = data)
            }

            override fun processResponse(response: BaseResponse<MedicationListModel.Response>): MedicationListModel.Response {
                return response.jSONData
            }

            override fun shouldFetch(data: MedicationListModel.Response?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun getDrugsListResponse(forceRefresh: Boolean, data: DrugsModel): LiveData<Resource<DrugsModel.DrugsResponse>> {

        return object :
            NetworkBoundResource<DrugsModel.DrugsResponse, BaseResponse<DrugsModel.DrugsResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): DrugsModel.DrugsResponse {
                return DrugsModel.DrugsResponse()
            }

            override suspend fun saveCallResults(items: DrugsModel.DrugsResponse) {}

            override fun createCallAsync(): Deferred<BaseResponse<DrugsModel.DrugsResponse>> {
                return dataSource.fetchDrugsListAsync(data)
            }

            override fun processResponse(response: BaseResponse<DrugsModel.DrugsResponse>): DrugsModel.DrugsResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: DrugsModel.DrugsResponse?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun getMedicine(data: GetMedicineModel): LiveData<Resource<GetMedicineModel.GetMedicineResponse>> {

        return object :
            NetworkBoundResource<GetMedicineModel.GetMedicineResponse, BaseResponse<GetMedicineModel.GetMedicineResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetMedicineModel.GetMedicineResponse {
                return GetMedicineModel.GetMedicineResponse()
            }

            override suspend fun saveCallResults(items: GetMedicineModel.GetMedicineResponse) {
            }

            override fun createCallAsync(): Deferred<BaseResponse<GetMedicineModel.GetMedicineResponse>> {
                return dataSource.getMedicine(data)
            }

            override fun processResponse(response: BaseResponse<GetMedicineModel.GetMedicineResponse>): GetMedicineModel.GetMedicineResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: GetMedicineModel.GetMedicineResponse?): Boolean = true

        }.build().asLiveData()
    }

    override suspend fun getMedicationListByDay(data: MedicineListByDayModel): LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> {

        return object : NetworkBoundResource<MedicineListByDayModel.MedicineListByDayResponse, BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>>(context) {

            var medications: List<MedicineListByDayModel.Medication> = listOf()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): MedicineListByDayModel.MedicineListByDayResponse {
                val resp = MedicineListByDayModel.MedicineListByDayResponse()
                resp.medications = medications
                return resp
            }

            override fun createCallAsync(): Deferred<BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>> {
                return dataSource.fetchMedicationListByDay(data)
            }

            override fun processResponse(response: BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>): MedicineListByDayModel.MedicineListByDayResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: MedicineListByDayModel.MedicineListByDayResponse) {
                for (medicine in items.medications) {
                    if (medicine.notification == null) {
                        medicine.notification = MedicineListByDayModel.Notification()
                        Timber.e("\nNotification is Null in MedicationId----->${medicine.medicationId}")
                    } else if (medicine.notification != null && medicine.notification!!.setAlert == null) {
                        medicine.notification = MedicineListByDayModel.Notification()
                        Timber.e("\nSetAlert is Null in MedicationId----->${medicine.medicationId}")
                    }
                }
                medications = items.medications
            }

            override fun shouldFetch(data: MedicineListByDayModel.MedicineListByDayResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

/*    override suspend fun getMedicationListByDay(data: MedicineListByDayModel): LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> {

        return object : NetworkBoundResource<MedicineListByDayModel.MedicineListByDayResponse,BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>>() {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): MedicineListByDayModel.MedicineListByDayResponse {
                return MedicineListByDayModel.MedicineListByDayResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>> {
                return dataSource.fetchMedicationListByDay(data)
            }

            override fun processResponse(response: BaseResponse<MedicineListByDayModel.MedicineListByDayResponse>): MedicineListByDayModel.MedicineListByDayResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: MedicineListByDayModel.MedicineListByDayResponse) {}

            override fun shouldFetch(data: MedicineListByDayModel.MedicineListByDayResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }*/

    override suspend fun setAlert(data: SetAlertModel): LiveData<Resource<SetAlertModel.SetAlertResponse>> {

        return object :
            NetworkBoundResource<SetAlertModel.SetAlertResponse, BaseResponse<SetAlertModel.SetAlertResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SetAlertModel.SetAlertResponse {
                return SetAlertModel.SetAlertResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<SetAlertModel.SetAlertResponse>> {
                return dataSource.setAlert(data)
            }

            override fun processResponse(response: BaseResponse<SetAlertModel.SetAlertResponse>): SetAlertModel.SetAlertResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: SetAlertModel.SetAlertResponse) {}

            override fun shouldFetch(data: SetAlertModel.SetAlertResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun saveMedicine(data: AddMedicineModel): LiveData<Resource<AddMedicineModel.AddMedicineResponse>> {

        return object :
            NetworkBoundResource<AddMedicineModel.AddMedicineResponse, BaseResponse<AddMedicineModel.AddMedicineResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): AddMedicineModel.AddMedicineResponse {
                return AddMedicineModel.AddMedicineResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<AddMedicineModel.AddMedicineResponse>> {
                return dataSource.saveMedicine(data)
            }

            override fun processResponse(response: BaseResponse<AddMedicineModel.AddMedicineResponse>): AddMedicineModel.AddMedicineResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: AddMedicineModel.AddMedicineResponse) {}

            override fun shouldFetch(data: AddMedicineModel.AddMedicineResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun updateMedicine(data: UpdateMedicineModel): LiveData<Resource<UpdateMedicineModel.UpdateMedicineResponse>> {

        return object :
            NetworkBoundResource<UpdateMedicineModel.UpdateMedicineResponse, BaseResponse<UpdateMedicineModel.UpdateMedicineResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): UpdateMedicineModel.UpdateMedicineResponse {
                return UpdateMedicineModel.UpdateMedicineResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<UpdateMedicineModel.UpdateMedicineResponse>> {
                return dataSource.updateMedicine(data)
            }

            override fun processResponse(response: BaseResponse<UpdateMedicineModel.UpdateMedicineResponse>): UpdateMedicineModel.UpdateMedicineResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: UpdateMedicineModel.UpdateMedicineResponse) {}

            override fun shouldFetch(data: UpdateMedicineModel.UpdateMedicineResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun addMedicineIntake(data: AddInTakeModel): LiveData<Resource<AddInTakeModel.AddInTakeResponse>> {

        return object :
            NetworkBoundResource<AddInTakeModel.AddInTakeResponse, BaseResponse<AddInTakeModel.AddInTakeResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): AddInTakeModel.AddInTakeResponse {
                return AddInTakeModel.AddInTakeResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<AddInTakeModel.AddInTakeResponse>> {
                return dataSource.addInTake(data)
            }

            override fun processResponse(response: BaseResponse<AddInTakeModel.AddInTakeResponse>): AddInTakeModel.AddInTakeResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: AddInTakeModel.AddInTakeResponse) {}

            override fun shouldFetch(data: AddInTakeModel.AddInTakeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun deleteMedicine(data: DeleteMedicineModel, medicationID: String): LiveData<Resource<DeleteMedicineModel.DeleteMedicineResponse>> {

        return object :
            NetworkBoundResource<DeleteMedicineModel.DeleteMedicineResponse, BaseResponse<DeleteMedicineModel.DeleteMedicineResponse>>(context) {

            var isProcessed = false
            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): DeleteMedicineModel.DeleteMedicineResponse {
                val resp = DeleteMedicineModel.DeleteMedicineResponse()
                resp.isProcessed = isProcessed
                return resp
            }

            override suspend fun saveCallResults(items: DeleteMedicineModel.DeleteMedicineResponse) {
                isProcessed = items.isProcessed
                Timber.i("isProcessed----->$isProcessed")
                if (isProcessed) {
                    medicationDao.deleteMedicineFromMedication(medicationID)
                    //medicationDao.deleteMedicineFromMedicineInTake(medicationID)
                    Timber.i("Deleted MedicationId----->$medicationID")
                }
            }

            override fun createCallAsync(): Deferred<BaseResponse<DeleteMedicineModel.DeleteMedicineResponse>> {
                return dataSource.deleteMedicine(data)
            }

            override fun processResponse(response: BaseResponse<DeleteMedicineModel.DeleteMedicineResponse>): DeleteMedicineModel.DeleteMedicineResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: DeleteMedicineModel.DeleteMedicineResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getMedicationInTakeByScheduleID(data: MedicineInTakeModel, scheduleId: Int): LiveData<Resource<MedicineInTakeModel.MedicineDetailsResponse>> {

        return object :
            NetworkBoundResource<MedicineInTakeModel.MedicineDetailsResponse, BaseResponse<MedicineInTakeModel.MedicineDetailsResponse>>(context) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): MedicineInTakeModel.MedicineDetailsResponse {
                return MedicineInTakeModel.MedicineDetailsResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<MedicineInTakeModel.MedicineDetailsResponse>> {
                return dataSource.fetchMedicationInTakeByScheduleID(data)
            }

            override fun processResponse(response: BaseResponse<MedicineInTakeModel.MedicineDetailsResponse>): MedicineInTakeModel.MedicineDetailsResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: MedicineInTakeModel.MedicineDetailsResponse) {}

            override fun shouldFetch(data: MedicineInTakeModel.MedicineDetailsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun getMedicineDetailsByMedicationId(medicationId: Int): Medication {
        return medicationDao.getMedicineDetailsByMedicationId(medicationId)
    }

    override suspend fun updateNotificationAlert(id: String, isAlert: Boolean) {
        val notification = MedicationEntity.Notification()
        notification.setAlert = isAlert
        return medicationDao.updateNotificationAlert(id, notification)
    }

    override suspend fun getOngoingMedicines(): List<Medication> {
        return medicationDao.getOngoingMedicines()
    }

    override suspend fun getCompletedMedicines(): List<Medication> {
        return medicationDao.getCompletedMedicines()
    }

    override suspend fun getAllMyMedicines(): List<Medication> {
        val list = mutableListOf<Medication>()
        list.addAll(medicationDao.getOngoingMedicines())
        list.addAll(medicationDao.getCompletedMedicines())
        return list
    }

    override suspend fun getPastMedicines(): List<Medication> {
        val completedMedicines = medicationDao.getCompletedMedicines()
        val ongoingMedicines = medicationDao.getOngoingMedicines()
        val recentMedList = mutableListOf<Medication>()

        for (i in completedMedicines.indices) {
            if (!containsModel(recentMedList, completedMedicines[i].drug.name!!)) {
                if (!containsModel(ongoingMedicines, completedMedicines[i].drug.name!!)) {
                    val medicineDetails = Medication()
                    medicineDetails.drug.name = completedMedicines[i].drug.name
                    medicineDetails.drug.strength = completedMedicines[i].drug.strength
                    medicineDetails.DrugTypeCode = completedMedicines[i].DrugTypeCode
                    medicineDetails.drugID = completedMedicines[i].drugID
                    recentMedList.add(medicineDetails)
                }
            }
        }
        return recentMedList
    }

    private fun containsModel(list: List<Medication>, name: String): Boolean {
        for (`object` in list) {
            if (`object`.drug.name.equals(name, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    override suspend fun logoutUser() {
        medicationDao.deleteMedicationTable()
    }

}