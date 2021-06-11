package com.caressa.hra.common

import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import timber.log.Timber
import java.util.*

class HraDataSingleton {

    var question = Question()
    val previousAnsList = HashMap<Int, MutableList<Option>>()
    var selectedOptionList: MutableList<Option> = ArrayList()

    fun getPrevAnsList(pageNumber: Int): MutableList<Option> {
        Timber.e("previousPageNumber---> $pageNumber")
        return if (previousAnsList.containsKey(pageNumber)) {
            previousAnsList[pageNumber]!!
        } else ArrayList()
    }

    fun clearData() {
        instance = null
    }

    companion object {
        private var instance: HraDataSingleton? = null
        fun getInstance(): HraDataSingleton? {
            if (instance == null) {
                instance = HraDataSingleton()
            }
            return instance
        }
    }

}