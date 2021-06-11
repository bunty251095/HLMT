package com.caressa.records_tracker.common

import android.util.ArrayMap
import com.caressa.model.entity.HealthDocument
import com.caressa.model.shr.HealthDataParameter
import java.util.*

class RecordSingleton private constructor() {

    private var record: HealthDocument = HealthDocument()
    private var digitizedParamList : List<HealthDataParameter> = ArrayList()

    fun getHealthRecord(): HealthDocument {
        return record
    }

    fun setHealthRecord( healthDocument: HealthDocument) {
        this.record = healthDocument
    }

    fun getDigitizedParamList(): List<HealthDataParameter> {
        return digitizedParamList
    }

    fun setDigitizedParamList(digitizedParamList: List<HealthDataParameter>) {
        this.digitizedParamList = digitizedParamList
    }

    companion object {
        private var instance: RecordSingleton? = null
        fun getInstance(): RecordSingleton? {
            if (instance == null) {
                instance = RecordSingleton()
            }
            return instance
        }
    }
}