package com.caressa.home.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.caressa.home.ui.SlidingImageFragment
import com.caressa.model.home.ListActiveBannerModel

class SlidingImagesAdapter(activity: FragmentActivity,
                           private val itemsCount: Int,
                           private val campaignDetailsList: MutableList<ListActiveBannerModel.CampaignDetails>) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) = SlidingImageFragment(campaignDetailsList,position)

}