package com.caressa.tools_calculators.ui.DiabetesCalculator

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
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentDiabetesSummaryBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent

class DiabetesSummaryFragment : BaseFragment(),KoinComponent {

    private lateinit var binding: FragmentDiabetesSummaryBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_diabetesSummaryFragment_to_diabetesCalculatorFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiabetesSummaryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.DIABETES_SUMMARY_SCREEN)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initialise() {

        binding.txtDibProbability.text = ( resources.getString(R.string.BASED_ON_YOUR_SCORE_YOU_HAVE) + " "
                + calculatorDataSingleton!!.diabetesSummeryModel.probabilityPercentage
                + " " + resources.getString(R.string.PROBABILITY_OF_GETTING_DIABETES))
        binding.indicatorDiabetesRisk.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        setDiabetesRiskDetails(calculatorDataSingleton!!.diabetesSummeryModel.totalScore.toDouble(),
            calculatorDataSingleton!!.diabetesSummeryModel.riskLabel)
    }

    private fun setDiabetesRiskDetails(riskPercentage: Double, riskType: String) {
        val heartColour: Int
        when (riskPercentage) {
            in 0.0..5.0 -> {
                heartColour = R.color.vivant_nasty_green
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
                heartColour = R.color.vivant_watermelon
            }
        }
        binding.indicatorDiabetesRisk.progressColor = ContextCompat.getColor(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorDiabetesRisk.mThumb!!.setTint(ContextCompat.getColor(requireContext(),heartColour))
        }
        //binding.layoutHeartRisk.backgroundTintList = ContextCompat.getColorStateList(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.layoutHeartRisk.background.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(requireContext(),heartColour), BlendMode.SRC_ATOP)
        } else {
            binding.layoutHeartRisk.background.setColorFilter(ContextCompat.getColor(requireContext(), heartColour),
                PorterDuff.Mode.SRC_ATOP)
        }
        //binding.indicatorDiabetesRisk.progress = riskPercentage.toInt()
        binding.indicatorDiabetesRisk.setProgress(riskPercentage)
        binding.txtScore.text = riskPercentage.toInt().toString()
        binding.txtObservation.text = riskType
    }

    private fun setClickable() {

        binding.btnViewReportDiabetes.setOnClickListener {
            findNavController().navigate(R.id.action_diabetesSummaryFragment_to_diabetesReportFragment)
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.DIABETES_VIEW_DETAILED_REPORT_CLICK)
        }

        binding.btnRestartDiabetes.setOnClickListener {
            findNavController().navigate(R.id.action_diabetesSummaryFragment_to_diabetesCalculatorFragment)
        }

    }

}