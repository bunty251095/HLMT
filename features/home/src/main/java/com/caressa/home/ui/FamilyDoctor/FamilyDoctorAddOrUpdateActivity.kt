package com.caressa.home.ui.FamilyDoctor

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseActivity
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.KeyboardUtils
import com.caressa.common.utils.Utilities
import com.caressa.common.utils.Validation
import com.caressa.common.view.AutocompleteTextViewAdapter
import com.caressa.common.view.AutocompleteTextViewModel
import com.caressa.home.R
import com.caressa.home.common.HomeSingleton
import com.caressa.home.databinding.ActivityFamilyDoctorAddOrUpdateBinding
import com.caressa.home.viewmodel.FamilyDoctorViewModel
import com.caressa.model.home.FamilyDoctor
import kotlinx.android.synthetic.main.toolbar_home.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import java.util.stream.Collectors

class FamilyDoctorAddOrUpdateActivity : BaseActivity() {

    private val viewModel : FamilyDoctorViewModel by viewModel()
    private lateinit var binding : ActivityFamilyDoctorAddOrUpdateBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var doctorId = 0
    internal var isSelectedMedicine = false
    var specialityList: MutableList<AutocompleteTextViewModel> =  mutableListOf()
    private lateinit var autocompleteTextViewAdapter:AutocompleteTextViewAdapter

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_family_doctor_add_or_update)
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

        viewModel.callGetDoctorSpecialitiesApi(this)
        if (from.equals("Update",ignoreCase = true)) {
            binding.btnAddUpdateDoctor.text = resources.getString(R.string.UPDATE)
            val doctorsDetails = HomeSingleton.getInstance()!!.getDoctorsDetails()
            doctorId = doctorsDetails.id
            binding.edtDoctorName.setText( doctorsDetails.firstName )
            binding.edtDoctorContact.setText( doctorsDetails.primaryContactNo )
            binding.edtDoctorEmail.setText( doctorsDetails.emailAddress )
            if (!Utilities.isNullOrEmpty(doctorsDetails.affiliatedTo)) {
                binding.edtAffilatedTo.setText( doctorsDetails.affiliatedTo )
            }
        } else {
            binding.btnAddUpdateDoctor.text = resources.getString(R.string.ADD)
        }

        autocompleteTextViewAdapter = AutocompleteTextViewAdapter(this,ArrayList())
        binding.edtSpecialization.setAdapter(autocompleteTextViewAdapter)

        val onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            isSelectedMedicine = true
            val medicineModel = adapterView.getItemAtPosition(i) as AutocompleteTextViewModel
            binding.edtSpecialization.setText(medicineModel.name)
            //drugId = medicineModel.iD
            KeyboardUtils.hideSoftInput(this as Activity)
        }
        binding.edtSpecialization.onItemClickListener = onItemClickListener

        binding.edtDoctorName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilDoctorName.error = null
                    binding.tilDoctorName.isErrorEnabled = false
                }
            }
        })

        binding.edtDoctorContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilDoctorContact.error = null
                    binding.tilDoctorContact.isErrorEnabled = false
                }
            }
        })

        binding.edtDoctorEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilDoctorEmail.error = null
                    binding.tilDoctorEmail.isErrorEnabled = false
                }
            }
        })
    }

    private fun registerObservers() {

        viewModel.listSpecialities.observe( this , Observer {
            if ( it != null ) {
                val list = it.speciality
                specialityList.clear()
                if ( !list.isNullOrEmpty() ) {
                    var actvm: AutocompleteTextViewModel
                    for ( item in list) {
                        if (!item.id.equals("0",ignoreCase = true)) {
                            actvm = AutocompleteTextViewModel()
                            actvm.id = item.id
                            actvm.name = item.specailityName
                            specialityList.add(actvm)
                        }
                    }
                }
            }
        })

        viewModel.addDoctor.observe( this , Observer {})
        viewModel.updateDoctor.observe( this , Observer {})
    }

    private fun setClickable() {

        binding.edtSpecialization.setOnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus && binding.edtSpecialization.text.toString() == "") {
                filterSpinnerList(specialityList, "")
            }
        }

        binding.edtSpecialization.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (isSelectedMedicine) {
                        isSelectedMedicine = false
                    } else {
                        if (!binding.edtSpecialization.isPerformingCompletion) {
                            if (editable.toString().length >= 2) {
                                filterSpinnerList(specialityList,editable.toString())
                            }
                        }
                    }

                    if (!editable.toString().equals("", ignoreCase = true)) {
                        binding.tilSpecialization.error = null
                        binding.tilSpecialization.isErrorEnabled = false
                    }
