package com.caressa.hra.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field


class VerticalViewPager : ViewPager {

    private var mScroller: FixedSpeedScroller? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return false
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return super.canScrollHorizontally(direction)
    }

    private fun init() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            mScroller = FixedSpeedScroller(context, DecelerateInterpolator())
            scroller.set(this, mScroller)

            setPageTransformer(true, VerticalPageTransformer())
            overScrollMode = View.OVER_SCROLL_NEVER
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setScrollDuration(duration: Int) {
        mScroller!!.setScrollDuration(duration)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val toIntercept = super.onInterceptTouchEvent(flipXY(ev))
        flipXY(ev)
        return toIntercept
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val toHandle = super.onTouchEvent(flipXY(ev))
        flipXY(ev)
        return toHandle
    }

    private fun flipXY(ev: MotionEvent): MotionEvent {
        val width = width.toFloat()
        val height = height.toFloat()
        val x = ev.y / height * width
        val y = ev.x / width * height
        ev.setLocation(x, y)
        return ev
    }

}