package com.caressa.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.model.parameter.DashboardObservationData
import com.caressa.model.parameter.TrackParamDashboardDataSet
import com.caressa.track_parameter.adapter.DashboardListAdapter
import com.caressa.track_parameter.databinding.DashboardFragmentBinding
import com.caressa.track_parameter.databinding.HomeFragmentBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import com.caressa.track_parameter.viewmodel.ParameterHomeViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class DashboardFragment: BaseFragment(){

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: DashboardFragmentBinding
    private var dataList: ArrayList<TrackParamDashboardDataSet> = arrayListOf()
    private var observationList: ArrayList<DashboardObservationData> = arrayListOf()
    private lateinit var dashboardListAdapter:DashboardListAdapter

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DashboardFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    private fun setClickable() {

    }

    private fun registerObserver(){
        viewModel.dashboardLiveData.observe(this, Observer {
            if (!it!!.isEmpty()){
                observationList = it as ArrayList<DashboardObservationData>
                viewModel.getSelectParameterList()
            }
        })
        viewModel.selectParamLiveData.observe(this, Observer {
            dataList.clear()
            var itemDataset: TrackParamDashboardDataSet
            var obList: List<DashboardObservationData>
            for (item in it){
                obList = observationList.filter { it.profileCode == item.profileCode }
                Timber.i("DataItem=> "+item.profileName+" :: "+ obList.size)

                itemDataset = TrackParamDashboardDataSet(observationData = obList,isStartTracking =  if(obList.isEmpty())false else true, profileName = item.profileName, profileCode = item.profileCode )
                dataList.add(itemDataset)
            }
            dashboardListAdapter.updateList(dataList)
            Timber.i("DataList=> "+dataList.size)
        })
    }

    private fun unregisterObserver(){
        viewModel.dashboardLiveData.removeObservers(this)
        viewModel.selectParamLiveData.removeObservers(this)
    }

    override fun onResume() {
        super.onResume()
        registerObserver()
    }

    override fun onPause() {
        super.onPause()
        unregisterObserver()
    }

    private fun initialise() {
        dashboardListAdapter = DashboardListAdapter(viewModel)
        binding.recParamList.layoutManager = LinearLayoutManager(context)
        binding.recParamList.adapter = dashboardListAdapter
        viewModel.getDashboardParamList()
    }

    override fun onDestroyView() {
        viewModel.selectParamLiveData.removeObservers(this)
        super.onDestroyView()
    }
}