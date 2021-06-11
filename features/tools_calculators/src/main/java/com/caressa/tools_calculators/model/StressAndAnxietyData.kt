package com.caressa.tools_calculators.model

import java.util.*

class StressAndAnxietyData {
    fun getStressAssessmentData(qCode: String) : Question {
        val question = Question()
        val opCodeList = ArrayList<String>()
        when (qCode) {
            "DASS-21_D_LIFEMEANINGLESS" -> {
                question.qCode = qCode
                question.question = "I felt that life was meaningless."
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_WORTHPERSON" -> {
                question.qCode = qCode
                question.question = "I felt I wasn’t worth much as a person."
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_BECOMEENTHUSIASTIC" -> {
                question.qCode = qCode
                question.question = "I was unable to become enthusiastic about anything."
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_DOWNHEARTEDBLUE" -> {
                question.qCode = qCode
                question.question = "I felt down-hearted and blue."
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_NOTHINGLOOKFORWARD" -> {
                question.qCode = qCode
                question.question = "I felt that I had nothing to look forward to"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_INITIATIVETHINGS" -> {
                question.qCode = qCode
                question.question = "I found it difficult to work up the initiative to do things"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_D_POSITIVEFEELING" -> {
                question.qCode = qCode
                question.question = "I couldn’t seem to experience any positive feeling at all"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_GOODREASON" -> {
                question.qCode = qCode
                question.question = "I felt scared without any good reason"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_ABSENCEPHYSICALEXERTION" -> {
                question.qCode = qCode
                question.question = "I was aware of the action of my heart in the absence of physical exertion\n" +
                        "(eg, sense of heart rate increase, heart missing a beat)"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_CLOSEPANIC" -> {
                question.qCode = qCode
                question.question = "I felt I was close to panic"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_WORRIEDSITUATIONS" -> {
                question.qCode = qCode
                question.question = "I was worried about situations in which I might panic and make a fool of myself"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_EXPERIENCEDTREMBLING" -> {
                question.qCode = qCode
                question.question = "I experienced trembling\n" + "(eg, in the hands)"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_BREATHINGDIFFICULTY" -> {
                question.qCode = qCode
                question.question = "I experienced breathing difficulty\n" +
                        "(eg, excessively rapid breathing, breathlessness in the absence of physical exertion)"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_A_DRYNESSMOUTH" -> {
                question.qCode = qCode
                question.question = "I was aware of dryness of my mouth"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_RATHERTOUCHY" -> {
                question.qCode = qCode
                question.question = "I felt that I was rather touchy"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_INTOLERANTANYTHING" -> {
                question.setqCode(qCode)
                question.question = "I was intolerant of anything that kept me from getting on with what I was doing"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_DIFFICULTTORELAX" -> {
                question.qCode = qCode
                question.question = "I found it difficult to relax"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_GETTINGAGITATED" -> {
                question.qCode = qCode
                question.question = "I found myself getting agitated"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_NERVOUSENERGY" -> {
                question.qCode = qCode
                question.question = "I felt that I was using a lot of nervous energy"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_OVERREACTSITUATIONS" -> {
                question.qCode = qCode
                question.question = "I tended to over-react to situations"
                opCodeList.add("NEVER")
                opCodeList.add("SOMETIMES")
                opCodeList.add("OFTEN")
                opCodeList.add("ALMOSTALWAYS")
            }
            "DASS-21_S_HRDWINDDOWN" -> {
                question.qCode = qCode
                question.question = "I found it hard to wind down"
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