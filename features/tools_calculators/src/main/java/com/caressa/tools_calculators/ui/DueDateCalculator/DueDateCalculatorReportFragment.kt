package com.caressa.tools_calculators.ui.DueDateCalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DateHelper
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.FragmentDueDateCalculatorReportBinding
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class DueDateCalculatorReportFragment : BaseFragment() {

    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private lateinit var binding : FragmentDueDateCalculatorReportBinding

    private var lmpDate = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDueDateCalculatorReportBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        lmpDate = requireArguments().getString("lmpDate")!!
        Timber.i("lmpDate--->$lmpDate")
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        val strLmpDate: String = DateHelper.convertDateToStr(DateHelper.convertStringDateToDate(lmpDate, "yyyy-MM-dd"), "dd MMM yyyy")
        binding.txtLmpDate.text = strLmpDate
        calculateBabyDueDate()
    }

    private fun calculateBabyDueDate() {
        val cal = Calendar.getInstance()
        val oldDate = DateHelper.convertStringDateToDate(lmpDate, "yyyy-MM-dd")
        cal.time = oldDate

        //Number of Days to add
        cal.add(Calendar.HOUR, 280 * 24)
        //Date after adding the days to the given date
        val newDate = DateHelper.convertDateToStr(cal.time, "dd MMM yyyy")
        Timber.i("Date after Addition--->$newDate")
        binding.txtDdDate.text = newDate
    }

    private fun setClickable() {

        binding.btnCancel.setOnClickListener {
            it.findNavController().navigate(R.id.action_dueDateCalculatorReportFragment_to_toolsCalculatorsDashboardFragment)
        }

        binding.btnRecalculate.setOnClickListener {
            it.findNavController().navigate(R.id.action_dueDateCalculatorReportFragment_to_dueDateInputFragment)
        }

    }

}
