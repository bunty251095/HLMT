package com.caressa.common.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class ExpandedHeightListView : RecyclerView {

    private var mContext: Context? = null
    internal var expanded = false

    constructor(context: Context) : super(context)  {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) :  super(context, attrs) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)  {
        this.mContext = context
    }

    fun isExpanded(): Boolean {
        return expanded
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isExpanded()) {
            val expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST)
            super.onMeasure(widthMeasureSpec, expandSpec)

            val params = layoutParams
            params.height = measuredHeight
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setExpanded(expanded: Boolean) {
        this.expanded = expanded
    }

}