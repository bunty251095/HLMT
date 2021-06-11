package com.caressa.fitness_tracker.util

import android.content.Context
import android.content.SharedPreferences
import android.util.ArrayMap
import com.caressa.common.constants.Constants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.CalculateParameters
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.fitness_tracker.common.StepsDataSingleton
import com.caressa.fitness_tracker.viewmodel.FitnessViewModel
import org.json.JSONArray
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.*

class StepCountHelper(private val context: Context) : KoinComponent {

    private val TAG = StepCountHelper::class.java.simpleName

    private val viewModel : FitnessViewModel = get()

    fun synchronize(mContext: Context?) {
        refreshStepCountSync(mContext)
    }

    fun synchronizeForce(mContext: Context?) {
        refreshStepCountSyncForce(mContext)
    }

    @Synchronized
    fun pullFitnessDataFromGoogleFitForDate(mContext: Context?, startDate: Date?, endDate: Date?) {
        getStepsDelta(mContext, startDate, endDate)
    }

    private fun refreshStepCountSync(mContext: Context?) {
        try {
            val stepsDataSingleton = StepsDataSingleton.instance!!
            var calendar = Calendar.getInstance()
            var dateBefore: Date? = null
            var dateAfter = Calendar.getInstance().time
            try {
                if (stepsDataSingleton.stepHistoryList.isNotEmpty()) {
                    val dateListHistory = ArrayList<Date?>()
                    for ( i in 0 until stepsDataSingleton.stepHistoryList.size ) {
                        dateListHistory.add(DateHelper.getDate(stepsDataSingleton.stepHistoryList[i].recordDate,DateHelper.SERVER_DATE_YYYYMMDD))
                    }
                    Timber.e("$TAG LAST RECORD_DATE FOUND---> ${Collections.max(dateListHistory)}")
                    if (Collections.max(dateListHistory) != null) {
                        //DateHelper.convertStrToDateOBJ(Collections.min(dateListHistory));
                        dateBefore = Collections.max(dateListHistory)
                    }
                } else {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -365)
                    dateBefore = calendar.time
                    dateAfter = Calendar.getInstance().time
                }
            } catch (e: Exception) {
                e.printStackTrace()
                calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -365)
                dateBefore = calendar.time
                dateAfter = Calendar.getInstance().time
            }
            Timber.e("$TAG dateBefore---> $dateBefore")
            Timber.e("$TAG dateAfter---> $dateAfter")

            if ( dateBefore != null && dateAfter != null ) {
                val difference = dateAfter.time - dateBefore.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                Timber.e("$TAG StepCountHelper daysDifference ---> $daysDifference")
                //ToDo: Keyur, force sync for at-least 1 day
                //daysDifference = (Math.max(daysDifference, 1));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    Timber.e("$TAG sync for date ---> ${calendar.time}")
                    pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                }
            }

