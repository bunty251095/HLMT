package com.caressa.common.fitness

import android.app.Activity
import android.content.Context
import com.caressa.common.utils.CalculateParameters
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FitnessDataManager(private val mContext: Context?) {
    var fitnessDataArray: JSONArray = JSONArray()
    private val fitnessOptions: FitnessOptions

    /**
     * Checks that the user is signed in, and if so, executes the specified function. If the user is
     * not signed in, initiates the sign in flow, specifying the post-sign in function to execute.
     *
     * @param requestCode The request code corresponding to the action to perform after sign in.
     */
    fun fitSignIn(requestCode: FitRequestCode) {
        try {
            if (oAuthPermissionsApproved()) {
                //readHistoryData(Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
            } else {
                GoogleSignIn.requestPermissions(
                    (mContext as Activity?)!!,
                    requestCode.value,
                    googleAccount,
                    fitnessOptions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = "There was an error signing into Fit."
        Logger.e(TAG, message)
    }

    fun oAuthPermissionsApproved(): Boolean {
        var hasPermission = false
        hasPermission = GoogleSignIn.hasPermissions(googleAccount, fitnessOptions)
        return hasPermission
    }

    /**
     * Gets a Google account for use in creating the Fitness client. This is achieved by either
     * using the last signed-in account, or if necessary, prompting the user to sign in.
     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
     * return `null` if there has been no sign in before.
     */
    private val googleAccount: GoogleSignInAccount
    get() = GoogleSignIn.getAccountForExtension(mContext!!, fitnessOptions)

    val googleAccountInfo: Task<GoogleSignInAccount>
        get() {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(mContext!!, gso).silentSignIn()
        }

    fun signOutGoogleAccount() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(mContext!!, gso).signOut()
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the
     * data.
     */
    fun readHistoryData(startDate: Date, endDate: Date): Task<DataReadResponse> {
        // Begin by creating the query.
        val readRequest = queryFitnessData(startDate, endDate)

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(mContext!!, googleAccount)
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse: DataReadResponse ->
                fitnessDataArray = JSONArray()
                processData(dataReadResponse)
            }
            .addOnFailureListener { e: Exception? ->
                Logger.e(TAG, "There was a problem reading the data.", e)
            }
    }

    /**
     * Returns a [DataReadRequest] for all step count changes in the past week.
     */
    private fun queryFitnessData(startDate: Date, endDate: Date): DataReadRequest {
        // [START build_read_data_request]
        val calStartTime = Calendar.getInstance()
        val calEndTime = Calendar.getInstance()
        // Setting a start and end date using a range of 1 day.
        // Fetch data from midnight 12:00AM to 11:59PM
        calStartTime.time = startDate
        calStartTime[Calendar.HOUR_OF_DAY] = 0
        calStartTime[Calendar.MINUTE] = 0
        calStartTime[Calendar.SECOND] = 0
        calStartTime[Calendar.MILLISECOND] = 0
        val startTime = calStartTime.timeInMillis
        calEndTime.time = endDate
        calEndTime[Calendar.HOUR_OF_DAY] = 23
        calEndTime[Calendar.MINUTE] = 59
        calEndTime[Calendar.SECOND] = 59
        calEndTime[Calendar.MILLISECOND] = 0
        val endTime = calEndTime.timeInMillis
        val dateFormat = DateFormat.getDateInstance()
        val timeFormat = DateFormat.getTimeInstance()
        Logger.d(
            TAG,
            "Range Start: " + dateFormat.format(startTime) + " " + timeFormat.format(startTime)
        )
        Logger.d(TAG, "Range End: " + dateFormat.format(endTime) + " " + timeFormat.format(endTime))
        val datasource = DataSource.Builder()
            .setAppPackageName("com.google.android.gms")
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .build()
        return DataReadRequest.Builder()
            .aggregate(datasource, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries() // Used to retrieve data from cloud
            .build()

        /* return new DataReadRequest.Builder()
            // The data request can specify multiple data types to return, effectively
            // combining multiple data queries into one call.
            // In this example, it's very unlikely that the request is for several hundred
            // datapoints each consisting of a few steps and a timestamp.  The more likely
            // scenario is wanting to see how many steps were walked per day, for 7 days.
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            // Analogous to a "Group By" in SQL, defines how data should be aggregated.
            // bucketByTime allows for a time span, whereas bucketBySession would allow
            // bucketing by "sessions", which would need to be defined in code.
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build();*/
    }
    //*********************************************
    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    private fun processData(dataReadResult: DataReadResponse): JSONArray {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (!dataReadResult.buckets.isEmpty()) {
            Logger.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                for (dataSet in bucket.dataSets) {
                    processDataSet(dataSet)
                }
            }
        } else if (!dataReadResult.buckets.isEmpty()) {
            Logger.i(TAG, "Number of returned DataSets is: " + dataReadResult.buckets.size)
            for (dataSet in dataReadResult.dataSets) {
                processDataSet(dataSet)
            }
        }
        // [END parse_read_data_result]
        return fitnessDataArray
    }

    // [START parse_dataset]
    private fun processDataSet(dataSet: DataSet) {
        Logger.i(TAG, "Data returned for Data type: " + dataSet.dataType.name)
        for (dp in dataSet.dataPoints) {
            Logger.i(TAG, "Data point:")
            Logger.i(TAG, "\tType: " + dp.dataType.name)
            Logger.i(TAG, "\tStart: " + dp.getStartTime(TimeUnit.MILLISECONDS))
            Logger.i(TAG, "\tEnd: " + dp.getEndTime(TimeUnit.MICROSECONDS))
            for (data in dp.dataType.fields) {
                Logger.i(TAG, "\tField: " + data.name + " Value: " + dp.getValue(data))
                Logger.i(TAG, "\tCalculated Fitness Data -> " + getCalculatedFitnessDataFromSteps(dataSet))
            }
        }
    }

    /**
     * https://fitness.stackexchange.com/questions/25472/how-to-calculate-calorie-from-pedometer
     * https://www.livestrong.com/article/353942-how-long-does-it-take-to-walk-10-000-steps/
     * https://www.walkingwithattitude.com/articles/features/how-to-measure-stride-or-step-length-for-your-pedometer
     * http://www.gearedtobefit.com/fitness_calculators.cfm
     *
     *
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
    private fun getCalculatedFitnessDataFromSteps(dataSet: DataSet): JSONArray {
        val dataMap = JSONArray()
        val dataJson = JSONObject()
        // default values
        var totalCalories = 0
        var totalDistanceInMtr = 0f
        var stepsActiveTime = 0
        val weightValue = 65f
        val heightValue = 165f
        val gender = 1
        for (dp in dataSet.dataPoints) {
            for (data in dp.dataType.fields) {
                try {
                    val stepsCount = dp.getValue(data).asInt()
                    try {
                        /*if (heightWeightMap != null) {
                            weightValue = heightWeightMap[GlobalConstants.WEIGHT]!!.toFloat()
                            heightValue = heightWeightMap[GlobalConstants.HEIGHT]!!.toFloat()
                            gender = heightWeightMap[GlobalConstants.GENDER]!!.toInt()
                        }*/
                        var calorieBurnedFactor: Double
                        var strideAsPerGenderInches = 0.0
                        strideAsPerGenderInches = if (gender == 1) {
                            84 * 0.415
                        } else {
                            84 * 0.413 // 0.415 for male, 0.413 for female
                        }
                        val activityTimeFactor = 100 //avg 100 steps per minute
                        calorieBurnedFactor = 138.462 //weightValue * (strideLengthInCms / 100);
                        totalDistanceInMtr =
                            (stepsCount * (CalculateParameters.convertInchToCm(strideAsPerGenderInches.toString()).toDouble() / 100)).toFloat()
                        stepsActiveTime = stepsCount / activityTimeFactor
                        if (stepsActiveTime <= 0) stepsActiveTime = 1

                        //===Calories based on weight and distance
                        //Double distanceInmiles = totalDistanceInMtr * 0.00062137;
                        //totalCalories = (int) (Double.parseDouble(Helper.convertKgToLbs(String.valueOf(weightValue))) * distanceInmiles * 0.468);

                        //===Calories based on weight,steps, stride length
                        var weightFactor = 0
                        if (weightValue >= 45 && weightValue <= 52) {
                            weightFactor = 107
                        } else if (weightValue >= 53 && weightValue <= 59) {
                            weightFactor = 123
                        } else if (weightValue >= 60 && weightValue <= 66) {
                            weightFactor = 138
                        } else if (weightValue >= 67 && weightValue <= 73) {
                            weightFactor = 157
                        } else if (weightValue >= 74 && weightValue <= 80) {
                            weightFactor = 169
                        } else if (weightValue >= 81 && weightValue <= 86) {
                            weightFactor = 183
                        } else if (weightValue >= 87 && weightValue <= 93) {
                            weightFactor = 198
                        } else if (weightValue >= 94 && weightValue <= 100) {
                            weightFactor = 213
                        } else if (weightValue >= 101 && weightValue <= 107) {
                            weightFactor = 228
                        } else if (weightValue >= 108 && weightValue <= 130) {
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
                    val sd = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    dataJson.put("RecordDate", sd.format(dp.getStartTime(TimeUnit.MILLISECONDS)))
                    dataJson.put("StepsCount", stepsCount.toString())
                    //dataJson.put("Calories", String.valueOf(totalCalories) + " Kcal");
                    dataJson.put("Calories", totalCalories.toString())
                    //dataJson.put("Distance", convertMtrToKms(String.valueOf(totalDistanceInMtr)));
                    dataJson.put("Distance", roundOffPrecision(totalDistanceInMtr.toDouble(), 2))
                    //dataJson.put("ActiveTime", get_Hour_Mins_FromMinutes(stepsActiveTime));
                    dataJson.put("ActiveTime", stepsActiveTime)
                    dataMap.put(dataJson)
                    fitnessDataArray!!.put(dataJson)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return dataMap
    }

    /**
     * Initializes a custom log class that outputs both to in-app targets and logcat.
     */
    private fun initializeLogging() {
        // Wraps Android's native log framework.
        val logWrapper = LogWrapper()
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Logger.mLogNode = logWrapper
        // Filter strips out everything except the message text.
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter
        Logger.i(TAG, "Ready.")
    }

    companion object {
        private const val TAG = "FitnessDataManager"
        private var fitnessDataManager: FitnessDataManager? = null
        fun getInstance(mContext: Context?): FitnessDataManager? {
            if (fitnessDataManager == null) fitnessDataManager = FitnessDataManager(mContext)
            return fitnessDataManager
        }

        fun get_Hour_Mins_FromMinutes(time: Int): String {
            if (time > 0) {
                val hours = time / 60 //since both are ints, you get an int
                val minutes = time % 60
                return if (hours > 0) {
                    hours.toString() + "h" + " " + minutes + "m"
                } else {
                    if (minutes <= 1) {
                        "$minutes min"
                    } else {
                        "$minutes mins"
                    }
                }
            }
            return "0 min"
        }

        fun roundOffPrecision(value: Double, precision: Int): Double {
            val scale = Math.pow(10.0, precision.toDouble()).toInt()
            return Math.round(value * scale).toDouble() / scale
        }
    }

    init {
        initializeLogging()
        fitnessOptions =
            FitnessOptions.builder() //.addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                //.addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build()
    }
}
