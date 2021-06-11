package com.caressa.common.utils

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import com.caressa.common.R
import com.caressa.common.databinding.CustomCounterBinding
import timber.log.Timber

/**
 * This class is used for custom counter.
 */
class CustomCounter : LinearLayout, View.OnClickListener, TextView.OnEditorActionListener,
    TextWatcher {

    private lateinit var binding: CustomCounterBinding
    internal var mOnConterSubmitListner: OnConterSubmitListner? = null
    internal var mOnEditTextChangeListener: OnEditTextChangeListener? = null
    private var mContext: Context? = null
    private var mIsNonDecimal = false
    private var mIsText = false
    private var mDecimalPlaces = 1
    internal var minRange: Double = 0.toDouble()
    internal var maxRange: Double = 0.toDouble()

    constructor(context: Context) : super(context) {
        this.mContext = context
        initlayout()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        initlayout()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        this.mContext = context
        initlayout()
    }

    interface OnEditTextChangeListener {
        fun onTextChange(value: String)
    }

    /**
     * gets Value
     */
/*    val value: Double
        get() {
            val counterValue = binding.etCountervalue?.text.toString().trim { it <= ' ' }

            try {
                return if (!Utilities.isNullOrEmpty(counterValue)) {
                    CalculateParameters.roundOffPrecision(java.lang.Double.valueOf(counterValue), 1)
                } else {
                    0.0
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0.0
        }*/

    fun getValue(): Double {
        //    val counterValue = binding.etCountervalue?.text.toString().trim { it <= ' ' }
        val counterValue = binding.etCountervalue?.text.toString().trim { it <= ' ' }
        Timber.i("counterValue" + counterValue)
        try {
            return if (!Utilities.isNullOrEmpty(counterValue)) {
                CalculateParameters.roundOffPrecision(java.lang.Double.valueOf(counterValue), 1)
                // Timber.i("counterValue"+)
            } else {
                0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0.0
    }

    /**
     * initialize Layout
     */
    private fun initlayout() {
        // val rootView = View.inflate(mContext, com.caressa.hra.R.layout.custom_counter, this)
/*        val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CustomCounterBinding.inflate(inflater)*/

        val inflater = LayoutInflater.from(mContext)
        binding = CustomCounterBinding.inflate(inflater, this, true)

        binding.ibCounterminus.setOnClickListener(this)
        binding.ibCounterplus.setOnClickListener(this)
        binding.etCountervalue.setOnEditorActionListener(this)

        binding.etCountervalue?.keyListener = DigitsKeyListener.getInstance(true, true)
        binding.etCountervalue?.addTextChangedListener(this)

        binding.ibCounterminus.setOnClickListener {
            decrementValue()
        }
        binding.ibCounterplus.setOnClickListener {
            incrementValue()
        }
    }

    fun setDecimalPlaces(decimalPlaces: Int) {
        this.mDecimalPlaces = decimalPlaces
        if (decimalPlaces > 0) {
            this.mIsNonDecimal = false
        }
        setFilters()
    }

    fun setAsNonDecimal(isNonDecimal: Boolean) {
        this.mIsNonDecimal = isNonDecimal
        if (isNonDecimal == true) {
            this.mDecimalPlaces = 0
        }
        setFilters()
    }

    fun setAsTextField(isText: Boolean) {
        this.mIsText = isText
        setFilters()
    }

    fun setFilters() {
        if (mIsNonDecimal == false && mIsText == false) {
            binding.etCountervalue?.filters =
                arrayOf<InputFilter>(DecimalDigitsInputFilter(mDecimalPlaces))
        } else if (mIsNonDecimal == true && mIsText == false) {
            binding.etCountervalue?.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(0))
        }
    }

    override fun onClick(view: View) {
        val viewId = view.id
        if (viewId == R.id.ib_counterminus) {
            decrementValue()
        } else if (viewId == R.id.ib_counterplus) {
            incrementValue()
        }
    }

    override fun onEditorAction(textView: TextView, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (mOnConterSubmitListner != null) {
                mOnConterSubmitListner!!.onConterSubmit()
            }
        }
        return false
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

    }

    override fun afterTextChanged(editable: Editable) {
        if (mOnConterSubmitListner != null) {
            mOnConterSubmitListner!!.onConterSubmit()
        }
    }

    /**
     * increments Value
     */
    private fun incrementValue() {
        val counterValue = getValue()
        try {
            if (counterValue.toString().isEmpty()) {
                binding.etCountervalue?.setText(minRange.toInt().toString())
                binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
            } else {
                if (counterValue >= maxRange) {
                    binding.etCountervalue!!.setText(maxRange.toInt().toString())
                    binding.etCountervalue!!.setSelection(binding.etCountervalue!!.text!!.length)
                } else if (counterValue < minRange) {
                    binding.etCountervalue!!.setText(minRange.toInt().toString())
                    binding.etCountervalue!!.setSelection(binding.etCountervalue!!.text!!.length)
                } else {
                    binding.etCountervalue!!.setText(
                        ((CalculateParameters.roundOffPrecision(
                            java.lang.Double.valueOf(counterValue),
                            1
                        ) + 1).toInt()).toString()
                    )
                    binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
                }
            }
        } catch ( e :Exception ) {

        }
    }

    /**
     * decrements Value
     */
    private fun decrementValue() {
        val counterValue = getValue()
        try {
            if (counterValue.toString().isEmpty()) {
                binding.etCountervalue?.setText(minRange.toInt().toString())
                binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
            } else {
                if (counterValue <= minRange) {
                    binding.etCountervalue?.setText(minRange.toInt().toString())
                    binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
                } else if (counterValue > maxRange) {
                    binding.etCountervalue?.setText(maxRange.toInt().toString())
                    binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
                } else {
                    binding.etCountervalue?.setText(
                        ((CalculateParameters.roundOffPrecision(java.lang.Double.valueOf(counterValue), 1) - 1).toInt()).toString())
                    binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
                }
            }
        } catch ( e :Exception ) {

        }
    }

    /**
     * sets Value
     */
    fun setValue(strValue: String?) {
        try {
            if (!Utilities.isNullOrEmpty(strValue)) {
                if (mIsNonDecimal) {
                    // binding.etCountervalue?.setText( round(java.lang.Double.valueOf(strValue), 1) as Int  + "")
                    binding.etCountervalue?.setText(
                        CalculateParameters.round(java.lang.Double.valueOf(strValue!!), 1).toInt().toString())
                    binding.etCountervalue?.setSelection(binding.etCountervalue!!.text!!.length)
                } else {
                    //binding.etCountervalue?.setText(Utilities.round(java.lang.Double.valueOf(strValue), 1) as Int  + "")
                    binding.etCountervalue!!.setText(
                        CalculateParameters.round(java.lang.Double.valueOf(strValue!!), 1).toInt().toString())
                    binding.etCountervalue!!.setSelection(binding.etCountervalue!!.text!!.length)
                }
            } else {
                binding.etCountervalue?.setText("")
            }
        } catch ( e : Exception ) {

        }
    }

    /**
     * sets Counter Name
     *
     * @param strCounterName
     */
    fun setCounterName(strCounterName: String) {
        binding.tvCountername!!.text = strCounterName
    }

    /**
     * set On ConterSubmit Listner
     *
     * @param onConterSubmitListner
     */
    fun setOnConterSubmitListner(onConterSubmitListner: OnConterSubmitListner) {
        mOnConterSubmitListner = onConterSubmitListner
    }

    fun setOnEditTextChangeListener(onEditTextChangeListener: OnEditTextChangeListener) {
        mOnEditTextChangeListener = onEditTextChangeListener
    }

    /**
     * sets Input Range
     *
     * @param start
     * @param end
     */
    fun setInputRange(start: Double, end: Double) {
        this.minRange = start
        this.maxRange = end
        val filter = object : InputFilterMinMax(0.0, 1000.0) {

        }
        binding.etCountervalue?.filters = arrayOf<InputFilter>(filter)
    }

    /**
     * checks if value is double or not
     *
     * @param str
     * @return
     */
    internal fun isDouble(str: String): Boolean {
        try {
            java.lang.Double.parseDouble(str)
            return true
        } catch (e: NumberFormatException) {
            return false
        }

    }

    /**
     * set Key Listener
     */
    fun setKeyListener(digits: Int, isDecimal: Boolean) {
        val decimalValueFilter = DecimalValueFilter(isDecimal)
        decimalValueFilter.setDigits(digits)
        binding.etCountervalue?.keyListener = decimalValueFilter
    }

    /**
     * set Counter Name Visibility
     *
     * @param visibility
     */
    fun setCounterNameVisibility(visibility: Int) {
        binding.tvCountername.visibility = visibility
    }

    fun setEmsEditText(ems: Int) {
        binding.etCountervalue.setEms(ems)
    }

    interface OnConterSubmitListner {
        fun onConterSubmit()
    }

    open inner class InputFilterMinMax : InputFilter {

        private var min: Double = 0.toDouble()
        private var max: Double = 0.toDouble()

        constructor(min: Double, max: Double) {
            this.min = min
            this.max = max
        }

        constructor(min: String, max: String) {
            this.min = java.lang.Double.parseDouble(min)
            this.max = java.lang.Double.parseDouble(max)
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(min, max, input.toDouble()))
                    return null
            } catch (nfe: NumberFormatException) {
            }

            return ""
        }

        private fun isInRange(a: Double, b: Double, c: Double): Boolean {
            return if (b > a) c >= a && c <= b else c >= b && c <= a
        }
    }

    inner class DecimalDigitsInputFilter
    /**
     * Constructor.
     *
     * @param decimalDigits maximum decimal digits
     */
        (private val decimalDigits: Int) : InputFilter {

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {

            println(" source data pressed $source")
            if (this.decimalDigits == 0) {
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
                val input = java.lang.Double.parseDouble(dest.toString() + source.toString())
                if (input > 1000.0)
                    return ""
            } catch (nfe: NumberFormatException) {
            }

            return null
        }

    }

}