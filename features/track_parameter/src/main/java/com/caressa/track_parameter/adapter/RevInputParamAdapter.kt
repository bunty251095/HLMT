package com.caressa.track_parameter.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.CalculateParameters
import com.caressa.common.utils.DecimalValueFilter
import com.caressa.common.utils.HeightWeightDialog
import com.caressa.common.utils.ParameterDataModel
import com.caressa.model.parameter.ParameterListModel
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.ItemInputParametersBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import com.caressa.track_parameter.viewmodel.UpdateParamViewModel
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Pattern

class RevInputParamAdapter(var profileCode: String, val viewModel: UpdateParamViewModel): RecyclerView.Adapter<RevInputParamAdapter.InputParameterViewHolder>() {

    val dataList: MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
    private var edtBMI: EditText? = null
    private var edtWHR: EditText? = null
    var validationMassage = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InputParameterViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_input_parameters, parent, false
        )
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: InputParameterViewHolder, position: Int) =
        holder.bindTo(dataList[position],position)

    fun updateData(items: List<ParameterListModel.InputParameterModel>) {
        dataList.clear()
        dataList.addAll(filterList(items))
        Timber.i("Inside updateData " + dataList.toString())
        this.notifyDataSetChanged()
    }

    private fun filterList(items: List<ParameterListModel.InputParameterModel>): Collection<ParameterListModel.InputParameterModel> {
        var filterList: List<ParameterListModel.InputParameterModel> = mutableListOf()
        if(items.get(0).profileCode.equals("BMI")){
            filterList = updateBMISequence(items)
        }else if(items.get(0).profileCode.equals("WHR")){
            filterList = updateWHRSequence(items)
        }else if (items.get(0).profileCode.equals("BLOODPRESSURE")) {
            filterList = updateBPSequence(items)
        }else{
            filterList = items
//                items.filter { !it.parameterCode.equals("WBC") && !it.parameterCode.equals("DLC") }
        }
        return filterList
    }

    private fun updateWHRSequence(items: List<ParameterListModel.InputParameterModel>): List<ParameterListModel.InputParameterModel> {
        var whrList:MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        var whr:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var waist:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var hip:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        for(item in items){
            if (item.parameterCode.equals("WHR")){
                whr = item
            }
            if (item.parameterCode.equals("WAIST")){
                waist = item
            }
            if (item.parameterCode.equals("HIP")) {
                hip = item
            }
        }
        whrList.add(hip)
        whrList.add(waist)
        whrList.add(whr)
        return whrList
    }

    private fun updateBPSequence(items: List<ParameterListModel.InputParameterModel>): List<ParameterListModel.InputParameterModel> {
        var bpList:MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        var systolic:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var diastolic:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
//        var pulse:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        for(item in items){
            if (item.parameterCode.equals("BP_SYS")){
                systolic = item
            }
            if (item.parameterCode.equals("BP_DIA")){
                diastolic = item
            }
//            if (item.parameterCode.equals("BP_PULSE")) {
//                pulse = item
//            }
        }
        bpList.add(systolic)
        bpList.add(diastolic)
