package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.common.HraHelper
import com.caressa.hra.common.Validations
import com.caressa.hra.databinding.FragmentHraQuesEdsCheckupBinding
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.ArrayList

class HraQuesExposeCheckupFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding : FragmentHraQuesEdsCheckupBinding
    private val viewModel: HraViewModel by viewModel()

    private var questionData = Question()
    private var viewPagerActivity : HraQuestionsActivity?  = null
    private var prevAnsList : MutableList<Option> = mutableListOf()
    private val selectedOptionList: MutableList<Option> = mutableListOf()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesEdsCheckupBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        if( userVisibleHint ) {
            try {
                initialise()
                setPreviousAnsData()
                loadData()
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
                loadData()
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
        selectedOptionList.clear()
        if ( qCode.equals("CHECKUP", ignoreCase = true) ) {
            binding.btnNext.text = resources.getString(R.string.THATS_ALL)
        }
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
        if ( prevAnsList.isEmpty() ) {
            prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 2)
        }
        Timber.e("prevAnsList---> $prevAnsList")
        viewModel.setPreviousAnswersList( prevAnsList )
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun loadData() {
        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner, {
            if ( it != null ) {
                if ( toProceed ) {
                    Timber.i("OptionList-----> "+it.optionList)
                    questionData = it
                    hraDataSingleton.question = it
                    selectedOptionList.addAll(it.optionList)
                    HraHelper.addButtonsMultiSelection(it.optionList,binding.optionContainer,checkChangeListener,onClickListener,requireContext())
                    loadPreviousData(it.optionList)
                    toProceed = false
                }
            }
        })

        viewModel.submitHra.observe(viewLifecycleOwner , {
            if ( it != null ) {
                val wellnessScore = it.WellnessScoreSummary.WellnessScore
                if ( !Utilities.isNullOrEmptyOrZero( wellnessScore ) ) {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }
        })
    }

    private fun loadPreviousData( optionList : ArrayList<Option>) {
        viewModel.getSelectedOptionListWithCode(qCode,optionList)
    }

    private fun setClickable() {

        binding.btnNext.setOnClickListener {
            if ( Validations.validateMultiSelectionOptions(binding.optionContainer) ) {
                nextButtonClick()
            } else {
                Utilities.toastMessageShort(context,resources.getString(R.string.PLEASE_SELECT_OPTION))
            }
        }

        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
            previousRadioBtnOptionClick()
        }

    }

    private fun nextButtonClick() {
        val selectedOptions =  selectedOptionList.filter { it.isSelected }.toMutableList()
        Timber.i("Saved_Options-----> $selectedOptions")
        saveResponseForNextScreen(selectedOptions)

        when(qCode)  {
            "EXPOSE" -> {
                saveResponse(selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }

            "CHECKUP" -> {
                saveResponse(selectedOptions)
                viewModel.saveAndSubmitHRA(viewPagerActivity!!.personId,viewPagerActivity!!.hraTemplateId)
            }
        }
        hraDataSingleton.selectedOptionList.clear()
    }

    private fun previousRadioBtnOptionClick() {
        //if ( validate() ) {
        val selectedOptions =  selectedOptionList.filter { it.isSelected }.toMutableList()
        Timber.i("Saved_Options-----> $selectedOptions")
        saveResponseForNextScreen(selectedOptions)

        when(qCode)  {
            "EXPOSE","CHECKUP" -> {
                if ( selectedOptions.isNullOrEmpty() ) {
                    val totalList = questionData.optionList.filter { it.answerCode.contains(",",ignoreCase = true) }
                    if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
                        for ( option in totalList ) {
                            val data = option.answerCode.split(",")
                            viewModel.saveResponseOther(data[0],data[2],option.description,questionData.category,questionData.tabName,"",qCode,Constants.FALSE)
                        }
                    } else {
                        val list = selectedOptionList.filter { !it.description.equals("None",ignoreCase = true) }.toMutableList()
                        if ( list.isNullOrEmpty() ) {
                            for ( option in list ) {
                                val data = option.answerCode.split(",")
                                if ( option.isSelected ) {
                                    viewModel.saveResponseOther(data[0],data[1],option.description,questionData.category,questionData.tabName,"",qCode,Constants.TRUE)
                                } else {
                                    viewModel.saveResponseOther(data[0],data[2],option.description,questionData.category,questionData.tabName,"",qCode,Constants.FALSE)
                                }
                            }
                        }
                    }
                }
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }
        }
/*            } else {
                Utilities.toastMessageShort(context, "Please Select Option")
            }*/
    }

    private fun saveResponse( selectedOptions : MutableList<Option> ) {
        val totalList = questionData.optionList.filter { it.answerCode.contains(",",ignoreCase = true) }
        if ( selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) } ) {
            for ( option in totalList ) {
                val data = option.answerCode.split(",")
                viewModel.saveResponseOther(data[0],data[2],option.description,questionData.category,questionData.tabName,"",qCode, Constants.FALSE)
            }
        } else {
            val list = selectedOptionList.filter { !it.description.equals("None",ignoreCase = true) }.toMutableList()
            for ( option in list ) {
                val data = option.answerCode.split(",")
                if ( option.isSelected ) {
                    viewModel.saveResponseOther(data[0],data[1],option.description,questionData.category,questionData.tabName,"",qCode,Constants.TRUE)
                } else {
                    viewModel.saveResponseOther(data[0],data[2],option.description,questionData.category,questionData.tabName,"",qCode,Constants.FALSE)
                }
            }
        }
    }

    private fun refreshSelectedOptionList() {
        hraDataSingleton.selectedOptionList.clear()
        hraDataSingleton.selectedOptionList.addAll(selectedOptionList)
    }

    private fun saveResponseForNextScreen(selectedOptions: MutableList<Option> ) {
        if ( selectedOptions.isNotEmpty() ) {
            hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] = selectedOptions
        }
    }

    private var checkChangeListener = CompoundButton.OnCheckedChangeListener { view, isChecked ->
        selectedOptionList[view.id].isSelected = isChecked
        refreshSelectedOptionList()
        if ( isChecked && !view.text.toString().equals("None", ignoreCase = true) ) {
            val none = binding.optionContainer.getChildAt(0) as CheckBox
            if ( none.isChecked ) {
                none.isChecked = false
            }
        }
    }

    // This listener is only for "None" Option to Navigate directly on Next Screen
    private var onClickListener = View.OnClickListener {
        val view = it as CheckBox
        selectedOptionList[view.id].isSelected = view.isChecked
        refreshSelectedOptionList()
        if ( selectedOptionList.any { option -> !option.description.equals("None",ignoreCase = true) && option.isSelected } ) {
            HraHelper.deselectExceptNone(binding.optionContainer)
        }
        selectedOptionList[0].isSelected = true
        if ( !qCode.equals("CHECKUP", ignoreCase = true) ) {
            nextButtonClick()
        }
    }

/*    private var listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        selectedOptionList[buttonView.id].isSelected = isChecked
        refreshSelectedOptionList()
        if ( isChecked && buttonView.text.toString().equals("None", ignoreCase = true)  ) {
            if ( selectedOptionList.any { option -> !option.description.equals("None",ignoreCase = true) && option.isSelected } ) {
                HraHelper.deselectExceptNone(binding.optionContainer)
            }
            //nextButtonClick()
        }
        if ( isChecked && !buttonView.text.toString().equals("None", ignoreCase = true) ) {
            val none = binding.optionContainer.getChildAt(0) as CheckBox
            if ( none.isChecked ) {
                none.isChecked = false
            }
        }
    }*/

}