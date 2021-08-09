package com.caressa.tools_calculators.model

import android.content.Context
import com.caressa.tools_calculators.R
import java.util.*

class StressAndAnxietyData {
    fun getStressAssessmentData(qCode: String,context: Context) : Question {
        val question = Question()
        val opCodeList = ArrayList<String>()
        when (qCode) {
            "DASS-21_D_LIFEMEANINGLESS" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_1)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_WORTHPERSON" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_2)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_BECOMEENTHUSIASTIC" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_3)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_DOWNHEARTEDBLUE" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_4)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_NOTHINGLOOKFORWARD" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_5)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_INITIATIVETHINGS" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_6)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_POSITIVEFEELING" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_7)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_GOODREASON" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_8)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_ABSENCEPHYSICALEXERTION" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_9)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_CLOSEPANIC" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_10)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_WORRIEDSITUATIONS" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_11)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_EXPERIENCEDTREMBLING" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_12)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_BREATHINGDIFFICULTY" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_13)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_DRYNESSMOUTH" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_14)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_RATHERTOUCHY" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_15)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_INTOLERANTANYTHING" -> {
                question.setqCode(qCode)
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_16)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_DIFFICULTTORELAX" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_17)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_GETTINGAGITATED" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_18)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_NERVOUSENERGY" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_19)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_OVERREACTSITUATIONS" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_20)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_HRDWINDDOWN" -> {
                question.qCode = qCode
                question.question = context.resources.getString(R.string.QUES_STRESS_ANXIETY_21)
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
                question.isLast = true
            }
        }
        question.optionCodes = opCodeList
        return question
    }
}