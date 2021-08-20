package com.caressa.tools_calculators.ui.HeartAgeCalculator

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentHeartSummaryBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.model.HeartAgeSummeryModel
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.ArrayList

class HeartSummaryFragment : BaseFragment() {

    val viewModel: ToolsCalculatorsViewModel by viewModel()
    private lateinit var binding: FragmentHeartSummaryBinding

    private var yourAge = 0
    private var heartAge = 0
    private var riskPercent = 0
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHeartSummaryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.HEART_AGE_SUMMARY_SCREEN)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        initialiseChart()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        binding.layoutHeartAgeView.visibility = View.VISIBLE
        binding.layoutHeartRiskView.visibility = View.GONE
        binding.indicatorYourAge.setOnTouchListener { _: View?, _: MotionEvent? -> true }
        binding.indicatorHeartAge.setOnTouchListener { _: View?, _: MotionEvent? -> true }
        binding.indicatorHeartRisk.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        try {
            yourAge = calculatorDataSingleton!!.personAge.toInt()
            heartAge = calculatorDataSingleton!!.heartAge.toInt()
            val riskPer: Double = calculatorDataSingleton!!.riskScorePercentage.toDouble()
            riskPercent = riskPer.toInt()
            seHeartAgeIndicatorDetails(heartAge, yourAge)
            setYourIndicatorDetails(yourAge, R.color.vivant_bright_sky_blue, heartAge)
            seHeartRiskIndicatorDetails(riskPer,calculatorDataSingleton!!.riskLabel)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setClickable() {

        binding.layoutHeartTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    binding.layoutHeartAgeView.visibility = View.VISIBLE
                    binding.layoutHeartRiskView.visibility = View.GONE
                    binding.barChartHeartAge.visibility = View.VISIBLE
                    binding.barChartHeartRisk.visibility = View.INVISIBLE
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEART_AGE_TAB_CLICKED)
                } else if (tab.position == 1) {
                    binding.layoutHeartRiskView.visibility = View.VISIBLE
                    binding.layoutHeartAgeView.visibility = View.GONE
                    binding.barChartHeartAge.visibility = View.INVISIBLE
                    binding.barChartHeartRisk.visibility = View.VISIBLE
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEART_RISK_TAB_CLICKED)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.btnRecalculate.setOnClickListener {
            findNavController().navigate(R.id.action_heartSummaryFragment_to_heartAgeRecalculateFragment)
        }

        binding.btnViewReport.setOnClickListener {
            findNavController().navigate(R.id.action_heartSummaryFragment_to_heartReportFragment)
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEART_AGE_VIEW_DETAILED_REPORT_CLICK)
        }

    }

    private fun seHeartAgeIndicatorDetails(heartAge: Int, yourAge: Int) {
        if (heartAge < yourAge) {
            binding.indicatorHeartAge.progressColor = ContextCompat.getColor(requireContext(), R.color.vivant_green_blue_two)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.layoutHeartAgeDetail.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.vivant_green_blue_two)
                binding.imgHeartColor.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.vivant_green_blue_two)
                binding.indicatorHeartAge.mThumb!!.setTint(ContextCompat.getColor(requireContext(), R.color.vivant_green_blue_two))
            }
        } else {
            binding.indicatorHeartAge.progressColor = ContextCompat.getColor(requireContext(), R.color.vivant_watermelon)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.imgHeartColor.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.vivant_watermelon)
                binding.layoutHeartAgeDetail.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.vivant_watermelon)
                binding.indicatorHeartAge.mThumb!!.setTint(ContextCompat.getColor(requireContext(), R.color.vivant_watermelon))
            }
        }
        //binding.indicatorHeartAge.progress = heartAge
        binding.indicatorHeartAge.setProgressWithAnimation(heartAge.toDouble())
        binding.txtHeartAge.text = heartAge.toString()
        binding.txtAgeHeart.text = heartAge.toString()
    }

    private fun setYourIndicatorDetails(yourAge: Int, heartColor: Int, heartAge: Int) {
        if (heartAge > 100) {
            binding.indicatorYourAge.max = heartAge
        }
        binding.indicatorYourAge.progressColor = ContextCompat.getColor(requireContext(),heartColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorYourAge.mThumb!!.setTint(ContextCompat.getColor(requireContext(),heartColor))
        }
        //binding.indicatorYourAge.progress = yourAge
        binding.indicatorYourAge.setProgressWithAnimation(yourAge.toDouble())
        binding.txtYourAge.text = yourAge.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun seHeartRiskIndicatorDetails(riskPercentage: Double, riskType: String ) {

        val heartColour: Int = when  {
            riskPercentage < 1 -> {
                R.color.vivant_green_blue_two
            }
            riskPercentage in 1.0..10.0 -> {
                R.color.vivant_marigold
            }
            riskPercentage in 11.0..20.0 -> {
                R.color.vivant_orange_yellow
            }
            else -> {
                R.color.vivant_watermelon
            }
        }

        binding.indicatorHeartRisk.progressColor = ContextCompat.getColor(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorHeartRisk.mThumb!!.setTint(ContextCompat.getColor(requireContext(),heartColour))
            binding.layoutHeartRiskDetail.backgroundTintList = AppCompatResources.getColorStateList(requireContext(),heartColour)
        }
        try {
            val finalRiskPercent = riskPercent
            //binding.indicatorHeartRisk.progress = riskPercent
            binding.indicatorHeartRisk.setProgressWithAnimation(riskPercent.toDouble())
            binding.txtRiskPercent.text = "$finalRiskPercent %"
            binding.txtRisk.text = riskType
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (calculatorDataSingleton!!.heartAgeSummeryList.size >= 3) {
            binding.btnRecalculate.visibility = View.INVISIBLE
        }
        if(calculatorDataSingleton!!.heartAgeSummeryList.size <= 1){
            binding.layoutHeart.visibility = View.VISIBLE
            binding.layoutHeartAgeChart.visibility = View.GONE
        }else
            if(calculatorDataSingleton!!.heartAgeSummeryList.size > 1){
                binding.layoutHeart.visibility = View.GONE
                binding.layoutHeartAgeChart.visibility = View.VISIBLE
            }
    }

    private fun initialiseChart() {
        setHeartAgeChartData()
        setHeartRiskChartData()

        val rightAxis: YAxis = binding.barChartHeartAge.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
        val leftAxis: YAxis = binding.barChartHeartAge.axisLeft
        leftAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxis.setLabelCount(5, true)
        leftAxis.setDrawTopYLabelEntry(true)
        leftAxis.setDrawZeroLine(true)
        leftAxis.granularity = 1f
        leftAxis.axisMinimum = 0f
        binding.barChartHeartAge.setFitBars(true) // make the x-axis fit exactly all bars
        binding.barChartHeartAge.legend.isEnabled = false
        val description = Description()
        description.text = ""
        binding.barChartHeartAge.setTouchEnabled(false)
        binding.barChartHeartAge.isDoubleTapToZoomEnabled = false
        binding.barChartHeartAge.description = description
        binding.barChartHeartAge.invalidate() // refresh

        // Initialise Heart risk chart Data
        val rightAxisRisk: YAxis = binding.barChartHeartRisk.axisRight
        rightAxisRisk.setDrawGridLines(false)
        rightAxisRisk.setDrawLabels(false)
        val leftAxisRisk: YAxis = binding.barChartHeartRisk.axisLeft
        leftAxisRisk.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxisRisk.setLabelCount(5, true)
        leftAxisRisk.setDrawTopYLabelEntry(true)
        leftAxisRisk.setDrawZeroLine(true)
        leftAxisRisk.granularity = 1f
        leftAxisRisk.axisMinimum = 0f
        binding.barChartHeartRisk.setFitBars(true) // make the x-axis fit exactly all bars
        binding.barChartHeartRisk.legend.isEnabled = false
        binding.barChartHeartRisk.setTouchEnabled(false)
        binding.barChartHeartRisk.isDoubleTapToZoomEnabled = false
        val descriptionRisk = Description()
        descriptionRisk.text = ""
        binding.barChartHeartRisk.description = descriptionRisk
        binding.barChartHeartRisk.invalidate() // refresh
    }

    private fun setHeartAgeChartData() {
        val entries: ArrayList<BarEntry> = ArrayList()
        val personAge: Float = calculatorDataSingleton!!.personAge.toFloat()
        entries.add(BarEntry(0f, personAge))
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> = calculatorDataSingleton!!.heartAgeSummeryList
        val colorArray = IntArray(summeryModelArrayList.size + 1)
        colorArray[0] = R.color.vivant_bright_sky_blue
        for (i in 1..summeryModelArrayList.size) {
            val heartAge: Float = summeryModelArrayList[i - 1].heartAge.toFloat()
            entries.add(BarEntry(i.toFloat(), heartAge))
            if (personAge <= heartAge) {
                colorArray[i] = R.color.vivant_watermelon
            } else {
                colorArray[i] = R.color.vivant_green_blue_two
            }
        }
        val set = BarDataSet(entries, "BarDataSet")
        set.colors = colorArray.toMutableList()
        //set.setColors(colorArray, this)
        set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        val data = BarData(set)
        //data.setBarWidth(0.9f); // set custom bar width
        binding.barChartHeartAge.data = data

        // the labels that should be drawn on the XAxis
        val xAxis: XAxis = binding.barChartHeartAge.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter =
            IAxisValueFormatter { value, _ ->
                var name = resources.getString(R.string.YOUR_AGE)
                if (value > 0) {
                    name = "${resources.getString(R.string.HEART_AGE)} " + value.toInt()
                }
                name
            }
        binding.barChartHeartAge.invalidate() // refresh
    }

    private fun setHeartRiskChartData() {
        val entries: MutableList<BarEntry> = ArrayList()
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> = calculatorDataSingleton!!.heartAgeSummeryList
        val colorArray = IntArray(summeryModelArrayList.size)
        for (i in summeryModelArrayList.indices) {
            val heartRisk: Float = summeryModelArrayList[i].heartRisk.toFloat()
            if (heartRisk < 1) {
                colorArray[i] = R.color.vivant_green_blue_two
            } else if (heartRisk in 1.0..10.0) {
                colorArray[i] = R.color.vivant_marigold
            } else if (heartRisk > 10 && heartRisk <= 20) {
                colorArray[i] = R.color.vivant_orange_yellow
            } else {
                colorArray[i] = R.color.vivant_watermelon
            }
            entries.add(BarEntry(i.toFloat(), heartRisk))
        }
        val set = BarDataSet(entries, "BarDataSet")
        set.colors = colorArray.toMutableList()
        //set.setColors(colorArray, this)
        set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        val data = BarData(set)
        //        data.setBarWidth(0.9f); // set custom bar width
        binding.barChartHeartRisk.data = data
        val xAxis: XAxis = binding.barChartHeartRisk.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            "${resources.getString(R.string.HEART_RISK)} " + (value.toInt() + 1)
        }
        binding.barChartHeartRisk.invalidate() // refresh
    }

}
