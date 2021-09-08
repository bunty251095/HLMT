package com.caressa.security.ui

import android.R.id
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.caressa.security.R
import androidx.navigation.fragment.navArgs
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.model.home.UpdateUserDetailsModel
import com.caressa.security.databinding.FragmentUserDetailsBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import com.yalantis.ucrop.UCrop
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.util.*
import android.text.InputType
import android.text.Spanned

import android.text.InputFilter
import java.lang.StringBuilder
import android.R.id.edit

class UserDetailsFragment: BaseFragment() {

    private var mCalendar: Calendar? = null
    private val viewModel : HlmtLoginViewModel by viewModel()
    private lateinit var binding: FragmentUserDetailsBinding
    private val args: UserDetailsFragmentArgs by navArgs()
    private val appColorHelper = AppColorHelper.instance!!

    private var fName = ""
    private var fPath = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        init()
        registerObserver()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {

        if(!args.hlmtUserID.isEmpty()){
            binding.layoutMobileEdit.visibility = View.GONE
        }else{
            binding.layoutMobileEdit.visibility = View.VISIBLE
        }

        binding.edtPhoneNumber.setText(args.mobileNo)

        binding.btnDone.setOnClickListener {

            if(args.isRegister.equals("true")) {
                var gender = "M"
                if (binding.rbMale.isChecked) {
                    gender = "M"
                } else {
                    gender = "F"
                }
                viewModel.fetchRegistrationResponse(
                    name = binding.edtUsername.text.toString(),
                    phoneNumber = args.mobileNo,
                    passwordStr = "123456",
                    gender = gender,
                    dob = binding.edtDob.text.toString(),
                    emailStr = binding.edtEmail.text.toString(),
                    fName = fName,
                    imgPath = fPath,
                    hlmtLoginStatus = args.loginStatus,
                    hlmtEmpId = args.hlmtEmployeeID,
                    hlmtUserId = args.hlmtUserID
                )
            }else{
//                viewModel.callUpdateUserDetailsApi()
            }
        }

        binding.layoutDob.setOnClickListener {
            showDatePickerDialog()
        }
        binding.edtDob.setOnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action) showDatePickerDialog() // Instead of your Toast
            false
        }

        binding.edtUsername.setFilters(arrayOf(ViewUtils.firstLetterCapInputFilter()))

    }

    private fun registerObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner, {
            Timber.i("Data=> "+it)
        })
        viewModel.uploadProfileImage.observe( viewLifecycleOwner , {})
    }

    private fun validateAndUpdate(name:String) {
        println("Inside 1")
        val username = name
        val newEmail = ""
        val newAlternateEmail = ""
        val newNumber = ""
        val newAlternateNumber = ""
        val address = ""

//            //Helper.showMessage(getContext(),"Details Updated");
//            val newUserDetails = UpdateUserDetailsModel.PersonRequest(
//                id = user.id,
//                firstName = username,
//                dateOfBirth = user.dateOfBirth,
//                gender = user.gender.toString(),
//                contact = UpdateUserDetailsModel.Contact(
//                    emailAddress = user.contact.emailAddress,
//                    primaryContactNo = user.contact.primaryContactNo,
//                    alternateEmailAddress = newAlternateEmail,
//                    alternateContactNo = newAlternateNumber,),
//                address = UpdateUserDetailsModel.Address(
//                    addressLine1 = address)
//            )
//            viewModel.callUpdateUserDetailsApi()

    }

    private fun showDatePickerDialog() {
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

        DialogHelper().showDatePickerDialog("Your Date of Birth",requireContext(), mCalendar,null, mCalendar, object :DialogHelper.DateListener{
            override fun onDateSet(date: String, year: String, month: String, dayOfMonth: String) {
                binding.edtDob.setText(date)
            }
        })


        /*val year = mCalendar!!.get(Calendar.YEAR)
        val month = mCalendar!!.get(Calendar.MONTH)
        val day = mCalendar!!.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val _monthOf = DateHelper.returnTwoDigitFromDate(monthOfYear + 1)
                val _monthOfDay = DateHelper.returnTwoDigitFromDate(dayOfMonth)

                val dob = "$year-$_monthOf-$_monthOfDay"
                val strDate = DateHelper.formatDateValue(dob)
                binding.edtDob.setText(strDate)

            },year,month,day
        )
        dpd.setTitle("Your Date of Birth")
        dpd.datePicker.setMaxDate(mCalendar!!.timeInMillis)
        dpd.show()*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

    }

}