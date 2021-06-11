package com.caressa.tools_calculators.model

import java.util.*

class SmartPhoneAddictionData {
    fun getSmartPhoneAddictionData(qCode: String?): Question {
        val question: Question = Question()
        val opList = ArrayList<String>()
        val opCodeList = ArrayList<String>()
        when (qCode) {
            "ADDIC1" -> {
                question.qCode = qCode
                question.question = "Do you often think that your smartphone is ringing/ vibrating when it is not?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC2" -> {
                question.qCode = qCode
                question.question = "When your phone rings buzzes , do you feel an intense urge to check ?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC3" -> {
                question.qCode = qCode
                question.question = "Do you look at your phone after you get up in the morning or before going to bed ?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC4" -> {
                question.qCode = qCode
                question.question = "Do you sleep with your smartphone on or under your pillow or next to your bed ?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC5" -> {
                question.qCode = qCode
                question.question = "Do you text, e-mail / surfed the web while waiting at the traffic signal?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC6" -> {
                question.qCode = qCode
                question.question = "Do you constantly check your phone if you did not have a data signal or Wi-Fi?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC7" -> {
                question.qCode = qCode
                question.question = "Do you feel a great deal of anxiety if your phone not working/you accidentally left it ?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC8" -> {
                question.qCode = qCode
                question.question = "Do you find yourself Always passing time in searching on google/ E-commerce sites ?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC9" -> {
                question.qCode = qCode
                question.question = "Do you spend more time texting, tweeting/emailing then talking to real-time people?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC10" -> {
                question.qCode = qCode
                question.question = "When you eat meals, is your cell phone always part of the table place setting?"
                opList.add("ADDICTION_LEVEL")
                opCodeList.add("ADDICTION_LEVEL")
            }
            "ADDIC11" -> {
                question.qCode = qCode
                question.question = "Do you feel lonely if your smartphone doesn't ring for several hours?"
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