package com.caressa.tools_calculators.views

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.caressa.tools_calculators.R

class ExpandableLinearLayout : LinearLayout {
    private var expanded = false
    private var duration = 0
    private var expandListener: ExpandListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
        defStyleAttr) {
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attributeSet: AttributeSet?) {
        val customValues =
            context.obtainStyledAttributes(attributeSet, R.styleable.ExpandableLinearLayout)
        duration = customValues.getInt(R.styleable.ExpandableLinearLayout_expandDuration, -1)
        customValues.recycle()
    }

    fun isExpanded(): Boolean {
        return expanded
    }

    fun setExpanded(expanded: Boolean) {
        Log.e("layout", expanded.toString() + "")
        this.expanded = expanded
    }

    fun toggle() {
        if (expanded) expandView(this) else hideView(this)
    }

    private fun expandView(view: View) {
        view.visibility = VISIBLE
    }

    private fun hideView(view: View) {
        view.visibility = GONE
    }

    fun setExpandListener(expandListener: ExpandListener?) {
        this.expandListener = expandListener
    }

    interface ExpandListener {
        fun onExpandComplete()
        fun onCollapseComplete()
    }
}