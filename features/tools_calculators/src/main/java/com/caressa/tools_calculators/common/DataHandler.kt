package com.caressa.tools_calculators.common

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.caressa.common.utils.CalculateParameters
import com.caressa.common.utils.ParameterDataModel
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.VitalParameter
import com.caressa.common.view.SpinnerModel
import com.caressa.model.toolscalculators.UserInfoModel
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.model.*
import com.caressa.tools_calculators.ui.HeartAgeCalculator.HeartAgeFragment
import com.caressa.tools_calculators.ui.HypertensionCalculator.HypertensionInputFragment
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class DataHandler(val context: Context) {

    fun getTrackersList(): ArrayList<TrackerDashboardModel> {
        val list: ArrayList<TrackerDashboardModel> = ArrayList()
        list.add(TrackerDashboardModel(context.resources.getString(R.string.TRACKER_HEART_AGE), context.resources.getString(R.string.TRACKER_DESC__HEART_AGE), R.drawable.img_heart_age, ContextCompat.getColor(context, R.color.vivantNavy), "HAC"))
        list.add(TrackerDashboardModel(context.resources.getString(R.string.TRACKER_DIABETES), context.resources.getString(R.string.TRACKER_DESC_DIABETES), R.drawable.img_diabetes, ContextCompat.getColor(context, R.color.vivantGreen), "DC"))
        list.add(TrackerDashboardModel(context.resources.getString(R.string.TRACKER_HYPERTENSION), context.resources.getString(R.string.TRACKER_DESC_HYPERTENSION), R.drawable.img_hypertension, ContextCompat.getColor(context, R.color.vivant_nasty_green), "HC"))
        list.add(TrackerDashboardModel(context.resources.getString(R.string.TRACKER_STRESS_ANXIETY), context.resources.getString(R.string.TRACKER_DESC_STRESS_ANXIETY), R.drawable.img_stress_anxiety, ContextCompat.getColor(context, R.color.vivantOrange), "SAC"))
        list.add(TrackerDashboardModel(context.resources.getString(R.string.TRACKER_SMART_PHONE), context.resources.getString(R.string.TRACKER_DESC_SMART_PHONE), R.drawable.img_smart_phone, ContextCompat.getColor(context, R.color.vivantCyan), "SPC"))
        list.add(TrackerDashboardModel(context.resources.getString(R.string.TRACKER_DUE_DATE), context.resources.getString(R.string.TRACKER_DESC_DUE_DATE), R.drawable.img_due_date, ContextCompat.getColor(context, R.color.vivantRed), "DDC"))
        //list.add(TrackerDashboardModel(context.resources.getString(R.string.tracker_vaccination), context.resources.getString(R.string.tracker_desc__vaccination), R.drawable.img_vaccination,ContextCompat.getColor(context,R.color.vivantRed), "VC"))
        return list
    }

    fun getGenderList(): ArrayList<SpinnerModel> {
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(context.resources.getString(R.string.MALE), "", 0, true))
        list.add(SpinnerModel(context.resources.getString(R.string.FEMALE), "", 1, false))
        return list
    }

    fun getModelList(): ArrayList<SpinnerModel> {
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(context.resources.getString(R.string.BMI), "", 0, true))
        list.add(SpinnerModel(context.resources.getString(R.string.LIPID), "", 1, false))
        return list
    }

    fun getAgeGroupList(): ArrayList<SpinnerModel> {
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel("Under 35 Years", "", 0, true))
        list.add(SpinnerModel("35 - 44", "", 1, false))
        list.add(SpinnerModel("45 - 54", "", 2, false))
        list.add(SpinnerModel("55 - 64", "", 3, false))
        list.add(SpinnerModel("65 yrs or Over", "", 4, false))
        return list
    }

    fun getDepressionList(): List<String> {
        val list = ArrayList<String>()
        list.add("Dysphoria")
        list.add("Lack of interest / involvement")
        list.add("Hopelessness")
        list.add("Anhedonia")
        list.add("Devaluation of life")
        list.add("Inertia")
        list.add("Self-deprecation")
        return list
    }

    fun getAnxietyList(): List<String> {
        val list = ArrayList<String>()
        list.add("Autonomic arousal")
        list.add("Self-deprecation")
        list.add("Skeletal muscle effects")
        list.add("Subjective experience of anxious affect.")
        list.add("Situational anxiety")
        return list
    }

    fun getStressList(): List<String> {
        val list = ArrayList<String>()
        list.add("Difficulty relaxing")
        list.add("Nervous arousal")
        list.add("Being easily upset/agitated, irritable/over-reactive and impatient.")
        return list
    }

    fun getVitalParameterData(parameter: String, context: Context): VitalParameter {
        val vitalParameter = VitalParameter()

        when(parameter) {

            context.resources.getString(R.string.ft) -> {
                vitalParameter.unit = context.resources.getString(R.string.ft)
                vitalParameter.minRange = 4
                vitalParameter.maxRange = 7
            }

            context.resources.getString(R.string.cm) -> {
                vitalParameter.unit = context.resources.getString(R.string.cm)
                vitalParameter.minRange = 120
                vitalParameter.maxRange = 240
            }

            context.resources.getString(R.string.lbs) -> {
                vitalParameter.unit = context.resources.getString(R.string.lbs)
                vitalParameter.minRange = 64
                vitalParameter.maxRange = 550
            }

            context.resources.getString(R.string.kg) -> {
                vitalParameter.unit = context.resources.getString(R.string.kg)
                vitalParameter.minRange = 30
                vitalParameter.maxRange = 250
            }

        }
        return vitalParameter
    }

    fun getParameterList(model: String, fragment: Fragment): ArrayList<ParameterDataModel> {
        val paramList: ArrayList<ParameterDataModel> = ArrayList()
        val userInfoModel: UserInfoModel? = if (fragment is HeartAgeFragment || fragment is HypertensionInputFragment) {
            UserInfoModel.getInstance()
        } else {
            CalculatorDataSingleton.getInstance()!!.userPreferences
        }
        var data = ParameterDataModel()
        if (model.equals("BMI", ignoreCase = true)) {
            data.title = "Height"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel!!.getHeight()) ) {
                val `val`: String = CalculateParameters.convertCmToFeet(userInfoModel.getHeight())
                    .toString() + "'" + CalculateParameters.convertCmToInch(
                    userInfoModel.getHeight()) + "''"
                data.value = `val`
                data.finalValue = userInfoModel.getHeight()
            } else {
                data.value = "- -"
                data.finalValue = "0"
            }
            data.unit = "Feet/inch"
            data.code = "HEIGHT"
            data.color = R.color.vivant_nasty_green
            data.img = R.drawable.height
            data.description = "Today via mobile entry"
            paramList.add(data)
            data = ParameterDataModel()
            data.title = "Systolic BP"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel.getSystolicBp()) ) {
                val `val` = "" + userInfoModel.getSystolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = "mm Hg"
            data.code = "SYSTOLIC_BP"
            data.color = R.color.vivant_bright_sky_blue
            data.img = R.drawable.systolic
            data.description = "Today via mobile entry"
            data.minRange = 0.01
            data.maxRange = 500.0
            paramList.add(data)
            data = ParameterDataModel()
            data.title = "Weight"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel.getWeight()) ) {
                val `val` = "" + userInfoModel.getWeight().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
                data.finalValue = "50"
            }
            data.unit = "Kg"
            data.code = "WEIGHT"
            data.color = R.color.vivant_marigold
            data.img = R.drawable.weight
            data.description = "Today via mobile entry"
            paramList.add(data)
            data = ParameterDataModel()
            data.title = "Diastolic BP"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel.getDiastolicBp()) ) {
                val `val` = "" + userInfoModel.getDiastolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = "mm Hg"
            data.code = "DIASTOLIC_BP"
            data.color = R.color.vivant_watermelon
            data.img = R.drawable.systolic
            data.description = "Today via mobile entry"
            data.minRange = 10.0
            data.maxRange = 500.0
            paramList.add(data)
        } else if (model.equals("LIPID", ignoreCase = true)) {
            data.title = "Cholesterol"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel!!.getCholesterol()) ) {
                val `val` = "" + userInfoModel.getCholesterol().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.description = "Today via mobile entry"
            data.unit = "mmol/L"
            data.code = "TOTAL_CHOL"
            data.color = R.color.vivant_nasty_green
            data.img = R.drawable.cholesteroal
            data.minRange = 0.01
            data.maxRange = 100.0
            paramList.add(data)
            data = ParameterDataModel()
            data.title = "Systolic BP"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel.getSystolicBp()) ) {
                val `val` = "" + userInfoModel.getSystolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = "mm Hg"
            data.code = "SYSTOLIC_BP"
            data.color = R.color.vivantCyan
            data.img = R.drawable.systolic
            data.description = "Today via mobile entry"
            data.minRange = 0.01
            data.maxRange = 500.0
            paramList.add(data)
            data = ParameterDataModel()
            data.title = "HDL"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel.getHdl()) ) {
                val `val` = "" + userInfoModel.getHdl().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = "mmol/L"
            data.code = "HDL"
            data.color = R.color.vivant_marigold
            data.img = R.drawable.hdl
            data.description = "Today via mobile entry"
            data.minRange = 0.01
            data.maxRange = 100.0
            paramList.add(data)
            data = ParameterDataModel()
            data.title = "Diastolic BP"
            if ( !Utilities.isNullOrEmptyOrZero(userInfoModel.getDiastolicBp()) ) {
                val `val` = "" + userInfoModel.getDiastolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = "mm Hg"
            data.code = "DIASTOLIC_BP"
            data.color = R.color.vivant_watermelon
            data.img = R.drawable.systolic
            data.description = "Today via mobile entry"
            data.minRange = 10.0
            data.maxRange = 500.0
            paramList.add(data)
        }
        return paramList
    }

        fun getDiabetesParameterList(): ArrayList<ParameterDataModel> {
            val waistSize = UserInfoModel.getInstance()!!.getWaistSize()
            val paramList = ArrayList<ParameterDataModel>()
            val data = ParameterDataModel()
            data.title = "Waist"
            Timber.e("Waist:: $waistSize")
            if ( !Utilities.isNullOrEmptyOrZero(waistSize) ) {
                try {
                    val `val` = "" + waistSize.toDouble().toInt()
                    val waist = CalculateParameters.convertCmToInch2(`val`).toDouble().roundToInt()
                    data.value = waist.toString()
                    data.finalValue = waist.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                data.value = "- -"
            }
            data.unit = "Inch"
            data.code = "WAISTMEASUREMENT"
            data.color = R.color.vivantCyan
            data.img = R.drawable.waist
            data.minRange = 25.0
            data.maxRange = 65.0
            data.description = "Today via mobile entry"
            paramList.add(data)
            return paramList
        }

        private val sequenceList = LinkedList<String>()

        fun getSmartPhoneAddictionNextQuestion(currentQuestionId: String): String {
            var result = "FIRST"
            when (currentQuestionId) {
                "FIRST" -> result = "ADDIC1"
                "ADDIC1" -> result = "ADDIC2"
                "ADDIC2" -> result = "ADDIC3"
                "ADDIC3" -> result = "ADDIC4"
                "ADDIC4" -> result = "ADDIC5"
                "ADDIC5" -> result = "ADDIC6"
                "ADDIC6" -> result = "ADDIC7"
                "ADDIC7" -> result = "ADDIC8"
                "ADDIC8" -> result = "ADDIC9"
                "ADDIC9" -> result = "ADDIC10"
                "ADDIC10" -> result = "ADDIC11"
                "ADDIC11" -> result = "ADDIC11"
            }
            return result
        }

        fun getSmartPhoneAddictionPrevious(questionId: String): String {
            var result = "FIRST"
            when (questionId) {
                "FIRST" -> result = "FIRST"
                "ADDIC1" -> result = "FIRST"
                "ADDIC2" -> result = "ADDIC1"
                "ADDIC3" -> result = "ADDIC2"
                "ADDIC4" -> result = "ADDIC3"
                "ADDIC5" -> result = "ADDIC4"
                "ADDIC6" -> result = "ADDIC5"
                "ADDIC7" -> result = "ADDIC6"
                "ADDIC8" -> result = "ADDIC7"
                "ADDIC9" -> result = "ADDIC8"
                "ADDIC10" -> result = "ADDIC9"
                "ADDIC11" -> result = "ADDIC10"
            }
            return result
        }

        fun getStressNextQuestion(currentQuesId: String): String {
            var result = "FIRST"
            when (currentQuesId) {
                "FIRST" -> result = "DASS-21_D_LIFEMEANINGLESS"
                "DASS-21_D_LIFEMEANINGLESS" -> result = "DASS-21_D_WORTHPERSON"
                "DASS-21_D_WORTHPERSON" -> result = "DASS-21_D_BECOMEENTHUSIASTIC"
                "DASS-21_D_BECOMEENTHUSIASTIC" -> result = "DASS-21_D_DOWNHEARTEDBLUE"
                "DASS-21_D_DOWNHEARTEDBLUE" -> result = "DASS-21_D_NOTHINGLOOKFORWARD"
                "DASS-21_D_NOTHINGLOOKFORWARD" -> result = "DASS-21_D_INITIATIVETHINGS"
                "DASS-21_D_INITIATIVETHINGS" -> result = "DASS-21_D_POSITIVEFEELING"
                "DASS-21_D_POSITIVEFEELING" -> result = "DASS-21_A_GOODREASON"
                "DASS-21_A_GOODREASON" -> result = "DASS-21_A_ABSENCEPHYSICALEXERTION"
                "DASS-21_A_ABSENCEPHYSICALEXERTION" -> result = "DASS-21_A_CLOSEPANIC"
                "DASS-21_A_CLOSEPANIC" -> result = "DASS-21_A_WORRIEDSITUATIONS"
                "DASS-21_A_WORRIEDSITUATIONS" -> result = "DASS-21_A_EXPERIENCEDTREMBLING"
                "DASS-21_A_EXPERIENCEDTREMBLING" -> result = "DASS-21_A_BREATHINGDIFFICULTY"
                "DASS-21_A_BREATHINGDIFFICULTY" -> result = "DASS-21_A_DRYNESSMOUTH"
                "DASS-21_A_DRYNESSMOUTH" -> result = "DASS-21_S_RATHERTOUCHY"
                "DASS-21_S_RATHERTOUCHY" -> result = "DASS-21_S_INTOLERANTANYTHING"
                "DASS-21_S_INTOLERANTANYTHING" -> result = "DASS-21_S_DIFFICULTTORELAX"
                "DASS-21_S_DIFFICULTTORELAX" -> result = "DASS-21_S_GETTINGAGITATED"
                "DASS-21_S_GETTINGAGITATED" -> result = "DASS-21_S_NERVOUSENERGY"
                "DASS-21_S_NERVOUSENERGY" -> result = "DASS-21_S_OVERREACTSITUATIONS"
                "DASS-21_S_OVERREACTSITUATIONS" -> result = "DASS-21_S_HRDWINDDOWN"
                "DASS-21_S_HRDWINDDOWN" -> result = "DASS-21_S_HRDWINDDOWN"
            }
            sequenceList.add(currentQuesId)
            //Timber.i("SEQUENCELIST => " + sequenceList)
            return result
        }

        fun getStressPreviousQuestion(questionId: String): String {
            //Timber.i("SEQUENCELIST => " + sequenceList)
            return if (sequenceList.size > 0) {
                sequenceList.removeLast()
            } else {
                "FIRST"
            }
        }

        fun clearSequenceList() {
            sequenceList.clear()
        }

}
