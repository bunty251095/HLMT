package com.caressa.tools_calculators.ui.HeartAgeCalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.HeartRiskReportAdapter
import com.caressa.tools_calculators.adapter.ParamDataReportAdapter
import com.caressa.tools_calculators.databinding.FragmentHeartReportBinding
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
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class HeartReportFragment : BaseFragment() {

    val viewModel: ToolsCalculatorsViewModel by viewModel()
    private lateinit var binding: FragmentHeartReportBinding

    private var heartRiskReportAdapter: HeartRiskReportAdapter? = null
    private var paramDataReportAdapter: ParamDataReportAdapter? = null
    private  val calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHeartReportBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        initialiseChart()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        heartRiskReportAdapter = HeartRiskReportAdapter(calculatorDataSingleton.heartAgeSummery.heartRiskReport, requireContext())
        binding.heartRiskRecycler.adapter = heartRiskReportAdapter

        paramDataReportAdapter = ParamDataReportAdapter(calculatorDataSingleton.heartAgeSummery.parameterReport, requireContext())
        binding.paramListRecyler.adapter = paramDataReportAdapter

        binding.txtRecommendationTitle.text = calculatorDataSingleton.heartAgeSummery.heartAgeReport.title

        binding.txtRecommendationDescription.text = calculatorDataSingleton.heartAgeSummery.heartAgeReport.description

        binding.txtHeartAge.text = calculatorDataSingleton.heartAge
        binding.txtHeartRisk.text = calculatorDataSingleton.riskScorePercentage
        binding.txtRisk2.text = calculatorDataSingleton.riskLabel

        try {
            val riskPercentage = calculatorDataSingleton.riskScorePercentage.toDouble()
            val color: Int

            color = when  {
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
            binding.txtRisk2.setTextColor(ContextCompat.getColor(requireContext(), color))
        } catch (e: Exception) {
            e.printStackTrace()
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
        val personAge: Float = calculatorDataSingleton.personAge.toFloat()
        entries.add(BarEntry(0f, personAge))
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> =
            calculatorDataSingleton.heartAgeSummeryList
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
                var name = "Your age"
                if (value > 0) {
                    name = "Heart age " + value.toInt()
                }
                name
            }
        binding.barChartHeartAge.invalidate() // refresh
    }

    private fun setHeartRiskChartData() {
        val entries: MutableList<BarEntry> = ArrayList()
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> = calculatorDataSingleton.heartAgeSummeryList
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
            "Heart Risk " + (value.toInt() + 1)
        }
        binding.barChartHeartRisk.invalidate() // refresh
    }

    private fun setClickable() {

        binding.btnRestart.setOnClickListener {
            calculatorDataSingleton.clearData()
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "Home")
            it.findNavController().navigate(R.id.action_heartReportFragment_to_heartAgeFragment)
        }

    }

}
