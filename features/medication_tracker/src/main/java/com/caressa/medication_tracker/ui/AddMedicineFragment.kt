package com.caressa.medication_tracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.KeyboardUtils
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.ViewUtils
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.adapter.DrugsListAdapter
import com.caressa.medication_tracker.adapter.MedicineTypeAdapter
import com.caressa.medication_tracker.common.MedicationTrackerHelper
import com.caressa.medication_tracker.databinding.FragmentAddMedicineBinding
import com.caressa.medication_tracker.model.MedTypeModel
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.caressa.model.entity.MedicationEntity
import com.caressa.model.medication.DrugsModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber

class AddMedicineFragment : BaseFragment(),MedicineTypeAdapter.OnMedTypeListener, KoinComponent {

    private val viewModel: MedicineTrackerViewModel by viewModel()
    private lateinit var binding: FragmentAddMedicineBinding

    //private val appColorHelper = AppColorHelper.instance!!
    private val medicationTrackerHelper : MedicationTrackerHelper = get()
    private var medicineTypeAdapter: MedicineTypeAdapter? = null
    private val medTypeList = medicationTrackerHelper.getMedTypeList()
    private var from = ""
    private var selectedDate = ""
    private var medicationId = 0
    private var drugId = 0
    private var drugTypeCode = ""
    private var medicineName = ""

