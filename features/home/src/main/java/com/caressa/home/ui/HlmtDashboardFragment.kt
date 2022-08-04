package com.caressa.home.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.fitness.FitRequestCode
import com.caressa.common.fitness.FitnessDataManager
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.adapter.DashboardFeaturesGridAdapter
import com.caressa.home.adapter.SlidingImagesAdapter
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

class HlmtDashboardFragment : BaseFragment() , ScoreListener,
    DashboardFeaturesGridAdapter.OnItemSelectionListener,
    HomeMainActivity.OnGoogleAccountSelectListener {

    private lateinit var binding: FragmentHlmtDashboardBinding
    private val viewModel: DashboardViewModel by viewModel()
    private val backgroundCallViewModel: BackgroundCallViewModel by viewModel()
    private var fitnessDataManager : FitnessDataManager? = null
    private val data = FitnessData()
    private var stepGoal = 3000
    private var dashboardGridAdapter: DashboardFeaturesGridAdapter? = null

    private val mContext: Context? = null

    private var imagesArray: Array<String> = arrayOf(
        "https://picsum.photos/id/0/5616/3744.jpg",
        "https://picsum.photos/id/2/5616/3744.jpg",
        "https://picsum.photos/id/1/5616/3744.jpg",
        "https://picsum.photos/id/3/5616/3744.jpg",
        "https://picsum.photos/id/4/5616/3744.jpg")

    private var imagesArray1: Array<String> = arrayOf(
        "https://cdn.pixabay.com/photo/2020/02/13/10/29/bees-4845211__340.jpg",
        "https://cdn.pixabay.com/photo/2020/04/24/08/57/street-5085971__340.jpg",
        "https://cdn.pixabay.com/photo/2020/03/11/01/53/landscape-4920705__340.jpg",
        "https://cdn.pixabay.com/photo/2020/02/11/12/07/portofino-4839356__340.jpg")

    private var currentPage = 0
    private var slidingImageDots: Array<ImageView?> = arrayOf()
    private var slidingDotsCount = 0

/*    private val slidingCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(context!!,R.drawable.non_active_dot))
            }
            slidingImageDots[position]?.setImageDrawable(ContextCompat.getDrawable(context!!,R.drawable.active_dot))
        }
    }*/

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
        try {
            initialise()
            setClickable()
            setObserver()
            if ( activity != null ) {
                setUpSlidingViewPager()
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setUpSlidingViewPager() {
        try {
            slidingDotsCount = imagesArray.size
            slidingImageDots = arrayOfNulls(slidingDotsCount)
            val landingImagesAdapter = SlidingImagesAdapter(requireActivity(),slidingDotsCount)

            for (i in 0 until slidingDotsCount) {
                slidingImageDots[i] = ImageView(binding.slidingViewPager.context)
                slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context,R.drawable.non_active_dot))
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(8, 0, 8, 0)
                binding.sliderDots.addView(slidingImageDots[i], params)
            }

            slidingImageDots[0]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context,R.drawable.active_dot))

            binding.slidingViewPager.apply {
                adapter = landingImagesAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        for (i in 0 until slidingDotsCount) {
                            slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context,R.drawable.non_active_dot))
                        }
                        slidingImageDots[position]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context,R.drawable.active_dot))
                    }
                })
            }

            val handler = Handler()
            val update = Runnable {
                if (currentPage == slidingDotsCount) {
                    currentPage = 0
                }
                binding.slidingViewPager.setCurrentItem(currentPage++, true)
            }

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    handler.post(update)
                }
            },3000, 3000)

        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    private fun setObserver() {
        viewModel.currentSelectedPerson.observe(viewLifecycleOwner) {
//            viewModel.goToHRA(it)
        }
        viewModel.dashboardFeatureListUpper.observe(viewLifecycleOwner,{})
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
        binding.rvDashboardGrid.layoutManager = GridLayoutManager(requireContext(),3)
        binding.rvDashboardGrid.setExpanded(true)
        binding.rvDashboardGrid.adapter = dashboardGridAdapter

        viewModel.setHraData(binding.lblHraDesc,binding.lblHraDescDefault)
        viewModel.setFitnessData(binding.lblActivityTrackerDesc,binding.cardActivityTracker)
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

        binding.cardHra.setOnClickListener {
            viewModel.goToHRA()
        }

        binding.cardActivityTracker.setOnClickListener {
            if(viewModel.checkForFirstTime()) {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = requireContext().resources.getString(R.string.DISCLAIMER_TITLE)
                dialogData.message = requireContext().resources.getString(R.string.DISCLAIMER_MESSAGE_ACTIVITY_TRACKER)
                dialogData.btnRightName = requireContext().resources.getString(R.string.CONTINUE)
                dialogData.showLeftButton = false
                val defaultNotificationDialog = DefaultNotificationDialog(context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
                            }
                        }
                    }, dialogData
                )
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            }else{
                findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
            }
        }

    }

    override fun onScore(hraSummary: HRASummary?) {
        viewModel.getHraSummaryDetails()
        viewModel.hraSummary = hraSummary
        viewModel.refreshDashboardFeatureList()
        //viewModel.refreshDashboardFeatureListUpper()
        viewModel.setHraData(binding.lblHraDesc,binding.lblHraDescDefault)
        viewModel.setFitnessData(binding.lblActivityTrackerDesc,binding.cardActivityTracker)
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
                    //viewModel.refreshDashboardFeatureListUpper()
                    viewModel.setHraData(binding.lblHraDesc,binding.lblHraDescDefault)
                    viewModel.setFitnessData(binding.lblActivityTrackerDesc,binding.cardActivityTracker)
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
                    dialogData.title = requireContext().resources.getString(R.string.DISCLAIMER_TITLE)
                    dialogData.message = requireContext().resources.getString(R.string.DISCLAIMER_MESSAGE_ACTIVITY_TRACKER)
                    dialogData.btnRightName = requireContext().resources.getString(R.string.CONTINUE)
                    dialogData.showLeftButton = false
                    val defaultNotificationDialog = DefaultNotificationDialog(context,
                        object : DefaultNotificationDialog.OnDialogValueListener {
                            override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                                if (isButtonRight) {
                                    findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
                                }
                            }
                        }, dialogData
                    )
                    defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    defaultNotificationDialog.show()
                }else{
                    findNavController().navigate(R.id.action_dashboardFragment_to_fitnessActivity)
                }
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
            "REW"->{viewModel.toastMessage(resources.getString(R.string.COMING_SOON))}
            "BLOG"->{findNavController().navigate(R.id.action_dashboardFragment_to_blogsActivity)}
        }
    }


}