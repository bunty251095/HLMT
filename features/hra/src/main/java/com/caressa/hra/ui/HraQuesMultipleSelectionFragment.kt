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
import com.caressa.common.utils.Utilities
import com.caressa.hra.R
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.common.HraHelper
import com.caressa.hra.common.Validations
import com.caressa.hra.databinding.FragmentHraQuesMultipleSelectionBinding
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import com.caressa.hra.viewmodel.HraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HraQuesMultipleSelectionFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding : FragmentHraQuesMultipleSelectionBinding
    private val viewModel: HraViewModel by viewModel()

    private var questionData = Question()
    private var viewPagerActivity : HraQuestionsActivity?  = null
    private var prevAnsList : MutableList<Option> = mutableListOf()
    private val selectedOptionList: MutableList<Option> = mutableListOf()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesMultipleSelectionBinding.inflate(inflater, container, false)
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
        Timber.e("CurrentPageNo--->" + viewPagerActivity!!.getCurrentScreen())
        viewModel.getHRAQuestionData(qCode)
        selectedOptionList.clear()
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 1)
        if ( prevAnsList.isEmpty() ) {
            prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen() - 2)
        }
        Timber.e("prevAnsList---> $prevAnsList")
        viewModel.setPreviousAnswersList(prevAnsList)
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
                    loadPreviousSelectedData()
                    toProceed = false
                }
            }
        })
    }

    private fun loadPreviousSelectedData() {
        when(qCode) {
            "KNWDIANUM","KNWLIPNUM" -> {
                val list = hraDataSingleton.getPrevAnsList( viewPagerActivity!!.getCurrentScreen() )
                if ( list.isNotEmpty() ) {
                    viewModel.getSelectedOptionListForLabQues(list,qCode)
                }
            }
            "EDS" -> {
                viewModel.getEdsSelectedOptionList(qCode)
            }
            else -> viewModel.getSelectedOptionListWithQuestionCode(qCode)
        }
    }

    private fun setClickable() {

        binding.btnNext.setOnClickListener {
            if ( Validations.validateMultiSelectionOptions(binding.optionContainer) ) {
                nextButtonClick()
            } else {
                Utilities.toastMessageShort(context, "Please Select Option")
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
            //SUGAR
            "KNWDIANUM" -> {
                Timber.i("Inside---> $qCode")
                if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                    viewModel.saveResponse(qCode, "85_NO", resources.getString(R.string.NO),questionData.category,questionData.tabName,"")
                    viewModel.clearHRALabValuesBasedOnType("SUGAR")
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }

            //LIPID
            "KNWLIPNUM" -> {
                Timber.i("Inside---> $qCode")
                if (selectedOptions.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                    viewModel.saveResponse(qCode, "84_NO", resources.getString(R.string.NO),questionData.category,questionData.tabName,"")
                    viewModel.clearHRALabValuesBasedOnType("LIPID")
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }

            "EDS" -> {
                Timber.i("Inside---> $qCode")
                viewModel.saveResponseEDS(qCode,questionData.tabName,questionData.category,selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }

            else -> {
                Timber.i("Inside---> $qCode")
                saveMultipleResponseInDb(selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
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

            //SUGAR
            "KNWDIANUM" -> {
                if (selectedOptions.isNotEmpty() &&
                    selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) }) {
                    viewModel.saveResponse(qCode, "85_NO", resources.getString(R.string.NO),questionData.category,questionData.tabName, "")
                    viewModel.clearHRALabValuesBasedOnType("SUGAR")
                }
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }

            //LIPID
            "KNWLIPNUM" -> {
                if ( selectedOptions.isNotEmpty() &&
                    selectedOptions.any { it.description.equals(resources.getString(R.string.NONE),ignoreCase = true) }) {
                    viewModel.saveResponse(qCode, "84_NO", resources.getString(R.string.NO), questionData.category, questionData.tabName, "")
                    viewModel.clearHRALabValuesBasedOnType("LIPID")
                }
                if (prevAnsList.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                    viewModel.saveResponse(qCode, "84_NO", resources.getString(R.string.NO), questionData.category, questionData.tabName, "")
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            }

            "HHILL" -> {
                saveMultipleResponseInDb(selectedOptions)
                if (prevAnsList.any { it.description.equals(resources.getString(R.string.NONE), ignoreCase = true) }) {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
                }
            }

            "EDS" -> {
                if ( selectedOptions.isNotEmpty() ) {
                    viewModel.saveResponseEDS(qCode,questionData.tabName,questionData.category,selectedOptions)
                }
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }

            else -> {
                saveMultipleResponseInDb(selectedOptions)
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() - 1)
            }

        }
/*            } else {
                Utilities.toastMessageShort(context, "Please Select Option")
            }*/
    }

    private fun refreshSelectedOptionList() {
        hraDataSingleton.selectedOptionList.clear()
        hraDataSingleton.selectedOptionList.addAll(selectedOptionList)
    }

    private fun saveResponseForNextScreen(selectedOptions: MutableList<Option>) {
        if ( selectedOptions.isNotEmpty() ) {
            hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] = selectedOptions
        }
    }

    private fun saveMultipleResponseInDb(selectedOptions: MutableList<Option>) {
        if ( selectedOptions.isNotEmpty() ) {
            viewModel.saveMultipleOptionResponses(qCode,selectedOptions, questionData.category, questionData.tabName, "")
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
        nextButtonClick()
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