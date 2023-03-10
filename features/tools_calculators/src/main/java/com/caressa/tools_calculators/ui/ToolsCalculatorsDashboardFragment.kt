package com.caressa.tools_calculators.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.TrackersDashboardAdapter
import com.caressa.tools_calculators.databinding.FragmentToolsCalculatorsDashboardBinding
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.model.TrackerDashboardModel
import com.caressa.model.toolscalculators.UserInfoModel
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import timber.log.Timber

class ToolsCalculatorsDashboardFragment : BaseFragment(),KoinComponent,TrackersDashboardAdapter.OnCalculatorClickListener {

    private lateinit var binding : FragmentToolsCalculatorsDashboardBinding
    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private val userInfoModel = UserInfoModel.getInstance()!!
    private var trackersDashboardAdapter: TrackersDashboardAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentToolsCalculatorsDashboardBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.TOOLS_CALCULATORS_HOME_SCREEN)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            initialise()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        viewModel.getAllTrackersList()
        trackersDashboardAdapter = TrackersDashboardAdapter(requireContext(), this)
        binding.rvTrackerDashboard.adapter = trackersDashboardAdapter

        if ( !userInfoModel.isDataLoaded ) {
            viewModel.getMedicalProfileSummary(true)
        }

        viewModel.medicalProfileSummary.observe(viewLifecycleOwner, {})
        viewModel.labRecords.observe(viewLifecycleOwner, {})
        viewModel.startQuiz.observe(viewLifecycleOwner, {})
    }

    override fun onCalculatorSelection(trackerDashboardModel: TrackerDashboardModel, view: View) {
        calculatorDataSingleton!!.clearData()
        when (trackerDashboardModel.code) {

            "HAC" -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_HEART_AGE)
                //SessionInfoSingleton.getInstance().setQuizId("5")
            }
            "DC" -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_DIABETES)
                //SessionInfoSingleton.getInstance().setQuizId("8")
            }
            "HC" -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_HYPERTENSION)
                //SessionInfoSingleton.getInstance().setQuizId("9")
            }
            "SAC" -> {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = requireContext().resources.getString(R.string.DISCLAIMER_TITLE)
                dialogData.message = requireContext().resources.getString(R.string.DISCLAIMER_MESSAGE_HRA)
                dialogData.btnRightName = requireContext().resources.getString(R.string.CONTINUE)
                dialogData.showLeftButton = false
                val defaultNotificationDialog = DefaultNotificationDialog(context,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_STRESS_ANXIETY)
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
                //SessionInfoSingleton.getInstance().setQuizId("7")
            }
            "SPC" -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_SMART_PHONE)
                //SessionInfoSingleton.getInstance().setQuizId("4")
            }
            "DDC" -> {
                view.findNavController()
                    .navigate(R.id.action_toolsCalculatorsDashboardFragment_to_dueDateInputFragment)
            }
            else -> Utilities.toastMessageShort(context, "Under Development")

        }
    }

}
