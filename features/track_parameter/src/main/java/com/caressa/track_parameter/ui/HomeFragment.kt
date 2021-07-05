package com.caressa.track_parameter.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.caressa.track_parameter.R
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.track_parameter.databinding.HomeFragmentBinding
import com.caressa.track_parameter.viewmodel.ParameterHomeViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : BaseFragment(){

    private val viewModel: ParameterHomeViewModel by viewModel()
    private lateinit var binding: HomeFragmentBinding

    override fun getViewModel(): BaseViewModel = viewModel

    private var from = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("from,selectedDate--->$from")
        }

        when( from ) {
            "DashboardBP" -> {
                viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToUpdateParameter(
                    "BLOODPRESSURE","true"))
            }
            "DashboardBMI" -> {
                viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToUpdateParameter(
                    "BMI","true"))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {

        binding.layoutSelectParam.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_bright_blue))
                ImageViewCompat.setImageTintList(binding.imgUpload,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgUpload,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.hlmt_warm_grey)))
                binding.lblUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutUpdateParam.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_marigold))
                ImageViewCompat.setImageTintList(binding.imgView,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgView,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.hlmt_warm_grey)))
                binding.lblViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutDashboardParam.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_dusky_blue))
                ImageViewCompat.setImageTintList(binding.imgShare,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgShare,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.hlmt_warm_grey)))
                binding.lblShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutHistoryParam.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_green_blue_two))
                ImageViewCompat.setImageTintList(binding.imgDigitize,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblDigitizeRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descDigitizeRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgDigitize,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.hlmt_warm_grey)))
                binding.lblDigitizeRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descDigitizeRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

    }

    private fun setClickable() {

        binding.layoutSelectParam.setOnClickListener{
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToSelectParameterFragment())
        }

        binding.layoutUpdateParam.setOnClickListener {
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToUpdateParameter())
        }

        binding.layoutDashboardParam.setOnClickListener {
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToDashboardFragmentNew())
        }

        binding.layoutHistoryParam.setOnClickListener {
            viewModel.navigateParam(HomeFragmentDirections.actionHomeFragmentToHistoryFragment())
        }

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

/*    private fun initialise() {
//        viewModel.getParameterList()
//        viewModel.paramList.observe(this, Observer {  })
//        viewModel.getLabRecordList()
//        viewModel.getBMIHistory()
//        viewModel.getWHRHistory()
//        viewModel.getBloodPressureHistory()
    }*/

}