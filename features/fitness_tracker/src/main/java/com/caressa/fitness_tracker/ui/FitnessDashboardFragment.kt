package com.caressa.fitness_tracker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.*
import com.caressa.fitness_tracker.FitnessHomeActivity
import com.caressa.fitness_tracker.R
import com.caressa.fitness_tracker.adapter.WeekdayAdapter
import com.caressa.fitness_tracker.common.StepsDataSingleton
import com.caressa.fitness_tracker.databinding.FitnessDashboardFragmentBinding
import com.caressa.fitness_tracker.util.*
import com.caressa.fitness_tracker.viewmodel.FitnessViewModel
import com.caressa.model.entity.FitnessEntity
import com.caressa.model.fitness.GetStepsGoalModel
import com.caressa.model.fitness.SetGoalModel
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
import kotlinx.android.synthetic.main.dialog_edittext_goal.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FitnessDashboardFragment : BaseFragment(),WeekdayAdapter.OnWeekDayClickListener,
    DefaultNotificationDialog.OnDialogValueListener,
    FitnessHomeActivity.OnGoogleAccountSelectListener,KoinComponent {

    private val viewModel: FitnessViewModel by viewModel()
    private lateinit var binding: FitnessDashboardFragmentBinding

    private val fitnessHelper : FitnessHelper = get()
    private val stepCountHelper : StepCountHelper = get()
    private val appColorHelper = AppColorHelper.instance!!
    private val stepsDataSingleton = StepsDataSingleton.instance!!
    private var fitnessDataManager : FitnessDataManager? = null

    private var from = ""
    private var isFromLatestGoal = false
    private var isFailure = false
    private var isWeekly = true
    private var isForceSync = false
    private var todayStepCount = 0
    private var todayStepGoal = 3000
    private val currentDate = DateHelper.currentDateAsStringyyyyMMdd
    private val weekDayList = fitnessHelper.getWeeklyDayList()
    private var selectedDataSetIndex = weekDayList.size - 1
    private var selectedFitnessData = FitnessEntity.StepGoalHistory( lastRefreshed =  Date() )
    private var weeklyDataList : MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()
    private var weekdayAdapter : WeekdayAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("routeFromOnCreate--->$from")
        }

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when( from ) {
                    Constants.TRACK_PARAMETER -> requireActivity().finish()
                    else -> (activity as FitnessHomeActivity).routeToHomeScreen()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FitnessDashboardFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            initView()
            registerObservers()
            setClickable()
        } catch ( e :Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as FitnessHomeActivity).setDataReceivedListener(this)
    }

    override fun onGoogleAccountSelection(from:String) {
        Timber.e("from---> $from")
        when(from) {
            Constants.SUCCESS -> {
                isFromLatestGoal = true
            }
            Constants.FAILURE -> {
                isFailure = true
            }
        }
        viewModel.getLatestStepGoal(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        from = requireArguments().getString(Constants.FROM,"")!!
        Timber.e("routeFromOnCreateView--->$from")

        fitnessDataManager = FitnessDataManager(context)
        viewModel.getStepsHistory(this,stepCountHelper)

        if ( !Utilities.isNullOrEmptyOrZero(stepsDataSingleton.latestGoal.goal.toString())
            && stepsDataSingleton.latestGoal.date.equals(currentDate,ignoreCase = true) ) {
            todayStepGoal = stepsDataSingleton.latestGoal.goal
        }

        binding.progressBar.isClickable = false
        binding.progressBar.setOnTouchListener { _, _ -> true  }
        binding.progressBar.setBarColor(appColorHelper.primaryColor())
        binding.progressBar.setFillCircleColor(appColorHelper.primaryColor())
        binding.txtTodaysDate.text = DateHelper.getDateTimeAs_EEE_MMM_yyyy(DateHelper.currentDateAsStringddMMMyyyy).toUpperCase(Locale.ROOT)

        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            Timber.e("oAuthPermissionsApproved---> true")
        } else {
            Timber.e("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }

        weekdayAdapter = WeekdayAdapter(this)
        weekdayAdapter!!.updateList(weekDayList)
        binding.rvWeekday.layoutManager = GridLayoutManager(context, weekDayList.size)
        binding.rvWeekday.adapter = weekdayAdapter
    }

    private fun registerObservers() {

        viewModel.stepsHistoryList.observe(viewLifecycleOwner, { })
        viewModel.getLatestStepsGoal.observe(viewLifecycleOwner, { })
        viewModel.saveStepsGoal.observe(viewLifecycleOwner, { })
        viewModel.saveStepsList.observe(viewLifecycleOwner, { data ->
            if (data != null) {
                Timber.e("StepsDetails--->${data.stepsDetails}")
                if (isForceSync) {
                    isForceSync = false
                    updateTodayData("saveStepsList")
                    //updateView("SaveStepsList",stepsDataSingleton.stepHistoryList)
                }
            }
        })

    }

    private fun setClickable() {

        binding.btnMonthlyStat.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,from)
            it.findNavController().navigate(R.id.action_fitnessDashboardFragment_to_fragmentStepsDetail,bundle)
        }

        binding.txtGoal.setOnClickListener {
            showGoalDialog()
        }

        binding.stepsChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {

            override fun onValueSelected(e: Entry, h: Highlight) {
                try {
                    Timber.e("Selected Index---> ${e.x.toInt()}")
                    selectedDataSetIndex = e.x.toInt()
                    weekdayAdapter!!.updateWeekDayPosition(e.x.toInt())
                    //onWeekDaySelection(selectedDataSetIndex,weekDayList[selectedDataSetIndex])
                } catch ( e :Exception ) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected() {}
        })

    }

    fun saveGoalResp( stepsGoals : SetGoalModel.StepsGoals ) {
        Timber.e("Save Goal--->${stepsGoals}")
        todayStepGoal = stepsGoals.goal
        val saveGoal = GetStepsGoalModel.LatestGoal(
            personID = stepsGoals.personID,
            date = stepsGoals.date.split("T").toTypedArray()[0],
            type = stepsGoals.type,
            goal = stepsGoals.goal,
            iD = stepsGoals.iD)
        stepsDataSingleton.latestGoal = saveGoal
        updateStepsData()
        for (item in weeklyDataList) {
            if (item.recordDate.equals(currentDate, ignoreCase = true)) {
                item.totalGoal = todayStepGoal
            }
        }
        refreshChartData()
    }

    fun latestGoalResp( latestGoal : GetStepsGoalModel.LatestGoal ) {
        Timber.e("Latest Goal--->${latestGoal}")
        if (!Utilities.isNullOrEmptyOrZero(latestGoal.goal.toString())) {
            if (currentDate.equals(latestGoal.date.split("T").toTypedArray()[0],ignoreCase = true)) {
                todayStepGoal = latestGoal.goal
                latestGoal.date = latestGoal.date.split("T").toTypedArray()[0]
                latestGoal.goal = todayStepGoal
                stepsDataSingleton.latestGoal = latestGoal
                binding.txtGoal.text = todayStepGoal.toString()
            }
        }
        if ( isFailure ) {
            updateViewFromApi()
        }
        if ( isFromLatestGoal ) {
            displayFitnessData()
        }
    }

    override fun onWeekDaySelection(position: Int, weekday: WeekDay) {
        Timber.e("SelectedDate---> ${weekday.dateString}")
        if ( weeklyDataList.isNotEmpty() ) {
            selectedFitnessData =  weeklyDataList.find { it.recordDate.equals(weekday.dateString, true) }!!
            //selectedFitnessData = getSelectedDateFitnessData(weekday.date)
            Timber.e("data---> $selectedFitnessData")
            val steps = selectedFitnessData.stepsCount
            binding.txtSteps.text = steps.toString()
            binding.txtCalories.text = fitnessHelper.getCaloriesWithUnit(selectedFitnessData.calories.toString())
            binding.txtDistance.text = CalculateParameters.convertMtrToKms(selectedFitnessData.distance.toString())
//            binding.txtActTime.text = DateHelper.getHourMinFromStrMinutes(selectedFitnessData.activeTime)
//            if ( !Utilities.isNullOrEmptyOrZero(selectedFitnessData.activeTime) ) {
//                binding.txtActTime.text = DateHelper.getHourMinFromStrMinutes(selectedFitnessData.activeTime)
//            } else {
                binding.txtActTime.text = fitnessHelper.getActiveTime(steps)
//            }
            selectedFitnessData.activeTime = binding.txtActTime.text.toString().split(" ").toTypedArray()[0]
            stepsDataSingleton.selectedDateHistory = selectedFitnessData
        }
    }

    private fun updateTodayData( from : String ) {
        val item = selectedFitnessData
        //val item = getSelectedDateFitnessData(Calendar.getInstance().time)
        Timber.e("from--->$from")
        Timber.e("TodayData--->$item")
        binding.txtTodayDistance.text = CalculateParameters.convertMtrToKms(item.distance.toString())
        binding.txtTodayCalories.text = fitnessHelper.getCaloriesWithUnit(item.calories.toString())
        binding.txtGoal.text = item.totalGoal.toString()

        todayStepCount = item.stepsCount
        todayStepGoal = item.totalGoal
        binding.txtStepCount.text = todayStepCount.toString()

        var progress = 0
        try {
            if(todayStepCount != 0){
                progress = ( todayStepCount * 100) / todayStepGoal
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.progressBar.setValueAnimated(progress.toFloat(),1000)

        todayStepGoal = if (todayStepGoal > 0) todayStepGoal else 3000
        val stepsToGo = if (todayStepGoal - todayStepCount < 0) 0 else todayStepGoal - todayStepCount
        binding.txtStepsToGo.text = stepsToGo.toString()
//        if(selectedFitnessData.activeTime.contains('h',true) || selectedFitnessData.activeTime.contains('m',true))
//            binding.txtTodayActTime.text = selectedFitnessData.activeTime
//        else
//            binding.txtTodayActTime.text = DateHelper.getHourMinFromStrMinutes(selectedFitnessData.activeTime)
        binding.txtTodayActTime.text = fitnessHelper.getActiveTime(todayStepCount)
    }

    private fun updateStepsData( ) {
        var progress = 0
        try {
            if(todayStepCount != 0){
                progress = ( todayStepCount * 100) / todayStepGoal
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.progressBar.setValueAnimated(progress.toFloat())

        binding.txtGoal.text = todayStepGoal.toString()

        val stepsToGo = if (todayStepGoal - todayStepCount < 0) 0 else todayStepGoal - todayStepCount
        binding.txtStepsToGo.text = stepsToGo.toString()

    }

    private fun showGoalDialog() {
        try {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_edittext_goal)
            dialog.dialog_et.setText(binding.txtGoal.text)
            dialog.show()

            dialog.dialog_btncancel.setOnClickListener { dialog.dismiss() }

            dialog.dialog_btnok.setOnClickListener {
                val strValue = dialog.dialog_et.text.toString()
                if (!Utilities.isNullOrEmptyOrZero(strValue)) {
                    val value = Integer.parseInt(strValue)
                    if (value in 30..50000) {
                        viewModel.saveStepsGoal(this,value)
                        dialog.dismiss()
                    } else {
                        Utilities.toastMessageLong(context, resources.getString(R.string.err_enter_value_in_30_50000))
                    }
                } else {
                    Utilities.toastMessageLong(context, resources.getString(R.string.err_enter_steps))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refreshChartData() {
        binding.stepsChart.clear()
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
        if ( !weeklyDataList.isNullOrEmpty() ) {
                    for (i in weeklyDataList.indices ) {
            val item = weeklyDataList[i]
            val stepsCount = item.stepsCount
            var totalGoal = item.totalGoal
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
            dateLabels.add(item.recordDate)
/*            if ( dateLabels[i].equals(stepsDataSingleton.selectedDateHistory.recordDate, ignoreCase = true) ) {
                selectedDataSetIndex = i
            }*/
        }
        setDataToChart(entriesSteps,entriesGoals,dateLabels,maxValue)
        }
    }

    private fun setDataToChart(
        stepsEntries: ArrayList<Entry>,
        goalEntries: ArrayList<Entry>,
        dateLabels: ArrayList<String>,
        maxValue: Int) {
        try {
            val lineDataSetSteps: LineDataSet
            val lineDataSetGoal: LineDataSet
            val dataSets = java.util.ArrayList<ILineDataSet>()
            if (binding.stepsChart.data != null && binding.stepsChart.data.dataSetCount > 0) {
                lineDataSetSteps = binding.stepsChart.data.getDataSetByIndex(0) as LineDataSet
                lineDataSetSteps.values = stepsEntries
                lineDataSetSteps.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

                lineDataSetGoal = binding.stepsChart.data.getDataSetByIndex(1) as LineDataSet
                lineDataSetGoal.values = goalEntries
                lineDataSetGoal.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

                binding.stepsChart.data.notifyDataChanged()
                binding.stepsChart.notifyDataSetChanged()
            } else {
                //-----------------------------------------------------------------------------------
                // create a dataset for STEPS entries
                lineDataSetSteps = LineDataSet(stepsEntries, resources.getString(R.string.STEPS))
                lineDataSetSteps.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                lineDataSetSteps.setDrawIcons(false)

                // set the line to be drawn like this "- - - - - -"
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
                    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.fade_blue_chart) }
                    lineDataSetSteps.fillDrawable = drawable
                } else {
                    lineDataSetSteps.fillColor = Color.WHITE
                }

                //-----------------------------------------------------------------------------------
                // create a dataset for GOAL entries
                lineDataSetGoal = LineDataSet(goalEntries, resources.getString(R.string.GOALS))
                lineDataSetGoal.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                lineDataSetGoal.setDrawIcons(false)

                val colorGoal = appColorHelper.primaryColor()
                lineDataSetGoal.setColors(colorGoal)
                lineDataSetGoal.valueTextColor = appColorHelper.textColor
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
                    val drawable = context?.let { ContextCompat.getDrawable(it, R.drawable.fade_blue_chart) }
                    lineDataSetGoal.fillDrawable = drawable
                } else {
                    lineDataSetGoal.fillColor = Color.WHITE
                }

                dataSets.add(lineDataSetGoal) // add the datasets Goal
                dataSets.add(lineDataSetSteps) // add the datasets Steps

                val data = LineData(dataSets)
                data.setDrawValues(true)
                // set data
                binding.stepsChart.data = data
            }
            binding.stepsChart.setDrawGridBackground(false)
            binding.stepsChart.description.isEnabled = false
            binding.stepsChart.setTouchEnabled(true)
            binding.stepsChart.isDragEnabled = false
            binding.stepsChart.isScaleYEnabled = false
            binding.stepsChart.isScaleXEnabled = false
            binding.stepsChart.setPinchZoom(false)
            binding.stepsChart.isHighlightPerTapEnabled = true
            binding.stepsChart.axisLeft.setDrawGridLines(false)
            binding.stepsChart.xAxis.setDrawGridLines(false)
            binding.stepsChart.setVisibleXRangeMaximum(7f)
            binding.stepsChart.xAxis.textSize = 12f
            binding.stepsChart.xAxis.textColor = appColorHelper.primaryColor()
            binding.stepsChart.axisLeft.textColor = appColorHelper.primaryColor()
            binding.stepsChart.axisRight.isEnabled = false
            binding.stepsChart.legend.isEnabled = false
            binding.stepsChart.xAxis.isEnabled = false
            binding.stepsChart.legend.setDrawInside(true)
            // For setting chart padding dynamically
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            //val width = displayMetrics.widthPixels
            binding.stepsChart.setExtraOffsets(30f, 20f, 30f, 0f)

            binding.stepsChart.animateXY(1200, 1200)
            binding.stepsChart.centerViewTo(stepsEntries[selectedDataSetIndex].x, stepsEntries[selectedDataSetIndex].y, YAxis.AxisDependency.RIGHT)
            Timber.e("selectedDataSetIndex--->$selectedDataSetIndex")
            binding.stepsChart.highlightValue(stepsEntries[selectedDataSetIndex].x, stepsEntries[selectedDataSetIndex].y, selectedDataSetIndex, true)

            val xAxis = binding.stepsChart.xAxis
            xAxis.position = XAxis.XAxisPosition.TOP
            xAxis.removeAllLimitLines()// reset all limit lines to avoid overlapping lines
            xAxis.labelCount = dateLabels.size
            xAxis.isGranularityEnabled = true
            xAxis.granularity = 1f
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawLimitLinesBehindData(false)

            val leftAxis = binding.stepsChart.axisLeft
            leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
            val axisMaximum = if (maxValue < 5000) maxValue + 500 else maxValue + 1000
            leftAxis.axisMaximum = axisMaximum.toFloat()
            leftAxis.isEnabled = false
            leftAxis.setDrawZeroLine(false)
            leftAxis.setDrawLimitLinesBehindData(false)

            xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
                try {
                    return@IAxisValueFormatter if (isWeekly) {
                        DateHelper.convertDateTimeValue(dateLabels[value.toInt()], DateHelper.SERVER_DATE_YYYYMMDD,"E")
                    } else {
                        DateHelper.convertDateTimeValue(dateLabels[value.toInt()], DateHelper.SERVER_DATE_YYYYMMDD,"dd-MMM")
                    }
                    //return dateLabels.get((int) value);
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                value.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflate menu
        inflater.inflate(R.menu.options_menu, menu)
        var drawable: Drawable = menu.findItem(R.id.action_force_sync).icon
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, appColorHelper.primaryColor())
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu item clicks
        val id = item.itemId
        if (id == R.id.action_force_sync) {
            showDialog(
                listener = this,
                title = this.resources.getString(R.string.FORCE_SYNC),
                message = this.resources.getString(R.string.FORCE_SYNC_CONFORMATION),
                leftText = this.resources.getString(R.string.NO),
                rightText = this.resources.getString(R.string.YES) )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if ( isButtonRight ) {
            isForceSync = true
            if (fitnessDataManager!!.oAuthPermissionsApproved()) {
                fitnessDataManager!!.signOutGoogleAccount()
                fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
            } else {
                fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
            }
        }
    }

    fun displayFitnessData() : MutableList<FitnessEntity.StepGoalHistory> {
        val fitnessDataList : MutableList<FitnessEntity.StepGoalHistory> = mutableListOf()
        try {
            val fitnessListSize = if (isWeekly) 7 else 30
            val fitnessDataDBList = stepsDataSingleton.stepHistoryList
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -6)
            fitnessDataManager!!.readHistoryData(calendar.time, Calendar.getInstance().time).addOnCompleteListener {
                val fitnessDataJsonArray = fitnessDataManager!!.fitnessDataArray
                if (fitnessDataJsonArray != null) {
                    Timber.e("Google Fit data for $fitnessListSize days---> $fitnessDataJsonArray")

                    if ( fitnessDataJsonArray.length() > 0 ) {
                        //for (int i = 0; i < fitnessListSize; i++) {
                        for (i in fitnessListSize - 1 downTo 0) {
                            try {
                                val recordDate = Calendar.getInstance()
                                val sd = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.getDefault())
                                val sd2 = SimpleDateFormat(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, Locale.getDefault())
                                val sd3 = SimpleDateFormat(DateHelper.DISPLAY_DATE_DDMMMYYYY, Locale.getDefault())
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
                                        if (recordDateStr.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE).split("T").toTypedArray()[0],ignoreCase = true) ||
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
                                        if (recordDateStr.equals(fitnessDataDBList[j].recordDate.split("T")[0],ignoreCase = true) ||
                                            recordDateStr2ndFormat.equals(fitnessDataDBList[j].recordDate, ignoreCase = true) ||
                                            recordDateStr3rdFormat.equals(fitnessDataDBList[j].recordDate, ignoreCase = true)) {
                                            dataMap.totalGoal = fitnessDataDBList[j].totalGoal
                                        }
                                    }
                                }
                                if ( recordDateStr == currentDate ) {
                                    val latestGoal = stepsDataSingleton.latestGoal
                                    if ( !Utilities.isNullOrEmptyOrZero(latestGoal.goal.toString()) ) {
                                        dataMap.totalGoal = latestGoal.goal
                                    }
                                }
                                fitnessDataList.add(dataMap)
                            } catch ( e:Exception) {
                                e.printStackTrace()
                            }
                        }

                        weeklyDataList.clear()
                        weeklyDataList.addAll(fitnessDataList)
                        //selectedFitnessData = getSelectedDateFitnessData(Calendar.getInstance().time)
                        selectedFitnessData =  weeklyDataList.find { it.recordDate == currentDate }!!
                        updateTodayData("displayData")
                        refreshChartData()
                    }
                    selectedDataSetIndex = weekDayList.size - 1
                    Timber.e("selectedPos--->$selectedDataSetIndex")
                    weekdayAdapter!!.updateWeekDayPosition(selectedDataSetIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fitnessDataList
    }

    private fun updateViewFromApi() {
        try {
            val stepGoalHistory = stepsDataSingleton.stepHistoryList
            if(!stepGoalHistory.isNullOrEmpty()) {
                //val list = stepGoalHistory.sortedBy { it.recordDate }
                weeklyDataList.clear()
                for (weekday in weekDayList) {
                    var history = FitnessEntity.StepGoalHistory(recordDate = weekday.dateString, lastRefreshed = Date())
                    for ( item in stepGoalHistory) {
                        val recordDate = item.recordDate.split("T").toTypedArray()[0]
                        //Timber.e("Record Date--->$recordDate")
                        if ( weekday.dateString.equals(recordDate, ignoreCase = true) ) {
                            item.recordDate = recordDate
                            history = item
                            // Today's Data
                            if ( recordDate.equals(currentDate, ignoreCase = true) ) {
                                item.totalGoal = stepsDataSingleton.latestGoal.goal
                                selectedFitnessData = item
                                updateTodayData("updateViewFromApi")
                            }
                            break
                        }
                    }
                    weeklyDataList.add(history)
                }
                selectedDataSetIndex = weekDayList.size - 1
                Timber.e("selectedPos--->$selectedDataSetIndex")
                var isData = false
                if ( weeklyDataList.find { it.stepsCount != 0 } != null ) {
                    isData = true
                }
                if ( isData ) {
                    refreshChartData()
                    weekdayAdapter!!.updateWeekDayPosition(selectedDataSetIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isForceSync) {
            if (fitnessDataManager!!.oAuthPermissionsApproved()) {
                //viewModel.showProgressBar(resources.getString(R.string.SYNCHRONIZING))
                stepCountHelper.synchronizeForce(context)
            }
            updateTodayData("onResume")
        }
        //updateTodayData("onResume")
    }

    /*    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }*/

}