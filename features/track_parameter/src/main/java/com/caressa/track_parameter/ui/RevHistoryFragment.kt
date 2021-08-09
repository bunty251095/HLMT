package com.caressa.track_parameter.ui

import android.content.ComponentName
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DateHelper
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.track_parameter.R
import com.caressa.track_parameter.adapter.ParameterSpinnerAdapter
import com.caressa.track_parameter.adapter.RevParamDetailsTableAdapter
import com.caressa.track_parameter.adapter.RevSavedParamAdapter
import com.caressa.track_parameter.adapter.RevSelectedParamAdapter
import com.caressa.track_parameter.databinding.FragmentParametersHistoryBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import com.caressa.track_parameter.viewmodel.HistoryViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class RevHistoryFragment : BaseFragment() {

    private var selectedParameterCode: String = ""
    private val viewModel: HistoryViewModel by viewModel()
    private lateinit var binding: FragmentParametersHistoryBinding
    override fun getViewModel(): BaseViewModel = viewModel
    var systolic = 0.0
    var diastolic = 0.0
    private var profileCode: String = "BMI";
    lateinit var profileAdapter: RevSelectedParamAdapter
    lateinit var savedParamAdapter: RevSavedParamAdapter
    lateinit var spinnerAdapter: ParameterSpinnerAdapter
    lateinit var paramDetailsTableAdapter: RevParamDetailsTableAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentParametersHistoryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        registerObserver()
        return binding.root
    }

    private fun registerObserver() {
        viewModel.paramBPHistory.observe(viewLifecycleOwner, {
            setBPChartData(it["BP_SYS"]!!, it["BP_DIA"]!!)
            setBpTableData(it["BP_SYS"]!!, it["BP_DIA"]!!)
        })

        viewModel.paramHistory.observe(viewLifecycleOwner, {
            setChartData(it)
            setTableData(it)
        })

        viewModel.savedParamList.observe(viewLifecycleOwner, {
            savedParamAdapter.selectedPosition = 0
            if (it.isNotEmpty()){
                Timber.i("Parameter 0 => "+ it[0])
                viewModel.parameterObservationListByParameterCode(it[0].parameterCode)
                if (it.get(0).profileCode.equals("BLOODPRESSURE")){
                    parameterSelection(savedParamAdapter.updateBloodPressureObservation(it)[0])
                }else {
                    parameterSelection(it[0])
                }
                binding.layoutNoHistory.visibility = Group.GONE
                binding.layoutParameterResultDetails.visibility = Group.VISIBLE
            }else{
                binding.layoutNoHistory.visibility = Group.VISIBLE
                binding.layoutParameterResultDetails.visibility = Group.GONE
            }
        })
    }


    private fun initialise() {
        profileAdapter = RevSelectedParamAdapter(false)
        binding.rvSelectedProfiles.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        binding.rvSelectedProfiles.adapter = profileAdapter

        savedParamAdapter = RevSavedParamAdapter(requireContext())
        binding.rvSavedParameters.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rvSavedParameters.adapter = savedParamAdapter
        viewModel.refreshSelectedParamList()

        binding.imgAddEdit.getBackground().setColorFilter(AppColorHelper.instance!!.primaryColor(),PorterDuff.Mode.SRC_ATOP)
        binding.lblAddEdit.setTextColor(AppColorHelper.instance!!.primaryColor())
        binding.txtLastCheckedDate.setTextColor(AppColorHelper.instance!!.primaryColor())
        binding.txtParamSpinner.getBackground().setColorFilter(AppColorHelper.instance!!.primaryColor(),PorterDuff.Mode.SRC_ATOP)

        spinnerAdapter = ParameterSpinnerAdapter(requireContext())
        binding.paramSpinner.adapter = spinnerAdapter

        paramDetailsTableAdapter = RevParamDetailsTableAdapter()
        binding.rvParamHistory.layoutManager = LinearLayoutManager(context)
        binding.rvParamHistory.adapter = paramDetailsTableAdapter

        viewModel.spinnerHistoryLiveData.observe(viewLifecycleOwner, {
            spinnerAdapter = ParameterSpinnerAdapter(requireContext())
            binding.paramSpinner.adapter = spinnerAdapter
            if(it!= null && it.isNotEmpty()) {
                val list = it.filter { item ->
                    !item.parameterCode.equals("WBC", true)
                            && !item.parameterCode.equals("DLC", true)
                }
                spinnerAdapter.updateList(list)
            }else{
                spinnerAdapter.updateList(it)
            }

            binding.paramSpinner.setSelection(0)
        })

        viewModel.parameterHistoryByProfileCode(profileCode)
        selectTabDetails()

    }

    private fun setClickable() {
        profileAdapter.setOnItemClickListener {
            Timber.i("Position: " + it.iconPosition + " :: " + it.profileName)
            viewModel.refreshSelectedParamList()
            profileCode = it.profileCode
            viewModel.parameterHistoryByProfileCode(it.profileCode)
            selectTabDetails()
        }
        savedParamAdapter.setOnItemClickListener {
            savedParamAdapter.notifyDataSetChanged()
            Timber.i("Parameter=> "+it.parameterCode)
            if(it.parameterCode == "BP_SYS" || it.parameterCode == "BP_DIA"){
                binding.rvObsRanges.visibility = View.INVISIBLE
            }else{
                binding.rvObsRanges.visibility = View.VISIBLE
                viewModel.parameterObservationListByParameterCode(it.parameterCode)
            }
            parameterSelection(it)
        }
        binding.layoutHistory.setOnClickListener {
            viewModel.goToDetailHistory()
        }
        binding.tabGraph.setOnClickListener {
            binding.tabGraph.background.setColorFilter(AppColorHelper.instance!!.primaryColor(), PorterDuff.Mode.SRC_ATOP)
            ImageViewCompat.setImageTintList(binding.tabGraph, ColorStateList.valueOf(resources.getColor(R.color.white)))
            binding.tabDetail.background.setColorFilter(Color.parseColor("#FEFCFF"), PorterDuff.Mode.SRC_ATOP)
            ImageViewCompat.setImageTintList(binding.tabDetail, ColorStateList.valueOf(resources.getColor(R.color.vivant_charcoal_grey)))
            binding.cardGraphView.visibility = View.VISIBLE
            binding.cardDetailsView.visibility = View.GONE

            viewModel.getSpinnerData(profileCode)

        }
        binding.tabDetail.setOnClickListener {
            selectTabDetails()
        }
        binding.txtParamSpinner.setOnClickListener { v: View? -> binding.paramSpinner.performClick() }

        binding.paramSpinner.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (spinnerAdapter.dataList.isNotEmpty()) {
                    spinnerAdapter.selectedPos = position
                    val name: String = spinnerAdapter.dataList.get(position).description!!
                    binding.txtParamSpinner.text = name
                    selectedParameterCode = spinnerAdapter.dataList.get(position).parameterCode
                    if (spinnerAdapter.dataList.get(position).unit != null) {
    //                    String unit = spinnerParamList.get(position).getName()+" ("+spinnerParamList.get(position).getUnit()+")";
                        binding.graphUnit.text = spinnerAdapter.dataList.get(position).unit
                    } else {
                        binding.graphUnit.text = " - - "
                    }
                    Timber.i("Selected Item:: $position,$name")
                    // Refresh Chart and table List
                    if (!selectedParameterCode.isNullOrEmpty()) {
                        if (selectedParameterCode.equals("BP_SYS", ignoreCase = true)
                            || selectedParameterCode.equals("BP_DIA", ignoreCase = true)) {

                            viewModel.getBPParameterHistory()
                        } else {

                            viewModel.getParameterHistory(selectedParameterCode)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.layoutAddEdit.setOnClickListener {
            viewModel.navigate(RevHistoryFragmentDirections.actionHistoryFragmentToUpdateParameterFragment(profileCode))
        }

        binding.btnParamResultUploadReport.setOnClickListener{
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.STORE_RECORDS)
            launchIntent.putExtra(Constants.FROM, Constants.TRACK_PARAMETER)
            startActivity(launchIntent)
        }

        binding.btnParamResultAddMedication.setOnClickListener{
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
            launchIntent.putExtra(Constants.FROM, Constants.TRACK_PARAMETER)
            startActivity(launchIntent)
        }
    }

    private fun parameterSelection(history: TrackParameterMaster.History) {

        try {
//        val recordDate:String = history.recordDate
        binding.txtLastCheckedDate.text = history.recordDate.replace("-"," ",true)
        val color = TrackParameterHelper.getObservationColor(history.observation,history.profileCode!!)

        binding.layoutSelectedParameterDetails.setBackgroundColor(resources.getColor(color))
            binding.txtParamTitle.text = history.description
            binding.txtParamValue.text = history.value.toString()
        if (history.observation.isNullOrEmpty()) {
            binding.txtParamObs.text = history.unit
        }else {
            binding.txtParamObs.text = history.observation
        }
        // Ranges not available for systolic and diastolic
        if (history.parameterCode.equals("BP_DIA", ignoreCase = true)
            || history.parameterCode.equals("BP_SYS", ignoreCase = true)) {
            binding.rvObsRanges.visibility = View.INVISIBLE
                binding.layoutSelectedParameterDetails.setBackgroundColor(resources.getColor(TrackParameterHelper.getObservationColor(history.observation!!, "BLOODPRESSURE")))
                binding.txtParamTitle.text = resources.getString(R.string.SYSTOLIC_DIASTOLIC)
                binding.txtParamValue.text = history.textValue

        }else{
            binding.rvObsRanges.visibility = View.VISIBLE
        }

        if (history.parameterCode.equals("BP_PULSE", ignoreCase = true) && history.value!!.isNaN()) {
            val observation = TrackParameterHelper.getPulseObservation(history.value!!.toString(),requireContext())
            binding.txtParamObs.text = observation
            binding.layoutSelectedParameterDetails.setBackgroundColor(resources.getColor(TrackParameterHelper.getObservationColor(observation, "BLOODPRESSURE")))
        }
        }catch (e: Exception){e.printStackTrace()}
    }

    private fun selectTabDetails() {
        binding.tabDetail.background.setColorFilter(AppColorHelper.instance!!.primaryColor(), PorterDuff.Mode.SRC_ATOP)
        ImageViewCompat.setImageTintList(binding.tabDetail, ColorStateList.valueOf(resources.getColor(R.color.white)))
        binding.tabGraph.background.setColorFilter(Color.parseColor("#FEFCFF"), PorterDuff.Mode.SRC_ATOP)
        ImageViewCompat.setImageTintList(binding.tabGraph, ColorStateList.valueOf(resources.getColor(R.color.vivant_charcoal_grey)))
        binding.cardDetailsView.visibility = View.VISIBLE
        binding.cardGraphView.visibility = View.GONE
    }

    private fun setBPChartData(sysList: List<TrackParameterMaster.History>,diaList: List<TrackParameterMaster.History>) {
        try {

            if (sysList != null && sysList.isNotEmpty()) {
                if (sysList.size == 1) {
                    binding.barChart.visibility = View.VISIBLE
                    binding.graphParameters.visibility = View.INVISIBLE
                    binding.barChart.setTouchEnabled(false)
                    binding.barChart.isDoubleTapToZoomEnabled = false
                    binding.barChart.legend.isEnabled = true
                    binding.barChart.isHighlightPerTapEnabled = false
                    val xAxis: XAxis = binding.barChart.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textSize = 11f
                    xAxis.textColor = resources.getColor(R.color.vivant_questionsteel_grey)
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.setCenterAxisLabels(true)
                    xAxis.axisMinimum = 0f
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in sysList.indices) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue("dd-MMM-yy", sysList.get(i).recordDate!!)
                                }
                            }
                            return ""
                        }
                    }
                    val yAxis: YAxis = binding.barChart.getAxisLeft()
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(false)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    yAxis.spaceTop = 15f
                    val rightAxisRisk: YAxis = binding.barChart.axisRight
                    rightAxisRisk.setDrawGridLines(false)
                    rightAxisRisk.setDrawLabels(false)
                    val entries: MutableList<BarEntry> = ArrayList()
                    for (i in sysList.indices) {
                        if (sysList[i].value == null || sysList[i].value!!.toDouble() == 0.0) {
                            entries.add(BarEntry(i.toFloat(), 0f))
                        } else {
                            entries.add(BarEntry(i.toFloat(), sysList[i].value!!.toFloat()))
                        }
                    }
                    val entriesDia: MutableList<BarEntry> = ArrayList()
                    for (i in diaList.indices) {
                        if (diaList[i].value == null || diaList[i].value!!.toDouble() == 0.0) {
                            entriesDia.add(BarEntry(i.toFloat(), 0f))
                        } else {
                            entriesDia.add(BarEntry(i.toFloat(), diaList[i].value!!.toFloat()))
                        }
                    }
                    val set = BarDataSet(entries, resources.getString(R.string.SYSTOLIC))
                    set.color = ContextCompat.getColor(requireContext(), R.color.vivant_heather)
                    set.valueTextSize = 11f
                    val setDia = BarDataSet(entriesDia, resources.getString(R.string.DIASTOLIC))
                    setDia.color = ContextCompat.getColor(requireContext(), R.color.vivant_orange_yellow)
                    setDia.valueTextSize = 11f
                    val groupSpace = 0.08f
                    val barSpace = 0.03f // x4 DataSet
                    val barWidth = 0.2f // x4 DataSet
                    // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval per "group"
                    val data = BarData(set, setDia)
                    data.barWidth = barWidth // set the width of each bar
                    binding.barChart.data = data
                    binding.barChart.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping
                    binding.barChart.description.isEnabled = false
                    binding.barChart.animateXY(2000, 2000)
                    binding.barChart.invalidate() // refresh
                } else {
                    binding.barChart.visibility = View.INVISIBLE
                    binding.graphParameters.visibility = View.VISIBLE
                    binding.graphParameters.description.text = ""
                    binding.graphParameters.legend.isEnabled = true
                    binding.graphParameters.axisRight.isEnabled = false
                    binding.graphParameters.isDoubleTapToZoomEnabled = false
                    binding.graphParameters.setTouchEnabled(false)
                    binding.graphParameters.isHighlightPerTapEnabled = false
                    val yAxis: YAxis = binding.graphParameters.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(true)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    val xAxis: XAxis = binding.graphParameters.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.axisMinimum = 0f
                    //xAxis.setAxisMaximum(5);
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in 1..sysList.size) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue("dd-MMM-yy", sysList[i - 1].recordDate!!)
                                }
                            }
                            return ""
                        }
                    }
                    val entries: MutableList<Entry> = ArrayList()
                    entries.add(Entry(0f, 0f))
                    for (i in sysList.indices) {
                        if (sysList[i].value == null || sysList[i].value!!.toDouble() == 0.0) {
                            entries.add(Entry((i + 1).toFloat(), 0f))
                        } else {
                            entries.add(Entry((i + 1).toFloat(), sysList[i].value!!.toFloat()))
                        }
                    }
                    val entriesDia: MutableList<Entry> = ArrayList()
                    entriesDia.add(Entry(0f, 0f))
                    for (i in diaList.indices) {
                        if (diaList[i].value == null || diaList[i].value!!.toDouble() == 0.0) {
                            entriesDia.add(Entry((i + 1).toFloat(), 0f))
                        } else {
                            entriesDia.add(Entry((i + 1).toFloat(), diaList[i].value!!.toFloat()))
                        }
                    }
                    val lineDataSet = LineDataSet(entries, resources.getString(R.string.SYSTOLIC))
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.setDrawValues(true)
                    lineDataSet.valueTextColor = resources.getColor(R.color.vivant_charcoal_grey)
                    lineDataSet.valueTextSize = 11f
                    lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.vivant_heather)
                    lineDataSet.fillAlpha = 255
                    lineDataSet.setDrawCircles(false)
                    lineDataSet.fillDrawable = resources.getDrawable(R.drawable.chart_fill)
                    val lineDataSetDia = LineDataSet(entriesDia, resources.getString(R.string.DIASTOLIC))
                    lineDataSetDia.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSetDia.setDrawFilled(true)
                    lineDataSetDia.setDrawValues(true)
                    lineDataSetDia.valueTextColor = resources.getColor(R.color.vivant_charcoal_grey)
                    lineDataSetDia.valueTextSize = 11f
                    lineDataSetDia.color = ContextCompat.getColor(requireContext(), R.color.vivant_orange_yellow)
                    lineDataSetDia.fillAlpha = 255
                    lineDataSetDia.setDrawCircles(false)
                    lineDataSetDia.fillDrawable = resources.getDrawable(R.drawable.chart_fill_orange)
                    val dataSets: MutableList<ILineDataSet> = ArrayList()
                    dataSets.add(lineDataSet)
                    dataSets.add(lineDataSetDia)
                    val lineData = LineData(dataSets)
                    binding.graphParameters.data = lineData
                    binding.graphParameters.invalidate()
                    binding.graphParameters.animateY(1000)
                }
            } else {
                binding.graphParameters.clearValues()
                binding.graphParameters.notifyDataSetChanged()
                binding.graphParameters.setNoDataText(resources.getString(R.string.NO_DATA_AVAILABLE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setChartData(recentDataList: List<TrackParameterMaster.History>) {
        try {
            if (recentDataList != null && recentDataList.isNotEmpty()) {
                if (recentDataList.size == 1) {
                    binding.barChart.visibility = View.VISIBLE
                    binding.graphParameters.visibility = View.INVISIBLE
                    binding.barChart.clear()
//                    binding.barChart.clearValues()
                    binding.barChart.invalidate()
                    binding.barChart.setTouchEnabled(false)
                    binding.barChart.isDoubleTapToZoomEnabled = false
                    binding.barChart.legend.isEnabled = false
                    binding.barChart.isHighlightPerTapEnabled = false
                    val xAxis: XAxis = binding.barChart.getXAxis()
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.textSize = 11f
                    xAxis.textColor = resources.getColor(R.color.vivant_charcoal_grey)
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.labelCount = 1
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in recentDataList.indices) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue("dd-MMM-yy",recentDataList.get(i).recordDate!!)
                                }
                            }
                            return ""
                        }
                    }
                    val yAxis: YAxis = binding.barChart.getAxisLeft()
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(false)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    yAxis.spaceTop = 15f
                    val rightAxisRisk: YAxis = binding.barChart.getAxisRight()
                    rightAxisRisk.setDrawGridLines(false)
                    rightAxisRisk.setDrawLabels(false)
                    val entries: MutableList<BarEntry> = ArrayList()
                    for (i in recentDataList.indices) {
                        if (recentDataList[i].value == null) {
                            entries.add(BarEntry(i.toFloat(), 0f))
                        } else {
                            entries.add(BarEntry(i.toFloat(),recentDataList[i].value!!.toFloat()))
                        }
                    }
                    val set = BarDataSet(entries, "BarDataSet")
                    set.color = ContextCompat.getColor(requireContext(),R.color.vivant_heather)
                    set.valueTextSize = 11f
                    val data = BarData(set)
                    data.barWidth = 0.4f // set custom bar width
                    binding.barChart.clear()
                    binding.barChart.data = data
                    binding.barChart.setFitBars(true) // make the x-axis fit exactly all bars
                    binding.barChart.description.isEnabled = false
                    binding.barChart.animateXY(2000, 2000)
                    binding.barChart.invalidate() // refresh
                } else {
                    binding.barChart.visibility = View.INVISIBLE
                    binding.graphParameters.visibility = View.VISIBLE
                    binding.graphParameters.description.text = ""
                    binding.graphParameters.legend.isEnabled = false
                    binding.graphParameters.axisRight.isEnabled = false
                    binding.graphParameters.isDoubleTapToZoomEnabled = false
                    binding.graphParameters.setTouchEnabled(false)
                    binding.graphParameters.isHighlightPerTapEnabled = false
                    val yAxis: YAxis = binding.graphParameters.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(true)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    val xAxis: XAxis = binding.graphParameters.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.axisMinimum = 0f
                    //xAxis.setAxisMaximum(5);
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in 1..recentDataList.size) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue("dd-MMM-yy", recentDataList[i - 1].recordDate!!)
                                }
                            }
                            return ""
                        }
                    }
                    val entries: MutableList<Entry> = ArrayList()
                    entries.add(Entry(0f, 0f))
                    for (i in recentDataList.indices) {
                        if (recentDataList[i].value == null)  {
                            entries.add(Entry((i + 1).toFloat(), 0f))
                        } else {
                            entries.add(Entry((i + 1).toFloat(), recentDataList[i].value!!.toFloat()))
                        }
                    }
                    val lineDataSet = LineDataSet(entries, "")
                    //lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.setDrawValues(true)
                    lineDataSet.valueTextColor = resources.getColor(R.color.vivant_questionsteel_grey)
                    lineDataSet.valueTextSize = 11f
                    //lineDataSet.setFillColor(ContextCompat.getColor(getContext(), R.color.vivant_heather));
                    lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.vivant_heather)
                    lineDataSet.fillAlpha = 255
                    lineDataSet.setDrawCircles(false)
                    lineDataSet.fillDrawable = resources.getDrawable(R.drawable.chart_fill)
                    val lineData = LineData(lineDataSet)
                    binding.graphParameters.data = lineData
                    binding.graphParameters.invalidate()
                    binding.graphParameters.animateY(1000)
                }
            } else {
                binding.graphParameters.clearValues()
                binding.graphParameters.notifyDataSetChanged()
                binding.graphParameters.setNoDataText(resources.getString(R.string.NO_DATA_AVAILABLE))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setTableData(recentDataList: List<TrackParameterMaster.History>) {
        try {
            Timber.i("Recent DATA list : $selectedParameterCode$recentDataList")
            paramDetailsTableAdapter.updateData(recentDataList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBpTableData(sysList: List<TrackParameterMaster.History>, diaList: List<TrackParameterMaster.History>) {
        try {
            val bpList = ArrayList<TrackParameterMaster.History>()

            bpList.addAll(sysList)
            var param: TrackParameterMaster.History
            var sys = 0
            var dia = 0
            for (i in bpList.indices) {
                try {
                    param = bpList.get(i)
                    sys = param.value!!.toDouble().toInt()
                    if (diaList != null && i < diaList.size) {
                        dia = diaList.get(i).value!!.toDouble().toInt()
                    }
                    param.parameterCode = "BP"
                    param.textValue = "$sys/$dia"
                    param.observation = TrackParameterHelper.getBPObservation(sys,dia,requireContext())
                } catch ( e: Exception) {
                    e.printStackTrace()
                }
            }
            if (bpList != null) {
                Timber.i("Recent DATA list : $selectedParameterCode$bpList")
                paramDetailsTableAdapter.updateData(bpList)
            } else {
                paramDetailsTableAdapter.updateData(bpList)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
