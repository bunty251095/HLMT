package com.caressa.hra.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.ViewUtils
import com.caressa.hra.common.HraHelper
import com.caressa.hra.common.HraDataSingleton
import com.caressa.hra.databinding.FragmentHraQuesSingleSelectionBinding
import com.caressa.hra.viewmodel.HraViewModel
import com.caressa.model.hra.Option
import com.caressa.model.hra.Question
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class HraQuesSingleSelectionFragment(val qCode: String) : BaseFragment() {

    private lateinit var binding : FragmentHraQuesSingleSelectionBinding
    private val viewModel: HraViewModel by viewModel()

    private var questionData = Question()
    private var viewPagerActivity : HraQuestionsActivity?  = null
    private var prevAnsList : MutableList<Option> = mutableListOf()
    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHraQuesSingleSelectionBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.singleSelectionFragment = this
        binding.qCode = qCode
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
    }

    private fun setPreviousAnsData() {
        binding.rgPrevious.removeAllViews()
        prevAnsList = hraDataSingleton.getPrevAnsList(viewPagerActivity!!.getCurrentScreen()-1)
        Timber.e("prevAnsList---> $prevAnsList")
        if ( prevAnsList.isNotEmpty() ) {
            viewModel.setPreviousAnswersList( prevAnsList )
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun loadData() {
        var toProceed = true
        viewModel.quesData.observe(viewLifecycleOwner) {
            if (it != null) {
                if (toProceed) {
                    Timber.i("OptionList-----> " + it.optionList)
                    questionData = it
                    hraDataSingleton.question = it
                    HraHelper.addRadioButtonsSingleSelection(it.optionList, binding.rgSelection, listener, requireContext())
                    loadPreviousSelectedData()
                    toProceed = false
                }
            }
        }
    }

    private fun loadPreviousSelectedData() {
        viewModel.getResponse(qCode)
    }

    private fun setClickable() {
        binding.rgPrevious.setOnCheckedChangeListener { _, _ ->
            viewPagerActivity!!.setCurrentScreen( viewPagerActivity!!.getCurrentScreen() -1 )
        }
    }

    var listener = View.OnClickListener { view ->
/*        val rb = view as RadioButton
        if ( rb.isChecked ) {
            rb.setButtonDrawable(R.drawable.ic_circle_check)
        } else {
            rb.setButtonDrawable(null)
        }*/
        val ansCode = ViewUtils.getRadioSelectedValueTag(binding.rgSelection)
        val ansDesc = ViewUtils.getRadioButtonSelectedValue(binding.rgSelection)
        //val ansDescEng = resources.getString(optionList.find { it.answerCode == ansCode }!!.description)
        Timber.e("AnswerCode----->$ansCode")
        Timber.e("AnswerDesc----->$ansDesc")
        //Timber.e("AnsDescEng----->$ansDescEng")
        viewModel.saveResponse(qCode, ansCode, ansDesc, questionData.category, questionData.tabName, "")
        saveResponseForNextScreen(ansCode, ansDesc)

        when (qCode) {
            "PHYSTRES" -> {
                if ( ansCode.equals("6_PHYSTRNO",ignoreCase = true) ) {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 2)
                } else {
                    viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
                }
            }
            else -> {
                viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
            }
        }
    }

    private fun saveResponseForNextScreen(ansCode: String, ansDesc: String) {
        val selectedOptionList: MutableList<Option> = mutableListOf()
        selectedOptionList.add(Option(ansDesc, ansCode, true))
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] = selectedOptionList
    }

/*    private fun saveQuestionResponse() {
        val ansCode = ViewUtils.getRadioSelectedValueTag(binding.rgSelection)
        val ansDesc = ViewUtils.getRadioButtonSelectedValue(binding.rgSelection)
        Timber.e("AnswerCode , AnswerDesc----->$ansCode , $ansDesc")
        viewModel.saveResponse(qCode,ansCode,ansDesc,questionData.category,questionData.tabName, "")
        saveResponseForNextScreen(ansCode,ansDesc)
    }

    private fun saveResponseForNextScreen(ansCode:String,ansDesc:String) {
        val selectedOptionList: MutableList<Option> = mutableListOf()
        selectedOptionList.add( Option(ansDesc,ansCode,true) )
        hraDataSingleton.previousAnsList[viewPagerActivity!!.getCurrentScreen()] = selectedOptionList
    }

    var listener = View.OnClickListener {
        saveQuestionResponse()
        viewPagerActivity!!.setCurrentScreen(viewPagerActivity!!.getCurrentScreen() + 1)
    }*/

}