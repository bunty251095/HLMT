package com.caressa.medication_tracker.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.showDialog
import com.caressa.common.view.SpinnerAdapter
import com.caressa.common.view.SpinnerModel
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.adapter.MyMedicationsAdapter
import com.caressa.medication_tracker.common.MedicationTrackerHelper
import com.caressa.medication_tracker.databinding.FragmentMyMedicationsBinding
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.entity.MedicationEntity
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.*

class MyMedicationsFragment : BaseFragment(),DefaultNotificationDialog.OnDialogValueListener,KoinComponent {

    private val viewModel: MedicineTrackerViewModel by viewModel()
    private lateinit var binding: FragmentMyMedicationsBinding

    private val appColorHelper = AppColorHelper.instance!!

    private val medicationTrackerHelper : MedicationTrackerHelper = get()
    private var from = ""
    private var selectedDate = ""
    private var dialogClickType = "Delete"
    private var selectedTab = 0
    private var positionOngoing = 0
    private var positionCompleted = 1
    private var positionAll = 2
    private var medicine = MedicationEntity.Medication()
    private var myMedicationsAdapter : MyMedicationsAdapter? = null
    private var spinnerAdapter: SpinnerAdapter? = null
    private var categoryList : ArrayList<SpinnerModel>? = null
    private var animation : LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            from = it.getString(Constants.FROM,"")!!
            selectedDate = it.getString(Constants.DATE,"")!!
            Timber.e("from,selectedDate--->$from,$selectedDate")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyMedicationsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        registerObservers()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        animation = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_slide_from_bottom)
        categoryList = medicationTrackerHelper.getCategoryList()
        spinnerAdapter = SpinnerAdapter(requireContext(), categoryList!!)
        binding.spinnerMedication.adapter = spinnerAdapter

        myMedicationsAdapter = MyMedicationsAdapter(viewModel,requireContext(),this)
        binding.rvMedications.layoutAnimation = animation
        binding.rvMedications.adapter = myMedicationsAdapter

        selectedTab = if (from.equals("Home", ignoreCase = true)
            || from.equals(Constants.DASHBOARD, ignoreCase = true)
            || from.equals("History", ignoreCase = true)) {
            positionOngoing
        } else if (from.equals("Reschedule", ignoreCase = true)) {
            positionCompleted
        } else {
            positionAll
        }

        binding.spinnerMedication.setSelection(selectedTab)
        updateData(selectedTab)
    }

    private fun updateMedicationList(medicineList: MutableList<MedicationEntity.Medication>, position: Int) {
        if (medicineList.size > 0) {
            binding.rvMedications.visibility = View.VISIBLE
            binding.layoutNoMedicines.visibility = View.GONE
            binding.rvMedications.layoutAnimation = animation
            myMedicationsAdapter!!.updateMedicines(medicineList, position)
            binding.rvMedications.scheduleLayoutAnimation()
        } else {
            binding.rvMedications.visibility = View.GONE
            binding.layoutNoMedicines.visibility = View.VISIBLE
        }
    }

    fun updateData(position: Int) {
        selectedTab = position
        when (position) {
            0 -> viewModel.getOngoingMedicines()
            1 -> viewModel.getCompletedMedicines()
            2 -> viewModel.getAllMyMedicines()
        }
    }

    private fun registerObservers() {

        viewModel.medicinesList.observe(viewLifecycleOwner, Observer {
            if ( it != null ) {
                updateMedicationList(it.toMutableList(),selectedTab)
            }
        })
        viewModel.deleteMedicine.observe(viewLifecycleOwner, Observer {})
    }

    private fun setClickable() {

        binding.spinnerMedication.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinnerAdapter!!.selectedPos = position
                val name: String = categoryList!![position].name
                binding.txtModelSpinner.text = name
                selectedTab = categoryList!![position].position
                println("Selected Item=>$selectedTab,$name")
                updateData(selectedTab)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtModelSpinner.setOnClickListener {
            binding.spinnerMedication.performClick()
        }

        binding.btnMedicationsUploadPre.setOnClickListener {
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.STORE_RECORDS)
            //launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            launchIntent.putExtra(Constants.FROM, Constants.MEDICATION)
            startActivity(launchIntent)
        }

      /*  binding.btnMedicationsOrderMedicine.setOnClickListener {
            val launchIntent = Intent()
            launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.ORDER_MEDICINE)
            //launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            launchIntent.putExtra(Constants.FROM, Constants.MEDICATION)
            startActivity(launchIntent)
        }*/

    }

    fun showDeleteMedicineDialog( medicineDetails : MedicationEntity.Medication ) {
        dialogClickType = "Delete"
        medicine = medicineDetails
        val medName = medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        showDialog(
            listener = this,
            title = this.resources.getString(R.string.DELETE_MEDICINE),
            message = this.resources.getString(R.string.MSG_CONFIRMATION_DELETE_MEDICINE) + " " + medName +"?",
            leftText = this.resources.getString(R.string.CANCEL),
            rightText = this.resources.getString(R.string.CONFIRM),
            showLeftBtn = true)
    }

    fun showRescheduleDialog( medicineDetails : MedicationEntity.Medication ) {
        dialogClickType = "Reschedule"
        medicine = medicineDetails
        val medName = medicineDetails.drug.name + " - " + medicineDetails.drug.strength
        showDialog(
            listener = this,
            title = this.resources.getString(R.string.RESCHEDULE_MEDICINE),
            message = this.resources.getString(R.string.MSG_CONFIRMATION_RESCHEDULE_MEDICINE) + " " + medName +"?",
            leftText = this.resources.getString(R.string.CANCEL),
            rightText = this.resources.getString(R.string.CONFIRM),
            showLeftBtn = true)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if ( isButtonRight ) {
            try {
                if ( dialogClickType.equals("Delete",ignoreCase = true) ) {
                    viewModel.callDeleteMedicineApi(selectedTab,medicine.medicationId.toString(),this)
                }
                if ( dialogClickType.equals("Reschedule",ignoreCase = true) ) {
                    val bundle = Bundle()
                    bundle.putString(Constants.FROM,"Reschedule")
                    bundle.putInt(Constants.MEDICATION_ID,0)
                    bundle.putString(Constants.MEDICINE_NAME,medicine.drug.name + " - " +medicine.drug.strength)
                    bundle.putInt(Constants.Drug_ID,medicine.drugID)
                    bundle.putString(Constants.DRUG_TYPE_CODE,medicine.DrugTypeCode)
                    findNavController().navigate(R.id.action_myMedicationsFragment_to_scheduleMedicineFragment,bundle)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun performBackClick() {
        if ( from.equals("Dashboard",ignoreCase = true) ) {
            val bundle = Bundle()
            bundle.putString(Constants.DATE,selectedDate)
            findNavController().navigate(R.id.action_myMedicationsFragment_to_medicineDashboardFragment,bundle)
        } else {
            findNavController().navigate(R.id.action_myMedicationsFragment_to_medicineHome)
        }
    }

}