package com.caressa.security.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DateHelper
import com.caressa.security.databinding.FragmentUserDetailsBinding
import com.caressa.security.viewmodel.HlmtLoginViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class UserDetailsFragment: BaseFragment() {

    private var mCalendar: Calendar? = null
    private val viewModel : HlmtLoginViewModel by viewModel()
    private lateinit var binding: FragmentUserDetailsBinding
    private val args: UserDetailsFragmentArgs by navArgs()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        init()
        registerObserver()
        return binding.root
    }

    private fun init() {
        binding.edtPhoneNumber.setText(args.mobileNo)

        binding.btnDone.setOnClickListener {

            var gender = "M"
            if(binding.rbMale.isChecked){
                gender = "M"
            }else{
                gender = "F"
            }
            viewModel.fetchRegistrationResponse(name = binding.edtUsername.text.toString(),phoneNumber = args.mobileNo,
            passwordStr = "123456",gender = gender,dob = binding.edtDob.text.toString(),emailStr = binding.edtEmail.text.toString())
        }

        binding.btnBack.setOnClickListener {

        }
        binding.layoutDob.setOnClickListener {
            showDatePickerDialog()
        }
        binding.edtDob.setOnTouchListener(OnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action) showDatePickerDialog() // Instead of your Toast
            false
        })
//        binding.edtDob.setOnClickListener {
//            showDatePickerDialog()
//        }
    }

    private fun registerObserver() {
        viewModel.loginResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Timber.i("Data=> "+it)
        })
    }

    private fun showDatePickerDialog() {
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)
        val year = mCalendar!!.get(Calendar.YEAR)
        val month = mCalendar!!.get(Calendar.MONTH)
        val day = mCalendar!!.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(requireContext(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                val _monthOf = returnTwoDigitFromDate(monthOfYear + 1)
                val _monthOfDay = returnTwoDigitFromDate(dayOfMonth)

                val dob = "$year-$_monthOf-$_monthOfDay"
                val strDate = DateHelper.formatDateValue(dob)
                binding.edtDob.setText(strDate)

            },year,month,day
        )
        dpd.setTitle("Your Date of Birth")
        dpd.datePicker.setMaxDate(mCalendar!!.timeInMillis)
        dpd.show()
    }

    fun returnTwoDigitFromDate(date: Int): String {
        var twoDigit = "" + date
        if (date < 10) {
            twoDigit = "0$twoDigit"
        }
        return twoDigit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

/*
        binding.btnBack.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.back_signup) )



        binding.btnDone.setOnClickListener {

            if (DateHelper.isDateAbove18Years(DateHelper.convertDateToStr(mCalendar?.time, DateHelper.DISPLAY_DATE_DDMMMYYYY))) {
//                if (args.passCode.equals("123456",true)){
//                    viewModel.callRegisterAPI(name = args.name, emailStr = args.email, passwordStr = "", phoneNumber = args.mobileNo, gender = (if (binding.rbMale.isChecked) 1 else 2).toString(),
//                        dob = DateHelper.convertDateToStr(mCalendar?.time,
//                            DateHelper.SERVER_DATE_YYYYMMDD), socialLogin = true)
//                }else {
//                    viewModel.callRegisterAPI(
//                        name = args.name,
//                        emailStr = args.email,
//                        passwordStr = args.passCode,
//                        phoneNumber = args.mobileNo,
//                        gender = (if (binding.rbMale.isChecked) 1 else 2).toString(),
//                        dob = DateHelper.convertDateToStr(mCalendar?.time, DateHelper.SERVER_DATE_YYYYMMDD))
//                }
            } else {
                viewModel.toastMessage(resources.getString(R.string.ERROR_AGE_GREATER_THEN_18_YEARS))
            }
        }
*/

    }


}