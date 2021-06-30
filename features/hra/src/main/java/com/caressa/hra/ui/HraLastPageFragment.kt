package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.databinding.FragmentHraLastPageBinding
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HraLastPageFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding : FragmentHraLastPageBinding
    private val viewModel: HraViewModel by viewModel()

    private var viewPagerActivity : HraQuestionsActivity?  = null
    private  val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraLastPageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        if( userVisibleHint ) {
            initialise()
            setClickable()
        }
        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HRA_COMPLETED_EVENT)
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if ( isVisibleToUser && isResumed ) {
            initialise()
            setClickable()
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun initialise() {
        viewPagerActivity = (activity as HraQuestionsActivity)
        Timber.e("qCode----->$qCode")
        Timber.e("CurrentPageNo--->" +viewPagerActivity!!.getCurrentScreen() )
        val prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen()-1)
        Timber.e("prevAnsList---> $prevAnsList")
    }

    private fun setClickable() {

        binding.btnViewReport.setOnClickListener {
            val intent = Intent(context, HraSummaryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(Constants.PERSON_ID,viewPagerActivity!!.personId)
            startActivity(intent)
        }

    }

}