package com.caressa.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.RealPathUtil
import com.caressa.home.R
import com.caressa.home.databinding.FragmentDashboardBinding
import com.caressa.home.di.ScoreListener
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.model.entity.HRASummary
import org.koin.android.viewmodel.ext.android.viewModel

class DashboardFragment : BaseFragment() , ScoreListener {

    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModel()
    private val backgroundCallViewModel: BackgroundCallViewModel by viewModel()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.backViewModel = backgroundCallViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        (activity as HomeMainActivity).registerListener(this)
        RealPathUtil.makeFilderDirectories()
        //Utilities.exportdatabase(requireContext())
    }

    private fun setClickable() {

        binding.layoutHra.setOnClickListener {
            viewModel.goToHRA()
        }

        binding.layoutTakeHealthCheckup.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.Toobar_Title, Constants.TOOLBAR_HEALTH_PACKAGES)
            bundle.putString(Constants.WEB_URL, Constants.strBookAppointmentURL)
            bundle.putBoolean(Constants.HAS_COOKIES, true)
            it.findNavController().navigate(R.id.action_dashboardFragment_to_commonWebViewActivity, bundle)
        }

        binding.layoutChatWithDoctor.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.Toobar_Title, Constants.TOOLBAR_CHAT_WITH_DOCTOR)
            bundle.putString(Constants.WEB_URL, Constants.strWebChatDoctorURL)
            bundle.putBoolean(Constants.HAS_COOKIES, false)
            it.findNavController().navigate(R.id.action_dashboardFragment_to_commonWebViewActivity, bundle)
        }

        binding.layoutOrderMedicines.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,"Dashboard")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_orderMedicinesActivity,bundle)
        }

        binding.layoutTrackParameter.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "Dashboard")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_trackParamActivity,bundle)
        }

        binding.layoutStoreHealthRecords.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_shrActivity)
        }

        binding.layoutMedicineTracker.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_medicationActivity)
        }

        binding.layoutActivityTracker.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
        }

        binding.layoutWellnessCentre.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_wellnessCentreActivity)
        }

        binding.layoutHospitalNearMe.setOnClickListener {
            viewModel.navigateToHospitalsNearMe(requireContext())
        }

        binding.layoutHealthLibrary.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_blogsActivity)
        }

        binding.layoutChatWithDietitian.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.Toobar_Title, Constants.TOOLBAR_CHAT_WITH_DIETICIAN)
            bundle.putString(Constants.WEB_URL, Constants.sirWebChatDietitianURL)
            bundle.putBoolean(Constants.HAS_COOKIES, true)
            it.findNavController().navigate(R.id.action_dashboardFragment_to_commonWebViewActivity, bundle)
        }

    }

    override fun onScore(hraSummary: HRASummary?) {
        viewModel.getHraSummaryDetails()
    }

}
