package com.caressa.tools_calculators.ui.HypertensionCalculator

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentHypertensionReportBinding
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

class HypertensionReportFragment : BaseFragment() {

    private lateinit var binding: FragmentHypertensionReportBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()

    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_hypertensionReportFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHypertensionReportBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialiseChart()
        setClickable()
        updateData()
        return binding.root
    }

    private fun initialiseChart() {
        setChartData()

        // Initialise Heart risk chart Data
        val rightAxisRisk: YAxis = binding.barChartHeartAge.axisRight
        rightAxisRisk.setDrawGridLines(false)
        rightAxisRisk.setDrawLabels(false)

        val leftAxisRisk: YAxis = binding.barChartHeartAge.axisLeft
        leftAxisRisk.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxisRisk.setLabelCount(5, true)
        leftAxisRisk.setDrawTopYLabelEntry(true)
        leftAxisRisk.setDrawZeroLine(true)
        leftAxisRisk.granularity = 1f
        leftAxisRisk.axisMinimum = 0f

        binding.barChartHeartAge.setFitBars(true) // make the x-axis fit exactly all bars

        //barChart.getLegend().setEnabled(false);
        binding.barChartHeartAge.setTouchEnabled(false)
        binding.barChartHeartAge.isDoubleTapToZoomEnabled = false
        val descriptionRisk = Description()
        descriptionRisk.text = ""
        binding.barChartHeartAge.description = descriptionRisk
        binding.barChartHeartAge.invalidate() // refresh
    }

    private fun setChartData() {
        val xAxis: XAxis = binding.barChartHeartAge.xAxis
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
                if (value == 0f) return@IAxisValueFormatter resources.getString(R.string.YEAR_1) else if (value == 1f) return@IAxisValueFormatter "${resources.getString(R.string.YEAR_2)}  " else if (value == 2f) return@IAxisValueFormatter "${resources.getString(R.string.YEAR_4)}  "
                ""
            }

//        ArrayList<HeartAgeSummeryModel> summeryModelArrayList = CalculatorDataSingleton.getInstance().getHeartAgeSummeryList();
        val hypertensionRiskList: ArrayMap<String, ArrayList<String>> = calculatorDataSingleton!!.hypertensionSummery.hypertensionRisk
        val entries1: MutableList<BarEntry> = ArrayList()
        val entries2: MutableList<BarEntry> = ArrayList()
        val entries3: MutableList<BarEntry> = ArrayList()

        for (i in 0..2) {
            entries1.add(BarEntry(i.toFloat(), hypertensionRiskList["ORISK"]!![i].toFloat()))
            //            entries1.add(new BarEntry(i, 12f));
            entries2.add(BarEntry(i.toFloat(), hypertensionRiskList["RISK1"]!![i].toFloat()))
            if (hypertensionRiskList.containsKey("RISK2")) {
                entries3.add(BarEntry(i.toFloat(), hypertensionRiskList["RISK2"]!![i].toFloat()))
            }
        }

        val setOptRisk = BarDataSet(entries1, resources.getString(R.string.OPTIMUM_RISK))
        setOptRisk.color = ContextCompat.getColor(requireContext(), R.color.vivant_bright_sky_blue)
        setOptRisk.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)

        val setRisk1 = BarDataSet(entries2, resources.getString(R.string.RISK_1))
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

        data.setValueTextSize(12f)
        data.barWidth = barWidth // set the width of each bar

        binding.barChartHeartAge.data = data
        binding.barChartHeartAge.groupBars(0f, groupSpace, barSpace) // perform the "explicit" grouping

        binding.barChartHeartAge.setDrawValueAboveBar(true)
        binding.barChartHeartAge.invalidate() // refresh
    }

    @SuppressLint("SetTextI18n")
    private fun updateData() {
        try {
            val model = calculatorDataSingleton!!.hypertensionSummery
            val sysBp: String = model.systolicBp
            val diaBp: String = model.diastolicBp
            val status: String = model.status
            binding.txtBp.setTextColor(Color.parseColor(model.color))
            binding.txtBpObsTitle.setTextColor(Color.parseColor(model.color))
            binding.txtBp.text = "$sysBp/$diaBp - $status"
            binding.txtBpObsTitle.text = model.stage.title
            binding.txtBpObsDescription.text = model.stage.description
            if ( !Utilities.isNullOrEmpty( model.stage.subTitle ) ) {
                binding.txtBpObsSubtitle.visibility = View.VISIBLE
                binding.txtBpObsSubtitle.text = model.stage.subTitle
            } else {
                binding.txtBpObsSubtitle.visibility = View.GONE
            }
            binding.txtRecommendationTitle.text = model.recommendation.subTitle
            binding.txtRecommendationDescription.text = model.recommendation.description
            binding.txtParameterTitle.text = model.parameterReport.title
            binding.txtParameterDescription.text = model.parameterReport.description
            binding.txtSmokeTitle.text = model.smokingReport.title
            binding.txtSmokeDescription.text = model.smokingReport.description
            binding.txtBpTitle.text = model.bpReport.title
            binding.txtBpDescription.text = model.bpReport.description
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.btnRestart.setOnClickListener {
            it.findNavController().navigate(R.id.action_hypertensionReportFragment_to_hypertensionInputFragment)
        }

    }

}