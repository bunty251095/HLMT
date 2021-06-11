package com.caressa.tools_calculators.ui.HypertensionCalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentHypertensionSummeryBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HypertensionSummeryFragment : BaseFragment() {

    private lateinit var binding: FragmentHypertensionSummeryBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()

    private val calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHypertensionSummeryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        if (calculatorDataSingleton.hypertensionSummery.hypertensionRisk.containsKey("RISK2")) {
            binding.btnRecalculate.text = resources.getString(R.string.RESTART)
        }
        initialiseChart()
    }

    private fun initialiseChart() {
        setChartData()

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

        //barChart.getLegend().setEnabled(false);
        //barChart.getLegend().setEnabled(false);
        binding.barChartHeartRisk.setTouchEnabled(false)
        binding.barChartHeartRisk.isDoubleTapToZoomEnabled = false
        val descriptionRisk = Description()
        descriptionRisk.text = ""
        binding.barChartHeartRisk.description = descriptionRisk
        binding.barChartHeartRisk.invalidate() // refresh

    }

    private fun setChartData() {
        val xAxis: XAxis = binding.barChartHeartRisk.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 3f
        xAxis.setCenterAxisLabels(true)

        xAxis.valueFormatter =
            IAxisValueFormatter { value, _ ->
                Timber.i("Position ==> $value")
                if (value == 0f) return@IAxisValueFormatter "Year 1" else if (value == 1f) return@IAxisValueFormatter "Year 2  " else if (value == 2f) return@IAxisValueFormatter "Year 4  "
                ""
            }

        //ArrayList<HeartAgeSummeryModel> summeryModelArrayList = CalculatorDataSingleton.getInstance().getHeartAgeSummeryList();
        val hypertensionRiskList:ArrayMap<String, ArrayList<String>> = calculatorDataSingleton.hypertensionSummery.hypertensionRisk
        val entries1: MutableList<BarEntry> = ArrayList()
        val entries2: MutableList<BarEntry> = ArrayList()
        val entries3: MutableList<BarEntry> = ArrayList()

        for (i in 0..2) {
            entries1.add(BarEntry(i.toFloat(), hypertensionRiskList["ORISK"]!![i].toFloat()))
            //entries1.add(new BarEntry(i.toFloat(), 12f));
            entries2.add(BarEntry(i.toFloat(), hypertensionRiskList["RISK1"]!![i].toFloat()))
            if (hypertensionRiskList.containsKey("RISK2")) {
                entries3.add(BarEntry(i.toFloat(), hypertensionRiskList["RISK2"]!![i].toFloat()))
            }
        }

        val setOptRisk = BarDataSet(entries1, "Optimum Risk")
        setOptRisk.color = ContextCompat.getColor(requireContext(), R.color.vivant_bright_sky_blue)
        setOptRisk.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)

        val setRisk1 = BarDataSet(entries2, "Risk 1")
        setRisk1.color = ContextCompat.getColor(requireContext(), R.color.vivant_marigold)
        setRisk1.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)

        var groupSpace = 0.20f
        var barSpace = 0.03f // x2 dataset
        var barWidth = 0.35f

        var data = BarData(setOptRisk, setRisk1)
        if (hypertensionRiskList.containsKey("RISK2")) {
            val setRisk2 = BarDataSet(entries3, "Risk 2")
            setRisk2.color = ContextCompat.getColor(requireContext(), R.color.vivant_watermelon)
            setRisk2.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
            data = BarData(setOptRisk, setRisk1, setRisk2)
            groupSpace = 0.13f
            barSpace = 0.02f
            barWidth = 0.26f
        }

        data.barWidth = barWidth // set the width of each bar

        binding.barChartHeartRisk.data = data
        binding.barChartHeartRisk.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping

        binding.barChartHeartRisk.setDrawValueAboveBar(true)
        binding.barChartHeartRisk.invalidate() // refresh
    }

    @SuppressLint("SetTextI18n")
    private fun setClickable() {

        if ( calculatorDataSingleton.heartAgeSummeryList.size >= 1) {
            binding.btnRecalculate.text = resources.getString(R.string.RESTART_ASSESSMENT)
        }

        binding.btnRecalculate.setOnClickListener {
            if (binding.btnRecalculate.text.toString().equals("restart", ignoreCase = true)) {
                val bundle = Bundle()
                bundle.putString(Constants.FROM, "Home")
                it.findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_toolsCalculatorsDashboardFragment)
            } else {
                val bundle = Bundle()
                bundle.putString(Constants.FROM, "Home")
                it.findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_hypertensionRecalculateFragment)
            }
        }

        binding.btnViewReportGraph.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "Home")
            it.findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_hypertensionReportFragment)
        }

    }

}