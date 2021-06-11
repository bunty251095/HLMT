package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.ParameterDataModel
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.HraHelper
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.databinding.FragmentHraQuesCholestrolInputBinding
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HraQuesCholesterolInputFragment(val qCode:String) : BaseFragment() {

    private lateinit var binding : FragmentHraQuesCholestrolInputBinding
    private val viewModel: HraViewModel by viewModel()

    private var questionData = Question()
    private  val hraDataSingleton = HraDataSingleton.getInstance()!!
    private var viewPagerActivity : HraQuestionsActivity?  = null
    private var selectedOptionList: MutableList<Option> = mutableListOf()
    private var prevAnsList: MutableList<Option> = mutableListOf()
    private var isCholesterolExist = false

    private var isTotalChol = false
    private var isHdl = false
    private var isLdl = false
    private var isTry = false
    private var isVldl = false

    private var paramCodeTotalChol = "CHOL_TOTAL"
    private var paramCodeHdl = "CHOL_HDL"
    private var paramCodeLdl = "CHOL_LDL"
    private var paramCodeTry = "CHOL_TRY"
    private var paramCodeVldl = "CHOL_VLDL"

    private val cholTotal: ParameterDataModel = HraHelper.getParamData(paramCodeTotalChol)
    private val hdl: ParameterDataModel = HraHelper.getParamData(paramCodeHdl)
    private val ldl: ParameterDataModel = HraHelper.getParamData(paramCodeLdl)
    private val triglyceride: ParameterDataModel = HraHelper.getParamData(paramCodeTry)
    private val vldl: ParameterDataModel = HraHelper.getParamData(paramCodeVldl)

    private val textWatcher: TextWatcher = object : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            enableOrDisableNextBtn()
        }

    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesCholestrolInputBinding.inflate(inflater, container, false)
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

        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = InputFilter.LengthFilter(4) //Filter to 4 characters

        binding.layTotalChol.setHint( "" + cholTotal.minRange.toInt() + " - " + cholTotal.maxRange.toInt() )
        binding.layHdl.setHint( "" + hdl.minRange.toInt() + " - " + hdl.maxRange.toInt() )
        binding.layLdl.setHint( "" + ldl.minRange.toInt() + " - " + ldl.maxRange.toInt() )
        binding.layTriglycerides.setHint( "" + triglyceride.minRange.toInt() + " - " + triglyceride.maxRange.toInt() )
        binding.layVldl.setHint( "" + vldl.minRange.toInt() + " - " + vldl.maxRange.toInt() )

        binding.layTotalChol.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.layHdl.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.layLdl.setInputType(InputType.TYPE_CLASS_NUMBER)
        binding.layTriglycerides.setInputType(InputType.TYPE_CLASS_NUMBER )
        binding.layVldl.setInputType(InputType.TYPE_CLASS_NUMBER)

        binding.layTotalChol.editText!!.filters = filters
        binding.layHdl.editText!!.filters = filters
        binding.layLdl.editText!!.filters = filters
        binding.layTriglycerides.editText!!.filters = filters
        binding.layVldl.editText!!.filters = filters

        binding.layTotalChol.setImage(R.drawable.img_cholesteroal)
        binding.layHdl.setImage(R.drawable.img_cholesteroal)
        binding.layLdl.setImage(R.drawable.img_cholesteroal)
        binding.layTriglycerides.setImage(R.drawable.img_cholesteroal)
        binding.layVldl.setImage(R.drawable.img_cholesteroal)

        binding.layTotalChol.setUnit(resources.getString(R.string.MG_DL))
        binding.layHdl.setUnit(resources.getString(R.string.MG_DL))
        binding.layLdl.setUnit(resources.getString(R.string.MG_DL))
        binding.layTriglycerides.setUnit(resources.getString(R.string.MG_DL))
        binding.layVldl.setUnit(resources.getString(R.string.MG_DL))

        binding.layTotalChol.editText!!.addTextChangedListener(textWatcher)
        binding.layHdl.editText!!.addTextChangedListener(textWatcher)
        binding.layLdl.editText!!.addTextChangedListener(textWatcher)
        binding.layTriglycerides.editText!!.addTextChangedListener(textWatcher)
        binding.layVldl.editText!!.addTextChangedListener(textWatcher)
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen()-1)
        Timber.e("prevAnsList---> $prevAnsList")
        if ( prevAnsList.isNotEmpty() ) {
            viewModel.setPreviousAnswersList( prevAnsList )
            showHideFields(prevAnsList)
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

        viewModel.labDetailsSavedResponse.observe(viewLifecycleOwner, {
            if (it != null) {
                val labDetailsList = it
                Timber.i("HRALabCholDetailsFromDB----> $labDetailsList")
                if ( labDetailsList.any { labDetail ->
                        prevAnsList.any { option ->
                            option.answerCode.equals(labDetail.ParameterCode,ignoreCase = true) } } ) {
                    for ( record in labDetailsList ) {
                        setSavedData(record.ParameterCode,record.LabValue!!)
                    }
                    isCholesterolExist = true
                }
                if ( !isCholesterolExist ) {
                    Timber.i("Cholesterol Details does not Exist.")
                    viewModel.callGetLabRecordsCholesterol(true,viewPagerActivity!!.personId)
                }
            }
        })

        viewModel.labRecordsChol.observe(viewLifecycleOwner, {
            if (it != null) {
                val labRecords = it.LabRecords!!
                if ( labRecords.isNotEmpty() ) {
                    val list = HraHelper.filterLabRecords(labRecords)
                    Timber.i("HRALabCholDetailsFromServer----> $list")
                    for ( record in list) {
                        setSavedData(record.ParameterCode!!,record.Value!!)
                        viewModel.saveHRALabDetailsBasedOnType("LIPID",record.ParameterCode!!,record.Value!!)
                    }
                }
            }
        })

    }

    private fun setClickable() {

        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
            if ( !Utilities.isNullOrEmpty(binding.layTotalChol.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layHdl.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layLdl.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layTriglycerides.getValue()) ||
                !Utilities.isNullOrEmpty(binding.layVldl.getValue())) {
                if ( validateValuesAndSaveData() ) {
                    Utilities.hideKeyboard(requireActivity())
                    hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList

                    viewModel.saveResponse("KNWLIPNUM","84_YES","Yes",questionData.category,questionData.tabName,"")
                    clearUnselectedLabValues()
                    viewModel.removeSource(qCode)
                    viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() - 1 )
                }
            } else {
                Utilities.hideKeyboard(requireActivity())
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() - 1 )
            }

        }

        binding.btnNext.setOnClickListener {
            if ( validateValuesAndSaveData() ) {
                Utilities.hideKeyboard(requireActivity())
                hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen() ] = selectedOptionList

                viewModel.saveResponse("KNWLIPNUM","84_YES","Yes",questionData.category,questionData.tabName,"")
                clearUnselectedLabValues()
                viewModel.removeSource(qCode)
                viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() +1 )
            }
        }

    }

    private fun setSavedData( parameterCode:String,value: String ) {
        if ( !Utilities.isNullOrEmptyOrZero(value) ) {
            when(parameterCode) {
                paramCodeTotalChol -> binding.layTotalChol.setValue(value.toDouble().toInt().toString())
                paramCodeHdl -> binding.layHdl.setValue(value.toDouble().toInt().toString())
                paramCodeLdl -> binding.layLdl.setValue(value.toDouble().toInt().toString())
                paramCodeTry -> binding.layTriglycerides.setValue(value.toDouble().toInt().toString())
                paramCodeVldl -> binding.layVldl.setValue(value.toDouble().toInt().toString())
            }
        }
    }

    private fun clearUnselectedLabValues() {
        if ( !isTotalChol ) {
            viewModel.clearHRALabValue(paramCodeTotalChol)
        }
        if ( !isHdl ) {
            viewModel.clearHRALabValue(paramCodeHdl)
        }
        if ( !isLdl ) {
            viewModel.clearHRALabValue(paramCodeLdl)
        }
        if ( !isTry ) {
            viewModel.clearHRALabValue(paramCodeTry)
        }
        if ( !isVldl ) {
            viewModel.clearHRALabValue(paramCodeVldl)
        }
    }

    private fun validateValuesAndSaveData(): Boolean {
        selectedOptionList.clear()
        val totalChol = binding.layTotalChol.getValue()
        val hdlChol = binding.layHdl.getValue()
        val ldlChol = binding.layLdl.getValue()
        val triglycerideChol = binding.layTriglycerides.getValue()
        val vldlChol = binding.layVldl.getValue()

        if ( binding.totalChol.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(totalChol) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid Total Cholesterol value")
                return false
            }  else {
                if ( totalChol.toDouble() < cholTotal.minRange || totalChol.toDouble() > cholTotal.maxRange ) {
                    Utilities.toastMessageShort(context, "Total Cholesterol value must be between "
                            + cholTotal.minRange + "-" + cholTotal.maxRange )
                    return false
                } else {
                    isTotalChol = true
                    viewModel.saveHRALabDetails( paramCodeTotalChol , totalChol )
                    selectedOptionList.add(Option(resources.getString(R.string.TOTAL_CHOLESTEROL) + " : $totalChol " + resources.getString(R.string.MG_DL), paramCodeTotalChol,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeTotalChol)
        }

        if ( binding.hdl.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(hdlChol) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid HDL value")
                return false
            }  else {
                if ( hdlChol.toDouble() < hdl.minRange || hdlChol.toDouble() > hdl.maxRange ) {
                    Utilities.toastMessageShort(context, "HDL value must be between "
                            + hdl.minRange + "-" + hdl.maxRange )
                    return false
                } else {
                    isHdl = true
                    viewModel.saveHRALabDetails( paramCodeHdl , hdlChol )
                    selectedOptionList.add(Option(resources.getString(R.string.HDL) + " : $hdlChol " + resources.getString(R.string.MG_DL), paramCodeHdl,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeHdl)
        }

        if ( binding.ldl.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(ldlChol) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid LDL value")
                return false
            }  else {
                if ( ldlChol.toDouble() < ldl.minRange || ldlChol.toDouble() > ldl.maxRange ) {
                    Utilities.toastMessageShort(context, "LDL value must be between "
                            + ldl.minRange + "-" + ldl.maxRange )
                    return false
                } else {
                    isLdl = true
                    viewModel.saveHRALabDetails( paramCodeLdl , ldlChol )
                    selectedOptionList.add(Option(resources.getString(R.string.LDL) + " : $ldlChol " + resources.getString(R.string.MG_DL), paramCodeLdl,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeLdl)
        }

        if ( binding.triglycerides.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(triglycerideChol) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid Triglycerides value")
                return false
            }  else {
                if ( triglycerideChol.toDouble() < triglyceride.minRange || triglycerideChol.toDouble() > triglyceride.maxRange ) {
                    Utilities.toastMessageShort(context, "Triglycerides value must be between "
                            + triglyceride.minRange + "-" + triglyceride.maxRange )
                    return false
                } else {
                    isTry = true
                    viewModel.saveHRALabDetails( paramCodeTry , triglycerideChol )
                    selectedOptionList.add(Option(resources.getString(R.string.TRIGLYCERIDES) + " : $triglycerideChol " + resources.getString(R.string.MG_DL), paramCodeTry,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeTry)
        }

        if ( binding.vldl.visibility == View.VISIBLE  ) {
            if ( Utilities.isNullOrEmpty(vldlChol) ) {
                Utilities.toastMessageShort(context, "Please Enter Valid VLDL value")
                return false
            }  else {
                if ( vldlChol.toDouble() < vldl.minRange || vldlChol.toDouble() > vldl.maxRange ) {
                    Utilities.toastMessageShort(context, "VLDL value must be between "
                            + vldl.minRange + "-" + vldl.maxRange )
                    return false
                } else {
                    isVldl = true
                    viewModel.saveHRALabDetails( paramCodeVldl , vldlChol )
                    selectedOptionList.add(Option(resources.getString(R.string.VLDL) + " : $vldlChol " + resources.getString(R.string.MG_DL), paramCodeVldl,true))
                }
            }
        } else {
            viewModel.clearHRALabValue(paramCodeVldl)
        }
        return true
    }

    private fun showHideFields( prevAnsList : MutableList<Option> ) {

        if ( prevAnsList.any { it.answerCode.equals(paramCodeTotalChol,ignoreCase = true) } ) {
            binding.totalChol.visibility = View.VISIBLE
        } else {
            binding.totalChol.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodeHdl,ignoreCase = true) } ) {
            binding.hdl.visibility = View.VISIBLE
        } else {
            binding.hdl.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodeLdl,ignoreCase = true) } ) {
            binding.ldl.visibility = View.VISIBLE
        } else {
            binding.ldl.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodeTry,ignoreCase = true) } ) {
            binding.triglycerides.visibility = View.VISIBLE
        } else {
            binding.triglycerides.visibility = View.GONE
        }

        if ( prevAnsList.any { it.answerCode.equals(paramCodeVldl,ignoreCase = true) } ) {
            binding.vldl.visibility = View.VISIBLE
        } else {
            binding.vldl.visibility = View.GONE
        }

    }

    private fun enableOrDisableNextBtn() {
        var toEnable = true
        if ( binding.totalChol.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layTotalChol.getValue()) ) {
            toEnable = false
        }
        if ( binding.hdl.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layHdl.getValue()) ) {
            toEnable = false
        }
        if ( binding.ldl.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layLdl.getValue()) ) {
            toEnable = false
        }
        if ( binding.triglycerides.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layTriglycerides.getValue()) ) {
            toEnable = false
        }
        if ( binding.vldl.visibility == View.VISIBLE && Utilities.isNullOrEmptyOrZero(binding.layVldl.getValue()) ) {
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