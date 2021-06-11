package com.caressa.medication_tracker.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities

import com.caressa.medication_tracker.R

class CounterView : ConstraintLayout, View.OnClickListener, OnEditorActionListener,
    TextWatcher {
    private var unit = ""
    private var valueToAddSubctract = 0.0
    private var mDecimalPlaces = 2
    private var minRange = 0.0
    private var maxRange = 0.0
    private var mIsNonDecimal = false
    private var mIsText = false
    var mOnConterSubmitListner: OnCounterSubmitListener? = null
    var mOnEditTextChangeListener: OnEditTextChangeListener? = null
    private var mContext: Context
    private var edtValue: EditText? = null
    private var txtUnit: TextView? = null
    private var imgMinus: ImageView? = null
    private var imgPlus: ImageView? = null
    private val appColorHelper = AppColorHelper.instance!!

    constructor(context: Context) : super(context) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initLayout()
    }

    private fun initLayout() {
        val rootView = View.inflate(mContext, R.layout.counter_view, this)
        edtValue = rootView.findViewById<View>(R.id.edt_value) as EditText
        txtUnit = rootView.findViewById<View>(R.id.txt_unit) as TextView
        imgMinus = rootView.findViewById<View>(R.id.img_minus) as ImageView
        imgPlus = rootView.findViewById<View>(R.id.img_plus) as ImageView

        imgMinus!!.setOnClickListener(this)
        imgPlus!!.setOnClickListener(this)
        edtValue!!.setOnEditorActionListener(this)
        edtValue!!.keyListener = DigitsKeyListener.getInstance(true, true)
        edtValue!!.addTextChangedListener(this)
    }

    fun setUnit(unit: String) {
        this.unit = unit
        txtUnit!!.text = unit
    }

    fun setValueToAddSubctract(value: Double) {
        valueToAddSubctract = value
    }

    fun setDecimalPlaces(decimalPlaces: Int) {
        mDecimalPlaces = decimalPlaces
        if (decimalPlaces > 0) {
            mIsNonDecimal = false
        }
        setFilters()
    }

    fun setAsNonDecimal(isNonDecimal: Boolean) {
        mIsNonDecimal = isNonDecimal
        if (isNonDecimal) {
            mDecimalPlaces = 0
        }
        setFilters()
    }

    fun setAsTextField(isText: Boolean) {
        mIsText = isText
        setFilters()
    }

    fun setFilters() {
        if (mIsNonDecimal == false && mIsText == false) {
            edtValue!!.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(mDecimalPlaces))
        } else if (mIsNonDecimal == true && mIsText == false) {
            edtValue!!.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(0))
        }
    }

    fun disableOnClickButtons() {
        imgMinus!!.setOnClickListener(null)
        imgPlus!!.setOnClickListener(null)
    }

    override fun onClick(view: View) {
        val viewId = view.id
        if (viewId == R.id.img_minus) {
            decrementValue()
        } else if (viewId == R.id.img_plus) {
            incrementValue()
        }
    }

    override fun onEditorAction(textView: TextView, actionId: Int, keyEvent: KeyEvent): Boolean {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (mOnConterSubmitListner != null) {
                mOnConterSubmitListner!!.onCounterSubmit()
            }
        }
        return false
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun afterTextChanged(editable: Editable) {
        if (mOnConterSubmitListner != null) {
            mOnConterSubmitListner!!.onCounterSubmit()
        }
    }

    private fun incrementValue() {
        try {
            val counterValue = value
            if (mIsNonDecimal) {
                if (counterValue >= maxRange) {
                    edtValue!!.setText(maxRange.toInt().toString())
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange(maxRange.toInt().toString())
                    }
                } else if (counterValue < minRange) {
                    edtValue!!.setText(minRange.toInt().toString())
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange(minRange.toInt().toString())
                    }
                } else {
                    edtValue!!.setText((Utilities.roundOffPrecision(counterValue,2) + valueToAddSubctract).toInt().toString())
                    //edt_value.setSelection(edt_value.getText().length());
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange(
                            (Utilities.roundOffPrecision(counterValue,2) + valueToAddSubctract).toInt().toString())
                    }
                }
            } else {
                if (counterValue >= maxRange) {
                    edtValue!!.setText(maxRange.toString())
                    //edt_value.setSelection(edt_value.getText().length());
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange(maxRange.toString())
                    }
                } else if (counterValue < minRange) {
                    edtValue!!.setText(minRange.toString())
                    //edt_value.setSelection(edt_value.getText().length());
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange(minRange.toString())
                    }
                } else {
                    edtValue!!.setText(java.lang.String.valueOf(Utilities.roundOffPrecision(counterValue, 2) + valueToAddSubctract))
                    //edt_value.setSelection(edt_value.getText().length());
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange(java.lang.String.valueOf(Utilities.roundOffPrecision(counterValue, 2) + valueToAddSubctract))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun decrementValue() {
        try {
            val counterValue = value
            if (counterValue > 0.50) {
                if (mIsNonDecimal) {
                    if (counterValue > 1) {
                        edtValue!!.setText((Utilities.roundOffPrecision(counterValue, 2) - valueToAddSubctract).toInt().toString())
                    }
                    //edt_value.setSelection(edt_value.getText().length());
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange((Utilities.roundOffPrecision(counterValue, 2) - valueToAddSubctract).toInt().toString())
                    }
                } else {
                    edtValue!!.setText(java.lang.String.valueOf(Utilities.roundOffPrecision(counterValue, 2) - valueToAddSubctract))
                    //edt_value.setSelection(edt_value.getText().length());
                    if (mOnEditTextChangeListener != null) {
                        mOnEditTextChangeListener!!.onTextChange((Utilities.roundOffPrecision(counterValue, 2) - valueToAddSubctract).toString())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val value: Double
        get() {
            val counterValue = edtValue!!.text.toString().trim { it <= ' ' }
            try {
                return if (!Utilities.isNullOrEmpty(counterValue)) {
                    Utilities.roundOffPrecision(counterValue.toDouble(), 2)
                } else {
                    0.0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0.0
        }

    fun setValue(strValue: String) {
        if (!Utilities.isNullOrEmpty(strValue)) {
            if (mIsNonDecimal) {
                edtValue!!.setText(strValue)
                edtValue!!.setSelection(edtValue!!.text.length)
            } else {
                edtValue!!.setText( Utilities.round(strValue.toDouble(), 2).toString() )
                edtValue!!.setSelection(edtValue!!.text.length)
            }
        }
    }

    fun setOnCounterSubmitListener(onConterSubmitListner: OnCounterSubmitListener?) {
        mOnConterSubmitListner = onConterSubmitListner
    }

    fun setOnEditTextChangeListener(onEditTextChangeListener: OnEditTextChangeListener?) {
        mOnEditTextChangeListener = onEditTextChangeListener
    }

    fun setInputRange(start: Double, end: Double) {
        minRange = start
        maxRange = end
        /*        InputFilterMinMax filter = new InputFilterMinMax(0, 1000) {
        };
        edt_value.setFilters(new InputFilter[]{filter});*/
    }

    fun setEditTextColor(color: Int) {
        edtValue!!.setTextColor(color)
    }

    fun setEmsEditText(ems: Int) {
        edtValue!!.setEms(ems)
    }

    interface OnCounterSubmitListener {
        fun onCounterSubmit()
    }

    interface OnEditTextChangeListener {
        fun onTextChange(value: String?)
    }

    inner class InputFilterMinMax : InputFilter {
        private var min: Double
        private var max: Double

        constructor(min: Double, max: Double) {
            this.min = min
            this.max = max
        }

        constructor(min: String, max: String) {
            this.min = min.toDouble()
            this.max = max.toDouble()
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            try {
                val input = (dest.toString() + source.toString()).toInt()
                if (isInRange(min, max, input.toDouble())) return null
            } catch (nfe: NumberFormatException) {
            }
            return ""
        }

        private fun isInRange(a: Double, b: Double, c: Double): Boolean {
            return if (b > a) c >= a && c <= b else c >= b && c <= a
        }
    }

    inner class DecimalDigitsInputFilter

        (private val decimalDigits: Int) : InputFilter {

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            println(" source data pressed $source")
            if (decimalDigits == 0) {
                if (source == ".") {
                    return ""
                }
            }
            var dotPos = -1
            val len = dest.length
            for (i in 0 until len) {
                val c = dest[i]
                if (c == '.' || c == ',') {
                    dotPos = i
                    break
                }
            }
            if (dotPos >= 0) {

                // protects against many dots
                if (source == "." || source == ",") {
                    return ""
                }

                // if the text is entered before the dot
                if (dend <= dotPos) {
                    return null
                }
                if (len - dotPos > decimalDigits) {
                    return ""
                }
            }
            try {
                val input = (dest.toString() + source.toString()).toDouble()
                if (input > 1000.0) return ""
            } catch (nfe: NumberFormatException) {
            }
            return null
        }

    }
}