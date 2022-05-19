package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.*
import com.caressa.common.utils.PermissionUtil.AppPermissionListener
import com.caressa.hra.HraHomeActivity
import com.caressa.hra.R
import com.caressa.hra.adapter.HraLabTestsAdapter
import com.caressa.hra.databinding.ActivityHraSummaryBinding
import com.caressa.model.hra.HraLabTest
import com.caressa.hra.viewmodel.HraSummaryViewModel
import com.caressa.model.hra.HraAssessmentSummaryModel
import kotlinx.android.synthetic.main.toolbar_hra_summary.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HraSummaryActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: ActivityHraSummaryBinding
    private val viewModel: HraSummaryViewModel by viewModel()

    private val appColorHelper = AppColorHelper.instance!!

    private var context : Context?= null
    private var activity : Activity?= null
    private var personId = ""
    private var hraCutOff = 0
    private var wellnessscore = 0
    private var currentHRAHistoryID = 0
    private var wellnessScoreProgress = 0
    private var color : Int = appColorHelper.primaryColor()
    private var observation = ""
    private var hraLabTestsAdapter: HraLabTestsAdapter? = null
    private var labTestsDoctorSuggestedAdapter: HraLabTestsAdapter? = null

    private val permissionUtil = PermissionUtil

    private val permissionListener = object : AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Timber.e("$isGranted")
            if ( isGranted ) {
                viewModel.callDownloadReport()
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hra_summary)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        context = this
        activity = this
        FirebaseHelper.logScreenEvent(FirebaseConstants.HRA_SUMMERY_SCREEN)
        try {
            val i = intent
            if ( i.hasExtra(Constants.PERSON_ID) ) {
                personId = i.getStringExtra(Constants.PERSON_ID)!!
            }
            setUpToolbar()
            initLayout()
            registerObservers()
            setClickable()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    private fun initLayout() {
        try {
            viewModel.getMedicalProfileSummary(true,personId)
            viewModel.getAssessmentSummary(true,personId)
            viewModel.getListRecommendedTests(true,personId)
            setWellnessScore()
            setHraRiskSummary()
            setLabTestsList()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    private fun registerObservers() {
        viewModel.medicalProfileSummary.observe( this , { })
        viewModel.assessmentSummary.observe( this , { })
        viewModel.listRecommendedTests.observe( this , { })
    }

    private fun setClickable() {

        binding.btnRestartHra.setOnClickListener {
            viewModel.clearPreviousQuesDataAndTable()
            val intentToPass = Intent(this, HraHomeActivity::class.java)
            intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intentToPass)
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_RESTART_CLICK)
        }

        binding.btnDownloadReport.setOnClickListener {
            val permissionResult = permissionUtil.checkStoragePermission(permissionListener,this)
            if (permissionResult) {
                viewModel.callDownloadReport()
            }
        }

        /*binding.btnBookPackage.setOnClickListener {
            val intentToPass = Intent()
            intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.COMMOM_WEBVIEW)
            intentToPass.putExtra(Constants.Toobar_Title, Constants.TOOLBAR_HEALTH_PACKAGES)
            intentToPass.putExtra(Constants.WEB_URL, Constants.strBookAppointmentURL)
            intentToPass.putExtra(Constants.HAS_COOKIES, true)
            startActivity(intentToPass)
        }*/

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setWellnessScore() {

        binding.indicatorScore.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        viewModel.hraSummaryDetails.observe(this, {
            if (it != null) {
                val wellnessSummary = it
                //Timber.i("HraSummaryDetails :- " + wellnessSummary)

                if ( !Utilities.isNullOrEmptyOrZero( wellnessSummary.currentHRAHistoryID.toString() ) ) {
                    currentHRAHistoryID = wellnessSummary.currentHRAHistoryID
                }
                if ( !Utilities.isNullOrEmptyOrZero( wellnessSummary.hraCutOff ) ) {
                    hraCutOff = wellnessSummary.hraCutOff.toInt()
                }

                wellnessscore = wellnessSummary.scorePercentile.toInt()

                wellnessScoreProgress = wellnessscore
                if (wellnessScoreProgress <= 0) {
                    wellnessScoreProgress = 0
                }

                Timber.i("WellnessScore,HrACutOff,CurrentHRAHistoryID--->$wellnessscore,$hraCutOff,$currentHRAHistoryID")

                when {
                    wellnessscore in 0..15 -> {
                        observation = resources.getString(R.string.OBS_HIGH_RISK)
                        color = R.color.high_risk
                    }

                    wellnessscore in 16..45 -> {
                        observation = resources.getString(R.string.OBS_MODERATE_RISK)
                        color = R.color.moderate_risk
                    }

                    wellnessscore in 46..85 -> {
                        observation = resources.getString(R.string.OBS_HEALTHY)
                        color = R.color.healthy_risk
                    }

                    wellnessscore > 85 -> {
                        observation = resources.getString(R.string.OBS_OPTIMUM)
                        color = R.color.optimum_risk
                    }
                }

                setColors(color)
                //binding.indicatorScore.progress = wellnessscore
                binding.indicatorScore.setProgressWithAnimation(wellnessscore.toDouble())
                binding.txtScore.text = wellnessscore.toString()
                binding.txtObservation.text = observation

                when {
                    currentHRAHistoryID == 0 -> {
                        binding.layoutScoreSummary.visibility = View.GONE
                        binding.txtUnableToCalculateScore.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.text = resources.getString(R.string.WELLNESS_DESC_CURRENT_HRA_HISTORY_ID)
                    }
                    hraCutOff == 0 -> {
                        binding.layoutScoreSummary.visibility = View.GONE
                        binding.txtUnableToCalculateScore.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.text = resources.getString(R.string.WELLNESS_DESC_HRA_CUTOFF)
                    }
                    wellnessscore <= 0 -> {
                        binding.layoutScoreSummary.visibility = View.GONE
                        binding.txtUnableToCalculateScore.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.text = resources.getString(R.string.WELLNESS_DESC_SCORE_ZERO_NEGATIVE)
                    }
                    else -> {
                        binding.layoutScoreSummary.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.visibility = View.GONE
                    }
                }

            }
        })
    }

    private fun setHraRiskSummary() {

        val atRiskList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        val needImprovementsList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        val moderateList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        val highList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        binding.layoutRiskSummery.visibility = View.GONE
        viewModel.hraAssessmentSummaryDetails.observe(this, {
            if (it != null) {
                for (i in it) {
                    if (i.riskLevel == "At Risk") {
                        if ( !atRiskList.contains(i) ) {
                            atRiskList.add(i)
                        }
                    }
                    if (i.riskLevel == "Need improvements") {
                        if ( !atRiskList.contains(i) ) {
                            needImprovementsList.add(i)
                        }
                    }
                    if (i.riskLevel == "Moderate") {
                        if ( !atRiskList.contains(i) ) {
                            moderateList.add(i)
                        }
                    }
                    if (i.riskLevel == "High") {
                        if ( !atRiskList.contains(i) ) {
                            highList.add(i)
                        }
                    }
                }
                //Timber.i("AtRiskList---->" + atRiskList.toString())
                //Timber.i("NeedImprovementsList---->" + needImprovementsList.toString())
                //Timber.i("ModerateList---->" + moderateList.toString())
                //Timber.i("HighList---->" + highList.toString())
                // ************************ At Risk data ************************
                val sbLow = StringBuilder()
                var separatorLow = ""
                for (assessmentIndex in atRiskList.indices) {
                    val strRiskLevel = atRiskList[assessmentIndex].riskCategory
                    sbLow.append(separatorLow + strRiskLevel)
                    separatorLow = ", "
                }
                val strLow = sbLow.toString()
                Timber.i("AtRisk---->%s", strLow)
                binding.layoutAtRisk.visibility = View.VISIBLE
                if (!Utilities.isNullOrEmpty(strLow)) {
                    binding.txtAtRisk.text = strLow
                } else {
                    binding.txtAtRisk.text = "-"
                    binding.layoutAtRisk.visibility = View.GONE
                }
                // ************************ At Risk data ************************

                //************************ Moderate Risk data ************************
                val sbNeedImprovements = StringBuilder()
                var separatorNeedImprovements = ""
                for (assessmentIndex in needImprovementsList.indices) {
                    val strRiskLevel = needImprovementsList[assessmentIndex].riskCategory
                    sbNeedImprovements.append(separatorNeedImprovements + strRiskLevel)
                    separatorNeedImprovements = ", "
                }

                val sbModerate = StringBuilder()
                var separatorModerate = ""
                for (assessmentIndex in moderateList.indices) {
                    val strRiskLevel = moderateList[assessmentIndex].riskCategory
                    sbModerate.append(separatorModerate + strRiskLevel)
                    separatorModerate = ", "
                }

                var strNeedImprovements = sbNeedImprovements.toString()
                val strModerate = sbModerate.toString()
                if (!Utilities.isNullOrEmpty(strNeedImprovements) && !Utilities.isNullOrEmpty(strModerate)) {
                    strNeedImprovements += ", "
                }
                Timber.i("Moderate---->%s", (strNeedImprovements + strModerate))
                binding.txtModerateDesc.text = (strNeedImprovements + strModerate)
                binding.layoutModerateRisk.visibility = View.VISIBLE
                if (Utilities.isNullOrEmpty(strNeedImprovements) && Utilities.isNullOrEmpty(strModerate)) {
                    binding.txtModerateDesc.text = "-"
                    binding.layoutModerateRisk.visibility = View.GONE
                }
                //************************ Moderate Risk data ************************

                //************************ High Risk data ************************
                val sbHigh = StringBuilder()
                var separatorHigh = ""
                for (assessmentIndex in highList.indices) {
                    val strRiskLevel = highList[assessmentIndex].riskCategory
                    sbHigh.append(separatorHigh + strRiskLevel)
                    separatorHigh = ", "
                }
                val strHigh = sbHigh.toString()
                Timber.i("High :-%s", strHigh)
                binding.layoutHighRisk.visibility = View.VISIBLE
                if (!Utilities.isNullOrEmpty(strHigh)) {
                    binding.txtHighDesc.text = strHigh
                } else {
                    binding.txtHighDesc.text = "-"
                    binding.layoutHighRisk.visibility = View.GONE
                }
                binding.layoutRiskSummery.visibility = View.VISIBLE
                //************************ High Risk data ************************
                if (Utilities.isNullOrEmpty(strLow) && Utilities.isNullOrEmpty(strModerate) &&
                    Utilities.isNullOrEmpty(strNeedImprovements) && Utilities.isNullOrEmpty(strHigh)) {
                    binding.layoutRiskSummery.visibility = View.GONE
                    binding.txtCongratsMsg.visibility = View.VISIBLE
                } else {
                    binding.layoutRiskSummery.visibility = View.VISIBLE
                    binding.txtCongratsMsg.visibility = View.GONE
                }
            }
        })
    }

    private fun setLabTestsList() {
        binding.layoutLabTests.visibility = View.GONE

        viewModel.hraRecommendedTests.observe(this, {
            val labTestsDetailsList = it
            Timber.e("TotalTests--->%s", labTestsDetailsList.size.toString())

            val labTestsCommon  = labTestsDetailsList.filter { labTest ->
                !labTest.Frequency.equals("As suggested by your doctor",ignoreCase = true)
            }
            val labTestsDoctorSuggested  = labTestsDetailsList.filter { labTest ->
                labTest.Frequency.equals("As suggested by your doctor",ignoreCase = true)
            }

            if ( labTestsDetailsList.isNotEmpty() ) {
                binding.layoutLabTests.visibility = View.VISIBLE

                if ( labTestsCommon.isNotEmpty() ) {
                    binding.cardCommonLabtests.visibility = View.VISIBLE
                    hraLabTestsAdapter = HraLabTestsAdapter(labTestsCommon, viewModel, this,this)
                    binding.rvLabTests.adapter = hraLabTestsAdapter
                    binding.rvLabTests.setExpanded(true)
                } else {
                    binding.cardCommonLabtests.visibility = View.GONE
                }

                if ( labTestsDoctorSuggested.isNotEmpty() ) {
                    binding.cardDoctorSuggestedLabtests.visibility = View.VISIBLE
                    labTestsDoctorSuggestedAdapter = HraLabTestsAdapter(labTestsDoctorSuggested, viewModel, this,this)
                    binding.rvLabTestDoctorSuggested.adapter = labTestsDoctorSuggestedAdapter
                    binding.rvLabTestDoctorSuggested.setExpanded(true)
                } else {
                    binding.cardDoctorSuggestedLabtests.visibility = View.GONE
                }

            } else {
                binding.layoutLabTests.visibility = View.GONE
            }

        })
    }

    private fun setColors( color : Int ) {
        binding.indicatorScore.progressColor = ContextCompat.getColor(this,color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.indicatorScore.mThumb!!.setTint(ContextCompat.getColor(this,color))
        }
        //binding.layoutScoreDetails.backgroundTintList = ContextCompat.getColorStateList(this,color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.layoutScoreDetails.background.colorFilter = BlendModeColorFilter(
                ContextCompat.getColor(this,color),BlendMode.SRC_ATOP)
        } else {
            binding.layoutScoreDetails.background.setColorFilter(ContextCompat.getColor(this,color),
                PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun showLabTestDetailsDialog( labTest : HraLabTest) {
        showDialog(
            listener = this,
            title = labTest.LabTestName,
            message = labTest.Reasons,
            showLeftBtn = false)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar_summary)
        toolbar_title_summary.text = resources.getString(R.string.TITLE_HRA_SUMMARY)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        toolbar_summary.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)

        toolbar_summary.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //viewModel.clearPreviousQuesDataAndTable()
        if ( viewModel.personId != personId ) {
            viewModel.clearHraDataTables()
        } else {
            viewModel.clearPreviousQuesDataAndTable()
        }
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.e("requestCode-> $requestCode")
        Timber.e("resultCode-> $resultCode")
        Timber.e("data-> $data")
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQ_CODE_SAF) {
                if (data != null) {
                    //this is the uri user has provided us for folder Access
                    val treeUri: Uri = data.data!!
                    permissionUtil.releasePermissions(treeUri,this,storageAccessListener)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val per = Environment.isExternalStorageManager()
                Timber.e("requestCode---> $requestCode")
                Timber.e("permissionGranted---> $per")
                when(requestCode) {
                    Constants.REQ_CODE_STORAGE -> {
                        if (per) {
                            permissionListener.isPermissionGranted(true)
                        } else {
                            Utilities.toastMessageShort(this,resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

}