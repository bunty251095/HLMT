package com.caressa.track_parameter.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.Utilities
import com.caressa.model.parameter.DashboardParamGridModel
import com.caressa.model.parameter.FitnessData
import com.caressa.track_parameter.ParameterHomeActivity
import com.caressa.track_parameter.adapter.DashboardGridAdapter
import com.caressa.track_parameter.databinding.FragmentRevDashboardBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class RevDashboardFragment : BaseFragment(), DashboardGridAdapter.ParameterSelectionListener,ParameterHomeActivity.OnGoogleAccountSelectListener {

    private val viewModel: DashboardViewModel by viewModel()
    private lateinit var binding: FragmentRevDashboardBinding

    private var dashboardGridAdapter: DashboardGridAdapter? = null
    private var fitnessDataManager : FitnessDataManager? = null
    private val data = FitnessData()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as ParameterHomeActivity).setDataReceivedListener(this)
    }

    override fun onGoogleAccountSelection(from:String) {
        Timber.e("from---> $from")
        when(from) {
            Constants.SUCCESS -> proceedWithFitnessData()
            Constants.FAILURE -> { }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRevDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            initialise()
            setClickable()
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        fitnessDataManager = FitnessDataManager(requireContext())

        viewModel.listHistoryWithLatestRecord(data)
        dashboardGridAdapter = DashboardGridAdapter(requireContext(), this,viewModel)
        binding.rvDashboardParametersGrid.adapter = dashboardGridAdapter

        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            Timber.e("oAuthPermissionsApproved---> true")
            proceedWithFitnessData()
        } else {
            Timber.e("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }
    }

    private fun proceedWithFitnessData() {
        try {
            viewModel.showProgressBar()
            fitnessDataManager!!.readHistoryData(Date(), Date()).addOnCompleteListener {
                if ( fitnessDataManager!!.fitnessDataArray.length() > 0 ) {
                    val todayData = fitnessDataManager!!.fitnessDataArray.getJSONObject(0)
                    Utilities.printData("TodayFitnessData",todayData)
                    data.recordDate = todayData.getString(Constants.RECORD_DATE)
                    data.stepsCount = todayData.getString(Constants.STEPS_COUNT)
                    data.calories = todayData.getString(Constants.CALORIES)
                    data.distance = todayData.get(Constants.DISTANCE).toString().toDouble()
                    data.activeTime = todayData.getString(Constants.ACTIVE_TIME).toString().toInt()
                    viewModel.listHistoryWithLatestRecord(data)
                    viewModel.hideProgressBar()
                } else {
                    Timber.e("Fitness Data not Available")
                    viewModel.hideProgressBar()
                }
            }
        } catch (e : Exception) {
            viewModel.hideProgressBar()
            e.printStackTrace()
        }
    }

    private fun setClickable() {

        binding.btnDashboardUploadReport.setOnClickListener{
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.STORE_RECORDS)
            launchIntent.putExtra(Constants.FROM, Constants.TRACK_PARAMETER)
            startActivity(launchIntent)
        }

        binding.btnDashboardAddMedication.setOnClickListener{
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
            launchIntent.putExtra(Constants.FROM, Constants.TRACK_PARAMETER)
            startActivity(launchIntent)
        }

    }

    override fun onSelection(paramGridModel: DashboardParamGridModel) {
        Timber.e("paramCode---> ${paramGridModel.paramCode}")
        when(paramGridModel.paramCode) {

            "STEPS","CAL" -> routeToActivityTracker()
            "BMI","WEIGHT" -> routeToUpdateParameter("BMI")
            "WAIST","WHR" -> routeToUpdateParameter("WHR")
            "BP","PULSE" -> routeToUpdateParameter("BLOODPRESSURE")
            "SUGAR" -> routeToUpdateParameter("DIABETIC")
            "CHOL" -> routeToUpdateParameter("LIPID")
            "HEMOGLOBIN" -> routeToUpdateParameter("HEMOGRAM")
//            "ADD" -> routeToUpdateParameter("BMI")
        }
    }

    private fun routeToUpdateParameter(profileCode: String) {
        viewModel.navigateParam(RevDashboardFragmentDirections.actionDashboardFragmentToUpdateParameterFragment(profileCode))
    }

    private fun routeToActivityTracker()  {
        val launchIntent = Intent()
        launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FITNESS_HOME)
        launchIntent.putExtra(Constants.FROM, Constants.TRACK_PARAMETER)
        startActivity(launchIntent)
    }

}