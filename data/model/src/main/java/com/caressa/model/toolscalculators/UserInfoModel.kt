package com.caressa.model.toolscalculators

class UserInfoModel {
    private var age = "0"
    var isMale = true
    private var height = "0"
    private var weight = "0"
    private var waistSize = "0"
    private var cholesterol = "0"
    private var hdl = "0"
    private var systolicBp = "0"
    private var diastolicBp = "0"
    var dob = ""
    var isDataLoaded = false

    fun getAge(): String {
        return age
    }

    fun setAge(age: String) {
        this.age = age
    }

    fun getHeight(): String {
        if (height.equals("0.0", ignoreCase = true)) {
            height = "0"
        }
        return height
    }

    fun setHeight(height: String) {
        this.height = height
    }

    fun getWeight(): String {
        if (weight.equals("0.0", ignoreCase = true)) {
            weight = "0"
        }
        return weight
    }

    fun setWeight(weight: String) {
        this.weight = weight
    }

    fun getWaistSize(): String {
        if (waistSize.equals("0.0", ignoreCase = true)) {
            waistSize = "0"
        }
        return waistSize
    }

    fun setWaistSize(waistSize: String) {
        this.waistSize = waistSize
    }

    fun getCholesterol(): String {
        if (cholesterol.equals("0.0", ignoreCase = true)) {
            cholesterol = "0"
        }
        return cholesterol
    }

    fun setCholesterol(cholesterol: String) {
        this.cholesterol = cholesterol
    }

    fun getHdl(): String {
        if (hdl.equals("0.0", ignoreCase = true)) {
            hdl = "0"
        }
        return hdl
    }

    fun setHdl(hdl: String) {
        this.hdl = hdl
    }

    fun getSystolicBp(): String {
        if (systolicBp.equals("0.0", ignoreCase = true)) {
            systolicBp = "0"
        }
        return systolicBp
    }

    fun setSystolicBp(systolicBp: String) {
        this.systolicBp = systolicBp
    }

    fun getDiastolicBp(): String {
        if (diastolicBp.equals("0.0", ignoreCase = true)) {
            diastolicBp = "0"
        }
        return diastolicBp
    }

    fun setDiastolicBp(diastolicBp: String) {
        this.diastolicBp = diastolicBp
    }

    fun clearData() {
        instance = null
    }

    companion object {
        private var instance: UserInfoModel? = null
        fun getInstance(): UserInfoModel? {
            if (instance == null) {
                instance = UserInfoModel()
            }
            return instance
        }
    }
}