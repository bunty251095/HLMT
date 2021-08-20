package com.caressa.tools_calculators.ui.DueDateCalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentDueDateInputBinding
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class DueDateInputFragment : BaseFragment() {

    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private lateinit var binding : FragmentDueDateInputBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnCancel.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       binding = FragmentDueDateInputBinding.inflate(inflater, container, false)
       binding.viewModel = viewModel
       binding.lifecycleOwner = viewLifecycleOwner
       FirebaseHelper.logScreenEvent(FirebaseConstants.DUE_DATE_CALCULATOR_INPUT_SCREEN)
       initialise()
       return binding.root
   }

    private fun initialise() {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -10)
        binding.datePicker.maxDate = Calendar.getInstance().timeInMillis - 1000
        binding.datePicker.minDate = calendar.timeInMillis

        binding.btnCalculate.setOnClickListener {
            val lmpDate: String = binding.datePicker.year.toString() +
                    "-" + (binding.datePicker.month + 1) + "-" + binding.datePicker.dayOfMonth
            val bundle = Bundle()
            bundle.putString("lmpDate",lmpDate)
            it.findNavController().navigate(R.id.action_dueDateInputFragment_to_dueDateCalculatorReportFragment,bundle)
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.DUE_DATE_CALCULATE_CLICK)
        }

        binding.btnCancel.setOnClickListener {
            it.findNavController().navigate(R.id.action_dueDateInputFragment_to_toolsCalculatorsDashboardFragment)
        }
    }

}
