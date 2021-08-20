package com.caressa.home.ui.ProfileAndFamilyMember

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.databinding.FragmentAddFamilyMemberBinding
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel
import com.caressa.model.entity.UserRelatives
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.StringBuilder
import java.util.*

class AddFamilyMemberFragment : BaseFragment() , com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener  {

    private val viewModel : ProfileFamilyMemberViewModel by viewModel()
    private lateinit var binding : FragmentAddFamilyMemberBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var from = ""
    private var relationCode = ""
    private var relation = ""
    private var gender = ""
    private var isValidFAM : Boolean = false
    private var userDob = ""
    private var relativeDob = ""
    private val cal = Calendar.getInstance()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddFamilyMemberBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.FAMILY_MEMBER_ADD_SCREEN)
        from = requireArguments().getString(Constants.FROM,"")!!
        relationCode = requireArguments().getString(Constants.RELATION_CODE)!!
        relation = requireArguments().getString(Constants.RELATION)!!
        gender = requireArguments().getString(Constants.GENDER)!!
        Timber.e("from----->$from")
        Timber.i("Relation , RelationCode , GENDER----->$relation , $relationCode , $gender")
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        viewModel.getLoggedInPersonDetails()
        binding.imgFamilyMember.setImageResource(viewModel.getRelationImgId(relationCode))
        binding.txtRelationship.text = relation

        viewModel.addRelative.observe( viewLifecycleOwner , {})

        binding.edtMemberName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtMemberName.error = null
                    binding.tilEdtMemberName.isErrorEnabled = false
                }
            }
        })

        binding.edtMemberName.setFilters(arrayOf(ViewUtils.firstLetterCapInputFilter()))

        binding.edtMemberDob.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtMemberDob.error = null
                    binding.tilEdtMemberDob.isErrorEnabled = false
                }
            }
        })

        binding.edtMemberMobile.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtMemberMobile.error = null
                    binding.tilEdtMemberMobile.isErrorEnabled = false
                }
            }
        })

        binding.edtMemberEmail.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtMemberEmail.error = null
                    binding.tilEdtMemberEmail.isErrorEnabled = false
                }
            }
        })

    }

    private fun setClickable() {

        binding.layoutMemberDob.setOnClickListener {
            showDatePicker()
        }

        binding.edtMemberDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnBackAddMember.setOnClickListener {
            it.findNavController().navigate(R.id.action_addFamilyMemberFragment_to_selectRelationshipFragment)
        }

        binding.btnAddMember.setOnClickListener {

            val name = binding.edtMemberName.text.toString()
            val dob = relativeDob
            val mobile = binding.edtMemberMobile.text.toString()
            val email = binding.edtMemberEmail.text.toString()

            if ( !Validation.isValidName(name) ) {
                binding.tilEdtMemberName.isErrorEnabled = true
                binding.tilEdtMemberName.error = resources.getString(R.string.VALIDATE_NAME)
            }

            if (Utilities.isNullOrEmpty(dob)) {
                binding.tilEdtMemberDob.isErrorEnabled = true
                binding.tilEdtMemberDob.error = resources.getString(R.string.VALIDATE_DATE_OF_BIRTH)
            }

            if (Utilities.isNullOrEmpty(mobile) || !Utilities.isValidPhoneNumber( mobile )) {
                binding.tilEdtMemberMobile.isErrorEnabled = true
                binding.tilEdtMemberMobile.error = resources.getString(R.string.VALIDATE_PHONE)
            }

            if (!Utilities.isNullOrEmpty(email) && email.length > 3) {
                if (!email.contains("@")) {
                    binding.tilEdtMemberEmail.isErrorEnabled = true
                    binding.tilEdtMemberEmail.error = resources.getString(R.string.VALIDATE_EMAIL_MUST_INCLUDE)
                } else {
                    if (!Validation.isValidEmail(email)) {
                        binding.tilEdtMemberEmail.isErrorEnabled = true
                        binding.tilEdtMemberEmail.error = resources.getString(R.string.VALIDATE_EMAIL)
                    }
                }
            } else {
                binding.tilEdtMemberEmail.isErrorEnabled = true
                binding.tilEdtMemberEmail.error = resources.getString(R.string.VALIDATE_EMAIL)
            }

          /*  if ( !DateHelper.isDateAbove18Years(DateHelper.getDateTimeAs_ddMMMyyyy(dob)) ) {
                binding.tilEdtMemberDob.isErrorEnabled = true
                binding.tilEdtMemberDob.error = "Age must be more than 18 years"
            }*/

            if (!binding.tilEdtMemberName.isErrorEnabled && !binding.tilEdtMemberDob.isErrorEnabled
                && !binding.tilEdtMemberMobile.isErrorEnabled && !binding.tilEdtMemberEmail.isErrorEnabled
            ) {
                //***********************
                val relativeId = RealPathUtil.getUniqueIdLong()
                viewModel.userDetails.observe( viewLifecycleOwner , {
                    if ( it != null ) {
                        userDob = DateHelper.getDateTimeAs_ddMMMyyyy(it.dateOfBirth)
                        val relDob = DateHelper.getDateTimeAs_ddMMMyyyy(dob)
                        Timber.i("UserDob  , RelativeDob----->$userDob , $relDob")
                        val famDate = DateHelper.convertStringToDate(relDob)
                        val userDOB = DateHelper.convertStringToDate(userDob)

                        // Validations for Age
                        when( relationCode ) {

                            Constants.FATHER_RELATIONSHIP_CODE,Constants.MOTHER_RELATIONSHIP_CODE -> {
                                if (userDOB!!.compareTo(famDate) > 0) {
                                    isValidFAM = true
                                    relativeDob = dob
                                } else {
                                    binding.tilEdtMemberDob.isErrorEnabled = true
                                    binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_PARENTS_AGE)
                                }
                            }

                            Constants.SON_RELATIONSHIP_CODE,Constants.DAUGHTER_RELATIONSHIP_CODE -> {
                                if (userDOB!!.compareTo(famDate) < 0) {
                                    isValidFAM = true
                                    relativeDob = dob
                                } else {
                                    binding.tilEdtMemberDob.isErrorEnabled = true
                                    binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_KIDS_AGE)
                                }
                            }

                            Constants.GRANDFATHER_RELATIONSHIP_CODE,Constants.GRANDMOTHER_RELATIONSHIP_CODE -> {
                                if (userDOB!!.compareTo(famDate) > 0) {
                                    isValidFAM = true
                                    relativeDob = dob
                                } else {
                                    binding.tilEdtMemberDob.isErrorEnabled = true
                                    binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR__GP_AGE)
                                }
                            }

                            Constants.HUSBAND_RELATIONSHIP_CODE,Constants.WIFE_RELATIONSHIP_CODE -> {
                                if (!DateHelper.isDateAbove18Years(dob)) {
                                    binding.tilEdtMemberDob.isErrorEnabled = true
                                    binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_AGE_NOT_LESS_THAN_18)
                                } else {
                                    isValidFAM = true
                                    relativeDob = dob
                                }
                            }

                            Constants.BROTHER_RELATIONSHIP_CODE,Constants.SISTER_RELATIONSHIP_CODE -> {
                                isValidFAM = true
                                relativeDob = dob
                            }

                        }

/*                        if ( RelationCode == Constants.FATHER_RELATIONSHIP_CODE || RelationCode == Constants.MOTHER_RELATIONSHIP_CODE) {
                            if (userDOB!!.compareTo(famDate) > 0) {
                                isValidFAM = true
                                relativeDob = dob
                            } else {
                                binding.tilEdtMemberDob.isErrorEnabled = true
                                binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_PARENTS_AGE)
                            }
                        } else if (RelationCode == Constants.SON_RELATIONSHIP_CODE || RelationCode == Constants.DAUGHTER_RELATIONSHIP_CODE)  {
                            if (userDOB!!.compareTo(famDate) < 0) {
                                isValidFAM = true
                                relativeDob = dob
                            } else {
                                binding.tilEdtMemberDob.isErrorEnabled = true
                                binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_KIDS_AGE)
                            }
                        }  else if (RelationCode == Constants.GRANDFATHER_RELATIONSHIP_CODE || RelationCode == Constants.GRANDMOTHER_RELATIONSHIP_CODE)  {
                            if (userDOB!!.compareTo(famDate) > 0) {
                                isValidFAM = true
                                relativeDob = dob
                            } else {
                                binding.tilEdtMemberDob.isErrorEnabled = true
                                binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR__GP_AGE)
                            }
                        } else if (RelationCode == Constants.HUSBAND_RELATIONSHIP_CODE || RelationCode == Constants.WIFE_RELATIONSHIP_CODE) {
                            if (!DateHelper.isDateAbove18Years(dob)) {
                                binding.tilEdtMemberDob.isErrorEnabled = true
                                binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_AGE_NOT_LESS_THAN_18)
                            } else {
                                isValidFAM = true
                                relativeDob = dob
                            }
                        } else if ( RelationCode == Constants.BROTHER_RELATIONSHIP_CODE || RelationCode == Constants.SISTER_RELATIONSHIP_CODE) {
                            isValidFAM = true
                            relativeDob = dob
                        }*/

                        if ( isValidFAM ) {
                            val newRelative = UserRelatives(
                                relativeID = relativeId,
                                firstName = name,
                                lastName = "",
                                dateOfBirth = dob,
                                gender = gender,
                                contactNo = mobile,
                                emailAddress = email,
                                relationshipCode = relationCode,
                                relationship = relation )
                            viewModel.callAddNewRelativeApi( true,newRelative,from,this)
                            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.FAMILY_MEMBER_ADD)
                        }
                    }
                })
                // it.findNavController().navigate(R.id.action_addFamilyMemberFragment_to_familyMembersListFragment)
                //***********************
            }
        }
    }

    fun navigateToHRA() {
        requireActivity().finish()
    }

    private fun showDatePicker() {
//        cal.add(Calendar.YEAR, -18)
        val dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
            this@AddFamilyMemberFragment,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        dpd.accentColor = appColorHelper.primaryColor()
        dpd.setTitle(resources.getString(R.string.PICK_DOB))
        dpd.maxDate = cal
        dpd.isThemeDark = false
        dpd.vibrate(false)
        dpd.dismissOnPause(false)
        dpd.showYearPickerFirst(false)
        dpd.show(this@AddFamilyMemberFragment.requireActivity().fragmentManager, "FamilyMember")
        //dpd.show(fragmentManager!!, "FamilyMember")
    }

    override fun onDateSet(view: com.wdullaer.materialdatetimepicker.date.DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()
        relativeDob = date
        binding.edtMemberDob.setText(DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date))
    }

}
