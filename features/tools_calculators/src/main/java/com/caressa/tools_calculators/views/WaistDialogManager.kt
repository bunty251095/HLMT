package com.caressa.tools_calculators.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import com.caressa.common.utils.CalculateParameters
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.DialogWeistLayoutBinding
import com.caressa.common.utils.ParameterDataModel
import com.caressa.common.utils.Utilities
import com.google.android.material.tabs.TabLayout
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class WaistDialogManager(context: Context, listener: OnDialogValueListener,
                         parameterDataModel: ParameterDataModel
) : Dialog(context) {

    private lateinit var binding : DialogWeistLayoutBinding

    private var onDialogValueListener: OnDialogValueListener? = null
    private var parameterDataModel: ParameterDataModel? = null

    init {
        this.onDialogValueListener = listener
        this.parameterDataModel = parameterDataModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_weist_layout, null, false)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        init()
    }

    private fun init() {
        try {
            //Picker Inch
            binding.pickerWeist1.setOnLongPressUpdateInterval(8000)
            binding.pickerWeist1.wrapSelectorWheel = false
            setPickerExtremeValues(1, 25, 65)

            //Picker Cm
            binding.pickerWeist2.setOnLongPressUpdateInterval(8000)
            binding.pickerWeist2.wrapSelectorWheel = false

            val minCm = CalculateParameters.inchesToCms(25.0)
            val maxCm = CalculateParameters.inchesToCms(65.0)
            setPickerExtremeValues(2, minCm.toInt(), maxCm.toInt())

            if ( !Utilities.isNullOrEmptyOrZero(parameterDataModel!!.finalValue) ) {
                val finalValue = parameterDataModel!!.finalValue.toInt()
                val cmValue = CalculateParameters.inchesToCms(finalValue.toDouble()).toInt()
                binding.pickerWeist1.value = finalValue
                binding.pickerWeist2.value = cmValue
            }

            if (parameterDataModel!!.unit.equals(context.resources.getString(R.string.INCH), ignoreCase = true)) {
                //Timber.e("ValueInch=>$finalValue")
                Objects.requireNonNull(binding.layoutTab.getTabAt(0))!!.select()
                binding.pickerWeist1.visibility = View.VISIBLE
                binding.pickerWeist2.visibility = View.GONE
                binding.txtUnit.text = context.resources.getString(R.string.INCH)
            } else if (parameterDataModel!!.unit.equals(context.resources.getString(R.string.CM), ignoreCase = true)) {
                //Timber.e("ValueCm=>$cmValue")
                Objects.requireNonNull(binding.layoutTab.getTabAt(1))!!.select()
                binding.pickerWeist1.visibility = View.GONE
                binding.pickerWeist2.visibility = View.VISIBLE
                binding.txtUnit.text = context.resources.getString(R.string.CM)
            }

            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                try {
                    if (tab.position == 0) {
                        binding.pickerWeist1.visibility = View.VISIBLE
                        binding.pickerWeist2.visibility = View.GONE
                        binding.txtUnit.text = context.resources.getString(R.string.INCH)
                    } else if (tab.position == 1) {
                        binding.pickerWeist1.visibility = View.GONE
                        binding.pickerWeist2.visibility = View.VISIBLE
                        binding.txtUnit.text = context.resources.getString(R.string.CM)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.btnSaveInput.setOnClickListener {
            if (binding.layoutTab.selectedTabPosition == 0) {
                val selectedValue = ("" + binding.pickerWeist1.value).toDouble()
                val cmValue: Double = CalculateParameters.inchesToCms(selectedValue)
                onDialogValueListener!!.onDialogValueListener(selectedValue.roundToInt().toString(),
                    cmValue.toInt().toString(), binding.txtUnit.text.toString())
            } else {
                val selectedValue = ("" + binding.pickerWeist2.value).toDouble()
                val inchesValue: Double = CalculateParameters.convertCmToInch2("" + selectedValue).toDouble()
                onDialogValueListener!!.onDialogValueListener(inchesValue.roundToInt().toString(),
                    selectedValue.toInt().toString(), binding.txtUnit.text.toString())
            }
            dismiss()
        }

        binding.imgCloseInput.setOnClickListener {
            dismiss()
        }

    }

    private fun setPickerExtremeValues(picker: Int, min: Int, max: Int) {
        if (picker == 1) {
            binding.pickerWeist1.minValue = min
            binding.pickerWeist1.maxValue = max
        } else if (picker == 2) {
            binding.pickerWeist2.minValue = min
            binding.pickerWeist2.maxValue = max
        }
    }

    interface OnDialogValueListener {
        fun onDialogValueListener(inch: String?, centimeter: String?, unit: String?)
    }

}