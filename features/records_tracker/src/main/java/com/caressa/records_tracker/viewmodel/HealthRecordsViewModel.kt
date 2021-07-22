package com.caressa.records_tracker.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.RealPathUtil
import com.caressa.model.entity.*
import com.caressa.model.shr.*
import com.caressa.model.shr.ListDocumentsModel.SearchCriteria
import com.caressa.model.shr.SaveDocumentModel.HealthDoc
import com.caressa.model.shr.DownloadDocumentModel.HealthRelatedDocument
import com.caressa.records_tracker.domain.ShrManagementUseCase
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.caressa.records_tracker.R
import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.common.RecordSingleton
import com.caressa.records_tracker.ui.*
import okhttp3.*
import timber.log.Timber
import java.io.*
import kotlin.collections.ArrayList

class HealthRecordsViewModel(
    private val dispatchers: AppDispatchers , val shrManagementUseCase : ShrManagementUseCase ,
    private val sharedPref: SharedPreferences , val context: Context , val dataHandler: DataHandler ) : BaseViewModel() {

    val personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
    val authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!
    val firstName = sharedPref.getString(PreferenceConstants.FIRSTNAME,"")!!
    val gender = sharedPref.getString(PreferenceConstants.GENDER,"")!!

    val documentTypeList = MutableLiveData<List<DocumentType>>()
    val recordsInSessionList = MutableLiveData<List<RecordInSession>>()
    val healthDocumentsList = MutableLiveData<List<HealthDocument>>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val recordToUploadRequestList = MutableLiveData<List<HealthDoc>>()

    val documentStatus = MutableLiveData<Resource.Status>()
    val postDownload = MutableLiveData<String>()

    var listDocumentsSource: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> = MutableLiveData()
    val _listDocuments = MediatorLiveData<Resource<ListDocumentsModel.ListDocumentsResponse>>()
    val listDocuments: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> get() = _listDocuments

    var saveDocSource: LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>> = MutableLiveData()
    val _saveDocument = MediatorLiveData<SaveDocumentModel.SaveDocumentsResponse>()
    val saveDocument: LiveData<SaveDocumentModel.SaveDocumentsResponse> get() = _saveDocument

    var deleteDocSource: LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>> = MutableLiveData()
    val _deleteDocument = MediatorLiveData<DeleteDocumentModel.DeleteDocumentResponse>()
    val deleteDocument: LiveData<DeleteDocumentModel.DeleteDocumentResponse> get() = _deleteDocument

    var downloadDocSource: LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>> = MutableLiveData()
    val _downloadDoc = MediatorLiveData<DownloadDocumentModel.DownloadDocumentResponse>()
    val downloadDoc: LiveData<DownloadDocumentModel.DownloadDocumentResponse> get() = _downloadDoc

    var ocrUnitExistSource: LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>> = MutableLiveData()
    val _ocrUnitExist = MediatorLiveData<OCRUnitExistModel.OCRUnitExistResponse>()
    val ocrUnitExist: LiveData<OCRUnitExistModel.OCRUnitExistResponse> get() = _ocrUnitExist

    var ocrSaveDocumentSource: LiveData<Resource<OCRSaveModel.OCRSaveResponse>> = MutableLiveData()
    val _ocrSaveDocument = MediatorLiveData<OCRSaveModel.OCRSaveResponse>()
    val ocrSaveDocument: LiveData<OCRSaveModel.OCRSaveResponse> get() = _ocrSaveDocument

    var ocrDigitizeDocumentSource: LiveData<Resource<OcrResponce>> = MutableLiveData()
    val _ocrDigitizeDocument = MediatorLiveData<OcrResponce>()
    val ocrDigitizeDocument: LiveData<OcrResponce> get() = _ocrDigitizeDocument

/*    private val _documentStatus = MutableLiveData<Resource.Status>()
    val documentStatus: LiveData<Resource.Status> get() = _documentStatus*/

/*    private val _postDownload = MutableLiveData<String>()
    val postDownload: LiveData<String> get() = _postDownload*/

    fun callListDocumentsApi( forceRefresh : Boolean ) = viewModelScope.launch(dispatchers.main) {
        val requestData = ListDocumentsModel(Gson().toJson(ListDocumentsModel.JSONDataRequest(
                searchCriteria = SearchCriteria(personID = personId)), ListDocumentsModel.JSONDataRequest::class.java) , authToken )

        _listDocuments.removeSource(listDocumentsSource)
        withContext(dispatchers.io) {
            listDocumentsSource = shrManagementUseCase.invokeDocumentList(isForceRefresh = forceRefresh, data = requestData)
        }
        _listDocuments.addSource(listDocumentsSource) {
            //_listDocuments.value = it.data
            _listDocuments.postValue(it)

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    val list =  it.data!!.documents
                    Timber.i("RecordCount----->" + list.size)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callSaveDocumentApi( from:String,code:String,healthDocumentsList:List<HealthDoc>,personName:String,personRel:String ,view:View) =
        viewModelScope.launch(dispatchers.main) {
            Timber.i("RecordToUploadRequestList----->"+healthDocumentsList.size)
            val requestDataSave = SaveDocumentModel(Gson().toJson(SaveDocumentModel.JSONDataRequest(
                personID = personId, from = 71 , healthDocuments = healthDocumentsList),
                SaveDocumentModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event("Saving Record.....")
            _saveDocument.removeSource(saveDocSource)
            withContext(dispatchers.io) {
                saveDocSource = shrManagementUseCase.invokeSaveRecordToServer(
                    isForceRefresh =true, data =requestDataSave,personName = personName,
                    personRel = personRel,healthDocumentsList= healthDocumentsList)
            }
            _saveDocument.addSource(saveDocSource) {
                _saveDocument.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        if ( it.data!!.healthDocuments.size > 0 ) {
                            toastMessage(context.resources.getString(R.string.MSG_RECORD_SAVED))
                            val bundle = Bundle()
                            bundle.putString("from",from)
                            bundle.putString("code",code)
                            view.findNavController().navigate(R.id.action_selectRelationFragment_to_viewRecordsFragment,bundle)
                        }
                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEALTH_RECORDS_UPLOAD_EVENT)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    fun callDeleteRecordsApi( deleteRecordIds:List<String> )
            = viewModelScope.launch(dispatchers.main) {
        Timber.i("DeleteRecordIds----->"+deleteRecordIds.toString())
        val requestData = DeleteDocumentModel(Gson().toJson(DeleteDocumentModel.JSONDataRequest(
                personID = personId , documentIDS = deleteRecordIds), DeleteDocumentModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Deleting Records.....")
        _deleteDocument.removeSource(deleteDocSource)
        withContext(dispatchers.io) {
            deleteDocSource = shrManagementUseCase.invokeDeleteRecordFromServer(true,requestData,deleteRecordIds)
        }
        _deleteDocument.addSource(deleteDocSource) {
            _deleteDocument.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null ) {
                    val isProcessed = it.data!!.isProcessed
                    if ( isProcessed.equals(Constants.TRUE , ignoreCase = true) ) {
                        documentStatus.postValue(it.status)
                        toastMessage(context.resources.getString(R.string.MSG_RECORD_DELETED))
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callDownloadRecordApi( from: String,record: HealthDocument) = viewModelScope.launch(dispatchers.main) {

        val documentId = record.Id.toString()
        val fileName = record.Name!!
        val requestData = DownloadDocumentModel(Gson().toJson(DownloadDocumentModel.JSONDataRequest(
            PersonID = personId , DocumentID = documentId), DownloadDocumentModel.JSONDataRequest::class.java) , authToken )

        _progressBar.value = Event("Downloading Document.....")
        _downloadDoc.removeSource(downloadDocSource)
        withContext(dispatchers.io) {
            downloadDocSource = shrManagementUseCase.invokeDownloadDocumentFromServer(isForceRefresh = true, data = requestData)
        }
        _downloadDoc.addSource(downloadDocSource) {
            _downloadDoc.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                saveDownloadedRecord( from,it.data!!.healthRelatedDocument,it.status,fileName)
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(context.resources.getString(R.string.MSG_REOCRD_DOWNLOADED))
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callDigitizeDocumentApi( from:String, categoryCode:String, record: HealthDocument )
            = viewModelScope.launch(dispatchers.main) {

        var fileBytes =""
        val path = record.Path!!
        val name = record.Name!!
        //val fullPath = path + "/" + name
        try {
            val file = File(path , name )
            val bytesFile = ByteArray(file.length().toInt())
            val fileInputStream = FileInputStream(file)
            fileInputStream.read(bytesFile)
            fileBytes = Base64.encodeToString(bytesFile, Base64.DEFAULT)
        } catch (e: Exception) {
            Timber.i("Error Digitizing document")
            e.printStackTrace()
        }
        val fileExt = name.substring(name.lastIndexOf('.') +1)
        val partnercode = Configuration.PartnerCode
        // Request Parameters in Parts
        val bytes = RequestBody.create(MediaType.parse("text/plain"), fileBytes)
        val pcode = RequestBody.create(MediaType.parse("text/plain"), partnercode)
        val ext = RequestBody.create(MediaType.parse("text/plain"), fileExt)

        _progressBar.value = Event("Saving data.....")
        _ocrDigitizeDocument.removeSource(ocrDigitizeDocumentSource)
        withContext(dispatchers.io) {
            ocrDigitizeDocumentSource = shrManagementUseCase.invokeocrDigitizeDocument(bytes , pcode , ext)
        }
        _ocrDigitizeDocument.addSource(ocrDigitizeDocumentSource) {
            _ocrDigitizeDocument.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Timber.i("DATA==>${it.data}")
                    val digitizedParametersList = it.data!!.body.healthDataParameters
                    if ( digitizedParametersList.size > 0 ) {
                        // Set Digitized ParametersList to Singleton class
                        RecordSingleton.getInstance()!!.setHealthRecord(record)
                        RecordSingleton.getInstance()!!.setDigitizedParamList(digitizedParametersList)
                        if ( from.equals(Constants.DIGITIZE,ignoreCase = true) ) {
                            navigate(DigitizeRecordListFragmentDirections.actionDigitizedRecordsListFragmentToFragmentDigitize(from,categoryCode))
                        } else {
                            navigate(ViewRecordsFragmentDirections.actionViewRecordsFragmentToFragmentDigitize(from,categoryCode))
                        }
                    }else{
                        toastMessage(context.resources.getString(R.string.ERROR_NOT_ABLE_TO_READ_FILE))
                    }
                } else {
                    toastMessage(context.resources.getString(R.string.ERROR_DOC_CANNOT_DIGITIZE))
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callCheckUnitExistApi(forceRefresh: Boolean ,parameterCode : String , unit : String ) = viewModelScope.launch(dispatchers.main) {
        val requestData = OCRUnitExistModel(Gson().toJson(OCRUnitExistModel.JSONDataRequest(
            parameterCode = parameterCode , unit = unit),
            OCRUnitExistModel.JSONDataRequest::class.java) , authToken )

        _ocrUnitExist.removeSource(ocrUnitExistSource)
        withContext(dispatchers.io) {
            ocrUnitExistSource = shrManagementUseCase.invokeCheckUnitExist(isForceRefresh = forceRefresh, data = requestData)
        }
        _ocrUnitExist.addSource(ocrUnitExistSource) {
            _ocrUnitExist.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    Timber.i("IsUnitExist----->"+it.data!!.isExist)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callOcrSaveDocumentApi(view: View,fragment: DigitizeRecordFragment,records:List<HealthDataParameter>,recordeDate:String,perId:String ) = viewModelScope.launch(dispatchers.main) {

        val labRecords: ArrayList<OCRSaveModel.LabRecord> = arrayListOf()
        var record : OCRSaveModel.LabRecord
        for ( item in records ) {
            record = OCRSaveModel.LabRecord(item.paramCode,perId,recordeDate,"",item.unit,item.observation)
            labRecords.add(record)
        }

        val requestData = OCRSaveModel(Gson().toJson(OCRSaveModel.JSONDataRequest(
            LabRecords = labRecords ), OCRSaveModel.JSONDataRequest::class.java) , authToken )
        _progressBar.value = Event("Saving data.....")
        _ocrSaveDocument.removeSource(ocrSaveDocumentSource)
        withContext(dispatchers.io) {
            ocrSaveDocumentSource = shrManagementUseCase.invokeOcrSaveDocument(isForceRefresh = true, data = requestData)
        }
        _ocrSaveDocument.addSource(ocrSaveDocumentSource) {
            _ocrSaveDocument.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(context.resources.getString(R.string.MSG_DATA_UPLOADED))
                fragment.performBackClick(view)
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014",true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun saveDownloadedRecord( from: String,document:HealthRelatedDocument,status:Resource.Status,fileName:String ) = viewModelScope.launch(dispatchers.main) {
        try {
            val byteArray = document.FileBytes
            val decodedString = Base64.decode(byteArray, Base64.DEFAULT)
            var save = false
            if (decodedString != null) {
                val documentId = document.ID
                val path = RealPathUtil.getRecordFolderLocation() + "/" + documentId
                val sync = "N"
                val extension = RealPathUtil.getFileExt(fileName).toUpperCase()
                when (extension) {
                    "PNG", "GIF", "BMP", "JPEG", "TIF", "TIFF", "ICO", "JPG" -> save = RealPathUtil.saveOtherAsFileToExternalCard( decodedString,fileName,Constants.RECORD,documentId )
                    else -> save = RealPathUtil.saveOtherAsFileToExternalCard( decodedString,fileName,Constants.RECORD,documentId )
                }
                if (save) {
                    withContext(dispatchers.io) {
                        RecordSingleton.getInstance()!!.getHealthRecord().Path = path
                        shrManagementUseCase.invokeUpdateHealthRecordPathSync( documentId , path ,sync )
                        documentStatus.postValue(status)
                        postDownload.postValue(from)
                    }
                }
            }
        } catch ( e : Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFileFromLocalSystem(path: String): Boolean {
        Timber.e("RecordPath----->"+path)
        val file = File(path)
        return file.delete()
    }

    fun showMessage(str:String){
        toastMessage(str)
    }

    fun getDocumentTypesList() = viewModelScope.launch {
        withContext(dispatchers.io) {
            documentTypeList.postValue(shrManagementUseCase.invokeGetDocumentTypes())
        }
    }

    fun getAllHealthDocuments() = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            healthDocumentsList.postValue(shrManagementUseCase.invokeGetAllHealthDocuments())
        }
    }

    fun getHealthDocumentsWhereCode( code : String ) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            if ( !code.equals("ALL",ignoreCase = true) ) {
                healthDocumentsList.postValue(shrManagementUseCase.invokeGetHealthDocumentsWhereCode(code))
            } else {
                healthDocumentsList.postValue(shrManagementUseCase.invokeGetAllHealthDocuments())
            }
        }
    }

    fun getRecordsInSession() = viewModelScope.launch {
        withContext(dispatchers.io) {
            recordsInSessionList.postValue(shrManagementUseCase.invokeGetRecordsInSession())
        }
    }

    fun getUserRelatives() = viewModelScope.launch {
        withContext(dispatchers.io) {
            userRelativesList.postValue(shrManagementUseCase.invokeGetUserRelatives())
        }
    }

    fun getRecordToUploadList( code:String,comment:String ,relativeId :String,personRelation:String,personName:String ) = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            recordToUploadRequestList.postValue(shrManagementUseCase.invokeCreateUploadList(code,comment,personId,relativeId,personRelation,personName))
        }
    }

    fun saveRecordsInSession( recordsInSession : RecordInSession ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            shrManagementUseCase.invokeSaveRecordsInSession(recordsInSession)
        }
    }

    fun deleteRecordInSession( record : RecordInSession ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            shrManagementUseCase.invokeDeleteRecordInSession(record)
        }
    }

    fun deleteRecordsInSessionTable() = viewModelScope.launch {
        withContext(dispatchers.io) {
            shrManagementUseCase.invokeDeleteRecordsInSessionTable()
        }
    }

}