package com.caressa.home.ui.ProfileAndFamilyMember

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.home.R
import com.caressa.home.databinding.FragmentEditFamilyMemberDetailsBinding
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel
import com.caressa.model.entity.UserRelatives
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class EditFamilyMemberDetailsFragment : BaseFragment() , DatePickerDialog.OnDateSetListener ,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel : ProfileFamilyMemberViewModel by viewModel()
    private lateinit var binding : FragmentEditFamilyMemberDetailsBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var relationCode = ""
    private var relation = ""
    private var relativeId = ""
    private var relationShipID = ""
    private var dateOfBirth = ""
    private var dob = ""
    private var userDob = ""
    private var isValidFAM : Boolean = false
    private val cal = Calendar.getInstance()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEditFamilyMemberDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        relativeId = requireArguments().getString(Constants.RELATIVE_ID)!!
        relationShipID = requireArguments().getString(Constants.RELATION_SHIP_ID)!!
        relationCode = requireArguments().getString(Constants.RELATION_CODE)!!
        relation = requireArguments().getString(Constants.RELATION)!!
        Timber.i("RelativeId , RelationShipID , RelationCode----->$relativeId , $relationShipID , $relationCode")
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        binding.imgFamilyMember.setImageResource(viewModel.getRelationImgId(relationCode))
        binding.txtRelationship.text = relation

        viewModel.removeRelative.observe( viewLifecycleOwner , {})
        viewModel.updateRelative.observe( viewLifecycleOwner , {})

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

        viewModel.getUserRelativeForRelativeId(relativeId)
        viewModel.getLoggedInPersonDetails()
        viewModel.userDetails.observe( viewLifecycleOwner , {
            if ( it != null ) {
                userDob = DateHelper.getDateTimeAs_ddMMMyyyy(it.dateOfBirth)
            }
        })
        viewModel.alreadyExistRelatives.observe( viewLifecycleOwner , {
            if ( it != null ) {
                val relativeDetails = it[0]
                if ( !Utilities.isNullOrEmpty(relativeDetails.dateOfBirth) ) {
                    //dob = DateHelper.getDateTimeAs_ddMMMyyyy(relativeDetails.dateOfBirth)
                    dob = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, relativeDetails.dateOfBirth)!!
                    dateOfBirth = DateHelper.formatDateValue("yyyy-MM-dd", relativeDetails.dateOfBirth)!!
                    try {
                        if ( !Utilities.isNullOrEmpty( dob ) ) {
                            binding.edtMemberDob.setText(dob)
                        } else {
                            binding.edtMemberDob.setText(dob)
                        }
                    } catch ( e : Exception ) {
                        e.printStackTrace()
                    }
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

        binding.btnDeleteMember.setOnClickListener {
            showDialog(
                listener = this,
                title = this.resources.getString(R.string.DELETE_FAMILY_MEMBER),
                message = this.resources.getString(R.string.MSG_DELETE_MEMBER_CONFIRMATION),
                leftText = this.resources.getString(R.string.CANCEL),
                rightText = this.resources.getString(R.string.CONFIRM),
                showLeftBtn = true)
        }

        binding.btnUpdateMember.setOnClickListener {

            if ( !Utilities.isNullOrEmpty(relativeId) && checkIfUSerChangesValues() ) {
                val username = binding.edtMemberName.text.toString().trim { it <= ' ' }
                val mobile = binding.edtMemberMobile.text.toString().trim { it <= ' ' }
                val email = binding.edtMemberEmail.text.toString().trim { it <= ' ' }
                val relativeDob = dateOfBirth

                if ( !Validation.isValidName(username) ) {
                    binding.tilEdtMemberName.isErrorEnabled = true
                    binding.tilEdtMemberName.error = "Please Enter Valid Name"
                }

                if (Utilities.isNullOrEmpty(dob)) {
                    binding.tilEdtMemberDob.isErrorEnabled = true
                    binding.tilEdtMemberDob.error = "Please Enter Valid Date of Birth"
                }

                if (Utilities.isNullOrEmpty(mobile) || !Utilities.isValidPhoneNumber( mobile )) {
                    binding.tilEdtMemberMobile.isErrorEnabled = true
                    binding.tilEdtMemberMobile.error = "Please Enter Valid Mobile Number"
                }

                if (!Utilities.isNullOrEmpty(email) && email.length > 3) {
                    if (!email.contains("@")) {
                        binding.tilEdtMemberEmail.isErrorEnabled = true
                        binding.tilEdtMemberEmail.error = "Email address must include @"
                    } else {
                        if (!Validation.isValidEmail(email)) {
                            binding.tilEdtMemberEmail.isErrorEnabled = true
                            binding.tilEdtMemberEmail.error = "Please Enter valid Email"
                        }
                    }
                } else {
                    binding.tilEdtMemberEmail.isErrorEnabled = true
                    binding.tilEdtMemberEmail.error = "Please Enter Valid Email"
                }

                if ( !DateHelper.isDateAbove18Years(DateHelper.getDateTimeAs_ddMMMyyyy(relativeDob)) ) {
                    binding.tilEdtMemberDob.isErrorEnabled = true
                    binding.tilEdtMemberDob.error = "Age must be more than 18 years"
                }

                if (!binding.tilEdtMemberName.isErrorEnabled && !binding.tilEdtMemberDob.isErrorEnabled
                    && !binding.tilEdtMemberMobile.isErrorEnabled && !binding.tilEdtMemberEmail.isErrorEnabled) {
                    viewModel.alreadyExistRelatives.observe( viewLifecycleOwner , {
                        if ( it != null ) {
                            val relativeDetails = it[0]
                            val gender = relativeDetails.gender
                            val relation = relativeDetails.relationship
                            dob = DateHelper.getDateTimeAs_ddMMMyyyy(relativeDetails.dateOfBirth)
                            if ( !Utilities.isNullOrEmpty(userDob) ) {
                                val famDate = DateHelper.convertStringToDate(dob)
                                val userDOB = DateHelper.convertStringToDate(userDob)

                                // Validations for Age
                                when( relationCode ) {

                                    Constants.FATHER_RELATIONSHIP_CODE,Constants.MOTHER_RELATIONSHIP_CODE -> {
                                        if ( userDOB!!.compareTo(famDate) > 0 ) {
                                            isValidFAM = true
                                        } else {
                                            binding.tilEdtMemberDob.isErrorEnabled = true
                                            binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_PARENTS_AGE)
                                        }
                                    }

                                    Constants.SON_RELATIONSHIP_CODE,Constants.DAUGHTER_RELATIONSHIP_CODE-> {
                                        if ( userDOB!!.compareTo(famDate) < 0 ) {
                                            isValidFAM = true
                                        } else {
                                            binding.tilEdtMemberDob.isErrorEnabled = true
                                            binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_KIDS_AGE)
                                        }
                                    }

                                    Constants.GRANDFATHER_RELATIONSHIP_CODE,Constants.GRANDMOTHER_RELATIONSHIP_CODE -> {
                                        if ( userDOB!!.compareTo(famDate) > 0 ) {
                                            isValidFAM = true
                                        } else {
                                            binding.tilEdtMemberDob.isErrorEnabled = true
                                            binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR__GP_AGE)
                                        }
                                    }

                                    Constants.HUSBAND_RELATIONSHIP_CODE,Constants.WIFE_RELATIONSHIP_CODE -> {
                                        if ( !DateHelper.isDateAbove18Years(dob) ) {
                                            binding.tilEdtMemberDob.isErrorEnabled = true
                                            binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_AGE_NOT_LESS_THAN_18)
                                        } else {
                                            isValidFAM = true
                                        }
                                    }

                                    Constants.BROTHER_RELATIONSHIP_CODE,Constants.SISTER_RELATIONSHIP_CODE -> {
                                        isValidFAM = true
                                    }

                                }

/*                                if (RelationCode == Constants.FATHER_RELATIONSHIP_CODE || RelationCode == Constants.MOTHER_RELATIONSHIP_CODE) {
                                    if ( userDOB!!.compareTo(famDate) > 0 ) {
                                        isValidFAM = true
                                    } else {
                                        binding.tilEdtMemberDob.isErrorEnabled = true
                                        binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_PARENTS_AGE)
                                    }
                                } else if (RelationCode == Constants.SON_RELATIONSHIP_CODE || RelationCode == Constants.DAUGHTER_RELATIONSHIP_CODE) {
                                    if ( userDOB!!.compareTo(famDate) < 0 ) {
                                        isValidFAM = true
                                    } else {
                                        binding.tilEdtMemberDob.isErrorEnabled = true
                                        binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_KIDS_AGE)
                                    }
                                } else if (RelationCode == Constants.GRANDFATHER_RELATIONSHIP_CODE || RelationCode == Constants.GRANDMOTHER_RELATIONSHIP_CODE) {
                                    if ( userDOB!!.compareTo(famDate) > 0 ) {
                                        isValidFAM = true
                                    } else {
                                        binding.tilEdtMemberDob.isErrorEnabled = true
                                        binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR__GP_AGE)
                                    }
                                } else if (RelationCode == Constants.HUSBAND_RELATIONSHIP_CODE || RelationCode == Constants.WIFE_RELATIONSHIP_CODE) {
                                    if ( !DateHelper.isDateAbove18Years(dob) ) {
                                        binding.tilEdtMemberDob.isErrorEnabled = true
                                        binding.tilEdtMemberDob.error = resources.getString(R.string.ERROR_AGE_NOT_LESS_THAN_18)
                                    } else {
                                        isValidFAM = true
                                    }
                                }*/

                                if (isValidFAM) {
                                    val newRelative = UserRelatives(
                                        relativeID = relativeId,
                                        firstName = username,
                                        lastName = "",
                                        dateOfBirth = relativeDob,
                                        gender = gender,
                                        contactNo = mobile,
                                        emailAddress = email,
                                        relationshipCode = relationCode,
                                        relationship = relation ,
                                        relationShipID = relationShipID )
                                    viewModel.callUpdateRelativesApi(true, newRelative,Constants.RELATIVE)
                                }
                            } else {
                                Utilities.toastMessageLong( context , resources.getString(R.string.ERROR_DOB_UNAVAILABLE))
                            }
                        }
                    })
                }

            } else {
                requireActivity().onBackPressed()
            }
        }

    }

    private fun showDatePicker() {
        cal.add(Calendar.YEAR, -18)
        val dateOnCalender = dateOfBirth
        val dob = dateOnCalender.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val year = Integer.parseInt(dob[0])
        val month = Integer.parseInt(dob[1]) - 1
        val day = Integer.parseInt(dob[2])
        val dpd = DatePickerDialog.newInstance(this@EditFamilyMemberDetailsFragment, year, month, day)
            dpd.accentColor = appColorHelper.primaryColor()
            dpd.setTitle(resources.getString(R.string.PICK_DOB))
            dpd.maxDate = cal
            dpd.isThemeDark = false
            dpd.vibrate(false)
            dpd.dismissOnPause(false)
            dpd.showYearPickerFirst(false)
            dpd.show(this.requireActivity().fragmentManager, "dateofbirth")
         //dpd.show(fragmentManager!!, "FamilyMember")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()
        if ( !Utilities.isNullOrEmpty( date ) ) {
            dateOfBirth = date
            binding.edtMemberDob.setText(DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, date))
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun checkIfUSerChangesValues() : Boolean {
        var isChanges = false
        val username = binding.edtMemberName.text.toString()
        val mobile = binding.edtMemberMobile.text.toString()
        val email = binding.edtMemberMobile.text.toString()
        val dobNew = DateHelper.getDateTimeAs_ddMMMyyyy(dateOfBirth)
        Timber.i("username,mobile,email,DateOfBirth----->$username , $mobile , $email , $dobNew")
        viewModel.alreadyExistRelatives.observe( viewLifecycleOwner , {
            if ( it != null ) {
                val relativeDetails = it[0]
                Timber.i("RelativeDetailsBefore----->"+it[0])
                val userNameBefore = relativeDetails.firstName
                val mobileBefore = relativeDetails.contactNo
                val emailBefore = relativeDetails.emailAddress
                val ageStringBefore = relativeDetails.dateOfBirth

                if (username != userNameBefore) {
                    isChanges = true
                }
                if (mobile != mobileBefore) {
                    isChanges = true
                }
                if (email != emailBefore) {
                    isChanges = true
                }
                if (dobNew != ageStringBefore) {
                    isChanges = true
                }
            }
        })
        Timber.i("Details_Changed----->$isChanges")
        return isChanges
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if ( isButtonRight ) {
            viewModel.callRemoveRelativesApi(true , relativeId , relationShipID)
        }
    }

}
