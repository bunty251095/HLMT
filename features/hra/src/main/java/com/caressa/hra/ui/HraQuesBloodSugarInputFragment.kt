package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DecimalDigitsInputFilter
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.HraHelper
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.databinding.FragmentHraQuesBloodSugarInputBinding
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import com.caressa.model.entity.TrackParameterMaster
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HraQuesBloodSugarInputFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding : FragmentHraQuesBloodSugarInputBinding
    private val viewModel: HraViewModel by viewModel()

    private var questionData = Question()
    private  val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var viewPagerActivity : HraQuestionsActivity?  = null
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var prevAnsList: MutableList<Option> = mutableListOf()
    private var isBsExist = false

    private var allParamList : MutableList<TrackParameterMaster.Parameter> = mutableListOf()

    private var isRa = false
    private var isFs = false
    private var isPm = false
    private var isHba1c = false

    private var paramCodeRa = "DIAB_RA"
    private var paramCodeFs = "DIAB_FS"
    private var paramCodePm = "DIAB_PM"
    private var paramCodeHba1c = "DIAB_HBA1C"

    private var rs = TrackParameterMaster.Parameter()
    private var fs = TrackParameterMaster.Parameter()
    private var ps = TrackParameterMaster.Parameter()
    private var hb = TrackParameterMaster.Parameter()

    private val textWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            enableOrDisableNextBtn()
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesBloodSugarInputBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if( userVisibleHint ) {
            try {
                initialise()
                setPreviousAnsData()
                checkLabDetailsExist()
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
                checkLabDetailsExist()
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
        viewModel.getParameterDataByProfileCode("DIABETIC")

        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = LengthFilter(4) //Filter to 4 characters
        val decimalFilters = arrayOfNulls<InputFilter>(1)
        decimalFilters[0] = DecimalDigitsInputFilter(5, 1) //Filter to 4 characters and 1 decimal point

        binding.layRandomBs.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.layFastingBs.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.layPostMealBs.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.layHbA1cBs.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)

        binding.layRandomBs.editText!!.filters = filters
        binding.layFastingBs.editText!!.filters = filters
        binding.layPostMealBs.editText!!.filters = filters
        binding.layHbA1cBs.editText!!.filters = decimalFilters

        binding.layRandomBs.setImage(R.drawable.img_diabetes)
        binding.layFastingBs.setImage(R.drawable.img_diabetes)
        binding.layPostMealBs.setImage(R.drawable.img_diabetes)
        binding.layHbA1cBs.setImage(R.drawable.img_diabetes)

        binding.layRandomBs.editText!!.addTextChangedListener(textWatcher)
        binding.layFastingBs.editText!!.addTextChangedListener(textWatcher)
        binding.layPostMealBs.editText!!.addTextChangedListener(textWatcher)
        binding.layHbA1cBs.editText!!.addTextChangedListener(textWatcher)
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen()-1)
        Timber.e("prevAnsList---> $prevAnsList")
        if ( prevAnsList.isNotEmpty() ) {
            viewModel.setPreviousAnswersList( prevAnsList )
            //showHideFields(prevAnsList)
        }
    }

    private fun checkLabDetailsExist()  {
        viewModel.getHRALabDetails()
    }

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

        viewModel.labParameter.observe(viewLifecycleOwner, {
            if ( it != null ) {
                Utilities.printData("allParamList",allParamList,true)
                allParamList.clear()
                allParamList.addAll(it)
                showHideFields(prevAnsList)
            }
        })

        viewModel.labDetailsSavedResponse.observe(viewLifecycleOwner, {
            if (it != null) {
                val labDetailsList = it
                Timber.i("HRALabBsDetailsFromDB----> $labDetailsList")
                if (  labDetailsList.any { labDetail ->
                        prevAnsList.any { option ->
                            option.answerCode.equals(labDetail.ParameterCode,ignoreCase = true) } } ) {
                    for ( record in labDetailsList ) {
                        setSavedData(record.ParameterCode,record.LabValue!!)
                    }
                    isBsExist = true
                }
                if ( !isBsExist ) {
                    Timber.i("Blood Sugar Details does not Exist.")
                    viewModel.callGetLabRecordsBloodSugar(true,viewPagerActivity!!.personId)
                }
            }
        })

        viewModel.labRecordsBs.observe(viewLifecycleOwner, {
            if (it != null) {
                val labRecords = it.LabRecords!!
                if ( labRecords.isNotEmpty() ) {
                    val list = HraHelper.filterLabRecords(labRecords)
                    Timber.i("HRALabBsDetailsFromServer----> $list")
                    for ( record in list) {
                        setSavedData(record.ParameterCode!!,record.Value!!)
                        viewModel.saveHRALabDetailsBasedOnType("SUGAR",record.ParameterCode!!,record.Value!!,record.Unit!!)
                    }
                }
            }
        })

    }

    private fun setClickable() {

        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
            if ( !Utilities.isNullOrEmpty(binding.layRandomBs.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layFastingBs.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layPostMealBs.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layHbA1cBs.getValue())) {
                if ( validateValuesAndSaveData() ) {
                    Utilities.hideKeyboard(requireActivity())
                    hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList

                    viewModel.saveResponse("KNWDIANUM","85_YES",resources.getString(R.string.YES),questionData.category,questionData.tabName,"")
                    clearUnselectedLabValues()
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            } else {
                Utilities.hideKeyboard(requireActivity())
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }
        }

        binding.btnNext.setOnClickListener {
            if ( validateValuesAndSaveData() ) {
                Utilities.hideKeyboard(requireActivity())
                hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList

                viewModel.saveResponse("KNWDIANUM","85_YES",resources.getString(R.string.YES),questionData.category,questionData.tabName,"")
                clearUnselectedLabValues()
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }
        }

    }

    private fun setSavedData( parameterCode:String,value: String ) {
        if ( !Utilities.isNullOrEmptyOrZero(value) ) {
            when(parameterCode) {
                paramCodeRa -> binding.layRandomBs.setValue(value.toDouble().toInt().toString())
                paramCodeFs -> binding.layFastingBs.setValue(value.toDouble().toInt().toString())
                paramCodePm -> binding.layPostMealBs.setValue(value.toDouble().toInt().toString())
                paramCodeHba1c -> binding.layHbA1cBs.setValue(value.toDouble().toInt().toString())
            }
        }
    }

    private fun clearUnselectedLabValues() {
        if ( !isRa ) {
            viewModel.clearHRALabValue(paramCodeRa)
        }
        if ( !isFs ) {
            viewModel.clearHRALabValue(paramCodeFs)
        }
        if ( !isPm ) {
            viewModel.clearHRALabValue(paramCodePm)
        }
        if ( !isHba1c ) {
            viewModel.clearHRALabValue(paramCodeHba1c)
        }
    }

    private fun validateValuesAndSaveData(): Boolean {
        selectedOptionList.clear()
        val rbs = binding.layRandomBs.getValue()
        val fbs = binding.layFastingBs.getValue()
        val pbs = binding.layPostMealBs.getValue()
        val hab1cbs = binding.layHbA1cBs.getValue()

        if ( binding.randomBs.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(rbs) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid Random Sugar value")
                return false
            }  else {
                if ( rbs.toDouble() < rs.minPermissibleValue!!.toDouble() || rbs.toDouble() > rs.maxPermissibleValue!!.toDouble() ) {
                    Utilities.toastMessageShort(context, "Random Sugar value must be between "
                            + rs.minPermissibleValue + "-" + rs.maxPermissibleValue )
                    return false
                } else {
                    isRa = true
                    viewModel.saveHRALabDetails( paramCodeRa,rbs,rs.unit!! )
                    selectedOptionList.add(Option(rs.description + " : $rbs " + rs.unit, paramCodeRa,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeRa)
        }

        if ( binding.fastingBs.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(fbs) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid Fasting Sugar value")
                return false
            }  else {
                if ( fbs.toDouble() < fs.minPermissibleValue!!.toDouble() || fbs.toDouble() > fs.maxPermissibleValue!!.toDouble() ) {
                    Utilities.toastMessageShort(context, "Fasting Sugar value must be between "
                            + fs.minPermissibleValue + "-" + fs.maxPermissibleValue )
                    return false
                } else {
                    isFs = true
                    viewModel.saveHRALabDetails( paramCodeFs,fbs,fs.unit!! )
                    selectedOptionList.add(Option(fs.description + " : $fbs " + fs.unit, paramCodeFs,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeFs)
        }

        if ( binding.postMealBs.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(pbs) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid Post Meal Blood Sugar value")
                return false
            }  else {
                if ( pbs.toDouble() < ps.minPermissibleValue!!.toDouble() || pbs.toDouble() > ps.maxPermissibleValue!!.toDouble() ) {
                    Utilities.toastMessageShort(context, "Post Meal Blood Sugar value must be between "
                            + ps.minPermissibleValue + "-" + ps.maxPermissibleValue )
                    return false
                } else {
                    isPm = true
                    viewModel.saveHRALabDetails( paramCodePm,pbs,ps.unit!! )
                    selectedOptionList.add(Option(ps.description + " : $pbs " + ps.unit, paramCodePm,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodePm)
        }

        if ( binding.hab1cBs.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(hab1cbs) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid HbA1c value")
                return false
            }  else {
                if ( hab1cbs.toDouble() < hb.minPermissibleValue!!.toDouble() || hab1cbs.toDouble() > hb.maxPermissibleValue!!.toDouble() ) {
                    Utilities.toastMessageShort(context, "HbA1c value must be between "
                            + hb.minPermissibleValue + "-" + hb.maxPermissibleValue )
                    return false
                } else {
                    isHba1c = true
                    viewModel.saveHRALabDetails( paramCodeHba1c,hab1cbs ,hb.unit!! )
                    selectedOptionList.add(Option(hb.description + " : $hab1cbs " + hb.unit, paramCodeHba1c,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeHba1c)
        }

        return true
    }

    private fun showHideFields( prevAnsList : MutableList<Option> ) {

        if ( prevAnsList.any { it.answerCode.equals(paramCodeRa,ignoreCase = true) } ) {
            rs =  allParamList.find { it.code.equals(paramCodeRa, true) }!!
            Utilities.printData("paramCodeRa",rs,true)
            binding.lblQuesRandomBs.text = rs.description
            binding.layRandomBs.setHint( "" + rs.minPermissibleValue + " - " + rs.maxPermissibleValue )
            binding.layRandomBs.setUnit(rs.unit!!)
            binding.randomBs.visibility = View.VISIBLE
        } else {
            binding.randomBs.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodeFs,ignoreCase = true) } ) {
            fs =  allParamList.find { it.code.equals(paramCodeFs, true) }!!
            Utilities.printData("paramCodeFs",fs,true)
            binding.lblQuesFastingBs.text = fs.description
            binding.layFastingBs.setHint( "" + fs.minPermissibleValue + " - " + fs.maxPermissibleValue )
            binding.layFastingBs.setUnit(fs.unit!!)
            binding.fastingBs.visibility = View.VISIBLE
        } else {
            binding.fastingBs.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodePm,ignoreCase = true) } ) {
            ps =  allParamList.find { it.code.equals(paramCodePm, true) }!!
            Utilities.printData("paramCodePm",ps,true)
            binding.lblQuesPostMealBs.text = ps.description
            binding.layPostMealBs.setHint( "" + ps.minPermissibleValue + " - " + ps.maxPermissibleValue )
            binding.layPostMealBs.setUnit(ps.unit!!)
            binding.postMealBs.visibility = View.VISIBLE
        } else {
            binding.postMealBs.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodeHba1c,ignoreCase = true) } ) {
            hb =  allParamList.find { it.code.equals(paramCodeHba1c, true) }!!
            Utilities.printData("paramCodeHba1c",hb,true)
            binding.lblQuesHbA1cBs.text = hb.description
            binding.layHbA1cBs.setHint( "" + hb.minPermissibleValue + " - " + hb.maxPermissibleValue )
            binding.layHbA1cBs.setUnit(hb.unit!!)
            binding.hab1cBs.visibility = View.VISIBLE
        } else {
            binding.hab1cBs.visibility = View.GONE
        }

    }

    private fun enableOrDisableNextBtn() {
        var toEnable = true
        if ( binding.randomBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layRandomBs.getValue()) ) {
            toEnable = false
        }
        if ( binding.fastingBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layFastingBs.getValue()) ) {
            toEnable = false
        }
        if ( binding.postMealBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layPostMealBs.getValue()) ) {
            toEnable = false
        }
        if ( binding.hab1cBs.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layHbA1cBs.getValue()) ) {
            toEnable = false
        }

        if ( toEnable ) {
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