/*                    if (editable.toString().length <= 1) {
                        binding.edtSpecialization.dismissDropDown()
                    }*/
                } catch (e: Exception) { e.printStackTrace() }
            }
        })

        binding.btnAddUpdateDoctor.setOnClickListener {
            validateBeforeAndOrUpdate()
        }

        binding.btnCancelDoctor.setOnClickListener {
            onBackPressed()
        }

    }

    private fun validateBeforeAndOrUpdate() {
        val name = binding.edtDoctorName.text.toString()
        val specialization = binding.edtSpecialization.text.toString()
        val contact = binding.edtDoctorContact.text.toString()
        val email = binding.edtDoctorEmail.text.toString()
        val affiliatedTo = binding.edtAffilatedTo.text.toString()

        if (Utilities.isNullOrEmpty(name) || !Validation.isValidName(name)) {
            binding.tilDoctorName.isErrorEnabled = true
            binding.tilDoctorName.error = "Please Enter Valid Name."
        }
        if (Utilities.isNullOrEmpty(specialization)) {
            binding.tilSpecialization.isErrorEnabled = true
            binding.tilSpecialization.error = "Please Enter Valid Specialization."
        }
        if (Utilities.isNullOrEmpty(contact) || !Validation.isValidPhoneNumber(contact)) {
            binding.tilDoctorContact.isErrorEnabled = true
            binding.tilDoctorContact.error = "Please Enter Valid Mobile Number"
        }
        if (Utilities.isNullOrEmpty(email) || !Validation.isValidEmail(email)) {
            binding.tilDoctorEmail.isErrorEnabled = true
            binding.tilDoctorEmail.error = "Please Enter Valid Email"
        }
        if (!binding.tilDoctorName.isErrorEnabled && !binding.tilSpecialization.isErrorEnabled &&
            !binding.tilDoctorContact.isErrorEnabled && !binding.tilDoctorEmail.isErrorEnabled ) {
            val doctorDetails = FamilyDoctor()
            doctorDetails.id = doctorId
            doctorDetails.firstName = name
            doctorDetails.specialty = specialization
            doctorDetails.primaryContactNo = contact
            doctorDetails.emailAddress = email
            doctorDetails.affiliatedTo = affiliatedTo
            if (from.equals("Update", ignoreCase = true)) {
                viewModel.callUpdateFamilyDoctorApi(this,doctorDetails)
            } else {
                viewModel.callAddFamilyDoctorApi(this,doctorDetails)
            }
        }
    }

    fun filterSpinnerList(specialities: MutableList<AutocompleteTextViewModel>, txt: String) {
        if (!txt.equals("", ignoreCase = true)) {
            var filteredList: List<AutocompleteTextViewModel> = ArrayList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filteredList = specialities.stream()
                    .filter { speciality: AutocompleteTextViewModel ->
                        speciality.name.toLowerCase(Locale.ROOT).contains(txt.toLowerCase(Locale.ROOT))
                    }.collect(Collectors.toList<AutocompleteTextViewModel>())
            }
            autocompleteTextViewAdapter.updateData(filteredList)
            if (filteredList.isNotEmpty()) {
                Utilities.hideKeyboard(binding.edtSpecialization, this)
            }
            binding.edtSpecialization.showDropDown()
        } else {
            autocompleteTextViewAdapter.updateData(specialities)
            if (specialities.size > 0) {
                Utilities.hideKeyboard(binding.edtSpecialization, this)
            }
            binding.edtSpecialization.showDropDown()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar_home)
        if (from.equals("Update",ignoreCase = true)) {
            toolbar_title!!.text = resources.getString(R.string.TITLE_UPDATE_DOCTOR)
        } else {
            toolbar_title!!.text = resources.getString(R.string.TITLE_ADD_DOCTOR)
        }
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
        //val intent = Intent()
        val intent = Intent(this, FamilyDoctorListActivity::class.java)
        intent.putExtra("from","Back")
        //intent.setComponent(ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_DOCTOR))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}