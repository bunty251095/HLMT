package com.caressa.hra.views

import android.view.View
import androidx.viewpager.widget.ViewPager

class VerticalPageTransformer : ViewPager.PageTransformer  {

    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width
        val pageHeight = view.height
        when {
            position < -1 -> {
                view.alpha = 0f
            }
            position <= 1 -> {
                view.alpha = 1f
                view.translationX = pageWidth * -position
                val yPosition = position * pageHeight
                view.translationY = yPosition
            }
            else -> {
                view.alpha = 0f
            }
        }
    }

/*    override fun transformPage(view: View, position: Float) {
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.alpha = 0.0f
        } else if (position <= 1) { // [-1,1]
            view.alpha = 1.0f

            // Counteract the default slide transition
            view.translationX = view.getWidth() * -position

            //set Y position to swipe in from top
            val yPosition: Float = position * view.getHeight()
            view.translationY = yPosition
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.alpha = 0.0f
        }
    }*/

}