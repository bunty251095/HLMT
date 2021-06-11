package com.caressa.hra.views

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

class FixedSpeedScroller : Scroller {

    // default duration
    private var mDuration = 500

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, interpolator: Interpolator?) : super(context, interpolator) {}
    constructor(context: Context?, interpolator: Interpolator?, flywheel: Boolean) : super(context, interpolator, flywheel) {}

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    // Viewpager Transition Duration can be changed using this fun
     fun setScrollDuration(duration: Int) {
        mDuration = duration
    }
}