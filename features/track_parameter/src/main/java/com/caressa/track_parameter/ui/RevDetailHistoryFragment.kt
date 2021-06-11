package com.caressa.track_parameter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.showDialog
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.MonthYear
import com.caressa.model.parameter.ParameterProfile
import com.caressa.model.parameter.ParentProfileModel
import com.caressa.track_parameter.R
import com.caressa.track_parameter.adapter.ExpandableProfileAdapter
import com.caressa.track_parameter.adapter.PreviousMonthsAdapter
import com.caressa.track_parameter.databinding.FragmentParametersDetailHistoryBinding
import com.caressa.track_parameter.viewmodel.HistoryViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RevDetailHistoryFragment: BaseFragment(),PreviousMonthsAdapter.OnMonthClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: HistoryViewModel by viewModel()
    private lateinit var binding: FragmentParametersDetailHistoryBinding

    private var previousMonthsAdapter: PreviousMonthsAdapter? = null
    private var expandableProfileAdapter: ExpandableProfileAdapter? = null
    private var previousMonthsList = ArrayList<MonthYear>()
    private var mapProfilesAll : MutableList<ParameterProfile> = mutableListOf()
    private var parentDataList: MutableList<ParentProfileModel> = mutableListOf()
    private var month = ""
    private var year = ""
    private var previousMonth = 0
    //private var from = ""
    private var cal = Calendar.getInstance()
    private var animation : LayoutAnimationController? = null
    private var toProceed = true

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentParametersDetailHistoryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            setMonthYearView()
            setExpandableList()
            registerObserver()
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setMonthYearView() {
        animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_from_bottom)
        cal.time = Date()
        for (i in 5 downTo 0) {
            cal.time = Date()
            cal.add(Calendar.MONTH, -i)
            month = getFormattedValue("MMMM", cal.time)
            year = getFormattedValue("YYYY", cal.time)
            val monthYear = MonthYear()
            monthYear.month = month
            monthYear.year = year
            previousMonthsList.add(monthYear)
            Timber.i("$month-$year")
        }
        previousMonthsList.reverse()

        month = previousMonthsList[0].month.substring(0, 3)
        year = previousMonthsList[0].year

        previousMonthsAdapter = PreviousMonthsAdapter( previousMonthsList, this)
        binding.rvPreviousMonths.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
        binding.rvPreviousMonths.adapter = previousMonthsAdapter
        binding.txtSelectedMonthYear.text = previousMonthsList[0].month + " " + year
    }

    @SuppressLint("SetTextI18n")
    private fun setExpandableList() {
        viewModel.getAllProfileCodes()
        expandableProfileAdapter = ExpandableProfileAdapter( requireContext(),this )
        binding.rvExpandableProfile.layoutManager = LinearLayoutManager(context)
        binding.rvExpandableProfile.setExpanded(true)
        binding.rvExpandableProfile.layoutAnimation = animation
        binding.rvExpandableProfile.setHasFixedSize(true)
        binding.rvExpandableProfile.adapter = expandableProfileAdapter
        //viewModel.getParentDataList(mapProfilesAll,month,year)
    }

    private fun registerObserver() {

        viewModel.listAllProfiles.observe(viewLifecycleOwner, {
            if ( it != null ) {
                mapProfilesAll.clear()
                mapProfilesAll.addAll(it)
                Utilities.printData("allProfileCodes",it)
                viewModel.getParentDataList(mapProfilesAll,month,year)
            }
        })

        viewModel.parentDataList.observe(viewLifecycleOwner, {
            if ( it != null ) {
                parentDataList.clear()
                parentDataList = it.toMutableList()
                toProceed = false
                //Timber.e("parentProfilesList---> ${parentDataList.size}")
                val filteredList = getFilteredData(parentDataList)
                Utilities.printData("filteredList",filteredList,false)
                if (filteredList.size > 0) {
                    binding.rvExpandableProfile.layoutAnimation = animation
                    expandableProfileAdapter!!.updateData(filteredList)
                    binding.rvExpandableProfile.scheduleLayoutAnimation()
                    binding.rvExpandableProfile.visibility = View.VISIBLE
                    binding.layoutNoHistory.visibility = View.GONE
                } else {
                    binding.rvExpandableProfile.visibility = View.GONE
                    binding.layoutNoHistory.visibility = View.VISIBLE
                }
            }
        })

    }

    @SuppressLint("SetTextI18n")
    override fun onMonthSelection(yearMonth: MonthYear, newMonthPosition: Int) {
        if (previousMonth != newMonthPosition) {
            previousMonth = newMonthPosition
            Timber.e("SelectedMonth=>$previousMonth")
            month = previousMonthsList[newMonthPosition].month.substring(0, 3)
            year = previousMonthsList[newMonthPosition].year
            binding.txtSelectedMonthYear.text = previousMonthsList[newMonthPosition].month + " " + year
            viewModel.getParentDataList(mapProfilesAll,month,year)
        }
    }

    private fun getFilteredData(list: MutableList<ParentProfileModel>): MutableList<ParentProfileModel> {
        try {
            for (item in list) {
                Timber.e( "${item.profileCode}--> ${item.childParameterList.size}")
                val param = item.childParameterList.filter { hist ->
                    !hist.parameterCode.equals("WBC", true)
                            && !hist.parameterCode.equals("DLC", true)
                }.toMutableList()
                item.childParameterList = param
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun getFormattedValue(format: String, date: Date): String {
        return SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    fun showDetailsDialog( parameter : TrackParameterMaster.History,color: Int) {
        val msg = if ( !Utilities.isNullOrEmpty(parameter.observation) ) {
            "${parameter.value}  ${parameter.unit} \n ${parameter.observation} \n (${parameter.recordDate})"
        } else {
            "${parameter.value}  ${parameter.unit} \n (${parameter.recordDate})"
        }
        showDialog(
            listener = this,
            title = "${parameter.description} ",
            message = msg,
            showLeftBtn = false)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}