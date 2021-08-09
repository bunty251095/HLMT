package com.caressa.tools_calculators.model

import android.content.Context
import com.caressa.tools_calculators.R
import java.util.*

class SmartPhoneAddictionData {
    fun getSmartPhoneAddictionData(qCode: String?,context: Context): Question {
        val question: Question = Question()
        val opList = ArrayList<String>()
        val opCodeList = ArrayList<String>()
        when (qCode) {
            "ADDIC1" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_1)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC2" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_2)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC3" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_3)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC4" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_4)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC5" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_5)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC6" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_6)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC7" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_7)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC8" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_8)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC9" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_9)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC10" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_10)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC11" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_SMART_PHONE_ADDICTION_11)
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
                question.isLast = true
            }
        }
        question.options = opList
        question.optionCodes = opCodeList
        return question
    }
}