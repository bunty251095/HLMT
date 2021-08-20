package com.caressa.tools_calculators.ui.SmartPhoneAddiction

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentSmartPhoneInputBinding
import com.caressa.tools_calculators.model.Answer
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.tools_calculators.model.Question
import com.caressa.tools_calculators.model.SmartPhoneAddictionData
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class SmartPhoneInputFragment : BaseFragment() {

    private lateinit var binding : FragmentSmartPhoneInputBinding
    private val viewModel : ToolsCalculatorsViewModel by viewModel()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var score = "1"
    private var currentQuestion = "FIRST"
    private var answerArrayMap: ArrayMap<String, Answer>? = ArrayMap()
    private var question: Question? = null
    private var dataHandler: DataHandler? = null
    private var smartPhoneAddictionData: SmartPhoneAddictionData? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("QuizID,ParticipationID---> $quizID , $participationID")

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_smartPhoneInputFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSmartPhoneInputBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.SMART_PHONE_CALCULATOR_INPUT_SCREEN)
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
        smartPhoneAddictionData = SmartPhoneAddictionData()
        currentQuestion = dataHandler!!.getSmartPhoneAddictionNextQuestion(currentQuestion)
        question = smartPhoneAddictionData!!.getSmartPhoneAddictionData(currentQuestion,requireContext())

        binding.indicatorSeekbar.setIndicatorTextFormat("  \${PROGRESS} / 11 ")
        binding.indicatorSeekbar.setUserSeekAble(false)
        binding.txtQuestion.text = question!!.question

        if (currentQuestion.equals("ADDIC11", ignoreCase = true)) {
            binding.btnBack.text = resources.getString(R.string.PREVIOUS)
            binding.btnBack.visibility = View.VISIBLE
            loadPreviousData()
        }
        viewModel.smartPhoneSaveResp.observe( viewLifecycleOwner , {})
    }

    private fun setClickable() {

        binding.imgNever.setOnClickListener {
            selectNever()
            autoNext()
        }

        binding.imgSometime.setOnClickListener {
            selectSometime()
            autoNext()
        }

        binding.imgOften.setOnClickListener {
            selectOften()
            autoNext()
        }

        binding.imgAlways.setOnClickListener {
            selectAlways()
            autoNext()
        }

        binding.btnBack.setOnClickListener {
            currentQuestion = dataHandler!!.getSmartPhoneAddictionPrevious(currentQuestion)
            binding.indicatorSeekbar.setProgress(binding.indicatorSeekbar.progressFloat - 1)
            binding.btnNext.text = resources.getString(R.string.NEXT)
            if (currentQuestion.equals("FIRST", ignoreCase = true) || currentQuestion.equals("ADDIC1", ignoreCase = true)) {
                binding.btnBack.text = resources.getString(R.string.BACK)
                binding.btnBack.visibility = View.INVISIBLE
            } else {
                binding.btnBack.text = resources.getString(R.string.PREVIOUS)
                binding.btnBack.visibility = View.VISIBLE
                question = smartPhoneAddictionData!!.getSmartPhoneAddictionData(currentQuestion,requireContext())
                loadPreviousData()
            }
        }

        binding.btnNext.setOnClickListener {
            if (validateData()) {
                updateArrayMap()
                binding.btnBack.visibility = View.VISIBLE
                binding.btnBack.text = resources.getString(R.string.PREVIOUS)
                currentQuestion = dataHandler!!.getSmartPhoneAddictionNextQuestion(currentQuestion)
                binding.indicatorSeekbar.setProgress(binding.indicatorSeekbar.progressFloat + 1)
                if (binding.btnNext.text.toString().trim { it <= ' ' }.equals("finish", ignoreCase = true)) {
                    val value = calculateScore()
                    viewModel.callSmartPhoneSaveResponseApi(value,participationID,quizID)
                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.SPA_CALCULATE_CLICK)
                } else {
                    question = smartPhoneAddictionData!!.getSmartPhoneAddictionData(currentQuestion,requireContext())
                    loadPreviousData()
                }
                if (currentQuestion.equals("ADDIC11", ignoreCase = true)) {
                    binding.btnNext.text = resources.getString(R.string.FINISH)
                }
            }
        }

    }

    private fun setSelection(imageView: ImageView, color: Int, tag: String, image: Drawable) {
        if (tag.equals("1", ignoreCase = true)) {
            val padding = imageView.paddingLeft
            imageView.background = ContextCompat.getDrawable(requireContext(),R.drawable.round_shape_bg)
            imageView.setImageDrawable(image)
            ViewCompat.setBackgroundTintList(imageView, ContextCompat.getColorStateList(requireContext(), color))
            ViewCompat.setElevation(imageView, 5f)
            imageView.setPadding(padding, padding, padding, padding)
        } else {
            imageView.background = null
            imageView.setImageDrawable(image)
            ViewCompat.setBackgroundTintList(imageView, null)
            ViewCompat.setElevation(imageView, 0f)
        }
    }

    private fun setTag(id: Int, isDefault: Boolean) {
        if (isDefault) {
            binding.imgAlways.tag = "0"
            binding.imgSometime.tag = "0"
            binding.imgOften.tag = "0"
            binding.imgNever.tag = "0"
            return
        }

        when(id) {
            R.id.img_always -> {
                binding.imgAlways.tag = "1"
                binding.imgSometime.tag = "0"
                binding.imgOften.tag = "0"
                binding.imgNever.tag = "0"
            }

            R.id.img_sometime -> {
                binding.imgAlways.tag = "0"
                binding.imgSometime.tag = "1"
                binding.imgOften.tag = "0"
                binding.imgNever.tag = "0"
            }
            R.id.img_often -> {
                binding.imgAlways.tag = "0"
                binding.imgSometime.tag = "0"
                binding.imgOften.tag = "1"
                binding.imgNever.tag = "0"
            }

            R.id.img_never -> {
                binding.imgAlways.tag = "0"
                binding.imgSometime.tag = "0"
                binding.imgOften.tag = "0"
                binding.imgNever.tag = "1"
            }
        }

    }

    private fun selectAlways() {
        setTag(binding.imgAlways.id, false)
        score = "5"
        setSelection(binding.imgNever, R.color.vivant_nasty_green, binding.imgNever.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_never)!!)
        setSelection(binding.imgSometime, R.color.vivant_marigold, binding.imgSometime.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_sometimes)!!)
        setSelection(binding.imgOften, R.color.vivant_dark_sky_blue, binding.imgOften.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_often)!!)
        setSelection(binding.imgAlways, R.color.vivant_watermelon, binding.imgAlways.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_always_hover)!!)
    }

    private fun selectSometime() {
        setTag(binding.imgSometime.id, false)
        score = "3"
        setSelection(binding.imgNever, R.color.vivant_nasty_green, binding.imgNever.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_never)!!)
        setSelection(binding.imgSometime, R.color.vivant_marigold, binding.imgSometime.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_sometimes_hover)!!)
        setSelection(binding.imgOften, R.color.vivant_dark_sky_blue, binding.imgOften.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_often)!!)
        setSelection(binding.imgAlways, R.color.vivant_watermelon, binding.imgAlways.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_always)!!)

    }

    private fun selectOften() {
        setTag(binding.imgOften.id, false)
        score = "4"
        setSelection(binding.imgNever, R.color.vivant_nasty_green, binding.imgNever.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_never)!!)
        setSelection(binding.imgSometime, R.color.vivant_marigold, binding.imgSometime.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_sometimes)!!)
        setSelection(binding.imgOften, R.color.vivant_dark_sky_blue, binding.imgOften.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_often_hover)!!)
        setSelection(binding.imgAlways, R.color.vivant_watermelon, binding.imgAlways.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_always)!!)

    }

    private fun selectNever() {
        setTag(binding.imgNever.id, false)
        score = "1"
        setSelection(binding.imgNever, R.color.vivant_nasty_green, binding.imgNever.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_never_hover)!!)
        setSelection(binding.imgSometime, R.color.vivant_marigold, binding.imgSometime.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_sometimes)!!)
        setSelection(binding.imgOften, R.color.vivant_dark_sky_blue, binding.imgOften.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_often)!!)
        setSelection(binding.imgAlways, R.color.vivant_watermelon, binding.imgAlways.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_always)!!)

    }

    private fun autoNext() {
        if (!currentQuestion.equals("ADDIC11", ignoreCase = true)) {
            val handler = Handler()
            handler.postDelayed({ //Do something after 100ms
                binding.btnNext.performClick()
            }, 300)
        }
    }

    private fun validateData(): Boolean {
        return if (binding.imgAlways.tag.toString().equals("0", ignoreCase = true)
            && binding.imgNever.tag.toString().equals("0", ignoreCase = true)
            && binding.imgOften.tag.toString().equals("0", ignoreCase = true)
            && binding.imgSometime.tag.toString().equals("0", ignoreCase = true)) {
            Utilities.toastMessageShort(context,"Please Select Option")
            false
        } else {
            true
        }
    }

    private fun loadPreviousData() {
        binding.txtQuestion.text = question!!.question
        if (answerArrayMap!!.containsKey(question!!.getqCode())) {
            when (answerArrayMap!![question!!.getqCode()]!!.value) {
                "1" -> selectNever()
                "3" -> selectSometime()
                "4" -> selectOften()
                "5" -> selectAlways()
            }
        } else {
            updateQuestion()
        }
    }

    private fun calculateScore(): String {
        var totalScore = 0
        try {
            for (key in answerArrayMap!!.keys) {
                totalScore += answerArrayMap!![key]!!.value.toInt()
                Timber.i("DATA :: " + key + "/" + answerArrayMap!![key]!!.value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Timber.i("Total Score :: $totalScore")
        return totalScore.toString()
    }

    private fun updateArrayMap() {
        val answer = Answer()
        answer.questionCode = question!!.getqCode()
        answer.answerCode = question!!.getqCode()
        answer.value = score
        Timber.i("UPDATE:: " + question!!.getqCode() + " :: " + score)
        answerArrayMap!![question!!.getqCode()] = answer
    }

    private fun updateQuestion() {
        binding.txtQuestion.text = question!!.question
        // Default Selection
        setTag(binding.txtQuestion.id, true)
        setSelection(binding.imgNever, R.color.vivant_nasty_green, binding.imgNever.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_never)!!)
        setSelection(binding.imgSometime, R.color.vivant_marigold, binding.imgSometime.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_sometimes)!!)
        setSelection(binding.imgOften, R.color.vivant_dark_sky_blue, binding.imgOften.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_often)!!)
        setSelection(binding.imgAlways, R.color.vivant_watermelon, binding.imgAlways.tag.toString(),
            ContextCompat.getDrawable(requireContext(),R.drawable.img_always)!!)
    }


}
