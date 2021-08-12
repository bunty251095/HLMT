package com.caressa.repository

import android.content.Context
import android.util.Base64
import androidx.lifecycle.LiveData
import com.caressa.common.constants.ApiConstants
import com.caressa.common.constants.Constants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.RealPathUtil
import com.caressa.common.utils.Utilities
import com.caressa.local.dao.DataSyncMasterDao
import com.caressa.local.dao.StoreRecordsDao
import com.caressa.local.dao.VivantUserDao
import com.caressa.model.entity.*
import com.caressa.model.shr.*
import com.caressa.remote.ShrDatasource
import com.caressa.repository.utils.NetworkBoundResource
import com.caressa.repository.utils.NetworkDataBoundResource
import com.caressa.repository.utils.Resource
import core.model.BaseResponse
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.io.FileInputStream

interface StoreRecordRepository {

    suspend fun fetchDocumentType(
        forceRefresh: Boolean = false,
        data: ListDocumentTypesModel
    ): LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>>

    suspend fun fetchDocumentList(
        forceRefresh: Boolean = false,
        data: ListDocumentsModel
    ): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>>

    suspend fun fetchRelativesList(
        forceRefresh: Boolean = false,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>>

    suspend fun saveRecordToServer(
        forceRefresh: Boolean = false,
        data: SaveDocumentModel,
        personName: String,
        personRel: String,
        healthDocumentsList: List<SaveDocumentModel.HealthDoc>
    ): LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>>

    suspend fun deleteRecordFromServer(
        forceRefresh: Boolean = false,
        data: DeleteDocumentModel,
        deleteRecordIds: List<String>
    ): LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>>

    suspend fun downloadDocumentFromServer(
        forceRefresh: Boolean = false,
        data: DownloadDocumentModel
    ): LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>>

    suspend fun checkUnitExist(
        forceRefresh: Boolean = false,
        data: OCRUnitExistModel
    ): LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>>

    suspend fun ocrSaveDocument(
        forceRefresh: Boolean = false,
        data: OCRSaveModel
    ): LiveData<Resource<OCRSaveModel.OCRSaveResponse>>

    suspend fun ocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    ): LiveData<Resource<OcrResponce>>

    suspend fun saveRecordsInSession(recordsInSession: RecordInSession)
    suspend fun updateHealthRecordPathSync(id: String, path: String, sync: String)
    suspend fun createUploadList(
        code: String,
        comment: String,
        personID: String,
        relativeId: String,
        relation: String,
        personName: String
    ): MutableList<SaveDocumentModel.HealthDoc>

    suspend fun getRecordsInSession(): List<RecordInSession>
    suspend fun getDocumentTypes(): List<DocumentType>
    suspend fun getUserRelatives(): List<UserRelatives>
    suspend fun getHealthDocumentsWherePersonId(personId: String): List<HealthDocument>
    suspend fun getHealthDocumentsWhereCode(code: String): List<HealthDocument>
    suspend fun getAllHealthDocuments(): List<HealthDocument>

    suspend fun deleteRecordInSession(record: RecordInSession)
    suspend fun deleteRecordInSessionTable()
    suspend fun logoutUser()
}

