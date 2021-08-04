package com.caressa.hra.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.HRAConstants
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.domain.HraManagementUseCase
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.model.hra.QuestionType
import com.caressa.hra.ui.HraQuestionsActivity
import com.caressa.model.entity.*
import com.caressa.model.hra.*
import com.caressa.model.hra.SaveAndSubmitHraModel.*
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

@SuppressLint("StaticFieldLeak")
class HraViewModel(
    private val dispatchers: AppDispatchers,
    private val hraManagementUseCase: HraManagementUseCase,
    private val sharedPref: SharedPreferences,
    val context: Context) : BaseViewModel() {

    private val authToken = sharedPref.getString(PreferenceConstants.TOKEN, "")!!
    val personId = sharedPref.getString(PreferenceConstants.PERSONID, "")!!
    val adminPersonId = sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID, "")!!
    val firstName = sharedPref.getString(PreferenceConstants.FIRSTNAME, "")!!
    val gender = sharedPref.getString(PreferenceConstants.GENDER, "")!!

    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    var quesData = MutableLiveData<Question>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    var savedResponse = MutableLiveData<String>()
    var selectedOptionList = MutableLiveData<List<HRAQuestions>>()
    var prevAnsList = MutableLiveData<List<Option>>()
    var vitalDetailsSavedResponse = MutableLiveData<List<HRAVitalDetails>>()
    var labDetailsSavedResponse = MutableLiveData<List<HRALabDetails>>()

    var labParameter = MutableLiveData<List<TrackParameterMaster.Parameter>>()

    private var hraStartSource: LiveData<Resource<HraStartModel.HraStartResponse>> = MutableLiveData()
    private val _hraStart = MediatorLiveData<HraStartModel.HraStartResponse>()
    val hraStart: LiveData<HraStartModel.HraStartResponse> get() = _hraStart

    private var checkBmiSource: LiveData<Resource<BMIExistModel.BMIExistResponse>> = MutableLiveData()
    private val _bmiDetails = MediatorLiveData<BMIExistModel.BMIExistResponse>()
    val bmiDetails: LiveData<BMIExistModel.BMIExistResponse> get() = _bmiDetails

    private var checkBPSource: LiveData<Resource<BPExistModel.BPExistResponse>> = MutableLiveData()
    private val _bpDetails = MediatorLiveData<BPExistModel.BPExistResponse>()
    val bpDetails: LiveData<BPExistModel.BPExistResponse> get() = _bpDetails

    private var labRecordBsSource: LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> = MutableLiveData()
    private val _labRecordsBs = MediatorLiveData<LabRecordsModel.LabRecordsExistResponse>()
    val labRecordsBs: LiveData<LabRecordsModel.LabRecordsExistResponse> get() = _labRecordsBs

    private var labRecordCholSource: LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> = MutableLiveData()
    private val _labRecordsChol = MediatorLiveData<LabRecordsModel.LabRecordsExistResponse>()
    val labRecordsChol: LiveData<LabRecordsModel.LabRecordsExistResponse> get() = _labRecordsChol

    private var submitHraSource: LiveData<Resource<SaveAndSubmitHraResponse>> = MutableLiveData()
    private val _submitHra = MediatorLiveData<SaveAndSubmitHraResponse>()
    val submitHra: LiveData<SaveAndSubmitHraResponse> get() = _submitHra

    fun getUserRelatives() = viewModelScope.launch {
        withContext(dispatchers.io) {
            if ( adminPersonId != personId ) {
                val list = hraManagementUseCase.invokeGetUserRelatives().filter { it.relativeID == personId }
                userRelativesList.postValue(list)
            } else {
                val list = hraManagementUseCase.invokeGetUserRelatives().filter {
                    DateHelper.isDateAbove18Years(it.dateOfBirth)
                }.toMutableList()
                userRelativesList.postValue(list)
            }
        }
    }

    fun removeSource( questionCode:String ) {

        labRecordsBs.removeObserver {  }

        when(questionCode) {

            "BMI" -> _bmiDetails.removeSource(checkBmiSource)
            "KNWBPNUM" -> _bpDetails.removeSource(checkBPSource)
            "SUGAR_INPUT" -> _labRecordsBs.removeSource(labRecordBsSource)
            "LIPID_INPUT" -> _labRecordsChol.removeSource(labRecordCholSource)

        }
    }

    fun startHra(relativeId:String,relativeName:String) = viewModelScope.launch {

        val requestData = HraStartModel(Gson().toJson(
                HraStartModel.JSONDataRequest(personID = relativeId),
                HraStartModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event(context.resources.getString(R.string.STARTING_HRA))
        _hraStart.removeSource(hraStartSource)
        withContext(dispatchers.io) {
            hraStartSource = hraManagementUseCase.invokeStartHra(true,requestData,relativeId)
        }
        _hraStart.addSource(hraStartSource) {
            _hraStart.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
/*                val bundle = Bundle()
                bundle.putString(Constants.PERSON_ID,relativeId)
                bundle.putString(Constants.FIRST_NAME,relativeName)
                bundle.putString(Constants.HRA_TEMPLATE_ID,templateId)
                view.findNavController().navigate(R.id.action_selectFamilyMemberFragment_to_hraQuestionsActivity,bundle)*/
                val templateId = it.data!!.template.ID!!
                val intent = Intent(context,HraQuestionsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(Constants.PERSON_ID,relativeId)
                intent.putExtra(Constants.FIRST_NAME,relativeName)
                intent.putExtra(Constants.HRA_TEMPLATE_ID,templateId)
                context.startActivity(intent)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014", true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callIsBMIExist(forceRefresh: Boolean,personId :String) = viewModelScope.launch(dispatchers.main) {
        try {
            val requestData = BMIExistModel(Gson().toJson(
                BMIExistModel.JSONDataRequest(PersonID = personId),
                BMIExistModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event(context.resources.getString(R.string.fetching_bmi_details))
            _bmiDetails.removeSource(checkBmiSource)
            withContext(dispatchers.io) {
                checkBmiSource = hraManagementUseCase.invokeBMIExist(isForceRefresh = forceRefresh, data = requestData)
            }
            _bmiDetails.addSource(checkBmiSource) {
                _bmiDetails.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if(it.errorNumber.equals("1100014", true)){
                        _sessionError.value = Event(true)
                    }else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callIsBPExist(forceRefresh: Boolean,personId :String) = viewModelScope.launch(dispatchers.main) {
        try {
            val requestData = BPExistModel(Gson().toJson(
                BPExistModel.JSONDataRequest(PersonID = personId),
                BPExistModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event(context.resources.getString(R.string.fetching_bp_details))
            _bpDetails.removeSource(checkBPSource)
            withContext(dispatchers.io) {
                checkBPSource = hraManagementUseCase.invokeBPExist(isForceRefresh = forceRefresh, data = requestData)
            }
            _bpDetails.addSource(checkBPSource) {
                _bpDetails.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if(it.errorNumber.equals("1100014", true)){
                        _sessionError.value = Event(true)
                    }else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callGetLabRecordsBloodSugar(forceRefresh: Boolean,personId :String) = viewModelScope.launch(dispatchers.main) {
        try {
            val requestData = LabRecordsModel(Gson().toJson(
                LabRecordsModel.JSONDataRequest(PersonID = personId),
                LabRecordsModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event(context.resources.getString(R.string.fetching_lab_details))
            _labRecordsBs.removeSource(labRecordBsSource)
            withContext(dispatchers.io) {
                labRecordBsSource = hraManagementUseCase.invokeLabRecords(isForceRefresh = forceRefresh, data = requestData)
            }
            _labRecordsBs.addSource(labRecordBsSource) {
                _labRecordsBs.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if(it.errorNumber.equals("1100014", true)){
                        _sessionError.value = Event(true)
                    }else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callGetLabRecordsCholesterol(forceRefresh: Boolean,personId :String) = viewModelScope.launch(dispatchers.main) {
        try {
            val requestData = LabRecordsModel(Gson().toJson(
                LabRecordsModel.JSONDataRequest(PersonID = personId),
                LabRecordsModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event(context.resources.getString(R.string.fetching_lab_details))
            _labRecordsChol.removeSource(labRecordCholSource)
            withContext(dispatchers.io) {
                labRecordCholSource = hraManagementUseCase.invokeLabRecords(isForceRefresh = forceRefresh, data = requestData)
            }
            _labRecordsChol.addSource(labRecordCholSource) {
                _labRecordsChol.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if(it.errorNumber.equals("1100014", true)){
                        _sessionError.value = Event(true)
                    }else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveAndSubmitHRA(hraPersonId :String,hraTemplateId:String) = viewModelScope.launch {
        var requestData: SaveAndSubmitHraModel? = null
        withContext(dispatchers.io) {
            val vitalDetailsList = hraManagementUseCase.invokeGetVitalDetails()
            val labDetailsList = hraManagementUseCase.invokeGetLabDetails()
            val genHealthList = hraManagementUseCase.invokeGetHRASaveDetailsTabName("SETGENHEALTH")
            val familyHistList = hraManagementUseCase.invokeGetHRASaveDetailsTabName("SETFAMILYHIST")
            Timber.i("vitalDetailsList----->$vitalDetailsList")
            Timber.i("labDetailsList----->$labDetailsList")
            Timber.i("genHealthList----->$genHealthList")
            Timber.i("familyHistList----->$familyHistList")

            val vitalDetailsObject = JSONObject()
            //vitalDetailsObject.put(HRAConstants.VitalKey_Pulse, "")
            //  VITALS
            for ( vital in vitalDetailsList ) {
                vitalDetailsObject.put(vital.VitalsKey, vital.VitalsValue)
            }

            //  SETGENHEALTH
            val genList = getHRASaveDetailsList(genHealthList)
            val sbGenHealth = StringBuilder()
            for (strGenHealth in genList) {
                // strGenHealth + "|"
                sbGenHealth.append("$strGenHealth|")
            }
            val strGenHealthDetails = sbGenHealth.toString()

            //  SETFAMILYHIST
            val famHistList = getHRASaveDetailsList(familyHistList)
            val sbFamilyHist = StringBuilder()
            for (strFamilyHist in famHistList) {
                // strFamilyHist + "|"
                sbFamilyHist.append("$strFamilyHist|")
            }
            val strFamilyHistDetails = sbFamilyHist.toString()

            val labRecordsList: ArrayList<LabRecord> = ArrayList()
            for (i in labDetailsList.indices) {
                labRecordsList.add(LabRecord(
                    ParameterCode = labDetailsList[i].ParameterCode,
                    RecordDate = labDetailsList[i].RecordDate!!,
                    Value = labDetailsList[i].LabValue!!.toDouble().toString(),
                    PersonID = hraPersonId,
                    Unit = labDetailsList[i].Unit!!))
            }

            var templateId = hraTemplateId
            if ( Utilities.isNullOrEmptyOrZero(templateId) ) {
                templateId = "5"
            }

            if(labRecordsList.size != 0) {
                requestData = SaveAndSubmitHraModel(
                    Gson().toJson(
                        JSONDataRequest(
                            HraResponse = HraResponse(
                                response = HraResp(
                                    VITALS = VITALS(
                                        Height = vitalDetailsObject.getString(HRAConstants.VitalKey_Height),
                                        Weight = vitalDetailsObject.getString(HRAConstants.VitalKey_Weight),
                                        SaveBMI = vitalDetailsObject.getString(HRAConstants.VitalKey_SaveBMI),
                                        BMI = vitalDetailsObject.getString(HRAConstants.VitalKey_BMI),
                                        SaveBP = vitalDetailsObject.getString(HRAConstants.VitalKey_SaveBP),
                                        SystolicBP = vitalDetailsObject.getString(HRAConstants.VitalKey_SystolicBP),
                                        Mode = vitalDetailsObject.getString(HRAConstants.VitalKey_Mode),
                                        DiastolicBP = vitalDetailsObject.getString(HRAConstants.VitalKey_DiastolicBP)
                                    ),
                                    SETGENHEALTH = strGenHealthDetails,
                                    SETFAMILYHIST = strFamilyHistDetails,
                                    PERSONID = hraPersonId,
                                    TEMPLATEID = templateId
                                )
                            ),
                            LabRecords = labRecordsList,
                            personID = hraPersonId
                        ), JSONDataRequest::class.java
                    ), authToken
                )
            }else{
                requestData = SaveAndSubmitHraModel(
                    Gson().toJson(
                        JSONDataRequestForEmptyParam(
                            HraResponse = HraResponse(
                                response = HraResp(
                                    VITALS = VITALS(
                                        Height = vitalDetailsObject.getString(HRAConstants.VitalKey_Height),
                                        Weight = vitalDetailsObject.getString(HRAConstants.VitalKey_Weight),
                                        SaveBMI = vitalDetailsObject.getString(HRAConstants.VitalKey_SaveBMI),
                                        BMI = vitalDetailsObject.getString(HRAConstants.VitalKey_BMI),
                                        SaveBP = vitalDetailsObject.getString(HRAConstants.VitalKey_SaveBP),
                                        SystolicBP = vitalDetailsObject.getString(HRAConstants.VitalKey_SystolicBP),
                                        Mode = vitalDetailsObject.getString(HRAConstants.VitalKey_Mode),
                                        DiastolicBP = vitalDetailsObject.getString(HRAConstants.VitalKey_DiastolicBP)
                                    ),
                                    SETGENHEALTH = strGenHealthDetails,
                                    SETFAMILYHIST = strFamilyHistDetails,
                                    PERSONID = hraPersonId,
                                    TEMPLATEID = templateId
                                )
                            ),
                            LabRecords = "",
                            personID = hraPersonId
                        ), JSONDataRequestForEmptyParam::class.java
                    ), authToken
                )
            }
        }

        _progressBar.value = Event(context.resources.getString(R.string.submitting_hra_details))
        _submitHra.removeSource(submitHraSource)
        withContext(dispatchers.io) {
            submitHraSource = hraManagementUseCase.invokeSaveAndSubmitHra(true, data = requestData!!)
        }
        _submitHra.addSource(submitHraSource) {
            _submitHra.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(it.errorNumber.equals("1100014", true)){
                    _sessionError.value = Event(true)
                }else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun saveBMIDetails(height: Double, weight: Double, bmi: Double) = viewModelScope.launch {
        val list: ArrayList<HRAVitalDetails> = ArrayList()
        list.add(HRAVitalDetails(HRAConstants.VitalKey_Height, height.toString()))
        list.add(HRAVitalDetails(HRAConstants.VitalKey_Weight, weight.toString()))
        list.add(HRAVitalDetails(HRAConstants.VitalKey_BMI, bmi.toString()))
        if (bmi > 0) {
            list.add(HRAVitalDetails(HRAConstants.VitalKey_SaveBMI, "true"))
        } else {
            list.add(HRAVitalDetails(HRAConstants.VitalKey_SaveBMI, "false"))
        }
        list.add(HRAVitalDetails(HRAConstants.VitalKey_Mode, "SAVE"))

        withContext(dispatchers.io) {
            hraManagementUseCase.invokeSaveVitalDetails(list)
        }
    }

    fun saveBloodPressureDetails(systolic: Int, diastolic: Int) = viewModelScope.launch {
        val list: ArrayList<HRAVitalDetails> = ArrayList()
        list.add(HRAVitalDetails(HRAConstants.VitalKey_SystolicBP, systolic.toString()))
        list.add(HRAVitalDetails(HRAConstants.VitalKey_DiastolicBP, diastolic.toString()))
        if (systolic > 0 && diastolic > 0) {
            list.add(HRAVitalDetails(HRAConstants.VitalKey_SaveBP, "true"))
        } else {
            list.add(HRAVitalDetails(HRAConstants.VitalKey_SaveBP, "false"))
        }
        list.add(HRAVitalDetails(HRAConstants.VitalKey_Mode, "SAVE"))
        list.add(HRAVitalDetails(HRAConstants.VitalKey_Pulse, ""))
        //list.add( HRAVitalDetails(HRAConstants.VitalKey_Pulse,CacheFactory.get(HRAGlobalConstants.VitalKey_Pulse)) )

        withContext(dispatchers.io) {
            hraManagementUseCase.invokeSaveVitalDetails(list)
        }
    }

    fun saveHRALabDetails(parameterCode: String,labValue:String,unit:String) = viewModelScope.launch {
        val strLabValue = if (Utilities.isNullOrEmpty(labValue)) {
            "0"
        } else {
            labValue
        }
        val recordedDate = DateHelper.currentDateAsStringddMMMyyyy
        val personId = sharedPref.getString(PreferenceConstants.PERSONID, "")!!
        val hraLabValue = HRALabDetails(parameterCode, recordedDate, strLabValue, personId, unit)
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeSaveLabValue(hraLabValue)
        }
    }

    fun getHRAVitalDetails() = viewModelScope.launch {
        withContext(dispatchers.io) {
            vitalDetailsSavedResponse.postValue(hraManagementUseCase.invokeGetVitalDetails())
        }
    }

    fun getHRALabDetails() = viewModelScope.launch {
        withContext(dispatchers.io) {
            labDetailsSavedResponse.postValue( hraManagementUseCase.invokeGetLabDetails() )
        }
    }

    fun clearHRALabValue(parameterCode: String) = viewModelScope.launch {
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeClearLabValue(parameterCode)
        }
    }

    fun clearHRALabValuesBasedOnType(type:String) = viewModelScope.launch {
        withContext(dispatchers.io) {
            if ( type.equals("SUGAR",ignoreCase = true) ) {
                hraManagementUseCase.invokeClearLabValue("DIAB_RA")
                hraManagementUseCase.invokeClearLabValue("DIAB_FS")
                hraManagementUseCase.invokeClearLabValue("DIAB_PM")
                hraManagementUseCase.invokeClearLabValue("DIAB_HBA1C")
            }
            if ( type.equals("LIPID",ignoreCase = true) ) {
                hraManagementUseCase.invokeClearLabValue("CHOL_TOTAL")
                hraManagementUseCase.invokeClearLabValue("CHOL_HDL")
                hraManagementUseCase.invokeClearLabValue("CHOL_LDL")
                hraManagementUseCase.invokeClearLabValue("CHOL_TRY")
                hraManagementUseCase.invokeClearLabValue("CHOL_VLDL")

            }
        }
    }

    fun getResponse(questionCode: String) = viewModelScope.launch {
        withContext(dispatchers.io) {
            savedResponse.postValue(hraManagementUseCase.invokeGetSavedResponse(questionCode))
        }
    }

    fun getSelectedOptionListWithQuestionCode(questionCode: String) = viewModelScope.launch {
        withContext(dispatchers.io) {
            selectedOptionList.postValue(hraManagementUseCase.invokeGetHRASavedDetailsWithQuestionCode(questionCode))
        }
    }

    fun getSelectedOptionListForLabQues(list: List<Option>, qCode:String) = viewModelScope.launch {
        val prevSelectedList : MutableList<HRAQuestions> = mutableListOf()
        for ( option in list ) {
            prevSelectedList.add( HRAQuestions(qCode,option.answerCode,option.description) )
        }
        withContext(dispatchers.io) {
            selectedOptionList.postValue( prevSelectedList )
        }
    }

    fun getSelectedOptionListWithCode( code: String,optionList: MutableList<Option> ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            try {
                val list : ArrayList<HRAQuestions> = arrayListOf()
                val selectedList = hraManagementUseCase.invokeGetHRASavedDetailsWithCode(code).filter { option ->
                    option.IsSelected.equals(Constants.TRUE,ignoreCase = true)
                }
                if ( selectedList.isNotEmpty() ) {
                    list.addAll(selectedList)
                }
                if ( list.isEmpty() ) {
                    val notSelectedList = hraManagementUseCase.invokeGetHRASavedDetailsWithCode(code).filter { option ->
                        option.IsSelected.equals(Constants.FALSE,ignoreCase = true)
                    }
                    if ( (optionList.size-1) == notSelectedList.size ) {
                        list.add( HRAQuestions(Code = code,AnswerCode = "DONT",AnsDescription = context.resources.getString(R.string.NONE) ) )
                    }
                }
                Timber.e("$code SelectedList---> $list")
                selectedOptionList.postValue(list)
            } catch ( e :Exception ) {
                e.printStackTrace()
            }
        }
    }

    fun getEdsSelectedOptionList(questionCode: String) = viewModelScope.launch {
        withContext(dispatchers.io) {
            try {
                val list : ArrayList<HRAQuestions> = arrayListOf()
                val denProbList = hraManagementUseCase.invokeGetHRASavedDetailsWithQuestionCode("DENPROB")
                val eyeProbList = hraManagementUseCase.invokeGetHRASavedDetailsWithQuestionCode("EYEPROB")
                val skinProbList = hraManagementUseCase.invokeGetHRASavedDetailsWithQuestionCode("SKINPRB")
                val noneList = hraManagementUseCase.invokeGetHRASavedDetailsWithQuestionCode("EDS")
                //Timber.e("denProbList---> $denProbList")
                //Timber.e("eyeProbList---> $eyeProbList")
                //Timber.e("skinProbList---> $skinProbList")
                if ( !denProbList.isNullOrEmpty() ) {
                    list.addAll(denProbList)
                }
                if ( !eyeProbList.isNullOrEmpty() ) {
                    list.addAll(eyeProbList)
                }
                if ( !skinProbList.isNullOrEmpty() ) {
                    list.addAll(skinProbList)
                }
/*                if ( list.isEmpty() ) {
                    val noneList = hraManagementUseCase.invokeGetHRASavedDetailsWithQuestionCode("EDS")
                    if ( !noneList.isNullOrEmpty() ) {
                        list.addAll(noneList)
                    }
                }*/
                if ( !noneList.isNullOrEmpty() ) {
                    list.clear()
                    list.addAll(noneList)
                }
                Timber.e("$questionCode SelectedList---> $list")
                selectedOptionList.postValue(list)
            } catch ( e :Exception ) {
                e.printStackTrace()
            }
        }
    }

    fun setPreviousAnswersList(list: MutableList<Option>) = viewModelScope.launch {
        withContext(dispatchers.io) {
            prevAnsList.postValue(list)
        }
    }

    fun getParameterDataByProfileCode(profileCode: String) = viewModelScope.launch {
        Timber.e("paramCode----> $profileCode")
        withContext(dispatchers.io) {
            val data = hraManagementUseCase.invokeGetParameterDataByProfileCode(profileCode)
            labParameter.postValue(data)
        }
    }

    fun saveResponse(quesCode:String,ansCode:String,ansDesc:String,Category:String,TabName:String,OthersVal:String) = viewModelScope.launch(dispatchers.main) {
        val list: ArrayList<HRAQuestions> = ArrayList()
        list.add(HRAQuestions(
            QuestionCode=quesCode,
            AnswerCode=ansCode,
            AnsDescription=ansDesc,
            Category=Category,
            TabName=TabName,
            OthersVal=OthersVal))
        Timber.i("SaveResponse---> $list")
        withContext(dispatchers.io) {
            if ( ansDesc.equals(context.resources.getString(R.string.NO),ignoreCase = true)
                || ansDesc.equals(context.resources.getString(R.string.NONE),ignoreCase = true) ) {
                if ( quesCode.equals("EDS",ignoreCase = true) ) {
                    hraManagementUseCase.invokeClearResponse("DENPROB")
                    hraManagementUseCase.invokeClearResponse("EYEPROB")
                    hraManagementUseCase.invokeClearResponse("SKINPRB")
                } else {
                    hraManagementUseCase.invokeClearResponse(quesCode)
                }
            } else {
                if ( !quesCode.equals("EDS",ignoreCase = true) ) {
                    hraManagementUseCase.invokeClearResponse(quesCode)
                }
            }
            hraManagementUseCase.invokeSaveQuesResponse(list)
        }
    }

    fun saveResponseEDS(quesCode:String,tabName:String,category:String,selectedOptions:MutableList<Option>) = viewModelScope.launch(dispatchers.main) {

        val list: ArrayList<HRAQuestions> = ArrayList()
        withContext(dispatchers.io) {

            hraManagementUseCase.invokeClearResponse(quesCode)
            hraManagementUseCase.invokeClearResponse("DENPROB")
            hraManagementUseCase.invokeClearResponse("EYEPROB")
            hraManagementUseCase.invokeClearResponse("SKINPRB")

            if (selectedOptions.any { it.description.equals(context.resources.getString(R.string.NONE), ignoreCase = true) }) {
                list.add(HRAQuestions(
                    QuestionCode = quesCode,
                    AnswerCode = "NONE",
                    AnsDescription = context.resources.getString(R.string.NONE),
                    Category = category,
                    TabName = tabName,
                    OthersVal = ""))
                list.add(HRAQuestions(
                    QuestionCode = "DENPROB",
                    AnswerCode = "63_NONE",
                    AnsDescription = context.resources.getString(R.string.NONE),
                    Category = "DENTAL",
                    TabName = tabName,
                    OthersVal = ""))
                list.add(HRAQuestions(
                    QuestionCode = "EYEPROB",
                    AnswerCode = "64_NONE",
                    AnsDescription = context.resources.getString(R.string.NONE),
                    Category = "EYE",
                    TabName = tabName,
                    OthersVal = ""))
                list.add(HRAQuestions(
                    QuestionCode = "SKINPRB",
                    AnswerCode = "65_SKNPRBNONE",
                    AnsDescription = context.resources.getString(R.string.NONE),
                    Category = "SKIN",
                    TabName = tabName,
                    OthersVal = ""))
            } else {
                var dental = true
                var eye = true
                var skin = true

                for (option in selectedOptions) {
                    val data = option.answerCode.split(",")
                    list.add(HRAQuestions(
                        QuestionCode = data[1],
                        AnswerCode = data[2],
                        AnsDescription = option.description,
                        Category = data[0],
                        TabName = tabName,
                        OthersVal = ""))
                    when {
                        data[1].equals("DENPROB",ignoreCase = true) -> {
                            dental = false
                        }
                        data[1].equals("EYEPROB",ignoreCase = true) -> {
                            eye = false
                        }
                        data[1].equals("SKINPRB",ignoreCase = true) -> {
                            skin = false
                        }
                    }
                }

                if ( dental ) {
                    list.add(HRAQuestions(
                        QuestionCode = "DENPROB",
                        AnswerCode = "63_NONE",
                        AnsDescription = context.resources.getString(R.string.NONE),
                        Category = "DENTAL",
                        TabName = tabName,
                        OthersVal = ""))
                }

                if ( eye ) {
                    list.add(HRAQuestions(
                        QuestionCode = "EYEPROB",
                        AnswerCode = "64_NONE",
                        AnsDescription = context.resources.getString(R.string.NONE),
                        Category = "EYE",
                        TabName = tabName,
                        OthersVal = ""))
                }

                if ( skin ) {
                    list.add(HRAQuestions(
                        QuestionCode = "SKINPRB",
                        AnswerCode = "65_SKNPRBNONE",
                        AnsDescription = context.resources.getString(R.string.NONE),
                        Category = "SKIN",
                        TabName = tabName,
                        OthersVal = ""))
                }
            }
            Timber.i("SaveResponse---> $list")
            hraManagementUseCase.invokeSaveQuesResponse(list)
        }
    }

    fun saveResponseOther(quesCode:String,ansCode:String,ansDesc:String,category:String,tabName:String,othersVal:String,code:String,isSelected :String) = viewModelScope.launch(dispatchers.main) {
        val list: ArrayList<HRAQuestions> = ArrayList()
        list.add(HRAQuestions(
            QuestionCode=quesCode,
            AnswerCode=ansCode,
            AnsDescription=ansDesc,
            Category=category,
            TabName=tabName,
            OthersVal=othersVal,
            Code=code,
            IsSelected=isSelected))
        Timber.i("SaveResponseOther---> $list")
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeClearResponse(quesCode)
            hraManagementUseCase.invokeSaveQuesResponse(list)
        }
    }

    fun saveMultipleOptionResponses(quesCode:String, data:MutableList<Option>, category:String, tabName:String, strOther:String) = viewModelScope.launch {
        val savedOptionList: ArrayList<HRAQuestions> = ArrayList()
        for ( option in data) {
            val item = HRAQuestions(
                QuestionCode=quesCode,
                AnswerCode=option.answerCode,
                AnsDescription=option.description,
                Category=category,TabName=tabName,
                OthersVal=strOther)
            savedOptionList.add(item)
        }
        Timber.i("SavedOptions----->$savedOptionList")
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeClearResponse(quesCode)
            hraManagementUseCase.invokeSaveQuesResponse(savedOptionList)
        }
    }

    private fun getHRASaveDetailsList(strList: List<HRAQuestions>): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()
        var strData: String

        for (i in strList.indices) {
            var answerCode = strList[i].AnswerCode
            var questionCode = strList[i].QuestionCode
            if (!Utilities.isNullOrEmpty(questionCode)) {
                if (questionCode.contains("_OTH")) {
                    if (!Utilities.isNullOrEmpty(strList[i].OthersVal)) {
                        answerCode = ""
                        questionCode = questionCode + "^" + strList[i].OthersVal
                    }
                }
            }
            strData = answerCode.trim { it <= ' ' } + "," + questionCode + "," + 0 + "," + strList[i].Category

            var dontAdd = false
            if ( questionCode.equals("CHECKUP",ignoreCase = true)
                || questionCode.equals("EXPOSE",ignoreCase = true)
                || questionCode.equals("EDS",ignoreCase = true) ) {
                dontAdd = true
            }
            if ( !dontAdd ) {
                list.add(strData)
            }
        }
        return list
    }

    fun clearHraQuestionsResp( quesCode:String ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeClearResponse(quesCode)
        }
    }

    fun clearSavedQuestionsData( ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            //Clear Questions Related Data
            hraManagementUseCase.invokeClearHraQuestionsTable()
            hraDataSingleton.previousAnsList.clear()
            hraDataSingleton.clearData()
        }
    }

    fun switchProfile( relativeId : String ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            val relativesList : MutableList<UserRelatives> = mutableListOf()
            relativesList.addAll(hraManagementUseCase.invokeGetUserRelatives())
            val userRelative = relativesList.filter { it.relativeID == relativeId }[0]
            Timber.i("userRelative----->$userRelative")
            hraManagementUseCase.invokeClearTablesForSwitchProfile()
            hraDataSingleton.previousAnsList.clear()
            hraDataSingleton.clearData()

            //Update Person Details in Shared Preference
            sharedPref.edit().putString(PreferenceConstants.PERSONID,userRelative.relativeID).apply()
            sharedPref.edit().putString(PreferenceConstants.RELATIONSHIPCODE,userRelative.relationshipCode).apply()
            sharedPref.edit().putString(PreferenceConstants.EMAIL,userRelative.emailAddress).apply()
            sharedPref.edit().putString(PreferenceConstants.PHONE,userRelative.contactNo).apply()
            sharedPref.edit().putString(PreferenceConstants.FIRSTNAME,userRelative.firstName).apply()
            sharedPref.edit().putString(PreferenceConstants.GENDER,userRelative.gender).apply()
        }
    }

    fun saveHRALabDetailsBasedOnType( type : String ,parameterCode: String, labValue: String , unit :String ) = viewModelScope.launch {
        withContext(dispatchers.io) {

            when( type ) {

                "SUGAR" -> {
                    if ( parameterCode.equals("DIAB_RA",ignoreCase = true)
                        || parameterCode.equals("DIAB_FS",ignoreCase = true)
                        || parameterCode.equals("DIAB_PM",ignoreCase = true)
                        || parameterCode.equals("DIAB_HBA1C",ignoreCase = true)
                        || parameterCode.equals("CHOL_TOTAL",ignoreCase = true)
                        || parameterCode.equals("CHOL_HDL",ignoreCase = true)
                        || parameterCode.equals("CHOL_LDL",ignoreCase = true)
                        || parameterCode.equals("CHOL_TRY",ignoreCase = true)
                        || parameterCode.equals("CHOL_VLDL",ignoreCase = true) ) {
                        saveHRALabDetails( parameterCode , labValue , unit )
                    }
                }

                "LIPID" -> {
                    if ( parameterCode.equals("CHOL_TOTAL",ignoreCase = true)
                        || parameterCode.equals("CHOL_HDL",ignoreCase = true)
                        || parameterCode.equals("CHOL_LDL",ignoreCase = true)
                        || parameterCode.equals("CHOL_TRY",ignoreCase = true)
                        || parameterCode.equals("CHOL_VLDL",ignoreCase = true) ) {
                        saveHRALabDetails( parameterCode , labValue , unit )
                    }
                }

            }

        }
    }

    fun getHRAQuestionData(qCode: String ) = viewModelScope.launch  {
        withContext(dispatchers.io) {
        var question = Question()
        val optionList : ArrayList<Option> = arrayListOf()

        when(qCode) {
            //************************Vital Question Data ************************
            "BMI" -> {
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_BMI,
                    questionType = QuestionType.Other,
                    optionList = optionList,
                    bgImage = R.drawable.img_bp,
                    category = "BMI")
            }

            "KNWBPNUM" -> {
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_BP,
                    questionType = QuestionType.Other,
                    optionList = optionList,
                    bgImage = R.drawable.img_sys_dia,
                    category = "BPSCREEN")
            }

            // SUGAR
            "KNWDIANUM" -> {

                val paramDataList : MutableList<TrackParameterMaster.Parameter> = mutableListOf()
                withContext(dispatchers.io) {
                    paramDataList.addAll(hraManagementUseCase.invokeGetParameterDataByProfileCode("DIABETIC"))
                    optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "DONT"))
                    for (param in paramDataList) {
                        optionList.add(Option(description = param.description!!, answerCode = param.code!!))
                    }
                }
/*                optionList.add(Option(description = context.resources.getString(R.string.RANDOM_SUGAR), answerCode = "DIAB_RA"))
                optionList.add(Option(description = context.resources.getString(R.string.FASTING_SUGAR), answerCode = "DIAB_FS"))
                optionList.add(Option(description = context.resources.getString(R.string.POST_MEAL_SUGAR), answerCode = "DIAB_PM"))
                optionList.add(Option(description = context.resources.getString(R.string.HBA1C), answerCode = "DIAB_HBA1C"))*/
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_BLOOD_SUGAR,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_blood_suger,
                    category = "HSNUM")
            }

            "SUGAR_INPUT" -> {
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_BLOOD_SUGAR_INPUT,
                    questionType = QuestionType.Other,
                    optionList = optionList,
                    bgImage = R.drawable.img_blood_suger,
                    category = "HSNUM")
            }

            // LIPID
            "KNWLIPNUM" -> {

                val paramDataList : MutableList<TrackParameterMaster.Parameter> = mutableListOf()
                withContext(dispatchers.io) {
                    paramDataList.addAll(hraManagementUseCase.invokeGetParameterDataByProfileCode("LIPID"))
                    optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "DONT"))
                    for (param in paramDataList) {
                        optionList.add(Option(description = param.description!!, answerCode = param.code!!))
                    }
                }
/*                optionList.add(Option(description = context.resources.getString(R.string.TOTAL_CHOLESTEROL), answerCode = "CHOL_TOTAL"))
                optionList.add(Option(description = context.resources.getString(R.string.HDL), answerCode = "CHOL_HDL"))
                optionList.add(Option(description = context.resources.getString(R.string.LDL), answerCode = "CHOL_LDL"))
                optionList.add(Option(description = context.resources.getString(R.string.TRIGLYCERIDES), answerCode = "CHOL_TRY"))
                optionList.add(Option(description = context.resources.getString(R.string.VLDL), answerCode = "CHOL_VLDL"))*/
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_CHOLESTEROL_NEW,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_deaseas,
                    category = "HSCREEN")
            }

            "LIPID_INPUT" -> {
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_CHOLESTEROL_INPUT,
                    questionType = QuestionType.Other,
                    optionList = optionList,
                    bgImage = R.drawable.img_deaseas,
                    category = "HSCREEN")
            }

            //************************Vital Question Data ************************

            "HHILL" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "15_NONE"))
                optionList.add(Option(description = context.resources.getString(R.string.HIGH_BP), answerCode = "15_HBP"))
                optionList.add(Option(description = context.resources.getString(R.string.THYROID_IMBALANCE), answerCode = "15_THYRIOD"))
                optionList.add(Option(description = context.resources.getString(R.string.HIGH_CHOLESTEROL), answerCode = "15_INC_CHOL"))
                optionList.add(Option(description = context.resources.getString(R.string.DIABETES), answerCode = "15_DIAB"))
                optionList.add(Option(description = context.resources.getString(R.string.ASTHMA), answerCode = "15_ASTAMA"))
                optionList.add(Option(description = context.resources.getString(R.string.ARTHRITIS), answerCode = "15_ARTH"))
                optionList.add(Option(description = context.resources.getString(R.string.MENTAL_ILLNESS), answerCode = "15_MENTAL"))
                optionList.add(Option(description = context.resources.getString(R.string.HEART_DISEASE), answerCode = "15_HRTPROB"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_HEALTH_CONDITION,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_certificate,
                    category = "HEALTHHIST")
            }

            "WOMOTHER" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "87_NONE"))
                optionList.add(Option(description = context.resources.getString(R.string.WOMEN_PERIOD), answerCode = "87_MENSTRUALPRB"))
                optionList.add(Option(description = context.resources.getString(R.string.WOMEN_PCOS), answerCode = "87_PCOS"))
                optionList.add(Option(description = context.resources.getString(R.string.WOMEN_DISCHARGE), answerCode = "87_VAGINALDIS"))
                optionList.add(Option(description = context.resources.getString(R.string.WOMEN_UTI), answerCode = "87_UTI"))
                optionList.add(Option(description = context.resources.getString(R.string.WOMEN_BRESTPAIN), answerCode = "87_BREASTPAIN"))
                optionList.add(Option(description = context.resources.getString(R.string.WOMEN_FIB), answerCode = "87_FIBROIDS"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_WOMEN,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_certificate,
                    category = "WOMEN")
            }

            "FHIST" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "1_NONE"))
                optionList.add(Option(description = context.resources.getString(R.string.DIABETES), answerCode = "1_DIABETIC"))
                optionList.add(Option(description = context.resources.getString(R.string.HIGH_BP), answerCode = "1_HIGHBP"))
                optionList.add(Option(description = context.resources.getString(R.string.OBESITY), answerCode = "1_OBESE"))
                optionList.add(Option(description = context.resources.getString(R.string.ASTHMA), answerCode = "1_ASTAMA"))
                optionList.add(Option(description = context.resources.getString(R.string.ARTHRITIS), answerCode = "1_ARTHRITIS"))
                optionList.add(Option(description = context.resources.getString(R.string.HIGH_CHOLESTEROL), answerCode = "1_ELECOHLESTEROL"))
                optionList.add(Option(description = context.resources.getString(R.string.HEART_PROBLEMS), answerCode = "1_HRTOPR"))
                if ( gender.equals("2",ignoreCase = true)  ) {
                    optionList.add(Option(description = context.resources.getString(R.string.BREAST_CANCER), answerCode = "1_BRECANCER"))
                    optionList.add(Option(description = context.resources.getString(R.string.CERVICAL_CANCER), answerCode = "1_CERCANCER"))
                } else {
                    optionList.add(Option(description = context.resources.getString(R.string.COLORECTAL_CANCER), answerCode = "1_COLORECTAL"))
                }
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_FAMILY_HISTORY,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_family_two,
                    category = "FHIST",
                    tabName = "SETFAMILYHIST")
            }

            "5FRUIT" -> {
                optionList.add(Option(description = context.resources.getString(R.string.YES), answerCode = "12_ALW"))
                optionList.add(Option(description = context.resources.getString(R.string.NO), answerCode = "12_NEV"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_SNACKS,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_fruits,
                    category = "NUTRITION")
            }

            "FATFOOD" -> {
                optionList.add(Option(description = context.resources.getString(R.string.YES), answerCode = "13_EV"))
                optionList.add(Option(description = context.resources.getString(R.string.NO), answerCode = "13_NEV"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_FAT,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_meat,
                    category = "NUTRITION")
            }

            "PHYEXER" -> {
                optionList.add(Option(description = context.resources.getString(R.string.RB_ALWAYS), answerCode = "6_PHYEXEALW"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_USUALLY), answerCode = "6_PHYEXEMSTWEEK"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_SOMETIMES), answerCode = "6_PHYEXERARE"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_NEVER), answerCode = "6_PHYEXENEVER"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_EXERCISE,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_exercise,
                    category = "PHYSICAL")
            }

            "PHYSLEEP" -> {
                optionList.add(Option(description = context.resources.getString(R.string.LESSTHAN_6Hours), answerCode = "6_PHYSLPMSTNGT"))
                optionList.add(Option(description = context.resources.getString(R.string.SIXTO_EIGHT_HOURS), answerCode = "6_PHYSLPMOST"))
                optionList.add(Option(description = context.resources.getString(R.string.MORE_THAN_8HOURS), answerCode = "6_PHYSLPALL"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_SLEEP,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_sleep,
                    category = "PHYSICAL")
            }

            "SMOKECNT" -> {
                optionList.add(Option(description = context.resources.getString(R.string.RB_DONT_SMOKE), answerCode = "2_NO"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_12CIGAR), answerCode = "SMKCNT12"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_34CIGAR), answerCode = "SMKCNT34"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_MORETHAN_4CIGAR), answerCode = "SMKCNTGT4"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_SMOKE2,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_cigar,
                    category = "DEPENDENCY")
            }

            "DRINKCNT" -> {
                optionList.add(Option(description = context.resources.getString(R.string.I_DONT_DRINK), answerCode = "1_GENDRINKNO"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_1_2_PEGS), answerCode = "DNKCNT12"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_3_4_PEGS), answerCode = "DNKCNT34"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_4_PEGS), answerCode = "DNKCNTGT4"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_DRINK,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_dirnk,
                    category = "DEPENDENCY")
            }

            "BALWF" -> {
                optionList.add(Option(description = context.resources.getString(R.string.RB_ALWAYS), answerCode = "68_BALWFALWAYS"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_USUALLY), answerCode = "68_BALWFSOMETIME"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_RARELY), answerCode = "68_BALWFRAR"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_NEVER), answerCode = "68_BALWFNEVER"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_FAMILY_LIFE,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_work_time,
                    category = "HEALTHHIST")
            }

            "SOCSYSTM" -> {
                optionList.add(Option(description = context.resources.getString(R.string.RB_VERY_STRONG), answerCode = "7_SOCSYSLOTFRD"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_ABOVE_AVERAGE), answerCode = "7_SOCSYSTLKFRD"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_BELOW_AVERAGE), answerCode = "7_SOCSYSRARFRD"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_NOT_SURE), answerCode = "7_SOCSYSDONTFRD"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_SOCIAL,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_friends,
                    category = "SOCIAL")
            }

            "GENOVER" -> {
                optionList.add(Option(description = context.resources.getString(R.string.COMPLETELY), answerCode = "1_GENOVRALW"))
                optionList.add(Option(description = context.resources.getString(R.string.MOSTLY), answerCode = "1_GENOVRSMT"))
                optionList.add(Option(description = context.resources.getString(R.string.PARTLY), answerCode = "1_GENOVRNVR"))
                optionList.add(Option(description = context.resources.getString(R.string.NOT_SATISFIED), answerCode = "1_GENOVRSTR"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_SATISFIED_LIFE,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_mask,
                    category = "SOCIAL")
            }

            "JPT" -> {
                optionList.add(Option(description = context.resources.getString(R.string.RB_ALWAYS), answerCode = "67_JPTALWAYS"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_USUALLY), answerCode = "67_JPTSOMETIME"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_RARELY), answerCode = "67_JPTRAR"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_NEVER), answerCode = "67_JPTNEVER"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_JOB_PAY,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_calender_hra,
                    category = "OCCUPATION")
            }

            "OCCSHIFT" -> {
                optionList.add(Option(description = context.resources.getString(R.string.YES), answerCode = "OCCSHIFTYES"))
                optionList.add(Option(description = context.resources.getString(R.string.NO), answerCode = "OCCSHIFTNO"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_SHIFT,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_work_time,
                    category = "OCCUPATION")
            }

            "OCCPCTIM" -> {
                optionList.add(Option(description = context.resources.getString(R.string.YES), answerCode = "OCCPCTIMYES"))
                optionList.add(Option(description = context.resources.getString(R.string.NO), answerCode = "OCCPCTIMNO"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_EXTENDED_TIME,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_extended_time,
                    category = "OCCUPATION")
            }

            "PHYSTRES" -> {
                optionList.add(Option(description = context.resources.getString(R.string.YES), answerCode = "6_PHYSTRYES"))
                optionList.add(Option(description = context.resources.getString(R.string.NO), answerCode = "6_PHYSTRNO"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_STREES,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_stressed,
                    category = "SOCIAL")
            }

            "PHY" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "66_PHYNONE"))
                optionList.add(Option(description = context.resources.getString(R.string.DEATH_OF_RELATIVE), answerCode = "66_PHYDEATH"))
                optionList.add(Option(description = context.resources.getString(R.string.END_OF_RELATIONSHIP), answerCode = "66_PHYDIVORCE"))
                optionList.add(Option(description = context.resources.getString(R.string.FEEL_DEMOTIVATED), answerCode = "66_PHYDEMOTIVE"))
                optionList.add(Option(description = context.resources.getString(R.string.PHYSICAL_PROBLEM), answerCode = "66_PHYABUSE"))
                optionList.add(Option(description = context.resources.getString(R.string.ONGOING_MEDICATION), answerCode = "66_PHYNMEDON"))
                optionList.add(Option(description = context.resources.getString(R.string.FINANCIAL_MATTER), answerCode = "66_PHYFINMATTER"))
//                optionList.add(Option(description = context.resources.getString(R.string.HEALTH_CONCERN), answerCode = "66_PHYHLTHCONCERN"))
                optionList.add(Option(description = context.resources.getString(R.string.MEDICAL_ILLNESS), answerCode = "66_PHYHLTHCONCERN"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_PHY,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_certificate,
                    category = "SOCIAL")
            }

            "EDS" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "NONE"))
                optionList.add(Option(description = context.resources.getString(R.string.CAVITIES), answerCode = "DENTAL,DENPROB,63_CAVITIES"))
                optionList.add(Option(description = context.resources.getString(R.string.BAD_BREATH), answerCode = "DENTAL,DENPROB,63_BAD_BREATH"))
                optionList.add(Option(description = context.resources.getString(R.string.STAINING), answerCode = "DENTAL,DENPROB,63_STAINING"))
                optionList.add(Option(description = context.resources.getString(R.string.BLEEDING_GUM), answerCode = "DENTAL,DENPROB,63_BLEEDING_GUMS"))
                optionList.add(Option(description = context.resources.getString(R.string.BLURRED_VISION), answerCode = "EYE,EYEPROB,64_SQUINT"))
                optionList.add(Option(description = context.resources.getString(R.string.WEARING_LENSES), answerCode = "EYE,EYEPROB,64_REFRACTORY_ERROR"))
                optionList.add(Option(description = context.resources.getString(R.string.DRY_EYE), answerCode = "EYE,EYEPROB,64_DRY_EYES"))
                optionList.add(Option(description = context.resources.getString(R.string.DANDRUFF), answerCode = "SKIN,SKINPRB,65_SKNPRBDAND"))
                optionList.add(Option(description = context.resources.getString(R.string.ACNE), answerCode = "SKIN,SKINPRB,65_SKNPRBACNE"))
                optionList.add(Option(description = context.resources.getString(R.string.DRYNESS), answerCode = "SKIN,SKINPRB,65_SKNPRBDRY"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_EDS,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_eye_dental,
                    category = "NA")
            }

            "EXPOSE" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "DONT"))
                optionList.add(Option(description = context.resources.getString(R.string.BRIGHT_LIGHT), answerCode = "OCCHWELD,OCCHWELDYES,OCCHWELDNO"))
                optionList.add(Option(description = context.resources.getString(R.string.LOUD_NOISE), answerCode = "OCCNOISE,OCCNOISYES,OCCNOISNO"))
                optionList.add(Option(description = context.resources.getString(R.string.POISONOUS_GASES), answerCode = "OCCHAZGAS,OCCHGASYES,OCCHGASNO"))
                optionList.add(Option(description = context.resources.getString(R.string.TALKING_LONG), answerCode = "OCCTALKLOT,OCCTALKLOTYES,OCCTALKLOTNO"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_EXPOSE,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_notification,
                    category = "OCCUPATION")
            }

            "CHECKUP" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "DONT"))
                optionList.add(Option(description = context.resources.getString(R.string.CHOL_TEST), answerCode = "LIPIDSCRN,13_LIPLT1YR,13_LIPNEVER"))
                if ( gender.equals("2",ignoreCase = true)  ) {
                    optionList.add(Option(description = context.resources.getString(R.string.PAP_SEAMER_TEST), answerCode = "WOMPAPSMR,11_WMNPAPLSTYR,11_WMNPAPNOPAP"))
                }
                optionList.add(Option(description = context.resources.getString(R.string.TSH_TEST), answerCode = "TSHSCREEN,TSHL1Y,TSHNVR"))
                optionList.add(Option(description = context.resources.getString(R.string.BASIC_HEALTH_TEST), answerCode = "GENSCRN,13_GENLT1YR,13_GENNEVER"))
                optionList.add(Option(description = context.resources.getString(R.string.SUGAR_TEST), answerCode = "DIABSCRN,13_DIBLT1YR,13_DIBNEVER"))
                if ( gender.equals("2",ignoreCase = true)  ) {
                    optionList.add(Option(description = context.resources.getString(R.string.MAMMOGRAM_TEST), answerCode = "WOMMAMO,11_WMNMAMLSTYR,11_WMNMAMANO"))
                }
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_CHECKUP,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_certificate,
                    category = "HSCREEN")
            }

            "OCCHWELD" -> {
                optionList.add(Option(description = context.resources.getString(R.string.RB_ALWAYS), answerCode = "OCCHWELDYES"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_SOMETIMES), answerCode = "OCCHWELDSOM"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_RARELY), answerCode = "OCCHWELDRAR"))
                optionList.add(Option(description = context.resources.getString(R.string.RB_NEVER), answerCode = "OCCHWELDNO"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUESTION_BRIGHT_LIGHT,
                    questionType = QuestionType.SingleSelection,
                    optionList = optionList,
                    bgImage = R.drawable.img_notification,
                    category = "OCCUPATION")
            }

