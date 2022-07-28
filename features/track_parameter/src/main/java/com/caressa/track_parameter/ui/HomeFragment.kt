package com.caressa.track_parameter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.track_parameter.R
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.track_parameter.databinding.HomeFragmentBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : BaseFragment(){

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: HomeFragmentBinding

    override fun getViewModel(): BaseViewModel = viewModel

    private var from = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("from,selectedDate--->$from")
        }

        when( from ) {
            "DashboardBP" -> {
                viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToUpdateParameter(
                    "BLOODPRESSURE","true"))
            }
            "DashboardBMI" -> {
                viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToUpdateParameter(
                    "BMI","true"))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.TRACK_PARAMETERS_HOME_SCREEN)
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {

    }

    private fun setClickable() {

        binding.layoutSelectParam.setOnClickListener{
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToSelectParameterFragment())
        }

        binding.layoutUpdateParam.setOnClickListener {
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToUpdateParameter())
        }

        binding.layoutDashboardParam.setOnClickListener {
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToDashboardFragmentNew())
        }

        binding.layoutHistoryParam.setOnClickListener {
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToHistoryFragment())
        }

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

}