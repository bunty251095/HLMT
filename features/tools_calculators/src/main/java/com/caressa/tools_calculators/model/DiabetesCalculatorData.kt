package com.caressa.tools_calculators.model

import java.util.*

class DiabetesCalculatorData {
    fun getDiabetesCodeData(qCode: String?): ArrayList<DiabetesOption> {
        val opCodeList = ArrayList<DiabetesOption>()
        when (qCode) {
            "AGEGROUP" -> {
                opCodeList.add(DiabetesOption("UNDER35YRS", "0"))
                opCodeList.add(DiabetesOption("35TO44YRS", "2"))
                opCodeList.add(DiabetesOption("45TO54YRS", "4"))
                opCodeList.add(DiabetesOption("55TO64YRS", "6"))
                opCodeList.add(DiabetesOption("65OVRYRS", "8"))
            }
            "GENDER" -> {
                opCodeList.add(DiabetesOption("MALE", "3"))
                opCodeList.add(DiabetesOption("FEMALE", "0"))
            }
            "ORIGIN" -> {
                opCodeList.add(DiabetesOption("rdbAsianOriginYes", "2"))
                opCodeList.add(DiabetesOption("rdbAsianOriginNo", "0"))
            }
            "FAMILYHEALTH" -> {
                opCodeList.add(DiabetesOption("YES", "3"))
                opCodeList.add(DiabetesOption("NO", "0"))
            }
            "BLOODGLUCOSE" -> {
                opCodeList.add(DiabetesOption("YES", "6"))
                opCodeList.add(DiabetesOption("NO", "0"))
            }
            "HIGHBLOODPRESSUREMEDICATION" -> {
                opCodeList.add(DiabetesOption("YES", "2"))
                opCodeList.add(DiabetesOption("NO", "0"))
            }
            "SMOKINGHABITS" -> {
                opCodeList.add(DiabetesOption("YES", "2"))
                opCodeList.add(DiabetesOption("NO", "0"))
            }
            "FRUITHABITS" -> {
                opCodeList.add(DiabetesOption("YES", "0"))
                opCodeList.add(DiabetesOption("NO", "1"))
            }
            "PHYSICALEXERCISE" -> {
                opCodeList.add(DiabetesOption("YES", "0"))
                opCodeList.add(DiabetesOption("NO", "2"))
            }
        }
        return opCodeList
    }

    inner class DiabetesOption(code: String, score: String) {
        var code = ""
        var score = ""

        init {
            this.code = code
            this.score = score
        }
    }
}