package com.caressa.security.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.navigation.Navigation
import com.caressa.common.utils.DateHelper
import java.text.DateFormatSymbols
import java.util.*
import androidx.navigation.fragment.navArgs
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.Utilities
import com.caressa.security.R
import org.koin.android.viewmodel.ext.android.viewModel
import com.caressa.security.databinding.FragmentUserInfoBinding
import com.caressa.security.viewmodel.SignUpViewModel
import timber.log.Timber

class UserInfoFragment: BaseFragment() {

    private var mCalendar: Calendar? = null
    private val viewModel : SignUpViewModel by viewModel()
    private lateinit var binding: FragmentUserInfoBinding
    private val args: UserInfoFragmentArgs by navArgs()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        registerObserver()
        return binding.root
    }

    private fun registerObserver() {
        viewModel.user.observe(viewLifecycleOwner, { })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mCalendar = Calendar.getInstance()
        mCalendar?.add(Calendar.YEAR, -18)

        binding.txtDate.text = DateHelper.getDayOfMonthSuffix( mCalendar?.get(Calendar.DAY_OF_MONTH)!! )
        binding.txtSelectDate.text = DateHelper.getDayOfMonthSuffix(mCalendar?.get(Calendar.DAY_OF_MONTH)!!)

        binding.txtMonth.text = mCalendar?.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)
        binding.txtSelectMonth.text = mCalendar?.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)

        binding.txtYear.text = mCalendar?.get(Calendar.YEAR).toString()
        binding.txtSelectYear.text = mCalendar?.get(Calendar.YEAR).toString()

        binding.txtSelectMonth.setOnClickListener {
            showPopupMenuForMonth(binding.txtSelectMonth)
        }

        binding.txtSelectDate.setOnClickListener {
            showPopupMenuForDate(binding.txtSelectDate)
        }

        binding.txtSelectYear.setOnClickListener {
              showPopupMenuForYear(binding.txtSelectYear)
        }

        binding.btnDone.setOnClickListener {

            Timber.i("Name--->${args.name}")
            Timber.i("Mobile No--->${args.mobileNo}")
            Timber.i("Email--->${args.email}")
            Timber.i("Password--->${args.passCode}")
            if (DateHelper.isDateAbove18Years(DateHelper.convertDateToStr(mCalendar?.time, DateHelper.DISPLAY_DATE_DDMMMYYYY))) {
                if (args.passCode.equals("123456",true)){
                    viewModel.callRegisterAPI(name = args.name, emailStr = args.email, passwordStr = "", phoneNumber = args.mobileNo, gender = (if (binding.rbMale.isChecked) 1 else 2).toString(),
                        dob = DateHelper.convertDateToStr(mCalendar?.time,
                            DateHelper.SERVER_DATE_YYYYMMDD), socialLogin = true)
                }else {
                    viewModel.callRegisterAPI(
                        name = args.name,
                        emailStr = args.email,
                        passwordStr = args.passCode,
                        phoneNumber = args.mobileNo,
                        gender = (if (binding.rbMale.isChecked) 1 else 2).toString(),
                        dob = DateHelper.convertDateToStr(mCalendar?.time, DateHelper.SERVER_DATE_YYYYMMDD))
                }
            } else {
                Utilities.toastMessageShort(context,resources.getString(R.string.ERROR_AGE_GREATER_THEN_18_YEARS))
            }
        }

    }

    private fun showPopupMenuForMonth(view : View) {

        val popupMenu = PopupMenu(context, view)
        val months = DateFormatSymbols().months
        for (i in months.indices) {
            popupMenu.menu.add(0, i, Menu.NONE, months[i])
        }

        popupMenu.setOnMenuItemClickListener {
            binding.txtSelectMonth.text = it.title.toString()
            binding.txtMonth.text = it.title.toString()
            mCalendar?.set( Calendar.MONTH , it.itemId )

            try {
                var selectedDate = binding.txtSelectDate.text.toString()
                selectedDate = selectedDate.substring(0, selectedDate.length - 2)
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, it.itemId)
                calendar.set(Calendar.YEAR, Integer.valueOf(binding.txtSelectYear.text.toString()))
                val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (Integer.valueOf(selectedDate) > days) {
                    val dayOfMonth = DateHelper.getDayOfMonthSuffix(days)
                    binding.txtSelectDate.text = dayOfMonth
                    binding.txtDate.text = dayOfMonth
                    calendar.set(Calendar.DAY_OF_MONTH, days)
                }
                mCalendar = calendar
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun showPopupMenuForDate(view : View) {

        val popupMenu = PopupMenu(context, view)
        val days = mCalendar?.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..days!!) {
            popupMenu.menu.add(0, i, Menu.NONE, i.toString())
        }

        popupMenu.setOnMenuItemClickListener {

            val dayOfMonth = DateHelper.getDayOfMonthSuffix(Integer.valueOf(it.title.toString()))
            binding.txtSelectDate.text = dayOfMonth
            binding.txtDate.text = dayOfMonth

            val months = DateFormatSymbols().months
            var currentMonth = 0
            for (i in months.indices) {
                if (binding.txtSelectMonth.text.toString().equals(months[i], ignoreCase = true)) {
                    currentMonth = i
                    break
                }
            }
            mCalendar?.set(Calendar.DAY_OF_MONTH, Integer.valueOf(it.title.toString()))
            mCalendar?.set(Calendar.MONTH, currentMonth)
            mCalendar?.set(Calendar.YEAR, Integer.valueOf(binding.txtSelectYear.text.toString()))

            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun showPopupMenuForYear(view : View) {

        val popupMenu = PopupMenu(context, view)
        for (i in 100 downTo 1) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -100)
            calendar.add(Calendar.YEAR, i)
            popupMenu.menu.add(0, i, Menu.NONE, calendar.get(Calendar.YEAR).toString())
        }

        popupMenu.setOnMenuItemClickListener {

            binding.txtSelectYear.text = it.title.toString()
            binding.txtYear.text = it.title.toString()
            try {
                val months = DateFormatSymbols().months
                var currentMonth = 0
                for (i in months.indices) {
                    if (binding.txtSelectMonth.text.toString().equals(months[i], ignoreCase = true)) {
                        currentMonth = i
                        break
                    }
                }
                var selectedDate = binding.txtSelectDate.text.toString()
                selectedDate = selectedDate.substring(0, selectedDate.length - 2)
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.MONTH, currentMonth)
                calendar.set(Calendar.YEAR, Integer.valueOf(it.title.toString()))
                val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (Integer.valueOf(selectedDate) > days) {
                    val dayOfMonth = DateHelper.getDayOfMonthSuffix(days)
                    binding.txtSelectDate.text = dayOfMonth
                    binding.txtDate.text = dayOfMonth
                    calendar.set(Calendar.DAY_OF_MONTH, days)
                }
                mCalendar = calendar
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

}
