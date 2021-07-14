package com.caressa.tools_calculators.model

import android.util.ArrayMap
import com.caressa.model.toolscalculators.UserInfoModel
import timber.log.Timber
import java.util.*

class CalculatorDataSingleton private constructor() {

    var quizId = ""
    var participantID = ""
    var templateId = ""
    var smartPhoneScore = "0"
    var riskLabel = ""
    var riskScorePercentage = ""
    var heartAge = ""

    var personAge = "0"
    var userPreferences = UserInfoModel()
    var heartAgeModel = "BMI"

    var healthConditionSelection = ArrayList<String>()

    var heartAgeSummery = HeartAgeSummeryModel()
    val heartAgeSummeryList : ArrayList<HeartAgeSummeryModel> = ArrayList()
    var diabetesSummeryModel = DiabetesSummeryModel()
    var hypertensionSummery = HypertensionSummeryModel()
    var stressSummeryData = StressCalculatorSummeryModel()
    var answerArrayMap = ArrayMap<String, Answer>()
    var lmpDate: String? = null

    fun clearData() {
        instance = null
        Timber.e("Cleared ToolsTracker Data")
    }

    companion object {
        private var instance: CalculatorDataSingleton? = null
        fun getInstance(): CalculatorDataSingleton? {
            if (instance == null) {
                instance = CalculatorDataSingleton()
            }
            return instance
        }
    }
}