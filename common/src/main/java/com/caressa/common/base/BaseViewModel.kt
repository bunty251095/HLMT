package com.caressa.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.caressa.common.utils.Event
import com.caressa.navigation.NavigationCommand


abstract class BaseViewModel : ViewModel() {

    // FOR ERROR HANDLER
    protected val _snackbarError = MutableLiveData<Event<Int>>()
    val snackBarError: LiveData<Event<Int>> get() = _snackbarError

    // FOR SESSION EXPIRED
    protected val _sessionError = MutableLiveData<Event<Boolean>>()
    val sessionError: LiveData<Event<Boolean>> get() = _sessionError

    // FOR Progress Dialog
    protected val _progressBar = MutableLiveData<Event<String>>()
    val progressBar: LiveData<Event<String>> get() = _progressBar

    // FOR ERROR HANDLER
    protected val _snackbarMessage = MutableLiveData<Event<String>>()
    val snackMessenger: LiveData<Event<String>> get() = _snackbarMessage

    // FOR ERROR HANDLER
    protected val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    // FOR NAVIGATION
    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    val navigation: LiveData<Event<NavigationCommand>> = _navigation

    /**
     * Convenient method to handle navigation from a [ViewModel]
     */
    fun navigate(directions: NavDirections) {
        _navigation.value = Event(NavigationCommand.To(directions))
    }

    fun toastMessage(message: String = "") {
        _toastMessage.value = Event(message)
    }

    fun snackMessage(message: String = "") {
        _snackbarMessage.value = Event(message)
    }

    fun showProgressBar(message: String = "") {
        _progressBar.value = Event(message)
    }

    fun hideProgressBar() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

}