/*            if (dateBefore != null!! and dateAfter != null) {
                val difference = dateAfter.time - dateBefore!!.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                Log.d("StepCountHelper", " daysDifference -- $daysDifference")
                //ToDo: Keyur, force sync for at-least 1 day
                //daysDifference = (Math.max(daysDifference, 1));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    Log.d(TAG, " sync for date -- " + calendar.time)
                    pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refreshStepCountSyncForce(mContext: Context?) {
        val policyDetails = JSONArray()
        try {
            var calendar = Calendar.getInstance()
            var dateBefore: Date? = null
            var dateAfter = Calendar.getInstance().time
            try {
                calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -365)
                dateBefore = calendar.time
                dateAfter = Calendar.getInstance().time
                val dateList = ArrayList<Date>()
                for (i in 0 until policyDetails.length()) {
                    dateList.add(DateHelper.getDate(policyDetails.getJSONObject(i).getString("StartDate"), DateHelper.SERVER_DATE_YYYYMMDD))
                }
                if (dateList.size > 0) {
                    Timber.d("%s%s", TAG, Collections.min(dateList).toString())
                    dateBefore = Collections.min(dateList)
                }
                if (Calendar.getInstance().time.before(dateBefore)) {
                    Timber.d("today.isBefore--> $dateBefore")
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -365)
                    dateBefore = calendar.time
                    dateAfter = Calendar.getInstance().time
                }
            } catch (e: Exception) {
                e.printStackTrace()
                calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -365)
                dateBefore = calendar.time
                dateAfter = Calendar.getInstance().time
            }

            if ( dateBefore != null && dateAfter != null ) {
                val difference = dateAfter.time - dateBefore.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                //Log.d("StepCountHelper", " daysDifference -- " + daysDifference);
                //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                //daysDifference = (Math.max(daysDifference, 30));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    Timber.d("$TAG sync for date ---> ${calendar.time}")
                    pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                }
            }

/*            if (dateBefore != null!! and dateAfter != null) {
                val difference = dateAfter.time - dateBefore!!.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                //Log.d("StepCountHelper", " daysDifference -- " + daysDifference);
                //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                //daysDifference = (Math.max(daysDifference, 30));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    Log.d(TAG, " sync for date -- " + calendar.time)
                    pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getStepsDelta(mContext: Context?, startDate: Date?, endDate: Date?) {
        FitnessDataManager.getInstance(mContext)!!
            .readHistoryData(startDate!!, endDate!!).addOnCompleteListener { task ->
                if (FitnessDataManager.getInstance(mContext)!!.fitnessDataArray != null) {
                    Timber.e(FitnessDataManager.getInstance(mContext)!!.fitnessDataArray.toString())
                    updateAllStepsAndCallService(FitnessDataManager.getInstance(mContext)!!.fitnessDataArray)
                }
            }
    }

    private fun updateAllStepsAndCallService(fitnessDataMap: JSONArray?) {
        try {
            if (fitnessDataMap != null) {
                if (fitnessDataMap.length() > 0) {
                    viewModel.saveStepsList( fitnessDataMap )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun forceSyncStepsData(mContext: Context?) {
        val nDays = 30
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -nDays)
        Timber.d("StepCountHelper sync for date ---> $calendar.time")
        pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
    }

    fun subscribe(context: Context?) {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        FitnessDataManager(context).fitSignIn(FitRequestCode.READ_DATA)
    }

    private fun setLastSyncDateForStepsInPreference(value: String?) {
        try {
            val pref: SharedPreferences = context.getSharedPreferences("FITNESS", Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString(Constants.LAST_SYNC_DATE, value)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val lastSyncDateForStepsFromPreference: String
        get() {
            var value = ""
            try {
                val pref: SharedPreferences = context.getSharedPreferences("FITNESS", Context.MODE_PRIVATE)
                value = pref.getString(Constants.LAST_SYNC_DATE, null)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return value
        }

    /**
     * https://fitness.stackexchange.com/questions/25472/how-to-calculate-calorie-from-pedometer
     * https://www.livestrong.com/article/353942-how-long-does-it-take-to-walk-10-000-steps/
     * https://www.walkingwithattitude.com/articles/features/how-to-measure-stride-or-step-length-for-your-pedometer
     * http://www.gearedtobefit.com/fitness_calculators.cfm
     *
     *
     * http://www.gearedtobefit.com/fitness_calculators.cfm#calburn
     * function Calculate2(form)
     * {
     * var weight = document.burn2.weight.options[document.burn2.weight.selectedIndex].value;;
     * var stride = document.burn2.stride.options[document.burn2.stride.selectedIndex].value;
     * var steps = document.burn2.steps.options[document.burn2.steps.selectedIndex].value;
     * var calories = (weight*stride*steps)/138.462;
     * document.burn2.calories.value = Math.round(calories);
     * }
     */
    private fun getCalculatedFitnessDataFromSteps(stepsCount: Int): HashMap<String, String> {
        val dataMap = HashMap<String, String>()
        // default values
        var totalCalories = 0
        var totalDistanceInMtr = 0f
        var stepsActiveTime = 0
        val weightValue = 65f
        //val heightValue = 165f
        val gender = 1
        try {
            /*if (heightWeightMap != null) {
                weightValue = Float.parseFloat(heightWeightMap.get(GlobalConstants.WEIGHT));
                heightValue = Float.parseFloat(heightWeightMap.get(GlobalConstants.HEIGHT));
                gender = Integer.parseInt(heightWeightMap.get(GlobalConstants.GENDER));
            }*/
            val strideAsPerGenderInches =
                if (gender == 1) 84 * 0.415 else 84 * 0.413 // 0.415 for male, 0.413 for female
            val activityTimeFactor = 100 //avg 100 steps per minute
            val calorieBurnedFactor = 138.462 //weightValue * (strideLengthInCms / 100);
            totalDistanceInMtr =
                (stepsCount * (CalculateParameters.convertInchToCm(strideAsPerGenderInches.toString())
                    .toDouble() / 100)).toFloat()
            stepsActiveTime = stepsCount / activityTimeFactor
            stepsActiveTime = if (stepsActiveTime <= 0) 1 else stepsActiveTime // At least 1 min
            //===Calories based on weight and distance
            //Double distanceInmiles = totalDistanceInMtr * 0.00062137;
            //totalCalories = (int) (Double.parseDouble(Helper.convertKgToLbs(String.valueOf(weightValue))) * distanceInmiles * 0.468);

            //===Calories based on weight,steps, stride length
            var weightFactor = 107
            if (weightValue in 45.0..52.0) {
                weightFactor = 107
            } else if (weightValue in 53.0..59.0) {
                weightFactor = 123
            } else if (weightValue in 60.0..66.0) {
                weightFactor = 138
            } else if (weightValue in 67.0..73.0) {
                weightFactor = 157
            } else if (weightValue in 74.0..80.0) {
                weightFactor = 169
            } else if (weightValue in 81.0..86.0) {
                weightFactor = 183
            } else if (weightValue in 87.0..93.0) {
                weightFactor = 198
            } else if (weightValue in 94.0..100.0) {
                weightFactor = 213
            } else if (weightValue in 101.0..107.0) {
                weightFactor = 228
            } else if (weightValue in 108.0..130.0) {
                weightFactor = 260
            } else if (weightValue >= 131) {
                weightFactor = 310
            }
            totalCalories =
                (weightFactor * strideAsPerGenderInches * stepsCount / calorieBurnedFactor).toInt() / 1000

            /*Log.d("History", "CalculatedFitness -- weightValue LBS" + Double.parseDouble(Helper.convertKgToLbs(String.valueOf(weightValue))));
            Log.d("History", "CalculatedFitness -- strideLengthIn Inch" + Double.parseDouble(Helper.convertCmToInch(String.valueOf(strideLengthInInches))));
            Log.d("History", "CalculatedFitness -- distanceInmiles " + distanceInmiles);
            Log.d("History", "CalculatedFitness -- weightValue" + weightValue);
            Log.d("History", "CalculatedFitness -- heightValue" + heightValue);
            Log.d("History", "CalculatedFitness -- gender" + gender);
            Log.d("History", "CalculatedFitness -- strideLengthInCms" + strideLengthInCms);
            Log.d("History", "CalculatedFitness -- totalDistanceInMtr" + totalDistanceInMtr);
            Log.d("History", "CalculatedFitness -- totalCalories" + totalCalories);
            Log.d("History", "CalculatedFitness -- stepsActiveTime" + stepsActiveTime);*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dataMap[Constants.CALORIES] = totalCalories.toString()
        dataMap[Constants.DISTANCE] = totalDistanceInMtr.toString()
        dataMap[Constants.ACTIVE_TIME] = stepsActiveTime.toString()
        return dataMap
    }

    fun updateStepsAndCallService(stepsDataMap: HashMap<String?, Int?>, givenDate: Date?) {
        try {
            if (stepsDataMap[Constants.STEPS_COUNT] != null) {
                val dataMap = ArrayMap<String, String?>()
                Timber.e("History : getStepsDelta is--> $stepsDataMap For Date--> ${DateHelper.dateToString(givenDate!!, DateHelper.SERVER_DATE_YYYYMMDD)}")

                val calculatedFitnessDataMap = getCalculatedFitnessDataFromSteps(
                    stepsDataMap[Constants.STEPS_COUNT]!!) //getCaloriesDistanceDelta(startTime, endTime);
                dataMap[Constants.STEPS_COUNT] = stepsDataMap[Constants.STEPS_COUNT].toString()
                dataMap[Constants.ACTIVE_TIME] = calculatedFitnessDataMap[Constants.ACTIVE_TIME]
                dataMap[Constants.CALORIES] = calculatedFitnessDataMap[Constants.CALORIES]
                dataMap[Constants.DISTANCE] = calculatedFitnessDataMap[Constants.DISTANCE]
                dataMap[Constants.RECORD_DATE] = DateHelper.dateToString(givenDate, DateHelper.SERVER_DATE_YYYYMMDD)
                dataMap[Constants.LAST_UPDATED_TIME] = DateHelper.dateToString(givenDate, DateHelper.SERVER_DATE_YYYYMMDD)
                dataMap[Constants.SYNC] = Constants.TRUE

                // Record strictly Today's Google Fit data for Local use only.
                if (DateHelper.dateToString(givenDate, DateHelper.SERVER_DATE_YYYYMMDD).equals(
                        DateHelper.dateToString(Calendar.getInstance().time, DateHelper.SERVER_DATE_YYYYMMDD),ignoreCase = true)) {

                    Timber.d("Storing today's FIT data -- > $dataMap")
                    //FitnessDBHelper.recordHistorySteps(dataMap);
                    //FitnessDBHelper.recordDailyStepsANDGoal(dataMap);
                    //FitnessDBHelper.recordDailyStepsANDGoalFromGoogleFit(dataMap);

                    //ServiceDispatcher.broadcastResponse(context,ActivityBase.BROADCAST_ACTION, -2001, 200, Constants.NO_UPDATE_PROGRESSBAR_CODE)
                }
                if (DateHelper.dateToString(givenDate, DateHelper.SERVER_DATE_YYYYMMDD).equals(
                        DateHelper.dateToString(Calendar.getInstance().time, DateHelper.SERVER_DATE_YYYYMMDD),ignoreCase = true)) {
                    //It's today's data, check if LastUpdatedTime is at least more than 5 minutes.
                    when {
                        Utilities.isNullOrEmpty(lastSyncDateForStepsFromPreference) -> {
                            Timber.d("$TAG Sending today's FIT data to server -- > LastSyncDateForSteps is null ")
                            setLastSyncDateForStepsInPreference(DateHelper.dateToString(Calendar.getInstance().time, DateHelper.DATETIMEFORMAT))
                            //FitnessDBHelper.recordHistorySteps(dataMap);
                            //FitnessDBHelper.recordDailyStepsANDGoal(dataMap);

                            //FitnessServiceDispatcher.callSaveStepsServiceForSpecificDate(context, StepHomeActivity::class.java.getName(), false, dataMap)
                        }
                        Calendar.getInstance().timeInMillis - DateHelper.convertStringDateToDate(
                            lastSyncDateForStepsFromPreference, DateHelper.DATETIMEFORMAT).time > 5 * 60 * 1000 -> {
                            Timber.d("$TAG Sending today's FIT data to server -- > more than 5 mins ")
                            setLastSyncDateForStepsInPreference(DateHelper.dateToString(Calendar.getInstance().time, DateHelper.DATETIMEFORMAT))
                            //FitnessDBHelper.recordHistorySteps(dataMap);
                            //FitnessDBHelper.recordDailyStepsANDGoal(dataMap);

                            //FitnessServiceDispatcher.callSaveStepsServiceForSpecificDate(context, StepHomeActivity::class.java.getName(), false, dataMap)
                        }
                        else -> {
                            Timber.d("$TAG NOT Sending today's FIT data to server -- > less than 5 mins ")
                            //It's less than 5 minutes, do not sync
                        }
                    }
                } else {
                    Timber.d("$TAG Sending FIT data to server -- > for date ${DateHelper.dateToString(givenDate, DateHelper.SERVER_DATE_YYYYMMDD)}")
                    //FitnessDBHelper.recordHistorySteps(dataMap);

                    //FitnessServiceDispatcher.callSaveStepsServiceForSpecificDate(context, StepHomeActivity::class.java.getName(), false, dataMap)
                }
            } else {
                Timber.d("getStepsDelta is NULL--> ${stepsDataMap[Constants.STEPS_COUNT]}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


/*    private fun updateAllStepsAndCallService(fitnessDataMap: JSONArray?) {
        try {
            if (fitnessDataMap != null) {
                if (fitnessDataMap.length() > 0) {
                    val fitnessDataMapList = ArrayList<ArrayMap<String, String>>()
                    for (i in 0 until fitnessDataMap.length()) {
                        if (fitnessDataMap.getJSONObject(i) != null) {
                            val stepsDataMap = fitnessDataMap.getJSONObject(i)
                            val dataMap = ArrayMap<String, String>()
                            dataMap[Constants.STEPS_COUNT] = stepsDataMap.getString(Constants.STEPS_COUNT)
                            dataMap[Constants.ACTIVE_TIME] = stepsDataMap.getString(Constants.ACTIVE_TIME)
                            dataMap[Constants.CALORIES] = stepsDataMap.getString(Constants.CALORIES)
                            dataMap[Constants.DISTANCE] = stepsDataMap.getString(Constants.DISTANCE)
                            dataMap[Constants.RECORD_DATE] = DateHelper.convertDateTimeValue(stepsDataMap.getString(Constants.RECORD_DATE),
                                DateHelper.SERVER_DATE_YYYYMMDD,
                                DateHelper.SERVER_DATE_YYYYMMDD)
                            dataMap[Constants.LAST_UPDATED_TIME] = DateHelper.convertDateTimeValue(
                                    stepsDataMap.getString(Constants.RECORD_DATE),
                                    DateHelper.SERVER_DATE_YYYYMMDD,
                                    DateHelper.SERVER_DATE_YYYYMMDD)
                            dataMap[Constants.SYNC] = Constants.TRUE
                            Timber.d(TAG, "Storing today's FIT data -- > $dataMap")
                            fitnessDataMapList.add(dataMap)
                        }
                    }
                    var showProgressBar = false
                    if (fitnessDataMapList.size >= 30) {
                        showProgressBar = true
                    }
                    viewModel.saveStepsList(showProgressBar)
                    //FitnessServiceDispatcher.callSaveStepsServiceAll(context,StepHomeActivity::class.java.getName(),showProgressBar, fitnessDataMapList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    /**
     * Below logic is to get fitness data from Google Fit
     * --
     */
    /*public static void refreshStepCountSync(Context mContext) {
        try {
            Calendar calendar = Calendar.getInstance();
            boolean getFitDataForToday = false;
            if (!StepsDataSingleton.getInstance().getStepHistoryList().getStepGoalHistory().isEmpty()) {
                StepGoalHistory latestFitnessData = StepsDataSingleton.getInstance().getStepHistoryList().getStepGoalHistory().get(0);
                if (latestFitnessData != null) {
                    if (latestFitnessData.getRecordDate() != null) {
                        Log.d("StepCountHelper", "LAST RECORD_DATE FOUND-- >" + latestFitnessData.getRecordDate());
                        Date dateBefore = DateHelper.convertStrToDateOBJ(latestFitnessData.getRecordDate());
                        Date dateAfter = DateHelper.convertStrToDateOBJ(DateHelper.getCurrentDateAsStringyyyyMMdd());

                        if (dateBefore != null & dateAfter != null) {
                            long difference = dateAfter.getTime() - dateBefore.getTime();
                            int daysDifference = (int) (difference / (1000 * 60 * 60 * 24));
                            //Log.d("StepCountHelper", " daysDifference -- " + daysDifference);
                            //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                            daysDifference = (Math.max(daysDifference, 30));
                            if (daysDifference > 0) {
                                calendar = Calendar.getInstance();
                                calendar.add(Calendar.DATE, -daysDifference);
                                Log.d("StepCountHelper", " sync for date -- " + calendar.getTime());
                                pullFitnessDataFromGoogleFitForDate(mContext, calendar.getTime(), Calendar.getInstance().getTime());
                            } else {
                                //fail safe
                                getFitDataForToday = true;
                            }
                        } else {
                            //fail safe
                            getFitDataForToday = true;
                        }
                    } else {
                        Log.d("StepCountHelper", "LAST RECORD_DATE NOT found so fetch all data from FIT-- >");
                        //We will try to fetch last n days data from google fit.
                        getFitDataForToday = true;
                        int nDays = 365;
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -nDays);
                        Log.d("StepCountHelper", " sync for date -- " + calendar.getTime());
                        pullFitnessDataFromGoogleFitForDate(mContext, calendar.getTime(), Calendar.getInstance().getTime());
                    }
                } else {
                    //fail safe
                    getFitDataForToday = true;
                    pullFitnessDataFromGoogleFitForDate(mContext, Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
                }
            } else {
                Log.d("StepCountHelper", "getStepHistoryList() is EMPTY-----");
                Log.d("StepCountHelper", "LAST RECORD_DATE NOT found so fetch all data from FIT-- >");
                //We will try to fetch last n days data from google fit.
                getFitDataForToday = true;
                int nDays = 365;
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -nDays);
                Log.d("StepCountHelper", " sync for date -- " + calendar.getTime());
                pullFitnessDataFromGoogleFitForDate(mContext, calendar.getTime(), Calendar.getInstance().getTime());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    /*    fun getTodayStepsDataMap(mContext: Context?): ArrayMap<String, String> {
        val arrayMap = ArrayMap<String, String>()
        object : AsyncTask<Void?, Void?, Void?>() {

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
            }

            override fun doInBackground(vararg params: Void?): Void? {
                FitnessDataManager.getInstance(mContext)!!
                    .readHistoryData(Calendar.getInstance().time, Calendar.getInstance().time)
                    .addOnCompleteListener { task ->
                        if (FitnessDataManager.getInstance(mContext)!!.fitnessDataMap != null) {
                            Log.e(TAG, FitnessDataManager.getInstance(mContext)!!.fitnessDataMap.toString())
                            try {
                                arrayMap[Constants.STEP_ID] = "1"
                                arrayMap[Constants.GOAL_ID] = "1"
                                arrayMap[Constants.STEPS_COUNT] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.STEPS_COUNT)
                                arrayMap[Constants.TOTAL_GOAL] = "3000"
                                arrayMap[Constants.GOAL_PERCENTILE] = "0"
                                arrayMap[Constants.RECORD_DATE] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.RECORD_DATE)
                                arrayMap[Constants.CALORIES] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.CALORIES)
                                arrayMap[Constants.DISTANCE] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.DISTANCE)
                                arrayMap[Constants.ACTIVE_TIME] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.ACTIVE_TIME)
                                arrayMap[Constants.STEP_NOTIFICATION] = "1"
                                arrayMap[Constants.LAST_UPDATED_TIME] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.RECORD_DATE)
                                arrayMap[Constants.SYNC] = Constants.FALSE
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                return null
            }
        }.execute()
        return arrayMap
    }*/

}
