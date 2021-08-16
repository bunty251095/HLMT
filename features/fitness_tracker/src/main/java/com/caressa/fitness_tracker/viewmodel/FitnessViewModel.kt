package com.caressa.fitness_tracker.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.fitness_tracker.R
import com.caressa.fitness_tracker.common.StepsDataSingleton
import com.caressa.fitness_tracker.domain.FitnessManagementUseCase
import com.caressa.fitness_tracker.ui.FitnessDashboardFragment
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.FirebaseHelper
import com.caressa.fitness_tracker.util.StepCountHelper
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.fitness.SetGoalModel
import com.caressa.model.fitness.StepsHistoryModel
import com.caressa.model.fitness.StepsSaveListModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
class FitnessViewModel(private val dispatchers: AppDispatchers,
                       private val sharedPref: SharedPreferences,
                       private val useCase: FitnessManagementUseCase,
                       private val context: Context) : BaseViewModel() {

    var personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
    var adminPersonId = sharedPref.getString(PreferenceConstants.ADMIN_PERSON_ID,"")!!
    private val stepsDataSingleton = StepsDataSingleton.instance!!

    private var stepsHistorySource: LiveData<Resource<StepsHistoryModel.Response>> = MutableLiveData()
    private val _stepsHistoryList = MediatorLiveData<StepsHistoryModel.Response>()
    val stepsHistoryList: LiveData<StepsHistoryModel.Response> get() = _stepsHistoryList

    private var getLatestStepsGoalSource: LiveData<Resource<GetStepsGoalModel.Response>> = MutableLiveData()
    private val _getLatestStepsGoal = MediatorLiveData<GetStepsGoalModel.Response>()
    val getLatestStepsGoal: LiveData<GetStepsGoalModel.Response> get() = _getLatestStepsGoal

    private var saveStepsGoalSource: LiveData<Resource<SetGoalModel.Response>> = MutableLiveData()
    private val _saveStepsGoal = MediatorLiveData<SetGoalModel.Response>()
    val saveStepsGoal: LiveData<SetGoalModel.Response> get() = _saveStepsGoal

    private var saveStepsListSource: LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>> = MutableLiveData()
    private val _saveStepsList = MediatorLiveData<StepsSaveListModel.StepsSaveListResponse>()
    val saveStepsList: LiveData<StepsSaveListModel.StepsSaveListResponse> get() = _saveStepsList

    fun getStepsHistory( fragment:FitnessDashboardFragment,stepCountHelper : StepCountHelper ) = viewModelScope.launch(dispatchers.main) {

        val df = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD,Locale.ENGLISH)
        val calEndTime = Calendar.getInstance()
        calEndTime.add(Calendar.DATE, -30)
        val fromDate = df.format(calEndTime.time)

        val searchCriteria: StepsHistoryModel.SearchCriteria = StepsHistoryModel.SearchCriteria(
            fromDate = fromDate, toDate = DateHelper.currentDateAsStringyyyyMMdd,
            personID = adminPersonId)

        val requestData = StepsHistoryModel(Gson().toJson(StepsHistoryModel.JSONDataRequest(
            searchCriteria = searchCriteria, message = "Getting List.."),
            StepsHistoryModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN, "")!!)

        _progressBar.value = Event("Loading...")
        _stepsHistoryList.removeSource(stepsHistorySource)
        withContext(dispatchers.io) { stepsHistorySource = useCase.invokeStpesHistory(requestData) }
        _stepsHistoryList.addSource(stepsHistorySource) {
            _stepsHistoryList.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //Timber.i("History---> ${it.data}")
                val history = it.data!!.stepGoalHistory.toMutableList()
                Utilities.printData("ApiHistory",history)
                if ( !history.isNullOrEmpty() ) {
                    stepsDataSingleton.stepHistoryList.clear()
                    stepsDataSingleton.stepHistoryList.addAll(history)
                }
                fragment.displayFitnessData()
                if (FitnessDataManager.getInstance(context)!!.oAuthPermissionsApproved()) {
                    stepCountHelper.synchronize(context)
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

    fun getLatestStepGoal(fragment:FitnessDashboardFragment) = viewModelScope.launch(dispatchers.main) {
        try {
            val requestData = GetStepsGoalModel(Gson().toJson(GetStepsGoalModel.JSONDataRequest(
                personID = sharedPref.getString(PreferenceConstants.PERSONID, "")!!),
                GetStepsGoalModel.JSONDataRequest::class.java), sharedPref.getString(
                PreferenceConstants.TOKEN, "")!!)

            _progressBar.value = Event("Loading...")
            _getLatestStepsGoal.removeSource(getLatestStepsGoalSource)
            withContext(dispatchers.io) {
                getLatestStepsGoalSource = useCase.invokeFetchStepsGoal(requestData)
            }
            _getLatestStepsGoal.addSource(getLatestStepsGoalSource) {
                _getLatestStepsGoal.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    fragment.latestGoalResp(it.data!!.latestGoal)
                }

                if (it.status == Resource.Status.ERROR) {
                    if(it.errorNumber.equals("1100014",true)){
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

    fun saveStepsGoal(fragment:FitnessDashboardFragment,goal: Int) = viewModelScope.launch(dispatchers.main) {

        val request: SetGoalModel.StepsGoalsReq = SetGoalModel.StepsGoalsReq(
            date = DateHelper.currentUTCDatetimeInMillisecAsString,
            goal = goal,
            personID = sharedPref.getString(PreferenceConstants.PERSONID, "")!!,
            type = "WAL")

        val requestData = SetGoalModel(Gson().toJson(SetGoalModel.JSONDataRequest(
                    stepsGoals = request,
                    message = "Getting List.."),
            SetGoalModel.JSONDataRequest::class.java), sharedPref.getString(PreferenceConstants.TOKEN, "")!!)

        _progressBar.value = Event("Loading...")
        _saveStepsGoal.removeSource(saveStepsGoalSource)
        withContext(dispatchers.io) {
            saveStepsGoalSource = useCase.invokeSaveStepsGoal(requestData)
        }
        _saveStepsGoal.addSource(saveStepsGoalSource) {
            _saveStepsGoal.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                fragment.saveGoalResp(it.data!!.stepsGoals)
                Utilities.toastMessageShort(context, context.resources.getString(R.string.MSG_GOALS_UPDATED))
                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.STEPS_GOAL_UPDATE)
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

    fun saveStepsList( fitnessDataJSONArray: JSONArray ) = viewModelScope.launch(dispatchers.main) {
        try {
            val stepsDetailsList = ArrayList<StepsSaveListModel.StepsDetail>()
            for ( i in 0 until fitnessDataJSONArray.length() ) {
                if (fitnessDataJSONArray.getJSONObject(i) != null) {
                    //StepsCount,Calories,Distance,ActiveTime
                    val stepsDataObj = fitnessDataJSONArray.getJSONObject(i)
                    val stepsDetail = StepsSaveListModel.StepsDetail(
                        personID = sharedPref.getString(PreferenceConstants.PERSONID, "")!!,
                        recordDate = DateHelper.convertDateTimeValue(stepsDataObj.getString(
                            Constants.RECORD_DATE), DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.SERVER_DATE_YYYYMMDD)!!,
                        count = stepsDataObj.getString(Constants.STEPS_COUNT),
                        calories = stepsDataObj.getString(Constants.CALORIES),
                        distance = stepsDataObj.getString(Constants.DISTANCE)
                    )
                    stepsDetailsList.add(stepsDetail)
                }
            }
/*            var showProgressBar = false
            if (stepsDetailsList.size >= 30) {
                showProgressBar = true
            }*/
            val requestData = StepsSaveListModel(Gson().toJson(StepsSaveListModel.JSONDataRequest(
                stepsDetails = stepsDetailsList),
                StepsSaveListModel.JSONDataRequest::class.java),sharedPref.getString(PreferenceConstants.TOKEN,"")!!)

            _progressBar.value = Event("Loading...")
            _saveStepsList.removeSource(saveStepsListSource)
            withContext(dispatchers.io) {
                saveStepsListSource = useCase.invokeSaveStepsList(requestData)
            }
            _saveStepsGoal.addSource(saveStepsListSource) {
                _saveStepsList.value = it.data

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    it.data!!.stepsDetails
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

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}