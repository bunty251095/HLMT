package com.caressa.tools_calculators.model

import android.util.ArrayMap
import java.util.*

class HypertensionSummeryModel {
    var hypertensionRisk: ArrayMap<String, ArrayList<String>> = ArrayMap()
    var stage: HypertensionResultPojo = HypertensionResultPojo()
    var recommendation: HypertensionResultPojo = HypertensionResultPojo()
    var smokingReport: HypertensionResultPojo = HypertensionResultPojo()
    var bpReport: HypertensionResultPojo = HypertensionResultPojo()
    var parameterReport: HypertensionResultPojo = HypertensionResultPojo()
    var status = ""
    var color = "#000000"
    var systolicBp = ""
    var diastolicBp = ""
}