//        bpList.add(pulse)
        return bpList
    }

    private fun updateBMISequence(items: List<ParameterListModel.InputParameterModel>): List<ParameterListModel.InputParameterModel> {
        var bmiList:MutableList<ParameterListModel.InputParameterModel> = mutableListOf()
        var bmi:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var height:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        var weight:ParameterListModel.InputParameterModel = ParameterListModel.InputParameterModel()
        for(item in items){
            if (item.parameterCode.equals("BMI")){
                bmi = item
            }
            if (item.parameterCode.equals("HEIGHT")){
                height = item
            }
            if (item.parameterCode.equals("WEIGHT")) {
                weight = item
            }
        }
        bmiList.add(height)
        bmiList.add(weight)
        bmiList.add(bmi)
        return bmiList
    }

    inner class InputParameterViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemInputParametersBinding.bind(parent)

        fun bindTo(parameter: ParameterListModel.InputParameterModel,position: Int) {
            Timber.i("DataAdapter=> "+parameter.minPermissibleValue+" : "+parameter.maxPermissibleValue+" : "+parameter.parameterUnit)
            binding.view.setBackgroundColor(binding.view.resources.getColor(getRandomColor(position)))
            binding.txtParamName.setText(parameter.description)
            binding.imgParam.setImageResource(
                TrackParameterHelper.getProfileImageByProfileCode(
                    profileCode
                )
            )

            binding.edtInputValue.setHint(getHint(parameter))

            if (!parameter.parameterUnit.isNullOrEmpty()) {
                binding.txtParamUnit.setText(parameter.parameterUnit)
            }else{
                binding.txtParamUnit.text = ""
            }
            if (parameter.parameterType.equals("Value",true)) {
                binding.edtInputValue.setText(parameter.parameterVal)
                binding.edtInputValue.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                val decimalValueFilter = DecimalValueFilter(true)
                if (parameter.parameterCode.equals("BP_SYS") || parameter.parameterCode
                        .equals("BP_DIA") || parameter.parameterCode.equals("BP_PULSE")
                ) {
                    decimalValueFilter.setDigits(0)
                    binding.edtInputValue.filters =
                        arrayOf<InputFilter>(decimalValueFilter, InputFilter.LengthFilter(4))
                    binding.edtInputValue.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                } else {
                    decimalValueFilter.setDigits(2)
                    binding.edtInputValue.filters =
                        arrayOf<InputFilter>(decimalValueFilter, InputFilter.LengthFilter(6))
                }
            } else {
                binding.edtInputValue.inputType = InputType.TYPE_CLASS_TEXT
                binding.edtInputValue.filters = arrayOf(filter, InputFilter.LengthFilter(60))
                binding.edtInputValue.setText(parameter.parameterTextVal)
            }
            if (dataList[position].parameterCode.equals("BMI",true)) {
                edtBMI = binding.edtInputValue
                //            binding.mainLayout.setAlpha(0.7f);
                binding.edtInputValue.inputType = 0
                binding.edtInputValue.isClickable = false
                binding.edtInputValue.isFocusable = false
                binding.edtInputValue.isFocusableInTouchMode = false
                if (!parameter.parameterVal.isNullOrEmpty()) {
                    val observation: String = TrackParameterHelper.getBMIObservation(parameter.parameterVal!!)
//                    val observation: String = ""
                    if (!observation.isNullOrEmpty()) {
                        binding.edtInputValue.setTextColor(
                            binding.edtInputValue.resources.getColor(
                                TrackParameterHelper.getObservationColor(observation, "BMI")
                            )
                        )
                    }
                }
            }else
            if (dataList.get(position).parameterCode.equals("WHR",true)) {
                edtWHR = binding.edtInputValue
                //            binding.mainLayout.setAlpha(0.7f);
                binding.edtInputValue.inputType = 0
                binding.edtInputValue.isFocusable = false
                binding.edtInputValue.isFocusableInTouchMode = false
                if (!parameter.parameterVal.isNullOrEmpty()) {
                    val observation: String = TrackParameterHelper.getWHRObservation(parameter.parameterVal!!,1)
//                    val observation: String = ""
                    if (!observation.isNullOrEmpty()) {
                        binding.edtInputValue.setTextColor(
                            binding.edtInputValue.resources.getColor(
                                TrackParameterHelper.getObservationColor(observation, "WHR")
                            )
                        )
                    }
                }
            }/*else{
                binding.edtInputValue.isFocusable = true
                binding.edtInputValue.isFocusableInTouchMode = false
                if (parameter.parameterType.equals("Value",true)) {
                    binding.edtInputValue.setText(parameter.parameterVal)
                    binding.edtInputValue.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
                    val decimalValueFilter = DecimalValueFilter(true)
                    if (parameter.parameterCode.equals("BP_SYS") || parameter.parameterCode
                            .equals("BP_DIA") || parameter.parameterCode.equals("BP_PULSE")
                    ) {
                        decimalValueFilter.setDigits(0)
                        binding.edtInputValue.filters =
                            arrayOf<InputFilter>(decimalValueFilter, InputFilter.LengthFilter(4))
                        binding.edtInputValue.inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                    } else {
                        decimalValueFilter.setDigits(2)
                        binding.edtInputValue.filters =
                            arrayOf<InputFilter>(decimalValueFilter, InputFilter.LengthFilter(6))
                    }
                } else {
                    binding.edtInputValue.inputType = InputType.TYPE_CLASS_TEXT
                    binding.edtInputValue.filters = arrayOf(filter, InputFilter.LengthFilter(60))
                    binding.edtInputValue.setText(parameter.parameterTextVal)
                }

            }*/

            binding.edtInputValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
                    if (!dataList.get(adapterPosition).parameterCode
                            .equals("BMI",true) &&
                        !dataList.get(adapterPosition).parameterCode
                            .equals("WHR",true)
                    ) {
                        if (dataList.get(adapterPosition).parameterType
                                .equals("value",true)
                        ) {
                            dataList.get(adapterPosition).parameterVal = editable.toString()
                            if (profileCode.equals("BMI", ignoreCase = true)) {
                                updateBMIParameter()
                            } else if (profileCode.equals("WHR", ignoreCase = true)) {
                                updateWHRParameter()
                            }
                        } else {
                            dataList.get(adapterPosition).parameterTextVal = editable.toString()
                        }
                    }
                }
            })
            if(parameter.parameterCode.equals("HEIGHT")){
                binding.layoutInputNonBmi.visibility = View.GONE
                binding.layoutHeightWeight.visibility = View.VISIBLE
                binding.edtInputValueBmi.isFocusable = false
                binding.edtInputValueBmi.isCursorVisible = false
                binding.edtInputValueBmi.isFocusableInTouchMode = false
                binding.edtInputValueBmi.setHint(binding.edtInputValue.hint)
                if(viewModel.getPreference("HEIGHT").equals("cm")){
                    binding.edtInputValueBmi.text = binding.edtInputValue.text
                    binding.txtParamUnitBmi.text = binding.txtParamUnit.text
                }else{
                    var text:String = binding.edtInputValue.text.toString()
                    if(!text.isNullOrEmpty()) {
                        binding.edtInputValueBmi.setText(
                            CalculateParameters.convertCmToFeetInch(
                                text
                            )
                        )
                        binding.txtParamUnitBmi.text = ""
                    }
                }
                binding.edtInputValueBmi.setOnClickListener {
                    val data = ParameterDataModel()
                    data.title = "Height"
                    data.value = " - - "
                    if (dataList.get(adapterPosition).parameterVal.isNullOrEmpty()) {
                        data.finalValue = "0"
                    }else{
                        data.finalValue = dataList.get(adapterPosition).parameterVal!!

                    }
                    if(viewModel.getPreference("HEIGHT").equals("cm")) {
                        data.unit = "cm"
                    }else{
                        data.unit = "Feet/inch"
                    }
//                        data.unit = "Feet/inch"
//                        data.unit = "lbs"
//                        data.unit = "Kg"
                    data.code = "HEIGHT"
                    val heightWeightDialog = HeightWeightDialog(binding.edtInputValue.context,object:HeightWeightDialog.OnDialogValueListener {
                        override fun onDialogValueListener(
                            dialogType: String,
                            height: String,
                            weight: String,
                            unit: String,
                            visibleValue: String
                        ) {
                            viewModel.updateUserPreference(unit)
                            binding.edtInputValue.setText(height)
                            if(unit.equals("cm",true)) {
                                binding.edtInputValueBmi.setText(height)
                                binding.txtParamUnitBmi.setText("cm")
                            }else{
                                binding.edtInputValueBmi.setText(
                                    CalculateParameters.convertCmToFeetInch(
                                        height
                                    )
                                )
                                binding.txtParamUnitBmi.setText("")
                            }
                        }
                    },"Height", data)
                    heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    heightWeightDialog.show()
                }
            }

            if(parameter.parameterCode.equals("WEIGHT")){
                binding.layoutInputNonBmi.visibility = View.GONE
                binding.layoutHeightWeight.visibility = View.VISIBLE
                binding.edtInputValueBmi.isFocusable = false
                binding.edtInputValueBmi.isCursorVisible = false
                binding.edtInputValueBmi.isFocusableInTouchMode = false
                binding.edtInputValueBmi.setHint(binding.edtInputValue.hint)
                if(viewModel.getPreference("WEIGHT").equals("kg")){
                    binding.edtInputValueBmi.text = binding.edtInputValue.text
                    binding.txtParamUnitBmi.text = binding.txtParamUnit.text
                }else{
                    var text:String = binding.edtInputValue.text.toString()
                    if(!text.isNullOrEmpty()) {
                        binding.edtInputValueBmi.setText(
                            CalculateParameters.convertKgToLbs(
                                text
                            )
                        )
                        binding.txtParamUnitBmi.text = "lbs"
                    }
                }
                binding.edtInputValueBmi.setOnClickListener {
                    val data = ParameterDataModel()
                    data.title = "Weight"
                    data.value = " - - "
                    if (dataList.get(adapterPosition).parameterVal.isNullOrEmpty()) {
                        data.finalValue = "0"
                    }else{
                        data.finalValue = dataList.get(adapterPosition).parameterVal!!

                    }
                    if(viewModel.getPreference("WEIGHT").equals("kg")) {
                        data.unit = "kg"
                    }else{
                        data.unit = "lbs"
                    }
//                        data.unit = "Feet/inch"
//                        data.unit = "lbs"
//                        data.unit = "Kg"
                    data.code = "WEIGHT"
                    val heightWeightDialog = HeightWeightDialog(binding.edtInputValue.context,object:HeightWeightDialog.OnDialogValueListener {
                        override fun onDialogValueListener(
                            dialogType: String,
                            height: String,
                            weight: String,
                            unit: String,
                            visibleValue: String
                        ) {
                            viewModel.updateUserPreference(unit)
                            binding.edtInputValue.setText(weight)
                            if(unit.equals("kg",true)) {
                                binding.edtInputValueBmi.setText(weight)
                                binding.txtParamUnitBmi.setText("kg")
                            }else{
                                binding.edtInputValueBmi.setText(
                                    CalculateParameters.convertKgToLbs(
                                        weight
                                    )
                                )
                                binding.txtParamUnitBmi.setText("lbs")
                            }
                        }
                    },"Weight", data)
                    heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    heightWeightDialog.show()
                }
            }

            /*if(parameter.parameterCode.equals("WEIGHT")){
                binding.edtInputValue.isFocusable = false
                binding.edtInputValue.isCursorVisible = false
                binding.edtInputValue.isFocusableInTouchMode = false
                binding.edtInputValue.setOnClickListener {
                    val data = ParameterDataModel()
                    data.title = "Weight"
                    data.value = " - - "
                    if (dataList.get(adapterPosition).parameterVal.isNullOrEmpty()) {
                        data.finalValue = "0"
                    }else{
                        data.finalValue = dataList.get(adapterPosition).parameterVal!!
                    }
                    data.unit = "Kg"
                    data.code = "WEIGHT"
                    val heightWeightDialog = HeightWeightDialog(binding.edtInputValue.context,object:HeightWeightDialog.OnDialogValueListener {
                        override fun onDialogValueListener(
                            dialogType: String,
                            height: String,
                            weight: String,
                            unit: String,
                            visibleValue: String
                        ) {
                            viewModel.updateUserPreference(unit)
                            binding.edtInputValue.setText(weight)
                        }
                    },"Weight", data)
                    heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    heightWeightDialog.show()
                }
            }*/
        }

        private fun getHint(parameter: ParameterListModel.InputParameterModel): String {

            if (parameter.maxPermissibleValue.isNullOrEmpty() || parameter.minPermissibleValue.isNullOrEmpty()){
                return ""
            }else {
                return parameter.minPermissibleValue
                    .toString() + " - " + parameter.maxPermissibleValue
            }
        }

    }

    private fun calculateBMI(heightVal: Double, weightVal: Double): String {
        var bmi = 0.0
        var bmiAsString = ""
        var height: String? = ""
        var weight: String? = ""
        try {
            try {
                height = heightVal.toString()
                weight = weightVal.toString()
                val weightValue = weight.toFloat()
                val heightValue = height.toFloat()
                if (weightValue >= Constants.WEIGHT_MIN_METRIC && weightValue <= Constants.WEIGHT_MAX_METRIC && heightValue >= Constants.HEIGHT_MIN && heightValue <= Constants.HEIGHT_MAX) {
                    bmi = (weightValue / (heightValue * heightValue) * 100 * 100).toDouble()
                    bmiAsString = DecimalFormat("##.##").format(bmi).toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            height = null
            weight = null
        }
        return bmiAsString
    }

    private fun calculateWHR(waistVal: Double, hipVal: Double): String {
        var waist: String? = ""
        var hip: String? = ""
        var whr = 0f
        var whrAsString = ""
        try {
            waist = waistVal.toString()
            hip = hipVal.toString()
            if (waist.isNullOrEmpty()) {
                return ""
            } else if (hip.isNullOrEmpty()) {
                return ""
            } else {
                try {
                    val waistValue = waist.toFloat()
                    val hipValue = hip.toFloat()
                    if (waistValue >= 63.5 && waistValue <= 165.1 && hipValue >= 63.5 && hipValue <= 165.1) {
                        whr = waistValue / hipValue
                        val nm = NumberFormat.getNumberInstance()
                        //whrAsString=String.valueOf(whr);
                        //whrAsString=(nm.format(whr));
                        whrAsString = DecimalFormat("##.##").format(whr.toDouble()).toString()
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            waist = null
            hip = null
        }
        return whrAsString
    }

        var filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 ]*")
                        .matcher(
                            source[i].toString()
                        ).matches()
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        private fun getRandomColor(position: Int): Int {
            return color[position % 7]
        }

        var color = intArrayOf(
            R.color.vivant_bright_sky_blue,
            R.color.vivant_watermelon,
            R.color.vivant_orange_yellow,
            R.color.vivant_bright_blue,
            R.color.vivant_soft_pink,
            R.color.vivant_nasty_green,
            R.color.vivant_dusky_blue
        )

    private fun updateBMIParameter() {
        try {
            var height = 0.0
            var weight = 0.0
            var bmiPosition = -1
            for (i in dataList.indices) {
                if (dataList.get(i).parameterCode.equals("HEIGHT",true)) {
                    if (!dataList.get(i).parameterVal.isNullOrEmpty() && dataList.size>0) {
                        height = dataList.get(i).parameterVal!!.toDouble()
                    }
                }
                if (dataList.get(i).parameterCode.equals("WEIGHT",true)) {
                    if (!dataList.get(i).parameterVal.isNullOrEmpty() && dataList.size>0) {
                        weight = dataList.get(i).parameterVal!!.toDouble()
                    }
                }
                if (dataList.get(i).parameterCode.equals("BMI",true)) {
                    bmiPosition = i
                }
            }
            if (height != 0.0 && weight != 0.0 && bmiPosition != -1) {
                val bmi = calculateBMI(height, weight)
                dataList.get(bmiPosition).parameterVal =bmi
                if (edtBMI != null) {
                    edtBMI!!.setText(bmi)
                    val observation: String = TrackParameterHelper.getBMIObservation(bmi)
                    if (!observation.isNullOrEmpty()) {
                        edtBMI!!.setTextColor(edtBMI!!.context.resources.getColor(TrackParameterHelper.getObservationColor(observation,"BMI")))


                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun updateWHRParameter() = try {
        var waist = 0.0
        var hip = 0.0
        var whrPosition = -1
        for (i in dataList.indices) {
            if (dataList.get(i).parameterCode.equals("WAIST",true)) {
                if (!dataList.get(i).parameterVal.isNullOrEmpty()) {
                    waist = dataList.get(i).parameterVal!!.toDouble()
                }
            }
            if (dataList.get(i).parameterCode.equals("HIP",true)) {
                if (!dataList.get(i).parameterVal.isNullOrEmpty() && dataList.get(i).parameterVal!!.toDouble() != 0.0) {
                    hip = dataList.get(i).parameterVal!!.toDouble()
                }
            }
            if (dataList.get(i).parameterCode.equals("WHR")) {
                whrPosition = i
            }
        }
        if (waist != 0.0 && hip != 0.0 && whrPosition != -1) {
            val whr = calculateWHR(waist, hip)
            dataList.get(whrPosition).parameterVal = whr
            if (edtWHR != null) {
                edtWHR!!.setText(whr)
                val observation: String = TrackParameterHelper.getWHRObservation(whr, 1)
                if (!observation.isNullOrEmpty()) {
                    edtWHR!!.setTextColor(
                        edtWHR!!.context.resources.getColor(
                            TrackParameterHelper.getObservationColor(
                                observation,
                                "WHR"
                            )
                        )
                    )
                }else{}
            }else{}
        }else{}
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

    /**
     * Validates Fields
     *
     * @param list
     * @return
     */
    fun validateFields(list: List<ParameterListModel.InputParameterModel>): Boolean {
        var isValid = false
        var isBP = false
        var systolic = 0.0
        var diastolic = 0.0
        var counter = 0
        try {
            for (param in list) {
                if (!TrackParameterHelper.isNullOrEmptyOrZero(param.maxPermissibleValue) && !TrackParameterHelper.isNullOrEmptyOrZero(param.minPermissibleValue)
                ) {
                    if (!TrackParameterHelper.isNullOrEmptyOrZero(param.parameterVal)) {
                        try {
                            var minVal = 0.0
                            var maxVal = 0.0
                            var paramVal = 0.0
                            minVal = param.minPermissibleValue!!.toDouble()
                            maxVal = param.maxPermissibleValue!!.toDouble()
                            paramVal = param.parameterVal!!.toDouble()
                            if (param.parameterCode.equals("BP_SYS",true)) {
                                isBP = true
                                systolic = paramVal
                            } else if (param.parameterCode.equals("BP_DIA",true)) {
                                isBP = true
                                diastolic = paramVal
                            }
                            if (paramVal < minVal || paramVal > maxVal) {
                                isValid = false
                                validationMassage = param.description
                                    .toString() + " value should be between " + param.minPermissibleValue + " to " + param.maxPermissibleValue
                                break
                            } else {
                                counter++
                                isValid = true
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                } else if (param.parameterType.equals("text",true)) {
                    isValid = true
                }
            }
            if (isValid && isBP) {
                if (systolic < diastolic) {
                    isValid = false
                    validationMassage = "diastolic value should be less than systolic value."
                } else if (systolic == 0.0) {
                    validationMassage = "Please enter systolic value"
                    isValid = false
                } else if (diastolic == 0.0) {
                    validationMassage = "Please enter diastolic value"
                    isValid = false
                }
            } else if (profileCode.equals("BMI", ignoreCase = true) && counter != 2 && isValid) {
                validationMassage = "Please enter all BMI profile fields."
                isValid = false
            } else if (profileCode.equals("WHR", ignoreCase = true) && counter != 2 && isValid) {
                validationMassage = "Please enter all WHR profile fields."
                isValid = false
            } else if (counter == 0 && validationMassage.equals("", ignoreCase = true)) {
//                if (!profileCode.equalsIgnoreCase("BMI") && !profileCode.equalsIgnoreCase("WHR") && !profileCode.equalsIgnoreCase("BLOODPRESSURE")) {
                validationMassage = "Please enter value."
                //                }else {
//                    validationMassage = "";
//                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            isValid = false
        }
        return isValid
    }

    fun getUpdatedParamList(): ArrayList<ParameterListModel.InputParameterModel>? {
        return dataList as ArrayList<ParameterListModel.InputParameterModel>?
    }
}