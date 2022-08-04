package com.caressa.home.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caressa.home.ui.SlidingImageFragment

class SlidingImagesAdapter(activity: FragmentActivity, private val itemsCount: Int) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) = SlidingImageFragment.getInstance(position)

}