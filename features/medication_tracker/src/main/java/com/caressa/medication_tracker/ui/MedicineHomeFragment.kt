package com.caressa.medication_tracker.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.databinding.FragmentMedicationHomeBinding
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

open class MedicineHomeFragment : BaseFragment() {

    val viewModel: MedicineTrackerViewModel by viewModel()
    private lateinit var binding: FragmentMedicationHomeBinding

    private var from = ""
    private var selectedDate = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            selectedDate = it.getString(Constants.DATE,"")!!
            Timber.e("from,selectedDate--->$from,$selectedDate")
        }

        when( from ) {
            Constants.NOTIFICATION_ACTION -> {
                val bundle = Bundle()
                bundle.putString(Constants.FROM,Constants.NOTIFICATION_ACTION)
                bundle.putString(Constants.DATE,selectedDate)
                findNavController().navigate(R.id.action_medicineHome_to_medicineDashboardFragment,bundle)
            }
            Constants.TRACK_PARAMETER -> {
                val bundle = Bundle()
                bundle.putString(Constants.FROM, Constants.TRACK_PARAMETER)
                findNavController().navigate(R.id.action_medicineHome_to_addMedicineFragment,bundle)
            }
        }

/*        if ( from.equals(Constants.NOTIFICATION_ACTION,ignoreCase = true) ) {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,Constants.NOTIFICATION_ACTION)
            bundle.putString(Constants.DATE,selectedDate)
            findNavController().navigate(R.id.action_medicineHome_to_medicineDashboardFragment,bundle)
        }*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMedicationHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initialise() {
        viewModel.fetchMedicationList()
        viewModel.medicineList.observe( viewLifecycleOwner , Observer {})

        binding.layoutDashboard.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_bright_blue))
                ImageViewCompat.setImageTintList(binding.imgDashboard, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblDashboard.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.txtDashboard.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgDashboard, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.vivant_icon_warm_grey)))
                binding.lblDashboard.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.txtDashboard.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutAddMedicine.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_marigold))
                ImageViewCompat.setImageTintList(binding.imgAddMedicine, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblAddMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.txtAddMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgAddMedicine, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.vivant_icon_warm_grey)))
                binding.lblAddMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.txtAddMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }

        binding.layoutMyMedications.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.vivant_soft_pink))
                ImageViewCompat.setImageTintList(binding.imgDueMedicine, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.white)))
                binding.lblDueMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
                binding.txtDueMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
                ImageViewCompat.setImageTintList(binding.imgDueMedicine, ColorStateList.valueOf(ContextCompat.getColor(requireContext(),R.color.vivant_icon_warm_grey)))
                binding.lblDueMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_title_charcoal_grey))
                binding.txtDueMedicine.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_charcoal_grey_55))
            }
            false
        }
    }

    private fun setClickable() {

        binding.layoutDashboard.setOnClickListener {
            it.findNavController().navigate(R.id.action_medicineHome_to_medicineDashboardFragment)
        }

        binding.layoutAddMedicine.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.MEDICATION_ID,0)
            it.findNavController().navigate(R.id.action_medicineHome_to_addMedicineFragment)
        }

        binding.layoutMyMedications.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.FROM,"Home")
            it.findNavController().navigate(R.id.action_medicineHome_to_myMedicationsFragment,bundle)
        }

        binding.imgBack.setOnClickListener {
            //requireActivity().onBackPressed()
            navigateToHomeScreen()
        }

    }

    private fun navigateToHomeScreen() {
        val intentToPass = Intent()
        intentToPass.component = ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
        intentToPass.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intentToPass)
    }

}