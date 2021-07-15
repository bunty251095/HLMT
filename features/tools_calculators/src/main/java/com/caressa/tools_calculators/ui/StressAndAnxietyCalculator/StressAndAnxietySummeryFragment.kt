package com.caressa.tools_calculators.ui.StressAndAnxietyCalculator

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.StressDetailReportAdapter
import com.caressa.tools_calculators.adapter.SummarySuggestionsAdapter
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentStressAndAnxietySummeryBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.model.StressData
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.android.viewmodel.ext.android.viewModel

class StressAndAnxietySummeryFragment : BaseFragment() {

    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private lateinit var binding : FragmentStressAndAnxietySummeryBinding
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private var summarySuggestionsAdapter1: SummarySuggestionsAdapter? = null
    private var summarySuggestionsAdapter2: SummarySuggestionsAdapter? = null
    private var summarySuggestionsAdapter3: SummarySuggestionsAdapter? = null
    private var stressDetailReportAdapter: StressDetailReportAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_stressAndAnxietySummeryFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStressAndAnxietySummeryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        summarySuggestionsAdapter1 = SummarySuggestionsAdapter( requireContext(),DataHandler(requireContext()).getDepressionList(),R.color.vivant_nasty_green)
        binding.rvDepressionScaleAssesses.layoutManager = GridLayoutManager(context, 2)
        binding.rvDepressionScaleAssesses.adapter = summarySuggestionsAdapter1

        summarySuggestionsAdapter2 = SummarySuggestionsAdapter( requireContext(),DataHandler(requireContext()).getAnxietyList(),R.color.vivant_marigold)
        binding.rvAnxietyScaleAssesses.layoutManager = GridLayoutManager(context, 2)
        binding.rvAnxietyScaleAssesses.adapter = summarySuggestionsAdapter2

        summarySuggestionsAdapter3 = SummarySuggestionsAdapter( requireContext(),DataHandler(requireContext()).getStressList(),R.color.vivant_orange_yellow)
        binding.rvStressScaleAssesses.layoutManager = GridLayoutManager(context, 2)
        binding.rvStressScaleAssesses.adapter = summarySuggestionsAdapter3

        val detailReportList = CalculatorDataSingleton.getInstance()!!.stressSummeryData.parameterReport
        stressDetailReportAdapter = StressDetailReportAdapter(requireContext(), detailReportList)
        binding.observationRecycler.adapter = stressDetailReportAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.indicatorProgtress.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

             override fun onTabSelected(tab: TabLayout.Tab) {
                updateData(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnRestartAssessment.setOnClickListener {
            it.findNavController().navigate(R.id.action_stressAndAnxietySummeryFragment_to_stressAndAnxietyInputFragment)
        }

        updateData(0)

    }

    private fun updateData(position: Int) {
        try {
            var stressData = StressData()
            when (position) {
                0 -> {
                    binding.imgDas.setImageResource(R.drawable.img_depression)
                    stressData = CalculatorDataSingleton.getInstance()!!.stressSummeryData.depression
                }
                1 -> {
                    binding.imgDas.setImageResource(R.drawable.img_anxiety)
                    stressData = CalculatorDataSingleton.getInstance()!!.stressSummeryData.anxiety
                }
                2 -> {
                    binding.imgDas.setImageResource(R.drawable.img_stress)
                    stressData = CalculatorDataSingleton.getInstance()!!.stressSummeryData.stress
                }
            }

            binding.txtRangeLowRisk.text = stressData.normal
            binding.txtRangeMildRisk.text = stressData.mild
            binding.txtRangeModerateRisk.text = stressData.moderate
            binding.txtRangeSeverRisk.text = stressData.severe
            binding.txtRangeHighRisk.text = stressData.extremeSevere
            setRiskDetails(stressData.score.toInt(), stressData.riskLabel, stressData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(java.lang.Exception::class)
    private fun setRiskDetails(riskPercentage: Int, riskType: String, stressData: StressData) {
        val heartColour: Int
        if (riskPercentage >= stressData.normal.split("-")[0].toDouble()
            && riskPercentage <= stressData.normal.split("-")[1].toDouble()) {
            heartColour = R.color.vivant_marigold
        } else if (riskPercentage >= stressData.mild.split("-")[0].toDouble()
            && riskPercentage <= stressData.mild.split("-")[1].toDouble()) {
            heartColour = R.color.vivant_orange_yellow
        } else if (riskPercentage >= stressData.moderate.split("-")[0].toDouble()
            && riskPercentage <= stressData.moderate.split("-")[1].toDouble()) {
            heartColour = R.color.vivant_dusty_orange
        } else if (riskPercentage >= stressData.severe.split("-")[0].toDouble()
            && riskPercentage <= stressData.severe.split("-")[1].toDouble()) {
            heartColour = R.color.vivant_watermelon
        } else if (riskPercentage >= stressData.extremeSevere.split("-")[0].toDouble()
            && riskPercentage <= stressData.extremeSevere.split("-")[1].toDouble()) {
            heartColour = R.color.vivant_red
        } else {
            heartColour = R.color.vivant_orange_yellow
        }

        binding.indicatorProgtress.progressColor = ContextCompat.getColor(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorProgtress.mThumb!!.setTint(ContextCompat.getColor(requireContext(),heartColour))
        }
        //binding.layoutRiskCircle.backgroundTintList = ContextCompat.getColorStateList(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.layoutRiskCircle.background.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(requireContext(),heartColour), BlendMode.SRC_ATOP)
        } else {
            binding.layoutRiskCircle.background.setColorFilter(ContextCompat.getColor(requireContext(),heartColour),
                PorterDuff.Mode.SRC_ATOP)
        }
        //binding.indicatorProgtress.progress = riskPercentage
        binding.indicatorProgtress.setProgressWithAnimation(riskPercentage.toDouble())
        binding.txtRiskPercentReport.text = "" + riskPercentage
        binding.txtDiabetesRiskTypeReport.text = riskType
    }

}
