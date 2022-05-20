package com.caressa.home.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.adapter.DashboardFeaturesGridAdapter
import com.caressa.home.common.DataHandler
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

class HlmtDashboardFragment : BaseFragment() , ScoreListener,DashboardFeaturesGridAdapter.OnItemSelectionListener,
    HomeMainActivity.OnGoogleAccountSelectListener {

    private lateinit var binding: FragmentHlmtDashboardBinding
    private val viewModel: DashboardViewModel by viewModel()
    private val backgroundCallViewModel: BackgroundCallViewModel by viewModel()
    private var fitnessDataManager : FitnessDataManager? = null
    private val data = FitnessData()
    private var stepGoal = 3000
    private var dashboardGridAdapter: DashboardFeaturesGridAdapter? = null


    override fun getViewModel(): BaseViewModel = viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setDataReceivedListener(this)
        FileUtils.makeFolderDirectories(requireContext())
    }

    override fun onGoogleAccountSelection(from:String) {
        Timber.e("from---> $from")
        when(from) {
            Constants.SUCCESS -> proceedWithFitnessData()
            Constants.FAILURE -> { }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHlmtDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.backViewModel = backgroundCallViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.HOME_DASHBOARD)
        initialise()
        setClickable()
        setObserver()
        return binding.root
    }

    private fun setObserver() {
        viewModel.currentSelectedPerson.observe(viewLifecycleOwner,{
//            viewModel.goToHRA(it)
        })
        viewModel.dashboardFeatureList.observe(viewLifecycleOwner,{})
    }

    private fun initialise() {
        fitnessDataManager = FitnessDataManager(requireContext())
        (activity as HomeMainActivity).registerListener(this)
        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            Timber.e("oAuthPermissionsApproved---> true")
            proceedWithFitnessData()
        } else {
            Timber.e("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }
        dashboardGridAdapter = DashboardFeaturesGridAdapter(requireContext(), this,viewModel)
        binding.rvDashboardGrid.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvDashboardGrid.adapter = dashboardGridAdapter

        viewModel.refreshDashboardFeatureList()

    }

    private fun setClickable() {

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
        viewModel.hraSummary = hraSummary
        viewModel.refreshDashboardFeatureList()
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
        binding.txtBmiValue.text = " -- "
        binding.txtHeightValue.text = " -- "
        binding.txtWeightValue.text = " -- "
        binding.txtBpValue.text = " -- "
            if (history.isNotEmpty()){
                try {
                    var systolic = 0
                    var diastolic = 0
                    for (item in history){
                        if (item.parameterCode.equals("BMI",true)){
                            binding.txtBmiValue.text = "${item.value} ${item.unit}"
                        }
                        if (item.parameterCode.equals("HEIGHT",true)){
                            if(viewModel.getPreference("HEIGHT").equals(resources.getString(R.string.CM),ignoreCase = true)){
                                binding.txtHeightValue.text = "${item.value.toString().toDouble().toInt()} ${resources.getString(R.string.CM)}"
                            }else{
                                binding.txtHeightValue.text = CalculateParameters.convertCmToFeetInch(item.value.toString())
                            }
                        }
                        if (item.parameterCode.equals("WEIGHT",true)){
                            if(viewModel.getPreference("WEIGHT").equals(resources.getString(R.string.KG),ignoreCase = true)){
                                binding.txtWeightValue.text = "${item.value} ${resources.getString(R.string.KG)}"
                            }else{
                                binding.txtWeightValue.text = Utilities.roundOffPrecision(CalculateParameters.convertKgToLbs(item.value.toString()).toDouble(),0).toInt().toString()+" lbs"
                            }
                        }
                        if (item.parameterCode.equals("BP_DIA",true)){
                            diastolic = item.value!!.toInt()
                        }
                        if (item.parameterCode.equals("BP_SYS",true)){
                            systolic = item.value!!.toInt()
                        }
                    }
                    if(systolic!=0 && diastolic!=0) {
                        binding.txtBpValue.text = "$systolic/$diastolic ${resources.getString(R.string.MM_HG)}"
                    }

                }catch (e: Exception){e.printStackTrace()}
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
                    viewModel.stepsData = "${data.stepsCount} ${resources.getString(R.string.STEPS_OF)} ${stepGoal}"
                    viewModel.refreshDashboardFeatureList()
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

    override fun onItemSelected(item: DataHandler.DashboardFeatureGrid) {
        when(item.code){
            "HRA"-> viewModel.goToHRA()
            "STEP"->{
                if(viewModel.checkForFirstTime()) {
                    val dialogData = DefaultNotificationDialog.DialogData()
                    dialogData.title =
                        requireContext().resources.getString(R.string.DISCLAIMER_TITLE)
                    dialogData.message =
                        requireContext().resources.getString(R.string.DISCLAIMER_MESSAGE_ACTIVITY_TRACKER)
                    dialogData.btnRightName =
                        requireContext().resources.getString(R.string.CONTINUE)
                    dialogData.showLeftButton = false
                    val defaultNotificationDialog = DefaultNotificationDialog(context,
                        object : DefaultNotificationDialog.OnDialogValueListener {
                            override fun onDialogClickListener(
                                isButtonLeft: Boolean,
                                isButtonRight: Boolean
                            ) {
                                if (isButtonRight) {
                                    findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
                                }
                            }
                        }, dialogData
                    )
                    defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    defaultNotificationDialog.show()
                }else{
                    findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)}
                }
            "PARAM"->{
                    val bundle = Bundle()
                    bundle.putString(Constants.FROM, "Dashboard")
                    findNavController().navigate(
                        R.id.action_dashboardFragment_to_trackParamActivity,
                        bundle
                    )
            }
            "RECORD"->{
                findNavController().navigate(R.id.action_dashboardFragment_to_shrActivity)
            }
            "MED"->{
                findNavController().navigate(R.id.action_dashboardFragment_to_medicationActivity)
            }
            "CAL"->{findNavController().navigate(R.id.action_dashboardFragment_to_toolsCalculatorsHomeActivity)}
            "REW"->{viewModel.toastMessage("Coming Soon..")}
            "BLOG"->{findNavController().navigate(R.id.action_dashboardFragment_to_blogsActivity)}
        }
    }

}