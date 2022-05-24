package com.caressa.tools_calculators.ui.SmartPhoneAddiction

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
import com.caressa.tools_calculators.R
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.tools_calculators.databinding.FragmentSmartPhoneAddictionSummaryBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SmartPhoneAddictionSummaryFragment : BaseFragment() {

    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private lateinit var binding : FragmentSmartPhoneAddictionSummaryBinding
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    var score = "0"

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            score = it.getString("Score","")!!
            Timber.e("Score---> $score")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_smartPhoneAddictionSummaryFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSmartPhoneAddictionSummaryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.SMART_PHONE_CALCULATOR_SUMMARY_SCREEN)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        binding.txtScore.text = score
        try {
            val score: Double = score.toDouble()
            val observation: String
            val detailObservation: String
            var detailObservationContent = ""
            val color: Int
            val progress = ( score * 100) / 55
            Timber.i("Progress=> ${progress.toInt()}")
            //binding.indicatorRisk.progress = progress.toInt()
            binding.indicatorRisk.setProgress(progress)
            when {
                score < 7 -> {
                    observation = resources.getString(R.string.NO_NOMOPHOBIC)
                    detailObservation = resources.getString(R.string.HAVE_NO_NOMOPHOBIA)
                    color = R.color.vivant_nasty_green
                    setColors(color)
                }
                score in 7.0..23.0 -> {
                    observation = resources.getString(R.string.MILD_NOMOPHOBIC)
                    detailObservation = resources.getString(R.string.HAVE_MILD_NOMOPHOBIA)
                    detailObservationContent = resources.getString(R.string.DESC_MILD_NOMOPHOBIC)
                    color = R.color.vivant_marigold
                }
                score in 24.0..39.0 -> {
                    observation = resources.getString(R.string.MODERATE_NOMOPHOBIC)
                    detailObservation = resources.getString(R.string.HAVE_MODERATE_NOMOPHOBIA)
                    detailObservationContent = resources.getString(R.string.DESC_MODERATE_NOMOPHOBIC)
                    color = R.color.vivant_orange_yellow
                }
                else -> {
                    observation = resources.getString(R.string.SEVERE_NOMOPHOBIC)
                    detailObservation = resources.getString(R.string.HAVE_SEVERE_NOMOPHOBIA)
                    detailObservationContent = resources.getString(R.string.DESC_SEVERE_NOMOPHOBIC)
                    color = R.color.vivant_watermelon
                }
            }
            binding.txtObservation.text = observation
            binding.txtSmartphoneResult.text = detailObservation
            binding.txtSmartphoneResult.setTextColor(ContextCompat.getColor(requireContext(),color))
            binding.txtSmartphoneResultMsg.text = detailObservationContent
            setColors(color)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.indicatorRisk.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.btnRestart.setOnClickListener {
            it.findNavController().navigate(R.id.action_smartPhoneAddictionSummaryFragment_to_smartPhoneInputFragment)
        }

    }

    private fun setColors(color: Int) {
        binding.indicatorRisk.progressColor = ContextCompat.getColor(requireContext(),color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorRisk.mThumb!!.setTint(ContextCompat.getColor(requireContext(),color))
        }
        //binding.layoutRisk.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.layoutRisk.background.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(requireContext(),color), BlendMode.SRC_ATOP)
        } else {
            binding.layoutRisk.background.setColorFilter(ContextCompat.getColor(requireContext(), color),
                PorterDuff.Mode.SRC_ATOP)
        }
    }

}
