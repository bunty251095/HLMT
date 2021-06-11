package com.caressa.home.common

import com.caressa.common.view.AutocompleteTextViewModel
import com.caressa.model.home.FamilyDoctor
import java.util.*

class HomeSingleton {

    private var doctorsDetails : FamilyDoctor = FamilyDoctor()
    private var doctorsList : List<FamilyDoctor> = ArrayList()
    private var specialityList: ArrayList<AutocompleteTextViewModel> = ArrayList()

    fun getDoctorsDetails() : FamilyDoctor {
        return doctorsDetails
    }

    fun setDoctorsDetails( doctorsDetails : FamilyDoctor) {
        this.doctorsDetails = doctorsDetails
    }

    fun getDoctorsList() : List<FamilyDoctor> {
        return doctorsList
    }

    fun setDoctorsList(doctorsList: List<FamilyDoctor>) {
        this.doctorsList = doctorsList
    }

    fun getSpecialityList(): ArrayList<AutocompleteTextViewModel> {
        return specialityList
    }

    fun setSpecialityList(specialityList: ArrayList<AutocompleteTextViewModel>) {
        this.specialityList = specialityList
    }

    companion object {
        private var instance: HomeSingleton? = null
        fun getInstance(): HomeSingleton? {
            if (instance == null) {
                instance = HomeSingleton()
            }
            return instance
        }
    }

}