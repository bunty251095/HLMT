package com.caressa.home.ui.FamilyDoctor

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.home.R
import com.caressa.home.adapter.FamilyDoctorsAdapter
import com.caressa.home.databinding.ActivityFamilyDoctorListBinding
import com.caressa.home.viewmodel.FamilyDoctorViewModel
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class FamilyDoctorListActivity : BaseActivity() {

    private val viewModel : FamilyDoctorViewModel by viewModel()
    private lateinit var binding : ActivityFamilyDoctorListBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var familyDoctorsAdapter: FamilyDoctorsAdapter? = null
    private var animation : LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_family_doctor_list)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        from = intent.getStringExtra(Constants.FROM)!!
        Timber.e("from----->$from")
        setupToolbar()
        initialise()
        registerObservers()
        setClickable()
    }

    private fun initialise() {
        animation = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_animation_slide_from_bottom)
        startShimmer()
        familyDoctorsAdapter = FamilyDoctorsAdapter( this , viewModel , this )
        binding.rvFamilyDoctorsList.layoutAnimation = animation
        binding.rvFamilyDoctorsList.adapter = familyDoctorsAdapter
        if ( from.equals("Back",ignoreCase = true) ) {
            viewModel.getFamilyDoctorsList()
        } else {
            viewModel.callGetFamilyDoctorsListApi(this)
        }
    }

    private fun registerObservers() {

        viewModel.familyDoctorsList.observe( this , {
            if ( it != null ) {
                val familyDoctorsList = it
                if (familyDoctorsList.isNotEmpty()) {
                    Timber.e("RecordCount----->${familyDoctorsList.size}")
                    binding.rvFamilyDoctorsList.visibility = View.VISIBLE
                    binding.layoutNoDoctors.visibility = View.GONE
                    binding.rvFamilyDoctorsList.layoutAnimation = animation
                    familyDoctorsAdapter!!.updateFamilyDoctorsList(familyDoctorsList)
                    binding.rvFamilyDoctorsList.scheduleLayoutAnimation()
                } else {
                    binding.rvFamilyDoctorsList.visibility = View.GONE
                    binding.layoutNoDoctors.visibility = View.VISIBLE
                }
                stopShimmer()
            }
        })
        viewModel.listDoctors.observe( this , {})
        viewModel.removeDoctor.observe( this , {})
    }

    private fun setClickable() {

        binding.btnAddDoctor.setOnClickListener {
            val intent = Intent()
            intent.putExtra("from","Add")
            intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_DOCTOR_ADD_UPDATE)
            startActivity(intent)
        }

    }

    private fun startShimmer() {
        binding.rvFamilyDoctorsShimmer.startShimmer()
        binding.rvFamilyDoctorsShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.rvFamilyDoctorsShimmer.stopShimmer()
        binding.rvFamilyDoctorsShimmer.visibility = View.GONE
    }

/*    fun noDataView() {
        binding.layoutDoctors.visibility = View.GONE
        binding.txtNoDoctors.visibility = View.VISIBLE
    }*/

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        toolbar_title!!.text = resources.getString(R.string.TITLE_FAMILY_DOCTORS)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow_white)
        toolbar_home.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.primaryColor(),BlendModeCompat.SRC_ATOP)

        toolbar_home.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
    }

}