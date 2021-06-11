package com.caressa.tools_calculators.ui.DiabetesCalculator

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.DiabetesReportExpandableAdapter
import com.caressa.tools_calculators.adapter.SummarySuggestionsAdapter
import com.caressa.tools_calculators.databinding.FragmentDiabetesReportBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.model.ExpandModel
import com.caressa.tools_calculators.model.SubSectionModel
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class DiabetesReportFragment : BaseFragment() {

    private lateinit var binding: FragmentDiabetesReportBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()
    private  val calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!

    private var detailReportList: ArrayMap<String, ArrayList<SubSectionModel>> = ArrayMap()
    private var diabetesSummarySuggestionsAdapter1: SummarySuggestionsAdapter? = null
    private var diabetesSummarySuggestionsAdapter2: SummarySuggestionsAdapter? = null
    private var diabetesSummarySuggestionsAdapter3: SummarySuggestionsAdapter? = null
    private var diabetesReportExpandableAdapter: DiabetesReportExpandableAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton.clearData()
                findNavController().navigate(R.id.action_diabetesReportFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiabetesReportBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {

        detailReportList = calculatorDataSingleton.diabetesSummeryModel.detailReport
        val arrayList: MutableList<ExpandModel> = ArrayList()
        for (i in 0..9) {
            arrayList.add(ExpandModel())
        }
        //recyclerView is passes to achieve expand/collapse functionality correctly.
        diabetesReportExpandableAdapter = DiabetesReportExpandableAdapter(binding.observationRecycler,detailReportList,arrayList,requireContext())
        binding.observationRecycler.layoutManager = LinearLayoutManager(context)
        binding.observationRecycler.setExpanded(true)
        binding.observationRecycler.adapter = diabetesReportExpandableAdapter

        binding.indicatorDiabetesRiskReport.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        setDiabetesRiskDetails(
            calculatorDataSingleton.diabetesSummeryModel.totalScore.toDouble(),
            calculatorDataSingleton.diabetesSummeryModel.riskLabel)

        var doingGoodList: List<String> = ArrayList()
        if (calculatorDataSingleton.diabetesSummeryModel.goodIn.isNotEmpty()) {
            val arrDoingGood: Array<String> = calculatorDataSingleton.diabetesSummeryModel.goodIn.split(
                ",").toTypedArray()
            doingGoodList = listOf(*arrDoingGood)
            binding.layoutDoingGood.visibility = View.VISIBLE
        } else {
            binding.layoutDoingGood.visibility = View.GONE
        }
        diabetesSummarySuggestionsAdapter1 = SummarySuggestionsAdapter(requireContext(), doingGoodList, R.color.vivant_nasty_green)
        binding.rvDoingGood.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDoingGood.adapter = diabetesSummarySuggestionsAdapter1

        var needImpList: List<String> = ArrayList()
        if (calculatorDataSingleton.diabetesSummeryModel.needImprovement.isNotEmpty()) {
            val arrNeedImp: Array<String> = calculatorDataSingleton.diabetesSummeryModel.needImprovement.split(
                ",").toTypedArray()
            needImpList = listOf(*arrNeedImp)
            binding.layoutNeedToImprove.visibility = View.VISIBLE
        } else {
            binding.layoutNeedToImprove.visibility = View.GONE
        }
        diabetesSummarySuggestionsAdapter2 = SummarySuggestionsAdapter(requireContext(), needImpList, R.color.vivant_bright_sky_blue)
        binding.rvNeedToImprove.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvNeedToImprove.adapter = diabetesSummarySuggestionsAdapter2

        var nonModRiskList: List<String> = ArrayList()
        if (calculatorDataSingleton.diabetesSummeryModel.nonModifiableRisk.isNotEmpty()) {
            val arrNonModRisk: Array<String> = calculatorDataSingleton.diabetesSummeryModel.nonModifiableRisk.split(
                ",").toTypedArray()
            nonModRiskList = listOf(*arrNonModRisk)
            binding.layoutHaveFollowingNonModerateRisk.visibility = View.VISIBLE
        } else {
            binding.layoutHaveFollowingNonModerateRisk.visibility = View.GONE
        }
        diabetesSummarySuggestionsAdapter3 = SummarySuggestionsAdapter(requireContext(), nonModRiskList, R.color.vivant_watermelon)
        binding.rvHaveFollowingNonModerateRisk.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvHaveFollowingNonModerateRisk.adapter = diabetesSummarySuggestionsAdapter3

    }

    private fun setDiabetesRiskDetails(riskPercentage: Double, riskType: String) {
        val heartColour: Int
        when (riskPercentage) {
            in 0.0..5.0 -> {
                heartColour = R.color.vivant_green_blue_two
            }
            in 6.0..11.0 -> {
                heartColour = R.color.vivant_marigold
            }
            in 12.0..19.0 -> {
                heartColour = R.color.vivant_orange_yellow
            }
            in 20.0..47.0 -> {
                heartColour = R.color.vivant_watermelon
            }
            else -> {
                heartColour = R.color.vivant_marigold
            }
        }
        binding.indicatorDiabetesRiskReport.progressColor = ContextCompat.getColor(requireContext(), heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorDiabetesRiskReport.mThumb!!.setTint(ContextCompat.getColor(requireContext(), heartColour))
        }
        //binding.layoutHeartRisk.backgroundTintList = ContextCompat.getColorStateList(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.layoutHeartRisk.background.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(requireContext(),heartColour), BlendMode.SRC_ATOP)
        } else {
            binding.layoutHeartRisk.background.setColorFilter(ContextCompat.getColor(requireContext(), heartColour),
                PorterDuff.Mode.SRC_ATOP)
        }
        //binding.indicatorDiabetesRiskReport.progress = riskPercentage.toInt()
        binding.indicatorDiabetesRiskReport.setProgressWithAnimation(riskPercentage)
        binding.txtDiabetesRiskPercentReport.text =  riskPercentage.toInt().toString()
        binding.txtDiabetesRiskTypeReport.text = riskType
    }

    private fun setClickable() {

        binding.btnRestartDiabetesReport.setOnClickListener {
            calculatorDataSingleton.clearData()
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "Home")
            it.findNavController().navigate(R.id.action_diabetesReportFragment_to_diabetesCalculatorFragment)
        }

    }

}