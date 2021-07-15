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
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentHeartSummaryBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.android.viewmodel.ext.android.viewModel

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
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
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
                } else if (tab.position == 1) {
                    binding.layoutHeartRiskView.visibility = View.VISIBLE
                    binding.layoutHeartAgeView.visibility = View.GONE
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
    }

}
