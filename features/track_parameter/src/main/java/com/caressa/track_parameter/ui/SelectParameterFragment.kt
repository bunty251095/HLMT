package com.caressa.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.Utilities
import com.caressa.track_parameter.adapter.SelectParameterAdapter
import com.caressa.track_parameter.databinding.SelectParameterFragmentBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SelectParameterFragment : BaseFragment(){

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: SelectParameterFragmentBinding

    private var selectParameterAdapter: SelectParameterAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SelectParameterFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        //viewModel.getAllProfileCodes()
        viewModel.getAllProfilesWithRecentSelectionList()
        selectParameterAdapter = SelectParameterAdapter(requireContext(),viewModel)
        binding.rvSelectParameters.adapter = selectParameterAdapter
    }

    private fun setClickable() {

        binding.btnNextSelectParameters.setOnClickListener {
            if ( validate() ) {
                viewModel.saveSelectedParameter( selectParameterAdapter!!.dataList )
                viewModel.navigateParam(SelectParameterFragmentDirections.actionSelectParamFragmentToUpdateParameterFragment(
                    selectParameterAdapter!!.dataList.get(0).profileCode,"false"))
            } else {
                Utilities.toastMessageShort(requireContext(),"Please Select a Profile to Proceed")
            }
        }

    }

    private fun validate() : Boolean {
        var isValid = false
        for ( profile in selectParameterAdapter!!.dataList ) {
            if ( profile.isSelection ) {
                isValid = true
                break
            }
        }
        Timber.e("isValid--->$isValid")
        return isValid
    }



}