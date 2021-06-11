package com.caressa.records_tracker.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.entity.*
import com.caressa.model.shr.*
import com.caressa.repository.StoreRecordRepository
import com.caressa.repository.utils.Resource
import okhttp3.RequestBody
import timber.log.Timber

class ShrManagementUseCase( private val repository: StoreRecordRepository ) {

    suspend fun invokeDocumentList(isForceRefresh : Boolean, data: ListDocumentsModel): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> {
        return Transformations.map(
            repository.fetchDocumentList(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeDeleteRecordFromServer(isForceRefresh : Boolean, data: DeleteDocumentModel ,deleteRecordIds : List<String>): LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>> {
        return Transformations.map(
            repository.deleteRecordFromServer(isForceRefresh,data,deleteRecordIds)) {
            it
        }
    }

    suspend fun invokeDownloadDocumentFromServer(isForceRefresh : Boolean, data: DownloadDocumentModel): LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>> {
        return Transformations.map(
            repository.downloadDocumentFromServer(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeSaveRecordToServer(isForceRefresh : Boolean, data: SaveDocumentModel,
                                         personName: String,personRel: String,healthDocumentsList : List<SaveDocumentModel.HealthDoc>): LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>> {
        return Transformations.map(
            repository.saveRecordToServer(isForceRefresh,data,personName,personRel,healthDocumentsList)) {
            it
        }
    }

    suspend fun invokeCheckUnitExist(isForceRefresh : Boolean, data: OCRUnitExistModel): LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>> {
        return Transformations.map(
            repository.checkUnitExist(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeOcrSaveDocument(isForceRefresh : Boolean, data: OCRSaveModel): LiveData<Resource<OCRSaveModel.OCRSaveResponse>> {
        return Transformations.map(
            repository.ocrSaveDocument(isForceRefresh,data)) {
            it
        }
    }

    suspend fun invokeocrDigitizeDocument(fileBytes: RequestBody, partnerCode: RequestBody, fileExtension: RequestBody): LiveData<Resource<OcrResponce>> {
        return Transformations.map(
            repository.ocrDigitizeDocument(fileBytes,partnerCode,fileExtension)) {
            it
        }
    }

    suspend fun invokeCreateUploadList(code:String,comment:String,personID:String,relativeId:String,relation:String,personName:String) : MutableList<SaveDocumentModel.HealthDoc> {
        return repository.createUploadList(code,comment,personID,relativeId,relation,personName)
    }

    suspend fun invokeGetDocumentTypes() : List<DocumentType> {
        return repository.getDocumentTypes()
    }

    suspend fun invokeGetRecordsInSession() : List<RecordInSession> {
        return repository.getRecordsInSession()
    }


    suspend fun invokeGetUserRelatives() : List<UserRelatives> {
        return repository.getUserRelatives()
    }

    suspend fun invokeGetHealthDocumentsWhereCode( code : String ) : List<HealthDocument> {
        return repository.getHealthDocumentsWhereCode ( code )
    }

    suspend fun invokeGetAllHealthDocuments() : List<HealthDocument> {
        return repository.getAllHealthDocuments()
    }

    suspend fun invokeUpdateHealthRecordPathSync(id : String, path : String, sync : String )  {
        Timber.e("Updating Record Path......!!!!!!!!!!")
        repository.updateHealthRecordPathSync( id , path , sync )
    }

    suspend fun invokeSaveRecordsInSession( recordsInSession : RecordInSession ) {
        repository.saveRecordsInSession(recordsInSession)
    }

    suspend fun invokeDeleteRecordInSession( record : RecordInSession ) {
        repository.deleteRecordInSession(record)
    }

    suspend fun invokeDeleteRecordsInSessionTable( ) {
        repository.deleteRecordInSessionTable()
    }

}