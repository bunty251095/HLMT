package com.caressa.tools_calculators.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.databinding.DataBindingUtil
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.ActivityHealthConditionDialogBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import timber.log.Timber
import java.util.*

class HealthConditionDialog(context: Context,listener: OnHealthConditionValueListener) : Dialog(context) {

    private lateinit var binding : ActivityHealthConditionDialogBinding
    private  val calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
    private val appColorHelper = AppColorHelper.instance!!
    private var selectionList = ArrayList<String>()

    private var onHealthConditionValueListener: OnHealthConditionValueListener? = null

    init {
        this.onHealthConditionValueListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.activity_health_condition_dialog, null, false)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus

        selectionList = calculatorDataSingleton.healthConditionSelection
        loadPreviousData()
        setClickable()
    }

    private fun loadPreviousData() {
        try {
            Timber.i("LayoutDeselection => ${binding.laySelection.childCount} :: ${binding.laySelection.childCount}" )
            Timber.i("Selection List :: $selectionList")
            val selectedViews = ArrayList<View>()
            for (i in 0 until binding.layDeselection.childCount) {
                val tag: String = binding.layDeselection.getChildAt(i).tag.toString()
                for (str in selectionList) {
                    if (tag.equals(str, ignoreCase = true)) {
                        selectedViews.add(binding.layDeselection.getChildAt(i))
                    }
                }
            }
            for (view in selectedViews) {
                addLayout(view, view.id, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.btnSaveHealthCondition.setOnClickListener {
            if (validate()) {
                Timber.e("Selected--->${selectionList.size}")
                calculatorDataSingleton.healthConditionSelection = selectionList
                onHealthConditionValueListener!!.onHealthConditionValueListener()
                dismiss()
            }
        }

        binding.btnCoronary.setOnClickListener {
            toggleView(it.id)
        }

        binding.btnDiabetes.setOnClickListener {
            toggleView(it.id)
        }

        binding.btnHeart.setOnClickListener {
            toggleView(it.id)
        }

        binding.btnFamily.setOnClickListener {
            toggleView(it.id)
        }

        binding.btnCholestrol.setOnClickListener {
            toggleView(it.id)
        }

        binding.btnStroke.setOnClickListener {
            toggleView(it.id)
        }

        binding.btnKidney.setOnClickListener {
            toggleView(it.id)
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

    }

    private fun validate(): Boolean {
        var isValid = false
        if (binding.laySelection.childCount > 0) {
            isValid = true
            selectionList.clear()
            for (i in 0 until binding.laySelection.childCount) {
                //selectionList.add(""+i)
                selectionList.add(binding.laySelection.getChildAt(i).tag.toString())
            }
        } else {
            Utilities.toastMessageShort(context, "Please choose anyone or close")
        }
        return isValid
    }

    private fun toggleView(id: Int) {
        var isFound = false
        for (i in 0 until binding.laySelection.childCount) {
            if (binding.laySelection.getChildAt(i) != null && binding.laySelection.getChildAt(i).id == id) {
                addLayout(binding.laySelection.getChildAt(i), id, false)
                isFound = true
            }
        }
        if (!isFound) {
            for (i in 0 until binding.layDeselection.childCount) {
                if ( binding.layDeselection.getChildAt(i) != null && binding.layDeselection.getChildAt(i).id == id) {
                    addLayout(binding.layDeselection.getChildAt(i), id, true)
                }
            }
        }
    }

    private fun addLayout(view: View, id: Int, selection: Boolean) {
        if (selection) {
            binding.layDeselection.removeView(view)
            TextViewCompat.setTextAppearance((view as TextView), R.style.ToolsBtnSelection)
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
            setTextViewDrawableColor(view, appColorHelper.selectionColor)
            view.setTextColor(appColorHelper.selectionColor)
            view.tag = view.text

            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.cornerRadius = 10f
            drawable.setStroke(4, appColorHelper.selectionColor)
            drawable.setColor(Color.WHITE)
            view.background = drawable
            binding.laySelection.addView(view)
        } else {
            binding.laySelection.removeView(view)
            TextViewCompat.setTextAppearance((view as TextView), R.style.ToolsBtnDeselection)
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add, 0)
            setTextViewDrawableColor(view, R.color.textViewColor)
            view.setBackgroundResource(R.drawable.button_deselction)
            binding.layDeselection.addView(view)
        }
        binding.laySelection.invalidate()
        binding.layDeselection.invalidate()
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                //drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
            }
        }
    }

    interface OnHealthConditionValueListener {
        fun onHealthConditionValueListener()
    }

}