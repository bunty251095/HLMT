package com.caressa.hra.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.hra.R
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.*
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.databinding.FragmentSelectFamilyMemberBinding
import com.caressa.model.hra.Option
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SelectFamilyMemberFragment : BaseFragment(), DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: HraViewModel by viewModel()
    private lateinit var binding : FragmentSelectFamilyMemberBinding

    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var relativeId = ""
    private var relativeName = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_selectFamilyMemberFragment_to_introductionFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSelectFamilyMemberBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_FAMILY_MEMBER_SELECTION_SCREEN)
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        binding.rgSelection.clearCheck()
        viewModel.getUserRelatives()

        if ( viewModel.adminPersonId != viewModel.personId ) {
            binding.btnAddFamilyMember.visibility = View.INVISIBLE
        } else {
            binding.btnAddFamilyMember.visibility = View.VISIBLE
        }
    }

    private fun registerObservers() {
        viewModel.hraStart.observe(viewLifecycleOwner , { })
    }

    private fun setClickable() {

        binding.rgSelection.setOnCheckedChangeListener { _, _ ->
            relativeId = ViewUtils.getRadioSelectedValueTag(binding.rgSelection)
            relativeName = ViewUtils.getRadioButtonSelectedValue(binding.rgSelection)
            if ( !Utilities.isNullOrEmptyOrZero(relativeId) ) {
                Timber.e("Selected RelativeId,RelativeName--->$relativeId,$relativeName")
                if ( viewModel.personId != relativeId ) {
                    showSwitchProfileDialog()
                } else {
                    viewModel.clearSavedQuestionsData()
                    viewModel.startHra(relativeId,relativeName)
                }
            }
        }

        binding.btnAddFamilyMember.setOnClickListener {
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE)
            //launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            launchIntent.putExtra(Constants.FROM, Constants.HRA)
            startActivity(launchIntent)
        }

    }

    private fun showSwitchProfileDialog() {
        showDialog(
            listener = this,
            title = resources.getString(R.string.SWITCH_PROFILE),
            message = resources.getString(R.string.SWITCH_PROFILE_DESC),
            rightText = resources.getString(R.string.PROCEED) )
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if ( isButtonRight ) {
            viewModel.switchProfile(relativeId)
            viewModel.startHra(relativeId,relativeName)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.rgSelection.removeAllViews()
        viewModel.getUserRelatives()
    }

}