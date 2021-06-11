package com.caressa.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.ParameterListModel
import com.caressa.track_parameter.adapter.ParamUpdateExpandableAdapter
import com.caressa.track_parameter.adapter.UpdateParamDataList
import com.caressa.track_parameter.databinding.SelectParameterFragmentBinding
import com.caressa.track_parameter.databinding.UpdateParameterFragmentBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class UpdateParameterFragment : BaseFragment(){

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: UpdateParameterFragmentBinding
    override fun getViewModel(): BaseViewModel = viewModel
    lateinit var dataListMaster:List<UpdateParamDataList>
    lateinit var  expandableListAdapter: ExpandableListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = UpdateParameterFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
//        dataList = getDummyData()


        binding.expandableListUpUpdate.setOnGroupExpandListener(ExpandableListView.OnGroupExpandListener { groupPosition ->
            //Utilities.toastMessageShort(context,dataList.get(groupPosition).headerName + " List Expanded.")
        })

        binding.expandableListUpUpdate.setOnGroupCollapseListener(ExpandableListView.OnGroupCollapseListener { groupPosition ->
            //Utilities.toastMessageShort(context,dataList.get(groupPosition).headerName + " List Collapsed.")
        })

        binding.expandableListUpUpdate.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            //Utilities.toastMessageShort(context,dataList.get(groupPosition).headerName + " -> " + dataList.get(groupPosition)!!.childList.get(childPosition).profileName )
            false
        })
    }

    private fun setClickable() {
        viewModel.getTrackParameters()
        viewModel.parameterLiveData.observe(this, Observer { data->
            val distinctElement = data.distinct().distinctBy { it.profileName }
            Timber.i("distinctElement:: " + distinctElement)
            var dataList:ArrayList<UpdateParamDataList> = arrayListOf()
            var dataItem: UpdateParamDataList

            for (item in distinctElement){
                if(!item.profileName.isNullOrEmpty() ) {
                    Timber.i("Item:: " + item.profileName + " :: " + data.filter { it.profileName == item.profileName }.size)
                    dataItem = UpdateParamDataList(headerName = item.profileName!!, childList = data.filter { it.profileName == item.profileName }, date = DateHelper.currentDateAsStringddMMMyyyy)
                    dataList.add(dataItem)
                }
            }
            dataListMaster = listOf()
            dataListMaster = dataList
            expandableListAdapter = ParamUpdateExpandableAdapter(dataListMaster,requireContext())
            binding.expandableListUpUpdate.setAdapter(expandableListAdapter)

        })
    }

}