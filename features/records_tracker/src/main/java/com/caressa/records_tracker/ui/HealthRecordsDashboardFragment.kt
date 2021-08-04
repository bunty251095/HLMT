package com.caressa.records_tracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.PermissionUtil
import com.caressa.common.utils.Utilities
import com.caressa.records_tracker.R
import com.caressa.records_tracker.databinding.FragmentHealthRecordsDashboardBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HealthRecordsDashboardFragment : BaseFragment() {

    private val viewModel : HealthRecordsViewModel by viewModel()
    private lateinit var binding : FragmentHealthRecordsDashboardBinding

    private var from = ""
    private var action = ""
    private val permissionListener = object : PermissionUtil.AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Timber.e("$isGranted")
            if ( isGranted ) {
                Timber.e("action-> $action")
                navigateTo(action)
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            Timber.e("from--->$from")
        }
        when( from ) {
            Constants.MEDICATION -> {
                val bundle = Bundle()
                bundle.putString(Constants.FROM, Constants.MEDICATION)
                bundle.putString(Constants.CODE, "ALL")
                findNavController().navigate(R.id.action_healthRecordsDashboardFragment_to_documentTypeFragment,bundle)
            }
            Constants.TRACK_PARAMETER -> {
                val bundle = Bundle()
                bundle.putString(Constants.FROM, Constants.TRACK_PARAMETER)
                bundle.putString(Constants.CODE, "ALL")
                findNavController().navigate(R.id.action_healthRecordsDashboardFragment_to_documentTypeFragment,bundle)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHealthRecordsDashboardBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {

        binding.layoutUploadRecords.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_dark_sky_blue))
                ImageViewCompat.setImageTintList(binding.imgUpload,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgUpload,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.colorPrimary)))
                binding.lblUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descUploadRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutViewRecords.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_orange_yellow))
                ImageViewCompat.setImageTintList(binding.imgView,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgView,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.colorPrimary)))
                binding.lblViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descViewRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutShareRecords.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_soft_pink))
                ImageViewCompat.setImageTintList(binding.imgShare,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.descShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgShare,
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.colorPrimary)))
                binding.lblShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descShareRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutDigitizeRecords.setOnTouchListener { v: View, event: MotionEvent ->
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
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.colorPrimary)))
                binding.lblDigitizeRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.descDigitizeRecord.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

    }

    private fun setClickable() {

        binding.layoutUploadRecords.setOnClickListener {
            routeToScreen(Constants.UPLOAD)
        }

        binding.layoutViewRecords.setOnClickListener {
            routeToScreen(Constants.VIEW)
        }

        binding.layoutShareRecords.setOnClickListener {
            routeToScreen(Constants.SHARE)
        }

        binding.layoutDigitizeRecords.setOnClickListener {
            routeToScreen(Constants.DIGITIZE)
        }

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    private fun routeToScreen(act: String) {
        action = act
        Timber.e("action-> $act")
        val isGranted = PermissionUtil().getInstance()!!.checkStoragePermissionFromFragment(
            permissionListener,requireContext(),this)
        if (isGranted) {
            navigateTo(act)
        }
    }

    private fun launchUploadRecords() {
        val bundle = Bundle()
        bundle.putString(Constants.CODE,"ALL")
        bundle.putString(Constants.FROM,Constants.DASHBOARD)
        findNavController().navigate(R.id.action_healthRecordsDashboardFragment_to_documentTypeFragment,bundle)
    }

    private fun launchViewAndShareRecords() {
        val bundle = Bundle()
        bundle.putString(Constants.CODE,"ALL")
        bundle.putString(Constants.FROM,Constants.DASHBOARD)
        findNavController().navigate(R.id.action_healthRecordsDashboardFragment_to_viewRecordsFragment,bundle)
    }

    private fun launchDigitizeRecords() {
        val bundle = Bundle()
        bundle.putString(Constants.CODE,"LAB")
        findNavController().navigate(R.id.action_healthRecordsDashboardFragment_to_digitizedRecordsListFragment,bundle)
    }

    private fun navigateTo(act: String) {
        Timber.e("action---> $act")
        when(act) {
            Constants.UPLOAD -> launchUploadRecords()
            Constants.VIEW,Constants.SHARE -> launchViewAndShareRecords()
            Constants.DIGITIZE -> launchDigitizeRecords()
        }
    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

}
