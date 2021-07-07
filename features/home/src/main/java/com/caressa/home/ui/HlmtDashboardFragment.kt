package com.caressa.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.CalculateParameters
import com.caressa.common.utils.RealPathUtil
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.databinding.FragmentHlmtDashboardBinding
import com.caressa.home.di.ScoreListener
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.model.entity.HRASummary
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.FitnessData
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HlmtDashboardFragment : BaseFragment() , ScoreListener {

    private lateinit var binding: FragmentHlmtDashboardBinding
    private val viewModel: DashboardViewModel by viewModel()
    private val backgroundCallViewModel: BackgroundCallViewModel by viewModel()
    private var fitnessDataManager : FitnessDataManager? = null
    private val data = FitnessData()
    private var stepGoal = 3000

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHlmtDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.backViewModel = backgroundCallViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        viewModel.currentSelectedPerson.observe(viewLifecycleOwner,{
            viewModel.goToHRA(it)
        })
    }

    private fun initialise() {
        fitnessDataManager = FitnessDataManager(requireContext())
        (activity as HomeMainActivity).registerListener(this)
        RealPathUtil.makeFilderDirectories()
        //Utilities.exportdatabase(requireContext())
        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            Timber.e("oAuthPermissionsApproved---> true")
            proceedWithFitnessData()
        } else {
            Timber.e("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }
    }

    private fun setClickable() {

        binding.cardHra.setOnClickListener {
            viewModel.getDOBOfPerson()
        }

//        binding.layoutTakeHealthCheckup.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString(Constants.Toobar_Title, Constants.TOOLBAR_HEALTH_PACKAGES)
//            bundle.putString(Constants.WEB_URL, Constants.strBookAppointmentURL)
//            bundle.putBoolean(Constants.HAS_COOKIES, true)
//            it.findNavController().navigate(R.id.action_dashboardFragment_to_commonWebViewActivity, bundle)
//        }

//        binding.layoutChatWithDoctor.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString(Constants.Toobar_Title, Constants.TOOLBAR_CHAT_WITH_DOCTOR)
//            bundle.putString(Constants.WEB_URL, Constants.strWebChatDoctorURL)
//            bundle.putBoolean(Constants.HAS_COOKIES, false)
//            it.findNavController().navigate(R.id.action_dashboardFragment_to_commonWebViewActivity, bundle)
//        }

//        binding.layoutOrderMedicines.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString(Constants.FROM,"Dashboard")
//            it.findNavController().navigate(R.id.action_dashboardFragment_to_orderMedicinesActivity,bundle)
//        }

        binding.cardTrackParameter.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "Dashboard")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_trackParamActivity,bundle)
        }

        binding.cardStoreRecord.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_shrActivity)
        }

        binding.cardMedicationTracker.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_medicationActivity)
        }

        binding.cardSteps.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
        }

        binding.cardToolsCalculator.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_toolsCalculatorsHomeActivity)
        }

//        binding.layoutWellnessCentre.setOnClickListener {
//            it.findNavController().navigate(R.id.action_dashboardFragment_to_toolsCalculatorsHomeActivity)
//            //it.findNavController().navigate(R.id.action_dashboardFragment_to_wellnessCentreActivity)
//        }

//        binding.layoutHospitalNearMe.setOnClickListener {
//            viewModel.navigateToHospitalsNearMe(requireContext())
//        }

        binding.cardBlog.setOnClickListener {
            it.findNavController().navigate(R.id.action_dashboardFragment_to_blogsActivity)
        }

//        binding.layoutChatWithDietitian.setOnClickListener {
//            val bundle = Bundle()
//            bundle.putString(Constants.Toobar_Title, Constants.TOOLBAR_CHAT_WITH_DIETICIAN)
//            bundle.putString(Constants.WEB_URL, Constants.sirWebChatDietitianURL)
//            bundle.putBoolean(Constants.HAS_COOKIES, true)
//            it.findNavController().navigate(R.id.action_dashboardFragment_to_commonWebViewActivity, bundle)
//        }

        binding.cardReward.setOnClickListener {
            viewModel.toastMessage("Coming Soon..")
        }
        binding.layoutHeight.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "DashboardBMI")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_trackParamActivity,bundle)
        }
        binding.layoutWeight.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "DashboardBMI")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_trackParamActivity,bundle)
        }

        binding.layoutBmi.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "DashboardBMI")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_trackParamActivity,bundle)
        }

        binding.layoutBp.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM, "DashboardBP")
            it.findNavController().navigate(R.id.action_dashboardFragment_to_trackParamActivity,bundle)
        }
    }

    override fun onScore(hraSummary: HRASummary?) {
        viewModel.getHraSummaryDetails()
        Timber.i("HRAData---> $hraSummary")
        try {
            if (hraSummary!!.hraCutOff.equals("1")) {
                binding.txtNoHra.visibility = View.GONE
                binding.txtHraValue.visibility = View.VISIBLE
                binding.txtHraValue.text = "${hraSummary.scorePercentile.toInt()}"
                binding.txtHraValue.setTextColor(ContextCompat.getColor(requireContext(),Utilities.getHraObservationColorFromScore(hraSummary.scorePercentile.toInt())))
            }else{
                binding.txtNoHra.visibility = View.VISIBLE
                binding.txtHraValue.visibility = View.INVISIBLE
                binding.txtHraValue.text = "${hraSummary.scorePercentile.toInt()}"
            }
        }catch (e: Exception){e.printStackTrace()}

    }

    override fun onVitalDataUpdateListener(history: List<TrackParameterMaster.History>) {
        Timber.i("Vitals Data=> "+history)
        updateDashboard(history)
    }

    override fun onStepGoalReceived(goal: Int) {
        if (goal == 0){ stepGoal = 3000 }
        else { stepGoal = goal }
        proceedWithFitnessData()
    }

    private fun updateDashboard(history: List<TrackParameterMaster.History>) {

            if (history.isNotEmpty()){
                try {
                    var systolic = 0
                    var diastolic = 0
                    for (item in history){
                        if (item.parameterCode.equals("BMI",true)){
                            binding.txtBmiValue.text = "${item.value} ${item.unit}"
                        }
                        if (item.parameterCode.equals("HEIGHT",true)){
                            val feetInch = CalculateParameters.convertCmToFeetInch(item.value.toString())
                            binding.txtHeightValue.text = "${feetInch}"
                        }
                        if (item.parameterCode.equals("WEIGHT",true)){
                            binding.txtWeightValue.text = "${item.value} ${item.unit}"
                        }
                        if (item.parameterCode.equals("BP_DIA",true)){
                            diastolic = item.value!!.toInt()
                        }
                        if (item.parameterCode.equals("BP_SYS",true)){
                            systolic = item.value!!.toInt()
                        }
                    }
                    if(systolic!=0 && diastolic!=0) {
                        binding.txtBpValue.text = "$systolic/$diastolic mm Hg"
                    }

                }catch (e: Exception){e.printStackTrace()}
            }else{
                binding.txtBmiValue.text = " -- "
                binding.txtHeightValue.text = " -- "
                binding.txtWeightValue.text = " -- "
                binding.txtBpValue.text = " -- "
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
                    binding.txtStepsCount.text = "${data.stepsCount} steps of ${stepGoal}"
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

}