/*            "GENSCRN" -> {
                optionList.add(Option(description = context.resources.getString(R.string.NONE), answerCode = "None"))
                optionList.add(Option(description = context.resources.getString(R.string.BASIC_HEALTH_CHECKUP), answerCode = "13_GENLT1YR"))
                optionList.add(Option(description = context.resources.getString(R.string.CHOLESTEROL_TEST), answerCode = "13_LIPLT1YR"))
                optionList.add(Option(description = context.resources.getString(R.string.SUGAR_PROFILE_TEST), answerCode = "13_DIBLT1YR"))
                optionList.add(Option(description = context.resources.getString(R.string.PAP_SMEAR_TEST), answerCode = "11_WMNPAPLSTYR"))
                optionList.add(Option(description = context.resources.getString(R.string.MAMMOGRAM), answerCode = "11_WMNMAMLSTYR"))
                question = Question(
                    qCode = qCode,
                    question = R.string.QUES_HEALTH_CHECKUP,
                    questionType = QuestionType.MultiSelection,
                    optionList = optionList,
                    bgImage = R.drawable.ic_hra_check_up,
                    category = "HSCREEN")
            }*/
        }
            quesData.postValue(question)
        }
    }

    fun updateUserPreference(unit: String?) {
        if(!unit.isNullOrEmpty()){
            when(unit.toLowerCase()){
                "cm"->{
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "cm").apply()
                }
                "kg"->{
                    sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "kg").apply()
                }
                "lbs"->{
                    sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "lib").apply()
                }
                "feet/inch"->{
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "feet").apply()
                }
            }
        }
    }

/*    fun saveHRALabDetailsBasedOnType( type : String ,parameterCode: String, labValue: String ) = viewModelScope.launch {
        withContext(dispatchers.io) {
            when( type ) {

                "SUGAR" -> {
                    if ( parameterCode.equals("DIAB_RA",ignoreCase = true)
                        || parameterCode.equals("DIAB_FS",ignoreCase = true)
                        || parameterCode.equals("DIAB_PM",ignoreCase = true)
                        || parameterCode.equals("DIAB_HBA1C",ignoreCase = true) ) {
                        saveHRALabDetails( parameterCode , labValue )
                    }
                }

                "LIPID" -> {
                    if ( parameterCode.equals("CHOL_TOTAL",ignoreCase = true)
                        || parameterCode.equals("CHOL_HDL",ignoreCase = true)
                        || parameterCode.equals("CHOL_LDL",ignoreCase = true)
                        || parameterCode.equals("CHOL_TRY",ignoreCase = true)
                        || parameterCode.equals("CHOL_VLDL",ignoreCase = true) ) {
                        saveHRALabDetails( parameterCode , labValue )
                    }
                }

            }
        }
    }*/

}