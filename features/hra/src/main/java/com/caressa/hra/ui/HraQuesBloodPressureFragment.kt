package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.HRAConstants
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.common.Observations
import com.caressa.hra.common.Validations
import com.caressa.hra.databinding.FragmentHraQuesBloodPressureBinding
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HraQuesBloodPressureFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding : FragmentHraQuesBloodPressureBinding
    private val viewModel: HraViewModel by viewModel()

    private var systolic = 0
    private var diastolic = 0
    private var questionData = Question()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var viewPagerActivity : HraQuestionsActivity?  = null
    private var selectedOptionList: MutableList<Option> = mutableListOf()

    private val textWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            updateValuesAndSetObs()
        }

    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesBloodPressureBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        if( userVisibleHint ) {
            try {
                initialise()
                setPreviousAnsData()
                checkBpExist()
                registerObservers()
                setClickable()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if ( isVisibleToUser && isResumed ) {
            try {
                initialise()
                setPreviousAnsData()
                checkBpExist()
                registerObservers()
                setClickable()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun initialise() {
        viewPagerActivity = (activity as HraQuestionsActivity)
        Timber.e("qCode----->$qCode")
        Timber.e("CurrentPageNo--->" +viewPagerActivity!!.getCurrentScreen() )
        viewModel.getHRAQuestionData(qCode)

        binding.laySystolic.setHint(resources.getString(R.string.SYSTOLIC))
        binding.laySystolic.setImage(R.drawable.img_systolic)
        binding.laySystolic.setUnit(resources.getString(R.string.MM_HG))
        binding.laySystolic.editText!!.addTextChangedListener(textWatcher)

        binding.layDiastolic.setHint(resources.getString(R.string.DIASTOLIC))
        binding.layDiastolic.setImage(R.drawable.img_systolic)
        binding.layDiastolic.setUnit(resources.getString(R.string.MM_HG))
        binding.layDiastolic.editText!!.addTextChangedListener(textWatcher)
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        val prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen()-1)
        Timber.e("prevAnsList---> $prevAnsList")
        if ( prevAnsList.isNotEmpty() ) {
            viewModel.setPreviousAnswersList(prevAnsList)
        }
    }

    private fun checkBpExist()  {
        viewModel.getHRAVitalDetails( )
    }

    private fun registerObservers() {

        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner) {
            if (it != null) {
                if (toProceed) {
                    questionData = it
                    hraDataSingleton.question = it
                    toProceed = false
                }
            }
        }

        viewModel.vitalDetailsSavedResponse.observe(viewLifecycleOwner) { vitalDetails ->
            if (vitalDetails != null) {
                //Timber.i("HraVitalDetails--->$vitalDetails")
                var strSystolic = ""
                var strDiastolic = ""
                val bpDetails = vitalDetails.filter { vital ->
                    vital.VitalsKey.equals(HRAConstants.VitalKey_SystolicBP, ignoreCase = true)
                            || vital.VitalsKey.equals(
                        HRAConstants.VitalKey_DiastolicBP,
                        ignoreCase = true
                    )
                }
                Timber.i("HraVitalDetails--->$bpDetails")
                for (bp in bpDetails) {
                    if (bp.VitalsKey.equals(HRAConstants.VitalKey_SystolicBP, ignoreCase = true)) {
                        if (!Utilities.isNullOrEmptyOrZero(bp.VitalsValue)) {
                            binding.laySystolic.setValue(bp.VitalsValue)
                            strSystolic = bp.VitalsValue
                        }
                        if (bp.VitalsValue == "0") {
                            binding.laySystolic.setValue("")
                            strSystolic = "0"
                        }
                    }
                    if (bp.VitalsKey.equals(HRAConstants.VitalKey_DiastolicBP, ignoreCase = true)) {
                        if (!Utilities.isNullOrEmptyOrZero(bp.VitalsValue)) {
                            binding.layDiastolic.setValue(bp.VitalsValue)
                            strDiastolic = bp.VitalsValue
                        }
                        if (bp.VitalsValue == "0") {
                            binding.layDiastolic.setValue("")
                            strDiastolic = "0"
                        }
                    }
                }
                if (Utilities.isNullOrEmpty(strSystolic) && Utilities.isNullOrEmpty(strDiastolic)) {
                    Timber.e("BP Details not Exist...!!!")
                    viewModel.callIsBPExist(true, viewPagerActivity!!.personId)
                }
            }
        }

        viewModel.bpDetails.observe(viewLifecycleOwner) { bpDetails ->
            if (bpDetails != null) {
                if (!Utilities.isNullOrEmptyOrZero(bpDetails.bloodPressure.Systolic) &&
                    !Utilities.isNullOrEmptyOrZero(bpDetails.bloodPressure.Diastolic)
                ) {
                    val systolic = bpDetails.bloodPressure.Systolic!!.toDouble().toInt()
                    val diastolic = bpDetails.bloodPressure.Diastolic!!.toDouble().toInt()
                    binding.laySystolic.setValue(systolic.toString())
                    binding.layDiastolic.setValue(diastolic.toString())
                }
            }
        }

    }

    private fun setClickable() {

        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->

            if ( !Utilities.isNullOrEmpty(binding.laySystolic.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layDiastolic.getValue()) ) {
                if ( Validations.validateBP(systolic,diastolic,requireContext()) ) {
                    Utilities.hideKeyboard(requireActivity())
                    saveResponseForNextScreen(true)

                    viewModel.saveResponse("KNWBPNUM","86_YES","Yes",questionData.category,questionData.tabName,"")
                    viewModel.saveBloodPressureDetails( systolic, diastolic )
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            } else {
                Utilities.hideKeyboard(requireActivity())
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() -1 )
            }
        }

        binding.btnNext.setOnClickListener {
            if ( Validations.validateBP(systolic,diastolic,requireContext()) ) {
                Utilities.hideKeyboard(requireActivity())
                saveResponseForNextScreen(true)
                viewModel.saveResponse("KNWBPNUM","86_YES","Yes",questionData.category,questionData.tabName,"")
                viewModel.saveBloodPressureDetails( systolic, diastolic )
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }
        }

        binding.btnDontRemember.setOnClickListener {
            systolic = 0
            diastolic = 0
            Utilities.hideKeyboard(requireActivity())
            saveResponseForNextScreen(false)
            viewModel.saveResponse("KNWBPNUM","86_NO","No",questionData.category,questionData.tabName,"")
            viewModel.saveBloodPressureDetails( systolic, diastolic )
            viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
        }

    }

    private fun updateValuesAndSetObs() {
        try {
            systolic = if (!Utilities.isNullOrEmptyOrZero(binding.laySystolic.editText!!.text.toString())) {
                binding.laySystolic.editText!!.text.toString().toDouble().toInt()
            } else {
                0
            }
            diastolic = if (!Utilities.isNullOrEmptyOrZero(binding.layDiastolic.editText!!.text.toString())) {
                binding.layDiastolic.editText!!.text.toString().toDouble().toInt()
            } else {
                0
            }

            if ( !Utilities.isNullOrEmptyOrZero(systolic.toString()) && !Utilities.isNullOrEmptyOrZero(diastolic.toString()) ) {
                enableNextBtn(true)
            } else {
                enableNextBtn(false)
            }
            Observations.setBPResult(systolic,diastolic,binding.txtObservation,requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveResponseForNextScreen( isFilled : Boolean ) {
        selectedOptionList = mutableListOf()
        if ( isFilled ) {
            selectedOptionList.add(Option(resources.getString(R.string.SYSTOLIC) + " : $systolic " + resources.getString(R.string.MM_HG), Constants.SYSTOLIC,true))
            selectedOptionList.add(Option(resources.getString(R.string.DIASTOLIC) + " : $diastolic "+ resources.getString(R.string.MM_HG), Constants.DIASTOLIC,true))
        } else {
            selectedOptionList.add(Option(resources.getString(R.string.I_DONT_REMEMBER),"",true))

        }
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList
    }

    private fun enableNextBtn( enable : Boolean ) {

        if ( enable ) {
            binding.btnNext.isEnabled = true
            binding.btnNext.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_round_enabled_hra)
            binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding.btnNext.isEnabled = false
            binding.btnNext.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_round_disabled_hra)
            binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.vivant_charcoal_grey_55))
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSource(qCode)
    }

}