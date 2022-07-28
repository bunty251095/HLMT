package com.caressa.home.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.LocaleHelper
import com.caressa.home.R
import com.caressa.home.adapter.LanguageAdapter
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ActivityLanguageBinding
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.home.viewmodel.SettingsViewModel
import com.caressa.model.tempconst.Configuration
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.Exception

class LanguageActivity : BaseActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private val backGroundCallViewModel: BackgroundCallViewModel by viewModel()
    private lateinit var binding: ActivityLanguageBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var languageAdapter:LanguageAdapter? = null
    private var languageDataSet:DataHandler.LanguageModel = DataHandler.LanguageModel("English","en")

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        try {
            from = intent.getStringExtra(Constants.FROM)!!
            Timber.e("from----->$from")
            setupToolbar()
            initialise()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
//        if(LocaleHelper.getLanguage(this).equals("vi",true)) {
//            viewModel.getSettingsOptionList()
//        }else{
//            viewModel.getSettingsOptionList1("English")
//        }
        viewModel.getLanguageList()
        languageAdapter = LanguageAdapter(object :LanguageAdapter.OnItemClickListener {

            override fun onItemSelection(position: Int, data: DataHandler.LanguageModel) {
                Timber.i("LanguageCode=> "+data.language+" :: "+data.languageCode)
                languageDataSet = data

            }

        })
        binding.rvLanguageList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvLanguageList.adapter = languageAdapter

        binding.btnSave.setOnClickListener {
            viewModel.callUpdateProfileListApi(languageDataSet.languageCode)
//            var intent = Intent(this,SettingsActivity::class.java)
//            intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(intent)
        }

        viewModel.updateProfile.observe(this) {
            if (it != null) {
                if (it.isUpdated.equals("true", true)) {
                    if (languageDataSet.languageCode.equals("my", true)) {
                        LocaleHelper.setLocale(this@LanguageActivity, "ms")
                        Configuration.LanguageCode = "my"
                    } else {
                        LocaleHelper.setLocale(this@LanguageActivity, "en")
                        Configuration.LanguageCode = "en"
                    }
                    viewModel.callRefreshTokenApi()
                }
            }
        }

        viewModel.refreshToken.observe(this) {
            if (it != null && !it.response.data.context.isNullOrEmpty()) {
                Timber.i("RefreshToken----->" + it)
                if (!it.response.data.context.isNullOrEmpty()) {
                    try {
                        viewModel.refreshToken(it.response.data.context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                //backGroundCallViewModel.getParameterList()
                //backGroundCallViewModel.getLabRecordList("")
                //backGroundCallViewModel.getLabRecordVitalsList("")

/*                backGroundCallViewModel.getBMIHistory("")
                backGroundCallViewModel.getBloodPressureHistory("")
                backGroundCallViewModel.getWHRHistory("")*/

//                viewModel.toastMessage(resources.getString(R.string.LANGUAGE_API_SUCCESS_MSG))

                if (from.equals(Constants.HOME, ignoreCase = true)) {
                    val intentToPass = Intent()
                    intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                    intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intentToPass)
                } else {
                    val intent = Intent(this, SettingsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }

        backGroundCallViewModel.paramList.observe(this) {}
//        backGroundCallViewModel.bmiHistoryList.observe(this, {})
//        backGroundCallViewModel.bloodPressureHistoryList.observe(this, {})
//        backGroundCallViewModel.whrHistoryList.observe(this, {})
        backGroundCallViewModel.labRecordList.observe(this) {}
        backGroundCallViewModel.labRecordVitalsList.observe(this) {}
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        toolbar_title.text = resources.getString(R.string.LANGUAGE)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)

        toolbar_home.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)

        toolbar_home.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}