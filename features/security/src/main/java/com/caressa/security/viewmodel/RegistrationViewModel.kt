package com.caressa.security.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.Event
import com.caressa.common.utils.LocaleHelper
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
import java.util.*

@SuppressLint("StaticFieldLeak")
class RegistrationViewModel(private val userManagementUseCase: UserManagementUseCase,
                            private val dispatchers: AppDispatchers,
                            private val sharedPref: SharedPreferences,
                            val context: Context) : BaseViewModel() {

    private val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!

    private val _isLoginName = MediatorLiveData<LoginNameExistsModel.IsExistResponse>()
    val isLoginName: LiveData<LoginNameExistsModel.IsExistResponse> get() = _isLoginName
    private var loginNameSource: LiveData<Resource<LoginNameExistsModel.IsExistResponse>> = MutableLiveData()

    fun checkLoginNameExistOrNot(name: String = "", emailAddress: String, phoneNumber:String, password:String, confirmPassword:String) = viewModelScope.launch(dispatchers.main){
        var result:Boolean = true
        var message:String = localResource.getString(R.string.ERROR_INVALID_DETAILS)

        if(!Validation.isValidName(name)){
            message = localResource.getString(R.string.ERROR_INVALID_NAME)
            result = false
        } else if(!Validation.isValidEmail(emailAddress)){
            message = localResource.getString(R.string.ERROR_INVALID_EMAIL)
            result = false
        } else if(!Validation.isValidPhoneNumber(phoneNumber)){
            message = localResource.getString(R.string.ERROR_INVALID_PHONE)
            result = false
        } else if(!Validation.isValidPassword(password)){
            message = localResource.getString(R.string.VALIDATE_PASSWORD)
            result = false
        } else if(!Validation.isValidPassword(confirmPassword)){
            message = localResource.getString(R.string.VALIDATE_PASSWORD)
            result = false
        }else if(!password.equals(confirmPassword,false)){
            message = localResource.getString(R.string.ERROR_PASSWORD_CONFIRM_DOES_NOT_MATCH)
            result = false
        }
        if(result) {
            val requestData = LoginNameExistsModel(Gson().toJson(LoginNameExistsModel.JSONDataRequest(
                loginName = emailAddress), LoginNameExistsModel.JSONDataRequest::class.java))

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
                        toastMessage(localResource.getString(R.string.ERROR_EMAIL_REGISTERED))
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