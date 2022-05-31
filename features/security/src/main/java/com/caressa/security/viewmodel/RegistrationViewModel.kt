package com.caressa.security.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.Validation
import com.caressa.model.security.LoginNameExistsModel
import com.caressa.repository.AppDispatchers
import com.caressa.repository.utils.Resource
import com.caressa.security.R
import com.caressa.security.domain.UserManagementUseCase
import com.caressa.security.model.UserInfo
import com.caressa.security.ui.RegistrationFragmentDirections
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationViewModel(private val userManagementUseCase: UserManagementUseCase, private val dispatchers: AppDispatchers,
                            private val sharedPref: SharedPreferences
) : BaseViewModel() {

    private val _isLoginName = MediatorLiveData<LoginNameExistsModel.IsExistResponse>()
    val isLoginName: LiveData<LoginNameExistsModel.IsExistResponse> get() = _isLoginName
    private var loginNameSource: LiveData<Resource<LoginNameExistsModel.IsExistResponse>> = MutableLiveData()

    fun checkLoginNameExistOrNot(name: String = "", emailAddress: String, phoneNumber:String, password:String, confirmPassword:String) = viewModelScope.launch(dispatchers.main){
        var result:Boolean = true
        var message:String = "Invalid Details"
        if(!Validation.isValidName(name)){
            message = "Please enter valid Name."
            result = false
        }else
        if(!Validation.isValidEmail(emailAddress)){
            message = "Please enter valid email address."
            result = false
        }else
        if(!Validation.isValidPhoneNumber(phoneNumber)){
            message = "Please enter valid phone number."
            result = false
        }else
        if(!Validation.isValidPassword(password)){
            message = "Please enter valid password."
            result = false
        }else
        if(!Validation.isValidPassword(confirmPassword)){
            message = "Please enter valid confirm password."
            result = false
        }else if(!password.equals(confirmPassword,false)){
            message = "Password and Confirm Password does not match."
            result = false
        }
        if(result) {
            val requestData = LoginNameExistsModel(
                Gson().toJson(
                    LoginNameExistsModel.JSONDataRequest(
                        loginName = emailAddress
                    ), LoginNameExistsModel.JSONDataRequest::class.java
                )
            )
            _progressBar.value = Event("Validating Username..")
            _isLoginName.removeSource(loginNameSource)
            withContext(dispatchers.io) {
                loginNameSource =
                    userManagementUseCase.invokeLoginNameExist(true, requestData)
            }
            _isLoginName.addSource(loginNameSource) {
                _isLoginName.value = it.data
                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.isExist.equals("true", true)) {
                        toastMessage("This email is already registered with us")
                    } else {
                        UserInfo.apply {
                            this.name = name
                            this.emailAddress = emailAddress
                            this.password = password
                            this.phoneNumber = phoneNumber
                            this.from = Constants.REGISTER
                        }
                        navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginWithOtpFragment())
                    }

                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }else{
            toastMessage(message)
        }
    }

}