package com.caressa.security.model

import com.caressa.common.constants.Constants
import com.caressa.common.utils.Validation

object UserInfo {
    var name:String = ""
    var dob:String = ""
    var emailAddress:String = ""
    var phoneNumber:String = ""
    var gender:String = ""
    var password:String = ""
    var from:String = ""
    var tempPassword:String? =""
    var fromChangePassword = false

    fun emptyAllValues(){
        name = ""
        dob = ""
        emailAddress = ""
        gender = ""
        password = ""
        from = ""
        tempPassword = ""
        fromChangePassword = false
    }
}