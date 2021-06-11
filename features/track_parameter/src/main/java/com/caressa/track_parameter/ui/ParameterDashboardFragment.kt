package com.caressa.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.HomeFragmentBinding
import com.caressa.track_parameter.databinding.ParameterDashboardFragmentBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import com.caressa.track_parameter.viewmodel.ParameterHomeViewModel
import kotlinx.android.synthetic.main.parameter_dashboard_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class ParameterDashboardFragment : BaseFragment(){

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: ParameterDashboardFragmentBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ParameterDashboardFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun setClickable() {

    }

    private fun initialise() {
        viewModel.getDashboardParamList()
//        viewModel.dashboardLiveData.observe(this, Observer {
////            Timber.i("DashboardData=> "+it)
//        })
    }
}