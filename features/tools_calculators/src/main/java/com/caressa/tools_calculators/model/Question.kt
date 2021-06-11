package com.caressa.tools_calculators.model

import java.util.*

class Question {
    var question = ""
    var qCode = ""
    var options = ArrayList<String>()
    var optionCodes =
        ArrayList<String>()
    var subQuestion = ""
    var subQuestionCode = ""
    var yesBtnAction = ""
    var noBtnAction = ""
    var txtFieldValue = ""
    var isLast = false

    fun getqCode(): String {
        return qCode
    }

    fun setqCode(qCode: String) {
        this.qCode = qCode
    }

}