    internal var isSelectedMedicine = false
    private lateinit var drugListAdapter: DrugsListAdapter

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            medicationId = it.getInt(Constants.MEDICATION_ID,0)
            drugId = it.getInt(Constants.Drug_ID,0)
            medicineName = it.getString(Constants.MEDICINE_NAME,"")!!
            drugTypeCode = it.getString(Constants.DRUG_TYPE_CODE,"")!!
            from = it.getString(Constants.FROM,"Home")
            selectedDate = it.getString(Constants.DATE,"")!!
            Timber.e("MedicationID,from,selectedDate----->$medicationId,$from,$selectedDate")
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
        binding = FragmentAddMedicineBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            initialise()
            registerObservers()
            setClickable()
        } catch (e : Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        viewModel.getPastMedicines()
        medicineTypeAdapter = MedicineTypeAdapter(requireContext(),medTypeList,this)
        binding.rvMedicineType.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMedicineType.adapter = medicineTypeAdapter

        drugListAdapter = DrugsListAdapter(requireContext(),ArrayList())
        binding.edtMedicineName.setAdapter(drugListAdapter)

        val onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            isSelectedMedicine = true
            val medicineModel = adapterView.getItemAtPosition(i) as DrugsModel.DrugsResponse.Drug
            if ( !Utilities.isNullOrEmpty(medicineModel.strength) ) {
                binding.edtMedicineName.setText(medicineModel.name + " - " + medicineModel.strength)
            } else {
                binding.edtMedicineName.setText(medicineModel.name)
            }
            drugId = medicineModel.iD.toInt()
            //Utilities.hideKeyboard(view, requireContext())
            KeyboardUtils.hideSoftInput(context as Activity)
            binding.edtMedicineName.setSelection(binding.edtMedicineName.text.length)
        }
        binding.edtMedicineName.onItemClickListener = onItemClickListener

        if ( !Utilities.isNullOrEmpty(drugTypeCode) ) {
            binding.imgMedicine.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(drugTypeCode))
            binding.edtMedicineName.setText(medicineName)
            if (drugTypeCode.equals("MOUTHWASH",ignoreCase = true)) {
                binding.txtMedicine.text = resources.getString(R.string.MOUTH_WASH)
            } else {
                binding.txtMedicine.setText(medicationTrackerHelper.getMedTypeByCode(drugTypeCode))
            }

            for (i in medTypeList.indices) {
                if (medTypeList[i].medTypeCode.equals(drugTypeCode,ignoreCase = true)) {
                    medicineTypeAdapter!!.updateSelectedPos(i)
                    break
                }
            }
        }
    }

    private fun registerObservers() {

        viewModel.medicinesList.observe(viewLifecycleOwner, {
            if ( it != null ) {
                setPastMedicines(it)
            }
        })

    }

    private fun setClickable() {

        binding.edtMedicineName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (isSelectedMedicine) {
                        isSelectedMedicine = false
                    } else {
                        if (!binding.edtMedicineName.isPerformingCompletion) {
                            if (editable.toString().length >= 3) {
                                viewModel.fetchDrugsList(editable.toString())
                            }
                        }
                    }

                    if (editable.toString().length <= 1) {
                        binding.edtMedicineName.dismissDropDown()
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        })

        binding.btnAddSchedule.setOnClickListener {
            if (!Utilities.isNullOrEmpty(binding.edtMedicineName.text.toString())) {
                val bundle = Bundle()
                bundle.putString(Constants.FROM,Constants.ADD)
                bundle.putInt(Constants.MEDICATION_ID,medicationId)
                bundle.putInt(Constants.Drug_ID,drugId)
                bundle.putString(Constants.MEDICINE_NAME,binding.edtMedicineName.text.toString())
                bundle.putString(Constants.DRUG_TYPE_CODE,drugTypeCode)
                viewModel.routeToScheduleDetails(it,bundle)
            } else {
                Utilities.toastMessageShort(context, resources.getString(R.string.ERROR_ENTER_MEDICINE_NAME))
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setPastMedicines( list : List<MedicationEntity.Medication> ) {
        try {
            binding.flowLayout.removeAllViews()
            if (list.isNotEmpty()) {
                binding.layoutRecentlyAddedMedicines.visibility = View.VISIBLE
                val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                par.setMargins(10, 10, 10, 10)
                for (i in list.indices) {
                    val textView = TextView(context)
                    textView.apply {
                        //text = list[i].drug.name + " - " +list[i].drug.strength
                        text = if ( !Utilities.isNullOrEmpty(list[i].drug.strength) ) {
                            list[i].drug.name + " - " + list[i].drug.strength
                        } else {
                            list[i].drug.name
                        }
                        tag = i
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, R.dimen.size_12sp.toFloat())
                        setPadding(dpToPx(18f).toInt(), dpToPx(10f).toInt(), dpToPx(18f).toInt(), dpToPx(10f).toInt())
                        setTextAppearance(context,R.style.textFlowLayout)
                        layoutParams = par
                        background = ViewUtils.getArcRbSelectorBgHra(false)
                        setTextColor(ViewUtils.getArcRbSelectorTxtColorHra(false))
                        setOnClickListener { view: View ->
                            val pos = view.tag as Int
                            deselectAll(view.tag.toString())
                            setMedicineDetails(list[pos])
                        }
                    }
                    Timber.d("Option--->${list[i].drug.name}")
                    binding.flowLayout.addView(textView)
                }
            } else {
                binding.layoutRecentlyAddedMedicines.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deselectAll(except: String) {
        for (i in 0 until binding.flowLayout.childCount) {
            if (!binding.flowLayout.getChildAt(i).tag.toString().equals(except,ignoreCase = true)) {
                val textView = binding.flowLayout.getChildAt(i) as TextView
                textView.setTextColor(ContextCompat.getColor(requireContext(),R.color.vivant_greyish))
                textView.setBackgroundResource(R.drawable.btn_oval_round)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setMedicineDetails(details: MedicationEntity.Medication) {

        try {
            Utilities.printData("details",details,true)
            //drugTypeCode = details.DrugTypeCode!!
            if ( !Utilities.isNullOrEmpty(details.DrugTypeCode!!)) {
                drugTypeCode = details.DrugTypeCode!!
            } else {
                drugTypeCode = "TAB"
            }

            if (drugTypeCode.equals("MOUTHWASH",ignoreCase = true)) {
                binding.txtMedicine.text = resources.getString(R.string.MOUTH_WASH)
            } else {
                binding.txtMedicine.text = resources.getString(medicationTrackerHelper.getMedTypeByCode(drugTypeCode))
            }
            binding.imgMedicine.setImageResource(medicationTrackerHelper.getMedTypeImageByCode(drugTypeCode))
            //binding.edtMedicineName.setText(details.drug.name + " - " +details.drug.strength)
            if ( !Utilities.isNullOrEmpty(details.drug.strength) ) {
                binding.edtMedicineName.setText(details.drug.name + " - " +details.drug.strength)
            } else {
                binding.edtMedicineName.setText(details.drug.name)
            }
            binding.edtMedicineName.setSelection(binding.edtMedicineName.text.length)
            drugId = details.drugID
            var medTypePos = 0
            for (i in medTypeList.indices) {
                if (medTypeList[i].medTypeCode.equals(drugTypeCode,ignoreCase = true)) {
                    medicineTypeAdapter!!.updateSelectedPos(i)
                    medTypePos = i
                    break
                }
            }
            println("Position,DrugTypeCode,DrugID==>" + medTypePos + "," + drugTypeCode + "," + drugId)
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    override fun onMedTypeSelection(position: Int, medType: MedTypeModel) {
        if (medType.medTypeCode.equals("MOUTHWASH",ignoreCase = true)) {
            binding.txtMedicine.text = resources.getString(R.string.MOUTH_WASH)
        } else {
            binding.txtMedicine.text = medType.medTypeTitle
        }
        binding.imgMedicine.setImageResource(medType.medTypeImageId)
        drugTypeCode = medType.medTypeCode
        println("Position,DRUG_TYPE_CODE----->" + position + " , " + medType.medTypeCode)
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    fun performBackClick() {
        val bundle = Bundle()
        if ( from.equals(Constants.DASHBOARD,ignoreCase = true) ) {
            bundle.putString(Constants.DATE,selectedDate)
            findNavController().navigate(R.id.action_addMedicineFragment_to_medicineDashboardFragment,bundle)
        } else if ( from.equals(Constants.TRACK_PARAMETER,ignoreCase = true) ) {
            requireActivity().finish()
        } else {
            findNavController().navigate(R.id.action_addMedicineFragment_to_medicineHome)
        }
    }

}