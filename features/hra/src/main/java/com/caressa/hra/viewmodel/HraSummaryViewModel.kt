package com.caressa.hra.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.hra.DownloadReportApiService
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.common.HraHelper
import com.caressa.hra.domain.HraManagementUseCase
import com.caressa.model.hra.HraLabTest
import com.caressa.model.entity.HRASummary
import com.caressa.model.hra.HraAssessmentSummaryModel
import com.caressa.model.hra.HraAssessmentSummaryModel.AssessmentDetails
import com.caressa.model.hra.HraListRecommendedTestsModel
import com.caressa.model.hra.HraMedicalProfileSummaryModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class HraSummaryViewModel(
    private val dispatchers: AppDispatchers,
    private val hraManagementUseCase: HraManagementUseCase,
    private val sharedPref: SharedPreferences,
    val context: Context) : BaseViewModel() {

    private val authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!
    val personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!

    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    var hraSummaryDetails = MutableLiveData<HRASummary>()
    var hraAssessmentSummaryDetails = MutableLiveData<List<AssessmentDetails>>()
    var hraRecommendedTests = MutableLiveData<List<HraLabTest>>()

    private var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> = MutableLiveData()
    private val _medicalProfileSummary = MediatorLiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>()
    val medicalProfileSummary: LiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse> get() = _medicalProfileSummary

    private var assessmentSummarySource: LiveData<Resource<HraAssessmentSummaryModel.AssessmentSummaryResponce>> = MutableLiveData()
    private val _assessmentSummary = MediatorLiveData<HraAssessmentSummaryModel.AssessmentSummaryResponce>()
    val assessmentSummary: LiveData<HraAssessmentSummaryModel.AssessmentSummaryResponce> get() = _assessmentSummary

    private var listRecommendedTestsSource: LiveData<Resource<HraListRecommendedTestsModel.ListRecommendedTestsResponce>> = MutableLiveData()
    private val _listRecommendedTests = MediatorLiveData<HraListRecommendedTestsModel.ListRecommendedTestsResponce>()
    val listRecommendedTests: LiveData<HraListRecommendedTestsModel.ListRecommendedTestsResponce> get() = _listRecommendedTests

    fun getMedicalProfileSummary(fr: Boolean,relativeId :String) = viewModelScope.launch(dispatchers.main) {

        var hraPersonId = relativeId
        if ( Utilities.isNullOrEmptyOrZero(hraPersonId) ) {
            hraPersonId = personId
        }

        val requestData = HraMedicalProfileSummaryModel(Gson().toJson(
                HraMedicalProfileSummaryModel.JSONDataRequest(PersonID = hraPersonId),
                HraMedicalProfileSummaryModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Getting Wellness Score.....")
        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(dispatchers.io) {
            medicalProfileSummarySource =
                hraManagementUseCase.invokeMedicalProfileSummary(isForceRefresh = fr, data = requestData,personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!! )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            _medicalProfileSummary.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Timber.i("MedicalProfileSummary :- %s", it.data)
                    hraSummaryDetails.postValue(it.data!!.MedicalProfileSummary)
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

    fun getAssessmentSummary(forceRefresh: Boolean,relativeId :String) = viewModelScope.launch(dispatchers.main) {

        var hraPersonId = relativeId
        if ( Utilities.isNullOrEmptyOrZero(hraPersonId) ) {
            hraPersonId = personId
        }

        val requestData = HraAssessmentSummaryModel(Gson().toJson(
                HraAssessmentSummaryModel.JSONDataRequest(PersonID = hraPersonId),
                HraAssessmentSummaryModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Getting Assessment Summary.....")
        _assessmentSummary.removeSource(assessmentSummarySource)
        withContext(dispatchers.io) {
            assessmentSummarySource = hraManagementUseCase.invokeGetAssessmentSummary(isForceRefresh = forceRefresh, data = requestData)
        }
        _assessmentSummary.addSource(assessmentSummarySource) {
            _assessmentSummary.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                 _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it != null) {
                    val assessmentSummaryData = it.data!!.hraSummaryReport
                    var assessmentSummaryList: MutableList<AssessmentDetails> = mutableListOf()
                    Timber.i("AssessmentSummary :- $it")
                    if (assessmentSummaryData.suggestedAssessments.isNotEmpty()) {
                        assessmentSummaryList.addAll(assessmentSummaryData.suggestedAssessments)
                    }
                    if (assessmentSummaryData.otherAssessments.isNotEmpty()) {
                        assessmentSummaryList.addAll(assessmentSummaryData.otherAssessments)
                    }
                    assessmentSummaryList = assessmentSummaryList.filter {
                        it.riskCategory != "Fitness"
                                && it.riskCategory != "Nutrition" }.toMutableList()
                    hraAssessmentSummaryDetails.postValue( assessmentSummaryList )
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

    fun getListRecommendedTests(forceRefresh: Boolean,relativeId :String) = viewModelScope.launch(dispatchers.main) {

        var hraPersonId = relativeId
        if ( Utilities.isNullOrEmptyOrZero(hraPersonId) ) {
            hraPersonId = personId
        }

        val requestData = HraListRecommendedTestsModel(Gson().toJson(
                HraListRecommendedTestsModel.JSONDataRequest(PersonID = hraPersonId),
                HraListRecommendedTestsModel.JSONDataRequest::class.java), authToken)

        _progressBar.value = Event("Getting Recommended Tests.....")
        _listRecommendedTests.removeSource(listRecommendedTestsSource)
        withContext(dispatchers.io) {
            listRecommendedTestsSource =
                hraManagementUseCase.invokeGetListRecommendedTests(isForceRefresh = forceRefresh, data = requestData)
        }
        _listRecommendedTests.addSource(listRecommendedTestsSource) {
            _listRecommendedTests.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    val listRecommendedTestsData = it.data!!.labTests
                    if (listRecommendedTestsData.isNotEmpty()) {
                        saveRecommendedTestsList(listRecommendedTestsData)
                    }
                }
                 _progressBar.value = Event(Event.HIDE_PROGRESS)
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

    fun callDownloadReport() {

        _progressBar.value = Event("Downloading HRA Report.....")
        val baseURL = Constants.strAPIUrl
        val proxyURL = Constants.strProxyUrl
        val hraUrl = Constants.HRAurl
        val partnerCode = Configuration.PartnerCode
        val personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
        val accountId = sharedPref.getString(PreferenceConstants.ACCOUNTID,"")!!
        val hash = sharedPref.getString(PreferenceConstants.TOKEN,"")!!
        // Hash will be AUTH_TICKET we are getting in Login response.

        val finalHRADownloadURL = baseURL + proxyURL + hraUrl + "&P=" + personId + "&PCD=" + partnerCode + "&UID=" + accountId + "&E=" + "&Skey=" + hash
        Timber.i("FinalHRADownloadURL--> $finalHRADownloadURL")

        val hraDownloadURL =  proxyURL + hraUrl + "&P=" + personId + "&PCD=" + partnerCode + "&UID=" + accountId + "&E=" + "&Skey=" + hash
        val logging = HttpLoggingInterceptor { message ->
            Timber.i("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        val retrofit = Retrofit.Builder()
            .client(OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .addInterceptor(logging)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .build())
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val downloadService = retrofit.create(DownloadReportApiService::class.java)

        val call = downloadService.downloadHraReport(hraDownloadURL)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if(response.body() != null) {
                    val result = response.body()!!
                    if (response.isSuccessful) {
                        Timber.i("Server contacted and has file--> ")
                        val writtenToDisk =  HraHelper.saveHRAReportAppDirectory(result,context)
                        Timber.i("File download was---->$writtenToDisk")
                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_DOWNLOAD_REPORT)
                    } else {
                        Timber.i("Server Contact failed")
                    }
                }else{
                    _snackbarMessage.value = Event("Unable to download.")
                }
            }
            @SuppressLint("BinaryOperationInTimber")
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Timber.i("Download Report call failed"+t.printStackTrace())
            }
        })
    }

    private fun saveRecommendedTestsList(list: List<HraListRecommendedTestsModel.LabTest>)  {

        val hraLabTestList: ArrayList<HraLabTest> = ArrayList()

        for (LabTestsIndex in list.indices) {
            for (TestsIndex in list[LabTestsIndex].tests.indices) {
                if (list[LabTestsIndex].tests.isNotEmpty()) {
                    val labTestName = list[LabTestsIndex].tests[TestsIndex].labTestName
                    val frequency = list[LabTestsIndex].tests[TestsIndex].frequency

                    var separator1 = ""
                    val sbReasonCodes = StringBuilder()
                    if (list[LabTestsIndex].tests[TestsIndex].reasonCodes.isNotEmpty()) {
                        for (element in list[LabTestsIndex].tests[TestsIndex].reasonCodes) {
                            sbReasonCodes.append(separator1 + element)
                            separator1 = ","
                        }
                    }

                    var separator2 = ""
                    val sbReasons = StringBuilder()
                    if (list[LabTestsIndex].tests[TestsIndex].reasons.isNotEmpty()) {
                        for (element in list[LabTestsIndex].tests[TestsIndex].reasons) {
                            sbReasons.append(separator2 + element)
                            separator2 = ","
                        }
                    }

                    val hraLabTest = HraLabTest(
                        LabTestName = labTestName,
                        ReasonCodes = sbReasonCodes.toString(),
                        Reasons = sbReasons.toString(),
                        Frequency = frequency)
                    Timber.i("HraLabTest :- $hraLabTest")
                    hraLabTestList.add(hraLabTest)
                }
            }
        }
        hraRecommendedTests.postValue( hraLabTestList )
    }

    fun clearPreviousQuesDataAndTable() = viewModelScope.launch {
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeClearHraQuestionsTable()
            hraDataSingleton.previousAnsList.clear()
            hraDataSingleton.clearData()
        }
    }

    fun clearHraDataTables() = viewModelScope.launch {
        withContext(dispatchers.io) {
            hraManagementUseCase.invokeClearHraDataTables()
            hraDataSingleton.previousAnsList.clear()
            hraDataSingleton.clearData()
        }
    }

}