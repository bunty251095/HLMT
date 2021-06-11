package com.caressa.track_parameter.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.extension.showToast
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.KeyboardUtils
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.track_parameter.ParameterHomeActivity
import com.caressa.track_parameter.R
import com.caressa.track_parameter.adapter.AddParameterAdapter
import com.caressa.track_parameter.databinding.ParameterDetailFragmentBinding
import com.caressa.track_parameter.viewmodel.ParameterDetailViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.parameter_detail_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class ParameterDetailFragment : BaseFragment(), OnChartValueSelectedListener,AddParameterAdapter.ParamListener {

    private val viewModel: ParameterDetailViewModel by viewModel()
    private lateinit var binding: ParameterDetailFragmentBinding
    val args: ParameterDetailFragmentArgs by navArgs()
    var paramList: List<TrackParameterMaster.Parameter> = listOf()
    private lateinit var chart: LineChart
    override fun getViewModel(): BaseViewModel = viewModel
    private var addParameterAdapter: AddParameterAdapter = AddParameterAdapter(this, listOf(), null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ParameterDetailFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        registerObserver()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        binding.txtProfileName.text = args.profileName
        viewModel.getParameterList(args.profileCode)
        viewModel.getParameterHistory(args.profileCode)
        chart = binding.chatView
        chart.setOnChartValueSelectedListener(this);

        val sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        sheetBehavior.peekHeight = 0
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.btnNewEntry.setOnClickListener {
            if(sheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.txtClose.setOnClickListener {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            KeyboardUtils.hideSoftInput(activity as ParameterHomeActivity)
        }
    }


    private fun setClickable() {

        binding.btnAddEntry.setOnClickListener {
            val saveParam = addParameterAdapter.getParameterDataList()
            if (saveParam.size > 0) {
                if(!edtDate.text.toString().isNullOrEmpty()) {
                    viewModel.saveParameter(
                        saveParam,
                        edtDate.text.toString()
                    )
                }else{
                    Utilities.toastMessageShort(context,"Please provide date.")
                }
            }

        }

        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.i("Selected Tab => " + paramList.get(tab!!.position).code)
                val param = paramList.get(tab.position)
                binding.txtSelectedChartVlaue.setText("- -")
                viewModel.getParameterHistory(param.code!!)
                if(!param.maxPermissibleValue.isNullOrEmpty()) {
                    binding.txtRefRangeValue.setText(param.minPermissibleValue+" / "+param.maxPermissibleValue+" ( "+param.unit+" )")
                }else{
                    binding.txtRefRangeValue.setText(" No reference range")
                }
            }
        })

        binding.edtDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun registerObserver(){
        val paramListObserver = Observer<List<TrackParameterMaster.Parameter>> { paramList ->
            // Update the UI, in this case, a TextView.

            this.paramList = paramList
            setupTabLayout(paramList)
            addParameterAdapter =  AddParameterAdapter(this, paramList, viewModel)
            binding.recyclerParam.layoutManager = LinearLayoutManager(context)
            binding.recyclerParam.adapter = addParameterAdapter
        }

        viewModel.paramList.observe(this, paramListObserver)

        viewModel.saveParam.observe(this, Observer {
            binding.txtClose.performClick()
        })
    }

    private fun setupTabLayout(paramList: List<TrackParameterMaster.Parameter>?) {
        binding.tabLayout.removeAllTabs()
        for (item in paramList!!) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(item.description).setTag(item.code))
        }
    }

    override fun onNothingSelected() {
        binding.txtSelectedChartVlaue.setText("- -")
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if(roundOffDecimal(e?.y!!.toDouble())!! > 0) {
            binding.txtSelectedChartVlaue.setText("" + roundOffDecimal(e?.y!!.toDouble()))
        }
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    fun showDatePicker() {
        var calendar = Calendar.getInstance()

        // Get the system current date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            edtDate.setText(DateHelper.convertDateTimeValue(""+dayOfMonth+"-"+(monthOfYear+1)+"-"+year,"dd-MM-yyyy", DateHelper.DISPLAY_DATE_DDMMMYYYY))
        }, year, month, day)
        dpd.datePicker.maxDate = calendar.timeInMillis
        dpd.show()
    }

    override fun updateList() {
        addParameterAdapter.notifyDataSetChanged()
    }
}