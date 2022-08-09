package com.caressa.home.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.databinding.FragmentSlidingImageBinding
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.home.views.HomeBinding.loadImageNew
import com.caressa.model.home.ListActiveBannerModel.CampaignDetails
import org.koin.android.viewmodel.ext.android.viewModel

class SlidingImageFragment(private val campaignDetailsList: MutableList<CampaignDetails>,
                           val position: Int) : Fragment(R.layout.fragment_sliding_image) {

    private lateinit var binding: FragmentSlidingImageBinding
    private val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSlidingImageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initialise()
        return binding.root
    }

    fun initialise() {

        binding.slidingImage.loadImageNew(campaignDetailsList[position].image!!)

        binding.slidingImage.setOnClickListener {

            viewModel.callSaveBannerAccessLogApi(campaignDetailsList[position].campaignID!!.toInt(),
                campaignDetailsList[position].redirectionUrl!!)
            Utilities.redirectToChrome(campaignDetailsList[position].redirectionUrl!!,requireContext())
            //Utilities.redirectToChrome("https://tinypng.com/",requireContext())

        }

        viewModel.saveBannerAccessLog.observe(viewLifecycleOwner) {}
    }

}
