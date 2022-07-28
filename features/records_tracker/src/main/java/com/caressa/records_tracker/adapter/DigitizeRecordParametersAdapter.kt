package com.caressa.records_tracker.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.Utilities
import com.caressa.model.shr.HealthDataParameter
import com.caressa.records_tracker.R
import com.caressa.records_tracker.databinding.ItemDigitizeHealthParameterBinding
import com.caressa.records_tracker.ui.DigitizeRecordFragment
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import timber.log.Timber

class DigitizeRecordParametersAdapter( val list:List<HealthDataParameter> ,val viewModel: HealthRecordsViewModel,
                                       val context: Context , val fragment : DigitizeRecordFragment ) :
    RecyclerView.Adapter<DigitizeRecordParametersAdapter.DigitizeRecordViewHolder>() {

     var lastUpdatedUnitItemPosition = 0;

     var healthParameterList: MutableList<HealthDataParameter> = list.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DigitizeRecordViewHolder =
        DigitizeRecordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_digitize_health_parameter, parent, false))

    override fun getItemCount(): Int = healthParameterList.size

    override fun onBindViewHolder(holder: DigitizeRecordViewHolder, position: Int) {
        val parameter = healthParameterList[position]
        holder.bindTo(parameter,viewModel)
    }

   inner class DigitizeRecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {

       private val binding = ItemDigitizeHealthParameterBinding.bind(view)

       val imgRemove = binding.imgRemoveParameter
       val parameterName = binding.txtParameterName
       val parameterValue = binding.edtParamValue
       val parameterUnit = binding.edtParamUnit
       val tilEdtParameterValue = binding.tilEdtParamValue
       val tilEdtParameterUnit = binding.tilEdtParamUnit

        fun bindTo(healthDataParameter : HealthDataParameter, viewModel: HealthRecordsViewModel) {
            binding.healthDataParameter = healthDataParameter
            binding.viewmodel = viewModel
            val maxVal = healthDataParameter.maxPermissibleValue
            val minVal = healthDataParameter.minPermissibleValue
            val observation = healthDataParameter.observation
            val unit = healthDataParameter.unit

            parameterName.text = healthDataParameter.name
            parameterValue.setText(observation)
            parameterUnit.setText(unit)
            Timber.i("bindTo==> "+healthDataParameter.name+" :: "+healthDataParameter.unitStatus)
            Timber.i("bindTo=> "+ observation+" :: "+ unit)
            try {
                if(healthDataParameter.unitStatus){
                    tilEdtParameterUnit.error = null
                }else{
                    tilEdtParameterUnit.error = context.resources.getString(R.string.ERROR_UNIT_NOT_CORRECT)
                }

                if (!maxVal.isNullOrEmpty() && !minVal.isNullOrEmpty() && !Utilities.isNullOrEmpty(healthDataParameter.observation!!.toString()) ){
                    if (!maxVal.equals("nan", ignoreCase = true) && !minVal.equals("nan", ignoreCase = true)) {
                        if(healthDataParameter.observation!!.toString().toDouble() > maxVal.toDouble() || healthDataParameter.observation!!.toString().toDouble() < minVal.toDouble()){
                            imgRemove.visibility = View.VISIBLE
                            parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_watermelon))
                            tilEdtParameterValue.error = context.resources.getString(R.string.VALUE_MUST_BE_BETWEEN) + " " + minVal + " " + context.resources.getString(R.string.AND) + " "+ maxVal+ "."
                        }else{
                            parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_nasty_green))
                            tilEdtParameterValue.error = null
                            tilEdtParameterValue.isErrorEnabled = false
                            imgRemove.visibility = View.INVISIBLE
                        }
                    }
                }else if (maxVal.isNullOrEmpty() && minVal.isNullOrEmpty() ){
                    if(!Utilities.isNullOrEmpty(healthDataParameter.observation!!.toString())){
                        imgRemove.visibility = View.INVISIBLE
                    }else{
                        imgRemove.visibility = View.VISIBLE
                    }
                }else{
                    imgRemove.visibility = View.INVISIBLE
                }

                if (tilEdtParameterUnit.getError() == null && tilEdtParameterValue.getError() == null) {
                    imgRemove.visibility = View.INVISIBLE
                } else {
                    imgRemove.visibility = View.VISIBLE
                }

                if (!Utilities.isNullOrEmpty(maxVal) && !Utilities.isNullOrEmpty(minVal) && !Utilities.isNullOrEmpty(observation)) {
                    if (!maxVal.equals("nan", ignoreCase = true) && !minVal.equals("nan", ignoreCase = true)) {
                        if (!healthDataParameter.unitStatus || observation.toDouble() > maxVal.toDouble()
                            || observation.toDouble() < minVal.toDouble() ) {
                            imgRemove.visibility = View.VISIBLE
                        } else {
                            imgRemove.visibility = View.INVISIBLE
                        }
                    } else {
                        imgRemove.visibility = View.VISIBLE
                    }
                } else {
                    imgRemove.visibility = View.VISIBLE
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }

       init {
           imgRemove.setOnClickListener {
               try {
                   healthParameterList?.removeAt(adapterPosition)
                   notifyItemRemoved(adapterPosition)
               }catch (e: Exception){
                   e.printStackTrace()
               }
           }

           parameterValue.addTextChangedListener( object : TextWatcher{

               override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   try {
                       Timber.i("CounterValue=> "+s)
                       val maxVal = healthParameterList.get(adapterPosition).maxPermissibleValue
                       val minVal = healthParameterList.get(adapterPosition).minPermissibleValue

                       if (!maxVal.isNullOrEmpty() && !minVal.isNullOrEmpty() && !Utilities.isNullOrEmpty(s!!.toString()) ){
                           if (!maxVal.equals("nan", ignoreCase = true) && !minVal.equals("nan", ignoreCase = true)) {
                               if(s!!.toString().toDouble() > maxVal.toDouble() || s!!.toString().toDouble() < minVal.toDouble()){
                                   parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_watermelon))
                                   tilEdtParameterValue.error = context.resources.getString(R.string.VALUE_MUST_BE_BETWEEN) +" " + minVal + " " + context.resources.getString(R.string.AND)+ " "  + maxVal +"."
                                   imgRemove.visibility = View.VISIBLE
                               }else{
                                   parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_nasty_green))
                                   tilEdtParameterValue.error = null
                                   tilEdtParameterValue.isErrorEnabled = false
                                   imgRemove.visibility = View.INVISIBLE
                               }
                           }
                       }else if (maxVal.isNullOrEmpty() && minVal.isNullOrEmpty() ){
                           if(!Utilities.isNullOrEmpty(s!!.toString())){
                               imgRemove.visibility = View.INVISIBLE
                           }else{
                               parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_watermelon))
                               tilEdtParameterValue.error = context.resources.getString(R.string.ERROR_ENTER_VALID_INPUT)
                               imgRemove.visibility = View.VISIBLE
                           }
                       } else if (Utilities.isNullOrEmpty(s.toString())) {
                           imgRemove.visibility = View.VISIBLE
                           parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_watermelon))
                           tilEdtParameterValue.error = context.resources.getString(R.string.ERROR_VALUE_NOT_CORRECT)
                       } else {
                           imgRemove.visibility = View.INVISIBLE
                           parameterValue.setTextColor(context.getResources().getColor(R.color.vivant_nasty_green))
                           tilEdtParameterValue.error = null
                           tilEdtParameterValue.isErrorEnabled = false
                       }
                       if (tilEdtParameterUnit.getError() != null || tilEdtParameterValue.getError() != null) {
                           imgRemove.visibility = View.INVISIBLE
                       }
                   }catch (e: Exception){
                       e.printStackTrace()
                   }
               }
               override fun afterTextChanged(s: Editable?) {
                    Timber.i("Observation afterTextChanged=> "+s.toString())
                   healthParameterList.get(adapterPosition).observation = s.toString()
               }
               override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

               }
           })

           parameterUnit.addTextChangedListener( object : TextWatcher {
               override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   if (s!!.toString().isEmpty()) {
                       parameterUnit.setTextColor(context.getResources().getColor(R.color.vivant_watermelon))
                       tilEdtParameterUnit.error = context.resources.getString(R.string.ERROR_UNIT_NOT_CORRECT)
                   } else {
                       parameterUnit.setTextColor(context.getResources().getColor(R.color.vivant_nasty_green))
                       tilEdtParameterUnit.error = null
                       tilEdtParameterUnit.isErrorEnabled = false
                   }
               }
               override fun afterTextChanged(s: Editable?) {
                   Timber.i("Unit afterTextChanged=> "+s.toString())
                   healthParameterList[adapterPosition].unit = s.toString()
               }
               override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                   //errorUnit.visibility = View.GONE
               }
           })

           parameterUnit.setOnFocusChangeListener { v, hasFocus ->
               if ( !hasFocus ) {
                   val paramCode = list[adapterPosition].paramCode
                   val unit = parameterUnit.text.toString()
                   healthParameterList.get(adapterPosition).unit = parameterUnit.text.toString()
                   viewModel.callCheckUnitExistApi(true,paramCode,unit)
                   lastUpdatedUnitItemPosition = adapterPosition
                   //notifyItemChanged(adapterPosition)
               }
           }
           /*           viewModel.ocrUnitExist.observe( fragment , Observer {
               if ( it != null ) {
                   Timber.i("OcrUnitExist----->"+it.isExist)
                   if ( !it.isExist ) {
                       //notifyItemChanged(lastUpdatedUnitItemPosition)
                       lastUpdatedUnitItemPosition = adapterPosition
                       Timber.i("lastUpdatedUnitItemPosition in Observer----->"+lastUpdatedUnitItemPosition)
                   }
               }
           } )*/
       }

    }

    fun itemViewRefresh( isExist :Boolean ) {
        healthParameterList.get(lastUpdatedUnitItemPosition).unitStatus = isExist
        this.notifyItemChanged(lastUpdatedUnitItemPosition)
        Timber.i("lastUpdatedUnitItemPosition----->"+healthParameterList.get(lastUpdatedUnitItemPosition).unitStatus)
    }

    fun validateData() : Boolean {
        var isValid : Boolean = false
        try {
            for (item in healthParameterList) {

                val maxVal = item.maxPermissibleValue
                val minVal = item.minPermissibleValue
                if (!maxVal.isNullOrEmpty() && !minVal.isNullOrEmpty() && !Utilities.isNullOrEmpty(item.observation!!.toString()) ){
                    if (!maxVal.equals("nan", ignoreCase = true) && !minVal.equals("nan", ignoreCase = true)) {
                        if(item.observation!!.toString().toDouble() > maxVal.toDouble() || item.observation!!.toString().toDouble() < minVal.toDouble()) {
                            isValid = false
                        }else{
                            isValid = true
                        }
                    } else {
                        isValid = false
                    }
                }else if (maxVal.isNullOrEmpty() && minVal.isNullOrEmpty() ){
                    if(!Utilities.isNullOrEmpty(item.observation!!.toString())){
                        isValid = true
                    }else{
                        isValid = false
                    }
                }else{
                    isValid = false
                }

                if (!item.unitStatus) {
                    isValid = false
                }

                if (!isValid)
                {
                    viewModel.showMessage(item.name + context.resources.getString(R.string.ERROR_DOES_NOT_MATCHING_REQUIREMENTS))
                    break
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return isValid
    }
}