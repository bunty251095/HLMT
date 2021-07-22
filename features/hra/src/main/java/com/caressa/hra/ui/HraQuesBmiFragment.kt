package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.CalculateParameters
import com.caressa.common.utils.HeightWeightDialog
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.*
import com.caressa.hra.databinding.FragmentHraQuesBmiBinding
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class HraQuesBmiFragment(val qCode: String) : BaseFragment(),
    HeightWeightDialog.OnDialogValueListener{

    private lateinit var binding : FragmentHraQuesBmiBinding
    private val viewModel: HraViewModel by viewModel()

    private var toShow : Boolean = true
    private var bmi : Double = 0.0
    private var height : Double = 0.0
    private var weight : Double = 0.0
    private var questionData = Question()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var viewPagerActivity : HraQuestionsActivity?  = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesBmiBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        if( userVisibleHint ) {
            try {
                initialise()
                setPreviousAnsData()
                checkBMIExist()
                registerObservers()
                setClickable()
            } catch ( e : Exception ) {
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
                checkBMIExist()
                registerObservers()
                setClickable()
            } catch ( e : Exception ) {
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

        binding.layHeight.setNonEditable()
        binding.layHeight.setImage(R.drawable.img_height)
        binding.layHeight.setHint("Height")
        binding.layHeight.setUnit(resources.getString(R.string.cm))

        binding.layWeight.setNonEditable()
        binding.layWeight.setImage(R.drawable.img_weight)
        binding.layWeight.setHint("Weight")
        binding.layWeight.setUnit(resources.getString(R.string.kg))
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        val prevAnsList : MutableList<Option> = mutableListOf()
        if ( !Utilities.isNullOrEmpty(viewPagerActivity!!.personName)
            && !Utilities.isNullOrEmpty(viewPagerActivity!!.personId) ) {
            prevAnsList.add(Option(viewPagerActivity!!.personName,viewPagerActivity!!.personId,true))
        }
        Timber.e("prevAnsList---> $prevAnsList")
        if ( prevAnsList.isNotEmpty() ) {
            viewModel.setPreviousAnswersList(prevAnsList)
        }
    }

    private fun checkBMIExist()  {
        viewModel.getHRAVitalDetails( )
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun registerObservers() {

        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner, {
            if ( it != null ) {
                if ( toProceed ) {
                    questionData = it
                    hraDataSingleton.question = it
                    toProceed = false
                }
            }
        })

        viewModel.vitalDetailsSavedResponse.observe(viewLifecycleOwner, { vitalDetails ->
            if (vitalDetails != null) {
                //Timber.i("HraVitalDetails--->$vitalDetails")
                val bmiDetails = vitalDetails.filter { vital ->
                    vital.VitalsKey.equals(HRAConstants.VitalKey_Height,ignoreCase = true)
                            || vital.VitalsKey.equals(HRAConstants.VitalKey_Weight,ignoreCase = true)
                            || vital.VitalsKey.equals(HRAConstants.VitalKey_BMI,ignoreCase = true)
                }
                Timber.i("HRAVitalDetails----> $bmiDetails")
                for ( vital in bmiDetails ) {
                    Timber.i("VitalParameter , VitalValue---->"+vital.VitalsKey + " , " +vital.VitalsValue)
                    if ( vital.VitalsKey.equals(HRAConstants.VitalKey_Height, ignoreCase = true)
                        && !Utilities.isNullOrEmpty(vital.VitalsValue) ) {
                        height = vital.VitalsValue.toDouble()
                        toShow = false
                        binding.layHeight.setValue(height.toInt().toString())
                        binding.layHeight.setUnit(resources.getString(R.string.cm))
                    }
                    if (vital.VitalsKey.equals(HRAConstants.VitalKey_Weight, ignoreCase = true)
                        && !Utilities.isNullOrEmpty(vital.VitalsValue) ) {
                        weight = vital.VitalsValue.toDouble()
                        binding.layWeight.setValue(weight.toInt().toString())
                        binding.layWeight.setUnit(resources.getString(R.string.kg))
                    }
                    if (vital.VitalsKey.equals(HRAConstants.VitalKey_BMI, ignoreCase = true)
                        && !Utilities.isNullOrEmpty(vital.VitalsValue) ) {
                        bmi = vital.VitalsValue.toDouble()
                    }
                }
                if ( Utilities.isNullOrEmptyOrZero(height.toString())
                    && Utilities.isNullOrEmptyOrZero(weight.toString())
                    && Utilities.isNullOrEmptyOrZero(bmi.toString()) ) {
                    Timber.e("BMI not Exist...!!!")
                    viewModel.callIsBMIExist(true,viewPagerActivity!!.personId)
                } else {
                    Observations.setBMIResult(bmi.toString(), binding.txtObservation, requireContext())
                    enableNextBtn()
                }
            }
        })

        viewModel.bmiDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                val bmiDetails = it
                if ( !Utilities.isNullOrEmptyOrZero( bmiDetails.BMI.Height)
                    && !Utilities.isNullOrEmptyOrZero( bmiDetails.BMI.Height) ) {
                    try {
                        height = bmiDetails.BMI.Height!!.toDouble()
                        weight = bmiDetails.BMI.Weight!!.toDouble()

                        binding.layHeight.setValue(height.toInt().toString())
                        binding.layHeight.setUnit(resources.getString(R.string.cm))

                        binding.layWeight.setValue(weight.toInt().toString())
                        binding.layWeight.setUnit(resources.getString(R.string.kg))

                        Observations.setBMIResult(bmiDetails.BMI.Value!!,binding.txtObservation,requireContext())
                        enableNextBtn()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
            viewPagerActivity!!.backToSelectFamilyMember()
        }

        binding.btnNext.setOnClickListener {
            if (Validations.validationBMI(height, weight, requireContext())) {
                saveResponseForNextScreen()
                viewModel.saveBMIDetails(height, weight, getBMI().toDouble())
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }
        }

        binding.layHeight.setOnClickListener {
            HraHelper.showHeightDialog(height.toInt(),binding.layHeight,this@HraQuesBmiFragment,requireContext())
        }

        binding.layHeight.editText!!.setOnClickListener {
            HraHelper.showHeightDialog(height.toInt(),binding.layHeight,this@HraQuesBmiFragment,requireContext())
        }

        binding.layHeight.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && toShow ) {
                HraHelper.showHeightDialog(height.toInt(),binding.layHeight,this@HraQuesBmiFragment,requireContext())
            }
        }

        binding.layWeight.setOnClickListener {
            HraHelper.showWeightDialog(weight.toInt(),binding.layWeight,this@HraQuesBmiFragment,requireContext())
        }

        binding.layWeight.editText!!.setOnClickListener {
            HraHelper.showWeightDialog(weight.toInt(),binding.layWeight,this@HraQuesBmiFragment,requireContext())
        }

        binding.layWeight.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                HraHelper.showWeightDialog(weight.toInt(),binding.layWeight,this@HraQuesBmiFragment,requireContext())
            }
        }

    }

    override fun onDialogValueListener(dialogType: String, height: String, weight: String, unit: String, visibleValue: String) {
        viewModel.updateUserPreference(unit)
        if (dialogType.equals("Height", ignoreCase = true)) {
            this.height = height.toDouble()
            binding.layHeight.setValue(visibleValue)
            binding.layHeight.setUnit(unit.toLowerCase(Locale.ROOT))
            Timber.i("Height::visibleValue----> $height , $visibleValue")
        } else {
            this.weight = weight.toDouble()
            binding.layWeight.setValue(visibleValue)
            binding.layWeight.setUnit(unit.toLowerCase(Locale.ROOT))
            Timber.i("Weight::visibleValue----> $weight , $visibleValue")
        }
        if (!Utilities.isNullOrEmptyOrZero(binding.layHeight.getValue())
            && !Utilities.isNullOrEmptyOrZero(binding.layWeight.getValue()) ) {
            Observations.setBMIResult( getBMI(), binding.txtObservation, requireContext() )
            enableNextBtn()
        }
    }

    private fun getBMI() : String {
        return String.format("%.1f", CalculateParameters.getBMI(height, weight))
    }

    private fun saveResponseForNextScreen() {
        selectedOptionList = mutableListOf()
        selectedOptionList.add(Option(resources.getString(R.string.HEIGHT) + " : ${height.toInt()} " + resources.getString(R.string.cm),Constants.HEIGHT,true))
        selectedOptionList.add(Option(resources.getString(R.string.WEIGHT) + " : $weight " + resources.getString(R.string.kg),Constants.WEIGHT,true))
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] = selectedOptionList
    }

    private fun enableNextBtn( ) {
        binding.btnNext.isEnabled = true
        binding.btnNext.background = ContextCompat.getDrawable(requireContext(), R.drawable.btn_round_fill_hra)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.btnNext.backgroundTintList = AppCompatResources.getColorStateList(requireContext(), R.color.vivant_marigold)
        }
        binding.btnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSource(qCode)
    }

}