class ShrRepositoryImpl(
    private val datasource: ShrDatasource,
    private val shrDao: StoreRecordsDao,
    private val userDao: VivantUserDao,
    private val dataSyncMasterDao: DataSyncMasterDao,
    private val context: Context
) : StoreRecordRepository {

    override suspend fun fetchDocumentType(
        forceRefresh: Boolean,
        data: ListDocumentTypesModel
    ): LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> {

        return object :
            NetworkBoundResource<ListDocumentTypesModel.ListDocumentTypesResponse, BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ListDocumentTypesModel.ListDocumentTypesResponse {
                val dbData = ListDocumentTypesModel.ListDocumentTypesResponse()
                dbData.documentTypes = shrDao.getDocumentTypes()
                return dbData
            }

            override suspend fun saveCallResults(items: ListDocumentTypesModel.ListDocumentTypesResponse) {
                if (items.documentTypes.isNotEmpty()) {
                    shrDao.deleteDocumentTypesTable()
                    for (documentType in items.documentTypes) {
                        shrDao.insertDocumentTypes(documentType)
                    }
                }
                val dataSyc = DataSyncMaster(
                    apiName = ApiConstants.DOC_TYPE_MASTER,
                    syncDate = DateHelper.currentDateAsStringyyyyMMdd
                )
//                if(dataSyncMasterDao.getLastSyncDataList().find { it.apiName == ApiConstants.DOC_TYPE_MASTER } == null)
                dataSyncMasterDao.insertApiSyncData(dataSyc)
//                else
//                    dataSyncMasterDao.updateRecord(dataSyc)
            }

            override fun createCallAsync(): Deferred<BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>> {
                return datasource.getDocumentTypeResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListDocumentTypesModel.ListDocumentTypesResponse>): ListDocumentTypesModel.ListDocumentTypesResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: ListDocumentTypesModel.ListDocumentTypesResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchRelativesList(
        forceRefresh: Boolean,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {

        return object :
            NetworkBoundResource<ListRelativesModel.ListRelativesResponse, BaseResponse<ListRelativesModel.ListRelativesResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): ListRelativesModel.ListRelativesResponse {
                return ListRelativesModel.ListRelativesResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<ListRelativesModel.ListRelativesResponse>> {
                return datasource.getRelativesListResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListRelativesModel.ListRelativesResponse>): ListRelativesModel.ListRelativesResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: ListRelativesModel.ListRelativesResponse) {
            }

            override fun shouldFetch(data: ListRelativesModel.ListRelativesResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun fetchDocumentList(
        forceRefresh: Boolean,
        data: ListDocumentsModel
    ): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> {

        return object :
            NetworkBoundResource<ListDocumentsModel.ListDocumentsResponse, BaseResponse<ListDocumentsModel.ListDocumentsResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): ListDocumentsModel.ListDocumentsResponse {
                val resp = ListDocumentsModel.ListDocumentsResponse()
                resp.documents = shrDao.getHealthDocuments()
                return resp
            }

            override suspend fun saveCallResults(items: ListDocumentsModel.ListDocumentsResponse) {
                val documentList = items.documents
                //shrDao.deleteHealthDocumentTableTable()
                var no = 1
                for (document in documentList) {
                    val record = shrDao.getHealthDocumentById(document.Id)
                    Timber.e("$no)Record_fetched----->$record")
                    if (record == null) {
                        if (document.PersonId != null) {
                            document.Relation = shrDao.getRelationShip(document.PersonId.toString())
                        }
                        document.Path = ""
                        if (!Utilities.isNullOrEmpty(document.Name)) {
                            document.Type = Utilities.FindTypeOfDocument(document.Name!!)
                        }
                        //document.RecordDate = DateHelper.getDateTimeAs_ddMMMyyyy(document.RecordDate)
                        document.RecordDate = document.RecordDate!!.split("T").toTypedArray()[0]
                        shrDao.insertDocument(document)
                        Timber.e("$no)Inserting_record_id----->${document.Id}")
                    } else {
                        shrDao.updateHealthDocument(
                            document.Id.toString(),
                            "N",
                            document.RecordDate!!.split("T").toTypedArray()[0],
                            document.PersonName!!
                        )
                        Timber.e("$no)Updating_record_id----->${document.Id}")
                    }
                    no++
                }
            }

            override fun createCallAsync(): Deferred<BaseResponse<ListDocumentsModel.ListDocumentsResponse>> {
                return datasource.getDocumentListResponse(data)
            }

            override fun processResponse(response: BaseResponse<ListDocumentsModel.ListDocumentsResponse>): ListDocumentsModel.ListDocumentsResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: ListDocumentsModel.ListDocumentsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun deleteRecordFromServer(
        forceRefresh: Boolean,
        data: DeleteDocumentModel,
        deleteRecordIds: List<String>
    ): LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>> {

        return object :
            NetworkBoundResource<DeleteDocumentModel.DeleteDocumentResponse, BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>>(
                context
            ) {

            var isProcessed = ""

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): DeleteDocumentModel.DeleteDocumentResponse {
                val resp = DeleteDocumentModel.DeleteDocumentResponse()
                resp.isProcessed = isProcessed
                return resp
            }

            override suspend fun saveCallResults(items: DeleteDocumentModel.DeleteDocumentResponse) {
                isProcessed = items.isProcessed
                Timber.i("isProcessed----->$isProcessed")
                if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                    for (i in deleteRecordIds) {
                        //shrDao.deleteHealthRecord( i )
                        shrDao.deleteHealthDocument(i)
                        Timber.i("Deleted RecordId----->$i")
                    }
                }
            }

            override fun createCallAsync(): Deferred<BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>> {
                return datasource.deleteRecordsFromServerResponse(data)
            }

            override fun processResponse(response: BaseResponse<DeleteDocumentModel.DeleteDocumentResponse>): DeleteDocumentModel.DeleteDocumentResponse {
                return response.jSONData
            }

            override fun shouldFetch(data: DeleteDocumentModel.DeleteDocumentResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun downloadDocumentFromServer(
        forceRefresh: Boolean,
        data: DownloadDocumentModel
    ): LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>> {

        return object :
            NetworkBoundResource<DownloadDocumentModel.DownloadDocumentResponse, BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): DownloadDocumentModel.DownloadDocumentResponse {
                return DownloadDocumentModel.DownloadDocumentResponse()
            }

            override fun processResponse(response: BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>): DownloadDocumentModel.DownloadDocumentResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<DownloadDocumentModel.DownloadDocumentResponse>> {
                return datasource.downloadDocumentFromServerResponse(data)
            }

            override suspend fun saveCallResults(items: DownloadDocumentModel.DownloadDocumentResponse) {

            }

            override fun shouldFetch(data: DownloadDocumentModel.DownloadDocumentResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun saveRecordToServer(
        forceRefresh: Boolean,
        data: SaveDocumentModel,
        personName: String,
        personRel: String,
        healthDocumentsList: List<SaveDocumentModel.HealthDoc>
    ): LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>> {

        return object :
            NetworkBoundResource<SaveDocumentModel.SaveDocumentsResponse, BaseResponse<SaveDocumentModel.SaveDocumentsResponse>>(
                context
            ) {

            var resp = SaveDocumentModel.SaveDocumentsResponse()

            override fun shouldStoreInDb(): Boolean = true

            override suspend fun loadFromDb(): SaveDocumentModel.SaveDocumentsResponse {
                // return SaveDocumentModel.SaveDocumentsResponse()
                return resp
            }

            override suspend fun saveCallResults(items: SaveDocumentModel.SaveDocumentsResponse) {
                resp = items
                val uploadedList = items.healthDocuments
                if (uploadedList.isNotEmpty()) {
                    for (document in uploadedList) {
                        document.Type = Utilities.FindTypeOfDocument(document.Name!!)
                        for (reqDoc in healthDocumentsList) {
                            if (document.Name.equals(reqDoc.fileName, ignoreCase = true)) {
                                document.Path = reqDoc.Path
                            }
                        }
                        document.PersonName = personName
                        document.Relation = personRel
                        //document.RecordDate = DateHelper.getDateTimeAs_ddMMMyyyy(document.RecordDate)
                        document.RecordDate = document.RecordDate!!.split("T").toTypedArray()[0]
                        shrDao.insertDocument(document)
                    }
                }
            }

            override fun processResponse(response: BaseResponse<SaveDocumentModel.SaveDocumentsResponse>): SaveDocumentModel.SaveDocumentsResponse {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<SaveDocumentModel.SaveDocumentsResponse>> {
                return datasource.saveRecordToServerResponse(data)
            }

            override fun shouldFetch(data: SaveDocumentModel.SaveDocumentsResponse?): Boolean {
                return true
            }

        }.build().asLiveData()
    }

    override suspend fun ocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    )
            : LiveData<Resource<OcrResponce>> {

        return object : NetworkDataBoundResource<OcrResponce, BaseResponse<OcrResponce>>(context) {
            override fun processResponse(response: BaseResponse<OcrResponce>): OcrResponce {
                return response.jSONData
            }

            override fun createCallAsync(): Deferred<BaseResponse<OcrResponce>> {
                return datasource.ocrDigitizeDocument(fileBytes, partnerCode, fileExtension)
            }

        }.build().asLiveData()
    }

    override suspend fun checkUnitExist(forceRefresh: Boolean, data: OCRUnitExistModel):
            LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>> {

        return object :
            NetworkBoundResource<OCRUnitExistModel.OCRUnitExistResponse, BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): OCRUnitExistModel.OCRUnitExistResponse {
                return OCRUnitExistModel.OCRUnitExistResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>> {
                return datasource.getUnitExistResponse(data)
            }

            override fun processResponse(response: BaseResponse<OCRUnitExistModel.OCRUnitExistResponse>): OCRUnitExistModel.OCRUnitExistResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: OCRUnitExistModel.OCRUnitExistResponse) {
            }

            override fun shouldFetch(data: OCRUnitExistModel.OCRUnitExistResponse?): Boolean {
                return true
            }
        }.build().asLiveData()
    }

    override suspend fun ocrSaveDocument(
        forceRefresh: Boolean,
        data: OCRSaveModel
    ): LiveData<Resource<OCRSaveModel.OCRSaveResponse>> {

        return object :
            NetworkBoundResource<OCRSaveModel.OCRSaveResponse, BaseResponse<OCRSaveModel.OCRSaveResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): OCRSaveModel.OCRSaveResponse {
                return OCRSaveModel.OCRSaveResponse()
            }

            override fun createCallAsync(): Deferred<BaseResponse<OCRSaveModel.OCRSaveResponse>> {
                return datasource.ocrSaveDocumentResponse(data)
            }

            override fun processResponse(response: BaseResponse<OCRSaveModel.OCRSaveResponse>): OCRSaveModel.OCRSaveResponse {
                return response.jSONData
            }

            override suspend fun saveCallResults(items: OCRSaveModel.OCRSaveResponse) {
            }

            override fun shouldFetch(data: OCRSaveModel.OCRSaveResponse?): Boolean {
                return true
            }
        }.build().asLiveData()
    }

    override suspend fun createUploadList(
        code: String,
        comment: String,
        personID: String,
        relativeId: String,
        relation: String,
        personName: String
    ):
            MutableList<SaveDocumentModel.HealthDoc> {
        val recordToUploadRequestList: MutableList<SaveDocumentModel.HealthDoc> = mutableListOf()
        try {
            val recordsToUploadList: ArrayList<HealthDocument> = ArrayList()
            val uploadRecordsInSession = shrDao.getRecordsInSession()
            Timber.i("UploadRecordsInSession----->${uploadRecordsInSession.size}")
            if (uploadRecordsInSession.isNotEmpty()) {
                for (recordInSession in uploadRecordsInSession) {
                    val newHealthRecord = HealthDocument(
                        Id = 0,
                        Title = recordInSession.OriginalFileName,
                        Name = recordInSession.Name,
                        Code = code,
                        Type = Utilities.FindTypeOfDocument(recordInSession.Name),
                        Comment = comment,
                        PersonId = relativeId.toInt(),
                        PersonName = personName,
                        Relation = relation,
                        Path = recordInSession.Path,
                        Sync = "Y",
                        RecordDate = DateHelper.currentDateAsStringddMMMyyyy
                    )
                    // document.CreatedDate = DateHelper.getDateTimeAs_ddMMMyyyy(document.CreatedDate)
                    //saveSessionRecordToHealthRecord
                    //shrDao.insertDocument(newHealthRecord)
                    //deleteRecordInSession
                    shrDao.deleteWhereRecordsInSessionTable(newHealthRecord.Name!!)
                    recordsToUploadList.add(newHealthRecord)
                }
                //recordsToUploadList = shrDao.getHealthDocumentsWhere("Y")
                Timber.i("New Records----->${recordsToUploadList}")
            }
            if (recordsToUploadList.size > 0) {
                for (newRecord in recordsToUploadList) {
                    val doc = prepareRecordToUpload(newRecord)
                    recordToUploadRequestList.add(doc)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return recordToUploadRequestList
    }

    fun prepareRecordToUpload(newRecord: HealthDocument): SaveDocumentModel.HealthDoc {
        var encodedFile = ""

        val imageFullPath = File(newRecord.Path, newRecord.Name!!).toString()
        if (!Utilities.isNullOrEmpty(imageFullPath)) {
            val isFileExist = RealPathUtil.isFileExist(imageFullPath)
            if (isFileExist) {
                try {
                    val file = File(newRecord.Path, newRecord.Name!!)
                    val bytesFile = ByteArray(file.length().toInt())

                    val fileInputStream = FileInputStream(file)
                    fileInputStream.read(bytesFile)
                    encodedFile = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                } catch (e: Exception) {
                    Timber.i("Error retrieving document to upload")
                    e.printStackTrace()
                }
            } else {
                Timber.i("${newRecord.Name} : File not found")
            }
        } else {
            Timber.i("${newRecord.Name} : File path is blank")
        }

        var documentTitle = newRecord.Title
        if (!Utilities.isNullOrEmpty(documentTitle)) {
            documentTitle = RealPathUtil.removeFileExt(documentTitle!!)
        }

        val healthDoc = SaveDocumentModel.HealthDoc(
            personID = newRecord.PersonId!!.toString(),
            documentTypeCode = newRecord.Code!!,
            comments = newRecord.Comment!!,
            fileName = newRecord.Name!!,
            title = documentTitle!!,
            relation = newRecord.Relation!!,
            fileBytes = encodedFile,
            personName = newRecord.PersonName!!,
            type = newRecord.Type!!,
            Path = newRecord.Path!!
        )
        return healthDoc
    }

    override suspend fun updateHealthRecordPathSync(id: String, path: String, sync: String) {
        shrDao.updateHealthDocumentPathSync(id, path, sync)
    }

    override suspend fun saveRecordsInSession(recordsInSession: RecordInSession) {
        shrDao.insertRecordInSession(recordsInSession)
        Timber.e("Record inserted in RecordsInSessionTable.....")
    }

    override suspend fun getHealthDocumentsWhereCode(code: String): List<HealthDocument> {
        return if (code.equals("OTR", ignoreCase = true)) {
            shrDao.getHealthDocumentsWhereCodeWithOther(code)
        } else {
            shrDao.getHealthDocumentsWhereCode(code)
        }
    }

    // getAllHealthDocuments
    override suspend fun getAllHealthDocuments(): List<HealthDocument> {
        return shrDao.getHealthDocuments()
    }

    override suspend fun getHealthDocumentsWherePersonId(personId: String): List<HealthDocument> {
        return shrDao.getHealthDocumentsWherePersonId(personId)
    }

    override suspend fun getRecordsInSession(): List<RecordInSession> {
        return shrDao.getRecordsInSession()
    }

    override suspend fun getDocumentTypes(): List<DocumentType> {
        return shrDao.getDocumentTypes()
    }

    override suspend fun getUserRelatives(): List<UserRelatives> {
        return shrDao.getUserRelatives()
    }

    override suspend fun deleteRecordInSession(record: RecordInSession) {
        shrDao.deleteRecordInSession(record.Name, record.Path)
        Utilities.deleteFileFromLocalSystem(record.Path + "/" + record.Name)
        Timber.e("Deleted Record from RecordsInSessionTable.....")
    }

    override suspend fun deleteRecordInSessionTable() {
        val list = shrDao.getRecordsInSession()
        if (!list.isNullOrEmpty()) {
            for (record in list) {
                Utilities.deleteFileFromLocalSystem(record.Path + "/" + record.Name)
                Timber.e("Deleted ${record.Name} from RecordsInSessionTable.....")
            }
        }
        shrDao.deleteRecordsInSessionTable()
    }

    override suspend fun logoutUser() {
        shrDao.deleteDocumentTypesTable()
        shrDao.deleteHealthDocumentTableTable()
        shrDao.deleteRecordsInSessionTable()
        shrDao.deleteUserRelativesTable()
    }
}