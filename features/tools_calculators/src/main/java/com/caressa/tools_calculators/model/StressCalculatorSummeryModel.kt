package com.caressa.tools_calculators.model

import java.util.*

class StressCalculatorSummeryModel {
    var stage = "0"
    var parameterReport: ArrayList<HypertensionResultPojo> =
        ArrayList<HypertensionResultPojo>()
    var depression: StressData = StressData()
    var anxiety: StressData = StressData()
    var stress: StressData = StressData()

}
