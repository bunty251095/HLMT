package com.caressa.fitness_tracker.ui

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.fitness_tracker.R
import com.caressa.fitness_tracker.common.StepsDataSingleton
import com.caressa.fitness_tracker.databinding.FragmentStepsDetailBinding
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.*
import com.caressa.fitness_tracker.util.FitnessHelper
import com.caressa.fitness_tracker.util.WeekDay
import com.caressa.fitness_tracker.viewmodel.FitnessViewModel
import com.caressa.model.entity.FitnessEntity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import org.json.JSONArray
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StepsDetailFragment : BaseFragment(), KoinComponent {

    private val viewModel: FitnessViewModel by viewModel()
    private lateinit var binding: FragmentStepsDetailBinding

    private val fitnessHelper : FitnessHelper = get()
    private val appColorHelper = AppColorHelper.instance!!
    private val stepsDataSingleton = StepsDataSingleton.instance!!
    private var fitnessDataManager : FitnessDataManager? = null

    private var from = ""
    private var isWeekly = false
    private var serverRecordDate = ""
    private var selectedDataSetIndex = 0
    private val currentDate = DateHelper.currentDateAsStringyyyyMMdd
    private var monthlyDataList : MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("routeFrom--->$from")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val bundle = Bundle()
                bundle.putString(Constants.FROM,from)
                findNavController().navigate(R.id.fragmentStepsDetail_to_fitnessDashboardFragment,bundle)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        FirebaseHelper.logScreenEvent(FirebaseConstants.ACTIVITY_TRACKER_MONTHLY_DETAIL_SCREEN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStepsDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            initView()
            setClickable()
        } catch ( e :Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initView() {
        try {
            fitnessDataManager = FitnessDataManager(context)
            //getFitnessData()
            if (fitnessDataManager!!.oAuthPermissionsApproved()) {
                Timber.e("oAuthPermissionsApproved---> true")
                getFitnessData()
            } else {
                Timber.e("oAuthPermissionsApproved---> false")
                binding.layoutFitnessData.visibility = View.GONE
                binding.lblNoData.visibility = View.VISIBLE
            }

            binding.layoutStepsGoals.setDataTitle(resources.getString(R.string.STEP_GOAL))
            binding.layoutStepsGoals.setDataImage(R.drawable.img_step)

            binding.layoutCalories.setDataTitle(resources.getString(R.string.CALORIES))
            binding.layoutCalories.setDataImage(R.drawable.img_calories)

            binding.layoutDistance.setDataTitle(resources.getString(R.string.DISTANCE))
            binding.layoutDistance.setDataImage(R.drawable.img_distance)

            binding.layoutActiveTime.setDataTitle(resources.getString(R.string.ACTIVE_TIME))
            binding.layoutActiveTime.setDataImage(R.drawable.img_active_time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.graphMonthly.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {

            override fun onValueSelected(e: Entry, h: Highlight) {
                Timber.e("Selected Index---> ${e.x.toInt()}")
                selectedDataSetIndex = e.x.toInt()
                setSelectedDateData(monthlyDataList[selectedDataSetIndex])
            }

            override fun onNothingSelected() {}
        })

    }

    private fun setSelectedDateData(selectedItem: FitnessEntity.StepGoalHistory) {
        Timber.e("data---> $selectedItem")
        serverRecordDate = selectedItem.recordDate.split("T").toTypedArray()[0]
        Timber.e("SelectedDate---> $serverRecordDate")
        binding.txtDate.text = DateHelper.convertDateTimeValue(serverRecordDate, DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.DATEFORMAT_DDMMMYYYY_NEW)
        val steps = selectedItem.stepsCount

        if ( !Utilities.isNullOrEmptyOrZero(steps.toString()) ) {
            binding.layoutFitnessData.visibility = View.VISIBLE
            binding.lblNoData.visibility = View.GONE

            binding.layoutStepsGoals.setDataValue(steps.toString() + "/" + selectedItem.totalGoal)
            binding.layoutCalories.setDataValue(fitnessHelper.getCaloriesWithUnit(selectedItem.calories.toString()))
            binding.layoutDistance.setDataValue(CalculateParameters.convertMtrToKms(selectedItem.distance.toString()))
            //binding.layoutActiveTime.setDataValue(DateHelper.getHourMinFromStrMinutes(selectedItem.activeTime))
//            if ( !Utilities.isNullOrEmptyOrZero(selectedItem.activeTime) ) {
//                binding.layoutActiveTime.setDataValue(DateHelper.getHourMinFromStrMinutes(selectedItem.activeTime))
//            } else {
                binding.layoutActiveTime.setDataValue( fitnessHelper.getActiveTime(steps) )
//            }
        } else {
            binding.layoutFitnessData.visibility = View.GONE
            binding.lblNoData.visibility = View.VISIBLE
        }
    }

    private fun refreshGraphData(monthlyList: MutableList<FitnessEntity.StepGoalHistory>) {
        binding.graphMonthly.clear()

        if ( monthlyList.isNotEmpty() ) {
            val entriesSteps = ArrayList<Entry>()
            val entriesGoals = ArrayList<Entry>()
            val dateLabels = ArrayList<String>()
            val colors = ArrayList<Int>()
            val colorAchieved = Color.argb(250, 34, 177, 76)
            val colorNormal = appColorHelper.primaryColor()
            var maxValue = 0
            var maxValueSteps = 0
            var maxValueGoal = 0
            var counter = 0f
            //Timber.e("weeklyList---> $weeklyList")
            var recordDate = stepsDataSingleton.selectedDateHistory.recordDate
            Timber.e("RecordDate---> $recordDate")
            for ( i in monthlyList.indices ) {
                val item = monthlyList[i]
                val stepsCount = item.stepsCount
                var totalGoal = item.totalGoal
                recordDate = item.recordDate.split("T").toTypedArray()[0]
                if ( !Utilities.isNullOrEmptyOrZero(totalGoal.toString()) ) {
                    totalGoal = if (totalGoal > 0) totalGoal else 3000
                }
                entriesSteps.add(Entry(counter, stepsCount.toFloat()))
                entriesGoals.add(Entry(counter, totalGoal.toFloat()))
                counter++

                if ( !Utilities.isNullOrEmptyOrZero(item.goalPercentile.toString()) ) {
                    val goalPercentile = item.goalPercentile.toInt()

                    if (goalPercentile >= 100) {
                        colors.add(colorAchieved)
                    } else {
                        colors.add(colorNormal)
                    }
                } else run { colors.add(colorNormal) }

                if (stepsCount > maxValueSteps) {
                    maxValueSteps = stepsCount
                }
                if (totalGoal > maxValueGoal) {
                    maxValueGoal = totalGoal
                }
                maxValue = if (maxValueSteps > maxValueGoal) maxValueSteps else maxValueGoal
                dateLabels.add(recordDate)
                //Timber.e("$i : RecordDate---> $recordDate")
                if ( recordDate.equals(stepsDataSingleton.selectedDateHistory.recordDate,ignoreCase = true) ) {
                    selectedDataSetIndex = i
                    //setSelectedDateData(stepsDataSingleton.selectedDateHistory)
                    //binding.graphMonthly.highlightValue(entriesGoals[selectedDataSetIndex].x, entriesSteps[selectedDataSetIndex].y, selectedDataSetIndex, true)
                }

            }
            setDataToChart(entriesSteps, entriesGoals, dateLabels, maxValue)
            //setDataToChart(entriesSteps, entriesGoals, colors, dateLabels, maxValue)
        }
    }

    private fun setDataToChart(
        stepsEntries: ArrayList<Entry>,
        goalEntries: ArrayList<Entry>,
        dateLabels: ArrayList<String>,
        maxValue: Int)
    {
        try {
            val lineDataSetSteps: LineDataSet
            val lineDataSetGoal: LineDataSet
            val dataSets = ArrayList<ILineDataSet>()
            if (binding.graphMonthly.data != null && binding.graphMonthly.data.dataSetCount > 0) {
                lineDataSetSteps = binding.graphMonthly.data.getDataSetByIndex(0) as LineDataSet
                lineDataSetSteps.values = stepsEntries
                lineDataSetSteps.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

                lineDataSetGoal = binding.graphMonthly.data.getDataSetByIndex(1) as LineDataSet
                lineDataSetGoal.values = goalEntries
                lineDataSetGoal.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

                binding.graphMonthly.data.notifyDataChanged()
                binding.graphMonthly.notifyDataSetChanged()
            } else {
                //-----------------------------------------------------------------------------------
                // create a dataset for STEPS entries
                lineDataSetSteps = LineDataSet(stepsEntries, resources.getString(R.string.STEPS))
                lineDataSetSteps.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                lineDataSetSteps.setDrawIcons(false)

                lineDataSetSteps.enableDashedLine(10f, 5f, 0f)
                val colorSteps = Color.argb(200, 255, 160, 0)
                lineDataSetSteps.setColors(colorSteps)
                lineDataSetSteps.valueTextColor = ContextCompat.getColor(requireContext(), R.color.vivantOrange)
                lineDataSetSteps.setCircleColor(colorSteps)
                //lineDataSetSteps.circleColors = colors
                lineDataSetSteps.lineWidth = 2f
                lineDataSetSteps.circleRadius = 7f
                lineDataSetSteps.circleHoleRadius = 4f
                lineDataSetSteps.setDrawCircleHole(true)
                lineDataSetSteps.valueTextSize = 12f
                lineDataSetSteps.formLineWidth = 2f
                lineDataSetSteps.setDrawHorizontalHighlightIndicator(true)
                lineDataSetSteps.setDrawVerticalHighlightIndicator(true)
                lineDataSetSteps.highLightColor = appColorHelper.primaryColor()

                lineDataSetSteps.setDrawFilled(true)
                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_blue_chart)
                    lineDataSetSteps.fillDrawable = drawable
                } else {
                    lineDataSetSteps.fillColor = Color.WHITE
                }

                //-----------------------------------------------------------------------------------
                // create a dataset for GOAL entries
                lineDataSetGoal = LineDataSet(goalEntries, resources.getString(R.string.GOALS))
                lineDataSetGoal.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                lineDataSetGoal.setDrawIcons(false)

                // set the line to be drawn like this "- - - - - -"
                val colorGoal = appColorHelper.primaryColor()
                lineDataSetGoal.setColors(colorGoal)
                lineDataSetGoal.valueTextColor = appColorHelper.primaryColor()
                lineDataSetGoal.setCircleColor(colorGoal)
                lineDataSetGoal.lineWidth = 2f
                lineDataSetGoal.circleRadius = 7f
                lineDataSetGoal.circleHoleRadius = 4f
                lineDataSetGoal.setDrawCircleHole(true)
                lineDataSetGoal.valueTextSize = 12f
                lineDataSetGoal.formLineWidth = 2f
                lineDataSetGoal.setDrawHorizontalHighlightIndicator(true)
                lineDataSetGoal.setDrawVerticalHighlightIndicator(true)
                lineDataSetGoal.highLightColor = appColorHelper.primaryColor()

                lineDataSetGoal.setDrawFilled(true)
                if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_blue_chart)
                    lineDataSetGoal.fillDrawable = drawable
                } else {
                    lineDataSetGoal.fillColor = Color.WHITE
                }

                dataSets.add(lineDataSetGoal) // add the datasets Goal
                dataSets.add(lineDataSetSteps) // add the datasets Steps

                // create a data object with the datasets
                val data = LineData(dataSets)
                data.setDrawValues(true)
                // set data
                binding.graphMonthly.data = data
            }
            binding.graphMonthly.setDrawGridBackground(false)
            binding.graphMonthly.description.isEnabled = false
            binding.graphMonthly.setTouchEnabled(true)
            binding.graphMonthly.isDragEnabled = true
            binding.graphMonthly.isScaleYEnabled = false
            binding.graphMonthly.isScaleXEnabled = false
            binding.graphMonthly.setPinchZoom(false)
            binding.graphMonthly.isHighlightPerTapEnabled = true
            binding.graphMonthly.axisLeft.setDrawGridLines(false)
            binding.graphMonthly.xAxis.setDrawGridLines(false)
            binding.graphMonthly.setVisibleXRangeMaximum(6f)
            binding.graphMonthly.xAxis.textSize = 12f
            binding.graphMonthly.xAxis.textColor = appColorHelper.textColor
            binding.graphMonthly.axisLeft.textColor = appColorHelper.primaryColor()
            binding.graphMonthly.axisRight.isEnabled = false
            binding.graphMonthly.legend.isEnabled = false
            //binding.graphMonthly.xAxis.isEnabled = false
            binding.graphMonthly.legend.setDrawInside(true)
            // For setting chart padding dynamically
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            //val width = displayMetrics.widthPixels
            binding.graphMonthly.setExtraOffsets(30f, 20f, 30f, 0f)

            binding.graphMonthly.animateXY(1000, 1000)
            binding.graphMonthly.centerViewTo(stepsEntries[selectedDataSetIndex].x, stepsEntries[selectedDataSetIndex].y, YAxis.AxisDependency.RIGHT)
            binding.graphMonthly.highlightValue(stepsEntries[selectedDataSetIndex].x, stepsEntries[selectedDataSetIndex].y, selectedDataSetIndex, true)

            val xAxis = binding.graphMonthly.xAxis
            xAxis.position = XAxis.XAxisPosition.TOP
            xAxis.removeAllLimitLines()// reset all limit lines to avoid overlapping lines
            xAxis.labelCount = dateLabels.size
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawLimitLinesBehindData(false)

            val leftAxis = binding.graphMonthly.axisLeft
            leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
            //val axisMaximum = if (maxValue < 5000) maxValue + 500 else maxValue + 1000
            val axisMaximum = if (maxValue < 10000) {
                maxValue + 5000
            } else {
                maxValue + 10000
            }
            leftAxis.axisMaximum = axisMaximum.toFloat()
            leftAxis.isEnabled = false
            leftAxis.setDrawZeroLine(false)
            leftAxis.setDrawLimitLinesBehindData(false)

            xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
                try {
                    return@IAxisValueFormatter if (isWeekly) {
                        DateHelper.convertDateTimeValue(dateLabels[value.toInt()], DateHelper.SERVER_DATE_YYYYMMDD, "E")
                    } else {
                        DateHelper.convertDateTimeValue(dateLabels[value.toInt()], DateHelper.SERVER_DATE_YYYYMMDD, "dd-MMM")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                value.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFitnessData(): MutableList<FitnessEntity.StepGoalHistory> {
        val fitnessDataList : MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()
        try {
            val fitnessListSize = if (isWeekly) 7 else 30
            val fitnessDataDBList = stepsDataSingleton.stepHistoryList
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -30)
            fitnessDataManager!!.readHistoryData(calendar.time, Calendar.getInstance().time).addOnCompleteListener {
                val fitnessDataJsonArray: JSONArray = fitnessDataManager!!.fitnessDataArray
                if ( fitnessDataJsonArray != null ) {
                    Timber.e("Google Fit data for $fitnessListSize days---> $fitnessDataJsonArray")
                    if ( fitnessDataJsonArray.length() > 0 ) {
                        for (i in fitnessListSize-1 downTo 0) {
                            try {
                                val recordDate = Calendar.getInstance()
                                val sd = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
                                val sd2 = SimpleDateFormat(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, Locale.ENGLISH)
                                val sd3 = SimpleDateFormat(DateHelper.DISPLAY_DATE_DDMMMYYYY, Locale.ENGLISH)
                                recordDate.add(Calendar.DATE, -i)
                                recordDate[Calendar.HOUR_OF_DAY] = 0
                                recordDate[Calendar.MINUTE] = 0
                                recordDate[Calendar.SECOND] = 0
                                recordDate[Calendar.MILLISECOND] = 0
                                val recordDateStr = sd.format(recordDate.time)
                                val recordDateStr2ndFormat = sd2.format(recordDate.time)
                                val recordDateStr3rdFormat = sd3.format(recordDate.time)

                                val dataMap = FitnessEntity.StepGoalHistory(lastRefreshed = Date())
                                dataMap.stepID = 1
                                dataMap.goalID = 1
                                dataMap.recordDate = recordDateStr
                                dataMap.stepsCount = 0
                                dataMap.totalGoal = 3000
                                dataMap.distance = 0.0
                                dataMap.calories = 0
                                dataMap.goalPercentile = 0.0
                                dataMap.activeTime = "0"

                                for (j in 0 until fitnessDataJsonArray.length()) {
                                    if ( !Utilities.isNullOrEmpty(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE)) ) {
                                        if (recordDateStr.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE).split("T".toRegex()).toTypedArray()[0], ignoreCase = true) ||
                                            recordDateStr2ndFormat.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE), ignoreCase = true) ||
                                            recordDateStr3rdFormat.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE), ignoreCase = true)) {
                                            dataMap.stepsCount = fitnessDataJsonArray.getJSONObject(j).getString(Constants.STEPS_COUNT).toInt()
                                            dataMap.calories = fitnessDataJsonArray.getJSONObject(j).getString(Constants.CALORIES).toInt()
                                            dataMap.distance = fitnessDataJsonArray.getJSONObject(j).getString(Constants.DISTANCE).toDouble()
                                            dataMap.activeTime = fitnessDataJsonArray.getJSONObject(j).getString(Constants.ACTIVE_TIME)
                                        }
                                    }
                                }
                                for (j in 0 until fitnessDataDBList.size) {
                                    if ( !Utilities.isNullOrEmpty(fitnessDataDBList[j].recordDate) ) {
                                        if (recordDateStr.equals(fitnessDataDBList[j].recordDate.split("T")[0], ignoreCase = true) ||
                                            recordDateStr2ndFormat.equals(fitnessDataDBList[j].recordDate, ignoreCase = true) ||
                                            recordDateStr3rdFormat.equals(fitnessDataDBList[j].recordDate, ignoreCase = true)) {
                                            dataMap.totalGoal = fitnessDataDBList[j].totalGoal
                                        }
                                    }
                                }
                                if ( recordDateStr == currentDate ) {
                                    val latestGoal = stepsDataSingleton.latestGoal
                                    if ( !Utilities.isNullOrEmptyOrZero(latestGoal.goal.toString())) {
                                        dataMap.totalGoal = latestGoal.goal
                                    }
                                }
                                monthlyDataList = fitnessDataList
                                fitnessDataList.add(dataMap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        monthlyDataList = getPastDataFromApi()
                        if ( !monthlyDataList.isNullOrEmpty() ) {
                            stepsDataSingleton.selectedDateHistory =  monthlyDataList.find { it.recordDate.equals(currentDate,true) }!!
                        }
                    }
                    monthlyDataList.sortBy { it.recordDate }
                    if ( monthlyDataList.isNotEmpty() ) {
                        selectedDataSetIndex = monthlyDataList.size - 1
                    }
                    Utilities.printData("monthlyDataList",monthlyDataList)
                    refreshGraphData(monthlyDataList)
                    setSelectedDateData(stepsDataSingleton.selectedDateHistory)
                }
            }
            fitnessDataList.reverse()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fitnessDataList
    }

    private fun getPastDataFromApi() : MutableList<FitnessEntity.StepGoalHistory>  {
        val list : MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()
        try {
            val monthDayList : MutableList<WeekDay> = fitnessHelper.getMonthlyDayList()
            val stepGoalHistory = stepsDataSingleton.stepHistoryList
            if(!stepGoalHistory.isNullOrEmpty()) {
                for ( monthDay in monthDayList) {
                    var history = FitnessEntity.StepGoalHistory(recordDate = monthDay.dateString, lastRefreshed = Date())
                    for ( item in stepGoalHistory) {
                        val recordDate = item.recordDate.split("T").toTypedArray()[0]
                        //Timber.e("Record Date--->$recordDate")
                        if ( monthDay.dateString.equals(recordDate, ignoreCase = true) ) {
                            item.recordDate = recordDate
                            history = item
                            // Today's Data
                            if ( recordDate.equals(currentDate, ignoreCase = true) ) {
                                item.totalGoal = stepsDataSingleton.latestGoal.goal
                            }
                            break
                        }
                    }
                    list.add(history)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

}