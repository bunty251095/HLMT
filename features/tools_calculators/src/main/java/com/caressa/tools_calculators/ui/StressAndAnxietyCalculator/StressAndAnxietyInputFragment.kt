package com.caressa.tools_calculators.ui.StressAndAnxietyCalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentStressAndAnxietyInputBinding
import com.caressa.tools_calculators.model.Answer
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.model.Question
import com.caressa.tools_calculators.model.StressAndAnxietyData
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class StressAndAnxietyInputFragment : BaseFragment() {

    private lateinit var binding : FragmentStressAndAnxietyInputBinding
    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var currentQuestion = "FIRST"
    private var isPrevious = false
    private var question : Question? = null
    private var dataHandler: DataHandler? = null
    private var stressAndAnxietyData: StressAndAnxietyData? = null
    private var answerArrayMap: ArrayMap<String, Answer>? = ArrayMap()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_stressAndAnxietyInputFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStressAndAnxietyInputBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.STRESS_ANXIETY_CALCULATOR_INPUT_SCREEN)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Timber.e("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        dataHandler = DataHandler(requireContext())
        dataHandler!!.clearSequenceList()
        stressAndAnxietyData = StressAndAnxietyData()
        currentQuestion = dataHandler!!.getStressNextQuestion(currentQuestion)
        question = stressAndAnxietyData!!.getStressAssessmentData(currentQuestion,requireContext())

        binding.indicatorSeekbar.setIndicatorTextFormat("  \${PROGRESS} / 21 ")
        binding.indicatorSeekbar.setUserSeekAble(false)
        binding.txtQuestion.text = question!!.question

        viewModel.stressAndAnxietySaveResp.observe( viewLifecycleOwner , {})
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.rgOptions.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            true
        }
        binding.rbAlways.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }
        binding.rbNever.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }
        binding.rbOften.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }
        binding.rbSometimes.setOnTouchListener { _: View?, _: MotionEvent? ->
            isPrevious = false
            false
        }

        binding.rgOptions.setOnCheckedChangeListener { _: RadioGroup?, _: Int ->
            if (!isPrevious) {
                if (!binding.btnNext.text.toString().trim { it <= ' ' }
                        .equals("finish", ignoreCase = true)) {
                    val handler = Handler()
                    handler.postDelayed({ //Do something after 100ms
                        binding.btnNext.performClick()
                        isPrevious = false
                    }, 300)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            currentQuestion = dataHandler!!.getStressPreviousQuestion(currentQuestion)
            binding.indicatorSeekbar.setProgress(binding.indicatorSeekbar.progressFloat - 1)
            binding.btnNext.text = resources.getString(R.string.NEXT)
            if (currentQuestion.equals("FIRST", ignoreCase = true)) {
                binding.btnBack.visibility = View.INVISIBLE
                //RoutingClass.routeToActivity(getApplicationContext(), GlobalConstants.TOOLS_AND_TRACKER, null, true)
            } else {
                question = stressAndAnxietyData!!.getStressAssessmentData(currentQuestion,requireContext())
                loadPreviousData()
                if (currentQuestion.equals("DASS-21_D_LIFEMEANINGLESS", ignoreCase = true)) {
                    binding.btnBack.visibility = View.INVISIBLE
                }
            }
        }

        binding.btnNext.setOnClickListener {
            if (validate()) {
                binding.btnBack.text = resources.getString(R.string.PREVIOUS)
                binding.btnBack.visibility = View.VISIBLE
                if (binding.btnNext.text.toString().trim { it <= ' ' }.equals("finish", ignoreCase = true)) {
                    // Make Api call.
                    saveDetails()
                    Timber.i("AnswerList=> %s", getAnswerList())
                    viewModel.callStressAndAnxietySaveResponseApi(participationID,quizID.toInt(),getAnswerList())
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.DAS_CALCULATE_CLICK)
                } else {
                    saveDetails()
                    currentQuestion = dataHandler!!.getStressNextQuestion(currentQuestion)
                    binding.indicatorSeekbar.setProgress(binding.indicatorSeekbar.progressFloat + 1)
                    question = stressAndAnxietyData!!.getStressAssessmentData(currentQuestion,requireContext())
                    loadPreviousData()
                }
                if (question!!.isLast) {
                    binding.btnNext.text = resources.getString(R.string.FINISH)
                }
            }
        }
    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap!!) {
            answers.add(value)
        }
        //Timber.i("AnswerList=>" +answers)
        return answers
    }

    private fun validate(): Boolean {
        val isValid = false
        if (binding.rgOptions.checkedRadioButtonId != -1) {
            return true
        } else {
            Utilities.toastMessageShort(context,resources.getString(R.string.PLEASE_FILL_DETAILS))
        }
        return isValid
    }

    private fun saveDetails() {
        // get selected radio button from radioGroup
        val selectedId: Int = binding.rgOptions.checkedRadioButtonId
        // find the radiobutton by returned id
        val radioButton = binding.rgOptions.findViewById(selectedId) as RadioButton?
        if (radioButton != null) {
            val answer = Answer()
            answer.questionCode = question!!.getqCode()
            answer.answerCode = radioButton.tag as String
            answer.value = getScore(radioButton.tag as String)
            //Timber.i("UPDATE:: " + question!!.getqCode() + " :: " + answer)
            answerArrayMap!![question!!.getqCode()] = answer
        }
    }

    private fun getScore(tag: String): String {
        var score = "0"
        when (tag) {
            "NEVER" -> score = "0"
            "SOMETIMES" -> score = "1"
            "OFTEN" -> score = "2"
            "ALMOSTALWAYS" -> score = "3"
        }
        return score
    }

    private fun loadPreviousData() {
        isPrevious = true
        binding.txtQuestion.text = question!!.question
        if (answerArrayMap!!.containsKey(question!!.getqCode())) {
            isPrevious = true

            when( answerArrayMap!![question!!.getqCode()]!!.answerCode ) {

                binding.rbNever.tag.toString() -> binding.rbNever.isChecked = true

                binding.rbOften.tag.toString() -> binding.rbOften.isChecked = true

                binding.rbSometimes.tag.toString() -> binding.rbSometimes.isChecked = true

                binding.rbAlways.tag.toString() -> binding.rbAlways.isChecked = true

                else -> binding.rgOptions.clearCheck()
            }

        } else {
            binding.rgOptions.clearCheck()
        }
    }

}
