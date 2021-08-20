package com.caressa.home.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.databinding.ActivityFeedbackBinding
import com.caressa.home.viewmodel.DashboardViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class FeedbackActivity : BaseActivity() {

    private val viewModel : DashboardViewModel by viewModel()
    private lateinit var binding : ActivityFeedbackBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        FirebaseHelper.logScreenEvent(FirebaseConstants.FEEDBACK_SCREEN)
        setClickable()
    }

    private fun setClickable() {

        binding.btnSubmitFeedback.setOnClickListener {
            val feedback = binding.edtAppFeedback.text.toString()
            if ( !Utilities.isNullOrEmpty(feedback) ) {
                viewModel.callSaveFeedbackApi(this,feedback)
            } else {
                Utilities.toastMessageShort(this,resources.getString(R.string.ERROR_PLEASE_ENTER_FEEDBACK_TO_SUBMIT))
            }
        }

        binding.imgBackFeedback.setOnClickListener {
            onBackPressed()
        }

        viewModel.saveFeedback.observe( this , {})
    }

}