package com.caressa.common.utils

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.caressa.common.R
import kotlinx.android.synthetic.main.dialog_vital_parameter.*
import org.koin.standalone.KoinComponent
import timber.log.Timber

class HeightWeightDialog(
    context: Context, listener: OnDialogValueListener, dialogType: String,
    parameterDataModel: ParameterDataModel
) : Dialog(context), KoinComponent {

    private val appColorHelper = AppColorHelper.instance!!
    private var onDialogValueListener: OnDialogValueListener? = null
    private var dialogType = "Height"
    private var parameterDataModel: ParameterDataModel? = null
    private var paramFt: VitalParameter? = null
    private var paramCm: VitalParameter? = null
    private var paramLbs: VitalParameter? = null
    private var paramKg: VitalParameter? = null
    private var pickerSpeed = 8000
    private var height = 0
    private var heightInch = 0
    private var weight = 0.0

    init {
        this.onDialogValueListener = listener
        this.dialogType = dialogType
        this.parameterDataModel = parameterDataModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_vital_parameter)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        init()
    }

    fun init() {
        try {
            paramFt =
                Utilities.getVitalParameterData(context.resources.getString(R.string.FT), context)
            paramCm =
                Utilities.getVitalParameterData(context.resources.getString(R.string.CM), context)
            paramLbs =
                Utilities.getVitalParameterData(context.resources.getString(R.string.LBS), context)
            paramKg =
                Utilities.getVitalParameterData(context.resources.getString(R.string.KG), context)

            if (dialogType.equals("Height", ignoreCase = true)) {
                lbl_title.text = context.resources.getString(R.string.PICK_YOUR_HEIGHT)
                txt_message.text = context.resources.getString(R.string.ENTER_HEIGHT)
                btn_left.text = context.resources.getString(R.string.FT_IN)
                btn_left.isSelected = true
                btn_right.text = context.resources.getString(R.string.CM)
                txt_unit1.text = paramFt!!.unit
                txt_unit2.text = context.resources.getString(R.string.INCH)
                picker1.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                picker1.wrapSelectorWheel = false
                picker1.minValue = paramFt!!.minRange
                picker1.maxValue = paramFt!!.maxRange
                picker2.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                picker2.wrapSelectorWheel = false
                picker2.minValue = 0
                picker2.maxValue = 11

                if (parameterDataModel!!.unit.equals("Feet/inch", ignoreCase = true)) {
                    layout_picker2.visibility = View.VISIBLE
                    btn_left.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.btn_fill_dialog,
                        null
                    )
                    btn_right.background =
                        ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn_left.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.selectionColor)
                        btn_right.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.deselectionColor)
                    }
                    picker1.value =
                        CalculateParameters.convertCmToFeet(parameterDataModel!!.finalValue)
                    picker2.value =
                        CalculateParameters.convertCmToInch(parameterDataModel!!.finalValue)
                    btn_left.isSelected = true
                    btn_right.isSelected = false
                    btn_left.setTextColor(ContextCompat.getColor(context, R.color.white))
                    btn_right.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                    picker1.value =
                        CalculateParameters.convertCmToFeet(parameterDataModel!!.finalValue)
                    picker2.value =
                        CalculateParameters.convertCmToInch(parameterDataModel!!.finalValue)
                } else if (parameterDataModel!!.unit.equals(
                        context.resources.getString(R.string.CM),
                        ignoreCase = true
                    )
                ) {
                    layout_picker2.visibility = View.GONE
                    btn_left.background =
                        ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                    btn_right.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.btn_fill_dialog,
                        null
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn_left.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.deselectionColor)
                        btn_right.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.selectionColor)
                    }
                    btn_left.isSelected = false
                    btn_right.isSelected = true

                    btn_left.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                    btn_right.setTextColor(ContextCompat.getColor(context, R.color.white))
                    txt_unit1.text = paramCm!!.unit
                    picker1.minValue = paramCm!!.minRange
                    picker1.maxValue = paramCm!!.maxRange
                    picker1.value = parameterDataModel!!.finalValue.toDouble().toInt()
                }
            } else {
                lbl_title.text = context.resources.getString(R.string.CURRENT_WEIGHT)
                txt_message.text = context.resources.getString(R.string.ENTER_WEIGHT)
                btn_left.text = context.resources.getString(R.string.LBS)
                btn_right.text = context.resources.getString(R.string.KG)
                picker1.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                picker1.wrapSelectorWheel = false
                picker2.setOnLongPressUpdateInterval(pickerSpeed.toLong())
                picker2.wrapSelectorWheel = false

                if (parameterDataModel!!.unit.equals(
                        context.resources.getString(R.string.LBS),
                        ignoreCase = true
                    )
                ) {
                    layout_picker2.visibility = View.GONE
                    btn_left.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.btn_fill_dialog,
                        null
                    )
                    btn_right.background =
                        ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn_left.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.selectionColor)
                        btn_right.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.deselectionColor)
                    }
                    btn_left.isSelected = true
                    btn_right.isSelected = false
                    txt_unit1.text = paramLbs!!.unit
                    picker1.minValue = paramLbs!!.minRange
                    picker1.maxValue = paramLbs!!.maxRange
                    val wt = CalculateParameters.convertKgToLbs(parameterDataModel!!.finalValue)
                        .toDouble()
                    Timber.i("Converted_Wt=>$wt")
                    picker1.value = wt.toInt()
                } else if (parameterDataModel!!.unit.equals(
                        context.resources.getString(R.string.KG),
                        ignoreCase = true
                    )
                ) {
                    layout_picker2.visibility = View.VISIBLE
                    btn_left.background =
                        ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                    btn_right.background = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.btn_fill_dialog,
                        null
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btn_left.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.deselectionColor)
                        btn_right.backgroundTintList =
                            ColorStateList.valueOf(appColorHelper.selectionColor)
                    }
                    btn_left.isSelected = false
                    btn_right.isSelected = true
                    txt_unit1.text = "."
                    txt_unit2.text = paramKg!!.unit
                    picker1.minValue = paramKg!!.minRange
                    picker1.maxValue = paramKg!!.maxRange
                    picker2.minValue = 0
                    picker2.maxValue = 9
                    val `val`: Double = parameterDataModel!!.finalValue.toDouble()
                    val indexOfDecimal = `val`.toString().indexOf(".")
                    picker1.value = `val`.toString().substring(0, indexOfDecimal).toInt()
                    picker2.value = `val`.toString().substring(indexOfDecimal + 1).toInt()
                }
            }
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        btn_left.setOnClickListener {

            if (dialogType.equals("Height", ignoreCase = true)) {
                picker1.displayedValues = null
                btn_left.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.btn_fill_dialog, null)
                btn_right.background =
                    ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn_left.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.selectionColor)
                    btn_right.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.deselectionColor)
                }
                btn_left.setTextColor(ContextCompat.getColor(context, R.color.white))
                btn_right.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                layout_picker2.visibility = View.VISIBLE
                txt_unit1.text = paramFt!!.unit
                picker1.minValue = paramFt!!.minRange
                picker1.maxValue = paramFt!!.maxRange
                btn_left.isSelected = true
                btn_right.isSelected = false
                picker1.value = CalculateParameters.convertCmToFeet(parameterDataModel!!.finalValue)
                picker2.value = CalculateParameters.convertCmToInch(parameterDataModel!!.finalValue)
                height = picker1.value
            } else {
                layout_picker2.visibility = View.GONE
                btn_left.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.btn_fill_dialog, null)
                btn_right.background =
                    ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn_left.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.selectionColor)
                    btn_right.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.deselectionColor)
                }
                btn_left.setTextColor(ContextCompat.getColor(context, R.color.white))
                btn_right.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                txt_unit1.text = paramLbs!!.unit
                picker1.minValue = paramLbs!!.minRange
                picker1.maxValue = paramLbs!!.maxRange
                btn_left.isSelected = true
                btn_right.isSelected = false
                val wt =
                    CalculateParameters.convertKgToLbs(parameterDataModel!!.finalValue).toDouble()
                        .toInt()
                picker1.value = wt
                weight = (picker1.value.toString() + "." + picker2.value).toDouble()
            }
        }

        btn_right.setOnClickListener {

            if (dialogType.equals("Height", ignoreCase = true)) {
                btn_left.background =
                    ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                btn_right.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.btn_fill_dialog, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn_left.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.deselectionColor)
                    btn_right.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.selectionColor)
                }
                btn_left.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                btn_right.setTextColor(ContextCompat.getColor(context, R.color.white))
                layout_picker2.visibility = View.GONE
                txt_unit1.text = paramCm!!.unit
                picker1.minValue = paramCm!!.minRange
                picker1.maxValue = paramCm!!.maxRange
                btn_left.isSelected = false
                btn_right.isSelected = true
                picker1.value = parameterDataModel!!.finalValue.toDouble().toInt()
                height = picker1.value
                heightInch = picker2.value
            } else {
                picker1.displayedValues = null
                layout_picker2.visibility = View.VISIBLE
                btn_left.background =
                    ResourcesCompat.getDrawable(context.resources, R.color.transparent, null)
                btn_right.background =
                    ResourcesCompat.getDrawable(context.resources, R.drawable.btn_fill_dialog, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    btn_left.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.deselectionColor)
                    btn_right.backgroundTintList =
                        ColorStateList.valueOf(appColorHelper.selectionColor)
                }
                btn_left.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                btn_right.setTextColor(ContextCompat.getColor(context, R.color.white))
                txt_unit1.text = "."
                txt_unit2.text = paramKg!!.unit
                picker1.minValue = paramKg!!.minRange
                picker1.maxValue = paramKg!!.maxRange
                picker2.minValue = 0
                picker2.maxValue = 9
                btn_left.isSelected = false
                btn_right.isSelected = true
                val `val`: Double = parameterDataModel!!.finalValue.toDouble()
                val indexOfDecimal = `val`.toString().indexOf(".")
                picker1.value = `val`.toString().substring(0, indexOfDecimal).toInt()
                picker2.value = `val`.toString().substring(indexOfDecimal + 1).toInt()
                weight = (picker1.value.toString() + "." + picker2.value).toDouble()
            }
        }

        btn_save.setOnClickListener {
            try {
                Utilities.hideKeyboard(btn_save, context)
                if (dialogType.equals("Height", ignoreCase = true)) {
                    height = picker1.value
                    heightInch = picker2.value
                    if (height in 4..8) {
                        onDialogValueListener!!.onDialogValueListener(
                            "Height",
                            CalculateParameters.convertFeetInchToCm(
                                height.toString(),
                                heightInch.toString()
                            ).toString(),
                            "0",
                            "Feet/inch",
                            "$height'$heightInch''"
                        )
                    } else {
                        onDialogValueListener!!.onDialogValueListener(
                            "Height",
                            height.toString(),
                            "0",
                            "Cm",
                            height.toString()
                        )
                    }
                } else {
                    if (txt_unit1.text.toString()
                            .contains(context.resources.getString(R.string.LBS))
                    ) {
                        weight = picker1.value.toDouble()
                        val finalValue = CalculateParameters.convertLbsToKg(weight.toString())
                        onDialogValueListener!!.onDialogValueListener(
                            "Weight",
                            "0",
                            finalValue,
                            paramLbs!!.unit,
                            weight.toString()
                        )
                    } else {
                        weight = (picker1.value.toString() + "." + picker2.value).toDouble()
                        onDialogValueListener!!.onDialogValueListener(
                            "Weight",
                            "0",
                            weight.toString(),
                            paramKg!!.unit,
                            weight.toString()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dismiss()
        }

        img_close.setOnClickListener {
            dismiss()
        }

    }

    interface OnDialogValueListener {
        fun onDialogValueListener(
            dialogType: String,
            height: String,
            weight: String,
            unit: String,
            visibleValue: String
        )
    }

}