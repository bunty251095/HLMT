package com.caressa.hra.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

class CustomViewPager : ViewPager {

    private var isPagingEnabled = false
    private var mScroller: FixedSpeedScroller? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            mScroller = FixedSpeedScroller(context, DecelerateInterpolator())
            scroller.set(this, mScroller)
        } catch (ignored: Exception) {
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
       // return this.isPagingEnabled && super.onTouchEvent(event)
        return if (this.isPagingEnabled) {
            super.onTouchEvent(event)
            performClick()
        } else false
    }

    fun setScrollDuration(duration: Int) {
        mScroller!!.setScrollDuration(duration)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
     //   return this.isPagingEnabled && super.onInterceptTouchEvent(event)
             return if (this.isPagingEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isPagingEnabled = enabled
    }

}