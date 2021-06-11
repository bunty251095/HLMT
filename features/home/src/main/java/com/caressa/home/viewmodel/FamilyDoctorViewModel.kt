package com.caressa.home.viewmodel

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
import com.caressa.common.utils.Event
import com.caressa.common.utils.Utilities
import com.caressa.common.view.AutocompleteTextViewModel
import com.caressa.home.common.DataHandler
import com.caressa.home.common.HomeSingleton
import com.caressa.home.domain.HomeManagementUseCase
import com.caressa.home.ui.FamilyDoctor.FamilyDoctorListActivity
import com.caressa.model.home.*
import com.caressa.model.home.FamilyDoctorAddModel.FamilyDoctorAddRequest
import com.caressa.model.home.FamilyDoctorUpdateModel.FamilyDoctorUpdateRequest
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class FamilyDoctorViewModel(private val homeManagementUseCase: HomeManagementUseCase, private val dispatchers: AppDispatchers,
                            private val sharedPref: SharedPreferences, private val dataHandler : DataHandler) : BaseViewModel() {

    var personId = sharedPref.getString(PreferenceConstants.PERSONID,"")!!
    var accountID = sharedPref.getString(PreferenceConstants.ACCOUNTID,"")!!
    val authToken = sharedPref.getString(PreferenceConstants.TOKEN,"")!!

    val familyDoctorsList = MutableLiveData<List<FamilyDoctor>>()

    var listDoctorsSource: LiveData<Resource<FamilyDoctorsListModel.FamilyDoctorsResponse>> = MutableLiveData()
    val _listDoctors = MediatorLiveData<FamilyDoctorsListModel.FamilyDoctorsResponse>()
    val listDoctors: LiveData<FamilyDoctorsListModel.FamilyDoctorsResponse> get() = _listDoctors

    var listSpecialitiesSource: LiveData<Resource<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>> = MutableLiveData()
    val _listSpecialities = MediatorLiveData<ListDoctorSpecialityModel.ListDoctorSpecialityResponse>()
    val listSpecialities: LiveData<ListDoctorSpecialityModel.ListDoctorSpecialityResponse> get() = _listSpecialities

    var addDoctorSource: LiveData<Resource<FamilyDoctorAddModel.FamilyDoctorAddResponse>> = MutableLiveData()
    val _addDoctor = MediatorLiveData<FamilyDoctorAddModel.FamilyDoctorAddResponse>()
    val addDoctor: LiveData<FamilyDoctorAddModel.FamilyDoctorAddResponse> get() = _addDoctor

    var updateDoctorSource: LiveData<Resource<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>> = MutableLiveData()
    val _updateDoctor = MediatorLiveData<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse>()
    val updateDoctor: LiveData<FamilyDoctorUpdateModel.FamilyDoctorUpdateResponse> get() = _updateDoctor

    var removeDoctorSource: LiveData<Resource<RemoveDoctorModel.RemoveDoctorResponse>> = MutableLiveData()
    val _removeDoctor = MediatorLiveData<RemoveDoctorModel.RemoveDoctorResponse>()
    val removeDoctor: LiveData<RemoveDoctorModel.RemoveDoctorResponse> get() = _removeDoctor

    fun callGetFamilyDoctorsListApi( context: Context ) = viewModelScope.launch(dispatchers.main) {

        val requestData = FamilyDoctorsListModel(Gson().toJson(
                FamilyDoctorsListModel.JSONDataRequest(accountID = accountID ),
            FamilyDoctorsListModel.JSONDataRequest::class.java) , authToken )

        _listDoctors.removeSource(listDoctorsSource)
        withContext(dispatchers.io) {
            listDoctorsSource = homeManagementUseCase.invokeDoctorsList(isForceRefresh = true, data = requestData)
        }
        _listDoctors.addSource(listDoctorsSource) {
            _listDoctors.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    val doctorsList = it.data!!.listFamilyDoctors
                    Timber.i("DoctorsList----->"+doctorsList)
                    HomeSingleton.getInstance()!!.setDoctorsList(doctorsList)
                    familyDoctorsList.postValue(doctorsList)
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

    fun callGetDoctorSpecialitiesApi( context: Context) = viewModelScope.launch(dispatchers.main) {

        val requestData = ListDoctorSpecialityModel(Gson().toJson(
            ListDoctorSpecialityModel.JSONDataRequest( speciality = "" ),
            ListDoctorSpecialityModel.JSONDataRequest::class.java) , authToken )

        _listSpecialities.removeSource(listSpecialitiesSource)
        withContext(dispatchers.io) {
            listSpecialitiesSource = homeManagementUseCase.invokeSpecialitiesList(isForceRefresh = true, data = requestData)
        }
        _listSpecialities.addSource(listSpecialitiesSource) {
            _listSpecialities.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    val specialityList = it.data!!.speciality
                    Timber.i("SpecialityList----->"+specialityList.size)
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

    fun callAddFamilyDoctorApi( context: Context,familyDoctor :FamilyDoctor) = viewModelScope.launch(dispatchers.main) {

        val requestData = FamilyDoctorAddModel(Gson().toJson(
            FamilyDoctorAddModel.JSONDataRequest(
                FamilyDoctorAddRequest(
                    accountID = accountID.toInt(),
                    firstName = familyDoctor.firstName,
                    emailAddress = familyDoctor.emailAddress,
                    primaryContactNo = familyDoctor.primaryContactNo,
                    specialty = familyDoctor.specialty,
                    affiliatedTo = familyDoctor.affiliatedTo)),
            FamilyDoctorAddModel.JSONDataRequest::class.java) , authToken )

        _addDoctor.removeSource(addDoctorSource)
        withContext(dispatchers.io) {
            addDoctorSource = homeManagementUseCase.invokeAddDoctor(isForceRefresh = true, data = requestData)
        }
        _addDoctor.addSource(addDoctorSource) {
            _addDoctor.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    val familyDoctor = it.data!!.familyDoctor
                    if ( !Utilities.isNullOrEmptyOrZero( familyDoctor.id.toString() ) ) {
                        Utilities.toastMessageShort(context, "Doctor Details Added Successfully")
                        val intent = Intent(context, FamilyDoctorListActivity::class.java)
                        intent.putExtra("from","Add")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
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

    fun callUpdateFamilyDoctorApi( context: Context,familyDoctor :FamilyDoctor) = viewModelScope.launch(dispatchers.main) {

        val requestData = FamilyDoctorUpdateModel(Gson().toJson(
            FamilyDoctorUpdateModel.JSONDataRequest(
                FamilyDoctorUpdateRequest(
                    id = familyDoctor.id,
                    accountID = accountID.toInt(),
                    firstName = familyDoctor.firstName,
                    emailAddress = familyDoctor.emailAddress,
                    primaryContactNo = familyDoctor.primaryContactNo,
                    specialty = familyDoctor.specialty,
                    affiliatedTo = familyDoctor.affiliatedTo)),
            FamilyDoctorUpdateModel.JSONDataRequest::class.java) , authToken )

        _updateDoctor.removeSource(updateDoctorSource)
        withContext(dispatchers.io) {
            updateDoctorSource = homeManagementUseCase.invokeUpdateDoctor(isForceRefresh = true, data = requestData)
        }
        _updateDoctor.addSource(updateDoctorSource) {
            _updateDoctor.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    val familyDoctor = it.data!!.familyDoctor
                    if ( !Utilities.isNullOrEmptyOrZero( familyDoctor.id.toString() ) ) {
                        Utilities.toastMessageShort(context, "Doctor Details Updated Successfully")
                        val intent = Intent(context, FamilyDoctorListActivity::class.java)
                        intent.putExtra("from","Update")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
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

    fun callDeleteFamilyDoctorApi( context: Context, doctorId : Int) = viewModelScope.launch(dispatchers.main) {

        val doctorIdsArray = listOf(doctorId)
        val requestData = RemoveDoctorModel(Gson().toJson(
            RemoveDoctorModel.JSONDataRequest(id = doctorIdsArray ),
            RemoveDoctorModel.JSONDataRequest::class.java) , authToken )

        _removeDoctor.removeSource(removeDoctorSource)
        withContext(dispatchers.io) {
            removeDoctorSource = homeManagementUseCase.invokeRemoveDoctor(isForceRefresh = true, data = requestData)
        }
        _removeDoctor.addSource(removeDoctorSource) {
            _removeDoctor.value = it.data

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null ) {
                    val isProcessed = it.data!!.isProcessed
                    Timber.i("isProcessed----->"+isProcessed)
                    if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                        val doctorsList = HomeSingleton.getInstance()!!.getDoctorsList().toMutableList()
                        for (i in doctorsList.indices) {
                            if (doctorsList[i].id.equals(doctorId)) {
                                doctorsList.removeAt(i)
                                Timber.i("Removed_Doctor_Id=>$doctorId")
                            }
                        }
                        familyDoctorsList.postValue(doctorsList)
                        Utilities.toastMessageShort(context, "Doctor deleted Successfully")
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

    fun getFamilyDoctorsList() = viewModelScope.launch(dispatchers.main) {
        withContext(dispatchers.io) {
            val doctorsList = HomeSingleton.getInstance()!!.getDoctorsList().toMutableList()
            familyDoctorsList.postValue(doctorsList)
        }
    }


}