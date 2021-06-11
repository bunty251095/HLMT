package com.caressa.track_parameter.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.CalculateParameters
import com.caressa.common.utils.DecimalDigitsInputFilter
import com.caressa.common.utils.DecimalValueFilter
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.ParameterData
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.AddParamListItemBinding
import com.caressa.track_parameter.ui.ParameterDetailFragment
import com.caressa.track_parameter.viewmodel.ParameterDetailViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

class AddParameterAdapter(
    val paramListener: ParameterDetailFragment,
    paramList: List<TrackParameterMaster.Parameter>,
    viewModel: ParameterDetailViewModel?
) : RecyclerView.Adapter<AddParameterAdapter.AddParamViewHolder>() {

    val dataList: MutableList<TrackParameterMaster.Parameter> =
        getFilteredList(paramList.toMutableList())
    val viewModel = viewModel
    var bmiHeight: Double = 0.0
    var bmiWeight: Double = 0.0
    var whrWaist: Double = 0.0
    var whrHip: Double = 0.0

    private fun getFilteredList(toMutableList: MutableList<TrackParameterMaster.Parameter>): MutableList<TrackParameterMaster.Parameter> {
        val param = toMutableList.filter { parameter ->
            parameter.code.equals(
                "BMI",
                true
            ) || parameter.code.equals("WHR", true)
        }
        if (!param.isEmpty()) {
            toMutableList.remove(param.get(0))
            toMutableList.add(param.get(0))
        }
        return toMutableList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddParamViewHolder =
        AddParamViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.add_param_list_item, parent, false
            )
        )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: AddParamViewHolder, position: Int) {
        var param = dataList.get(position)
        holder.bindTo(param)
    }

    inner class AddParamViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = AddParamListItemBinding.bind(parent)

        fun bindTo(item: TrackParameterMaster.Parameter) {
            var hintText = item.description
            if (item.parameterType.equals("Text", true)) {
                binding.edtParameter.inputType = InputType.TYPE_CLASS_TEXT
                binding.edtParameter.maxLines = 1
                binding.edtParameter.maxWidth = 50
            } else {
                hintText = hintText + " ( " + item.unit + " )"
                if (item.code.equals("BP_SYS", true) || item.code.equals("BP_DIA", true))
                    binding.edtParameter.inputType = InputType.TYPE_CLASS_NUMBER
                else
                    binding.edtParameter.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED

                binding.edtParameter.filters = arrayOf(DecimalDigitsInputFilter(5,1))
                binding.edtParameter.maxLines = 1
            }

            binding.inpLayout.hint = hintText
            binding.edtParameter.setText(item.paramValue)

//            binding.edtParameter.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//                dataList.get(adapterPosition).paramValue = binding.edtParameter.text.toString()
//            }

            //  disable BMI and WHR field
            if (item.code.equals("BMI", true) || item.code.equals("WHR", true)) {
                binding.edtParameter.isEnabled = false
                binding.edtParameter.isFocusable = false
                binding.inpLayout.isEnabled = false
                binding.edtParameter.isClickable = false
            }

            binding.edtParameter.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    dataList.get(adapterPosition).paramValue = binding.edtParameter.text.toString()

                    if ((item.code.equals("HEIGHT", true) || item.code.equals("WEIGHT", true)) && !s.toString().isNullOrEmpty()) {
                        if (item.code.equals("HEIGHT", true)) bmiHeight = s.toString().toDouble()
                        if (item.code.equals("WEIGHT", true)) bmiWeight = s.toString().toDouble()

                        if (bmiHeight != 0.0 && bmiWeight != 0.0) {
                            val bmiRecord = dataList.find { data -> data.code.equals("BMI", true) }
                            if (bmiRecord != null) {
                                bmiRecord.paramValue = CalculateParameters.getBMI(bmiHeight, bmiWeight).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toString()
                                notifyItemChanged(dataList.size-1)
                            }
                        }
                    }
                    if ((item.code.equals("WAIST", true) || item.code.equals("HIP", true)) && !s.toString().isNullOrEmpty()) {
                        if (item.code.equals("WAIST", true)) whrWaist = s.toString().toDouble()
                        if (item.code.equals("HIP", true)) whrHip = s.toString().toDouble()

                        if (whrWaist != 0.0 && whrHip != 0.0) {
                            val record = dataList.find { data -> data.code.equals("WHR", true) }
                            if (record != null) {
                                record.paramValue = CalculateParameters.getWHR(whrWaist, whrHip).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toString()
                                notifyItemChanged(dataList.size-1)
                            }
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    fun getParameterDataList(): ArrayList<ParameterData> {
        val paramDataList = arrayListOf<ParameterData>()
        for (item in dataList) {
            if (item.maxPermissibleValue.isNullOrEmpty()) {
                val param = ParameterData(
                    item.code.toString(),
                    item.profileCode.toString(),
                    item.unit.toString(),
                    item.paramValue.toString()
                )
                paramDataList.add(param)
            } else {
                if (!item.paramValue.isNullOrEmpty()) {
                    if (item.paramValue!!.toDouble() >= item.minPermissibleValue!!.toDouble() &&
                        item.paramValue!!.toDouble() <= item.maxPermissibleValue!!.toDouble()
                    ) {
                        val param = ParameterData(
                            item.code.toString(),
                            item.profileCode.toString(),
                            item.unit.toString(),
                            item.paramValue.toString()
                        )
                        paramDataList.add(param)
                    } else {
                        viewModel?.showMessage("Please enter " + item.description + " value between " + item.minPermissibleValue + " to " + item.maxPermissibleValue)
                        paramDataList.clear()
                        break
                    }
                }
            }
        }
        return paramDataList
    }

    interface ParamListener{
        abstract fun updateList()
    }

}