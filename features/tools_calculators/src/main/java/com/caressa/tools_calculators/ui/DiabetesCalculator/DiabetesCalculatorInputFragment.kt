package com.caressa.tools_calculators.ui.DiabetesCalculator

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.ParameterDataModel
import com.caressa.common.utils.Utilities
import com.caressa.common.view.SpinnerAdapter
import com.caressa.common.view.SpinnerModel
import com.caressa.model.toolscalculators.UserInfoModel
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.ParameterAdapter
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentDiabetesCalculatorInputBinding
import com.caressa.tools_calculators.model.*
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.caressa.tools_calculators.views.WaistDialogManager
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

class DiabetesCalculatorInputFragment : BaseFragment(),KoinComponent,ParameterAdapter.ParameterOnClickListener,
    WaistDialogManager.OnDialogValueListener {

    private lateinit var binding: FragmentDiabetesCalculatorInputBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()
    private val dataHandler : DataHandler = get()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var genderAdapter : SpinnerAdapter? = null
    private var modelAdapter : SpinnerAdapter? = null
    private var parameterAdapter: ParameterAdapter? = null
    private var genderList = ArrayList<SpinnerModel>()
    private var ageGroupList = ArrayList<SpinnerModel>()
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var userPreferenceModel = UserInfoModel()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_diabetesCalculatorFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiabetesCalculatorInputBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        FirebaseHelper.logScreenEvent(FirebaseConstants.DIABETES_CALCULATOR_INPUT_SCREEN)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Timber.e("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
            loadUserData()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        userPreferenceModel = UserInfoModel.getInstance()!!

        ageGroupList = dataHandler.getAgeGroupList()
        modelAdapter = SpinnerAdapter(requireContext(), ageGroupList)
        binding.spAgeGroup.adapter = modelAdapter

        genderList = dataHandler.getGenderList()
        genderAdapter = SpinnerAdapter(requireContext(), genderList)
        binding.spinnerGender.adapter = genderAdapter

        paramList = dataHandler.getDiabetesParameterList()
        parameterAdapter = ParameterAdapter(paramList,this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.paramRecyclerView.adapter = parameterAdapter

        binding.spAgeGroup.tag = "AGEGROUP"
        binding.spinnerGender.tag = "GENDER"
        binding.rgOrigin.tag = "ORIGIN"
        binding.rgDiabetes.tag = "FAMILYHEALTH"
        binding.rgBloodSugar.tag = "BLOODGLUCOSE"
        binding.rgBpMedication.tag = "HIGHBLOODPRESSUREMEDICATION"
        binding.rgSmoke.tag = "SMOKINGHABITS"
        binding.rgFruits.tag = "FRUITHABITS"
        binding.rgExercise.tag = "PHYSICALEXERCISE"

        viewModel.diabetesSaveResp.observe( viewLifecycleOwner , {})
    }

    private fun loadUserData() {
        if (!Utilities.isNullOrEmpty(UserInfoModel.getInstance()!!.getAge())) {
            val age = UserInfoModel.getInstance()!!.getAge().toDouble()
            when {
                age < 35 -> {
                    binding.spAgeGroup.setSelection(0)
                }
                age < 45 -> {
                    binding.spAgeGroup.setSelection(1)
                }
                age < 55 -> {
                    binding.spAgeGroup.setSelection(2)
                }
                age < 65 -> {
                    binding.spAgeGroup.setSelection(3)
                }
                else -> {
                    binding.spAgeGroup.setSelection(4)
                }
            }
        }
        if (UserInfoModel.getInstance()!!.isMale) {
            binding.spinnerGender.setSelection(0)
        } else {
            binding.spinnerGender.setSelection(1)
        }
    }

    private fun setClickable() {

        binding.spinnerGender.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.i("Selected Item:: $position")
                binding.txtGender.text = genderList[position].name
                for (i in genderList.indices) {
                    genderList[i].selection = i == position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spAgeGroup.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.i("Selected Item:: $position")
                binding.txtAgeGroup.text = ageGroupList[position].name
                for (i in ageGroupList.indices) {
                    ageGroupList[i].selection = i == position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtGender.setOnClickListener {
            binding.spinnerGender.performClick()
        }

        binding.txtAgeGroup.setOnClickListener {
            binding.spAgeGroup.performClick()
        }

        binding.btnCalculateDiabetes.setOnClickListener {
            if (validate()) {
                prepareAnswerArray()
                viewModel.callDiabetesSaveResponseApi(participationID, quizID, getAnswerList())
                FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.DIABETES_CALCULATE_CLICK)
            }
        }

    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap) {
            answers.add(value)
        }
        return answers
    }

    private fun validate(): Boolean {
        var isValid = false
        try {
            if (paramList[0].finalValue.isNotEmpty()) {
                val `val`: Double = (paramList[0].finalValue.toDouble() / 2.54).roundToInt().toDouble()
                Timber.e("ValidateInchValue=>$`val`")
                if ( `val` >= paramList[0].minRange && `val` <= paramList[0].maxRange ) {
                    isValid = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!isValid) {
            Utilities.toastMessageShort(context,resources.getString(R.string.PLEASE_ENTER_WAIST_SIZE_BETWEEN)
                    + " " + paramList[0].minRange + " " + resources.getString(R.string.TO) + " " + paramList[0].maxRange + resources.getString(R.string.INCH))

        }
        return isValid
    }

    private fun prepareAnswerArray() {
        try {
            var answer: Answer
            val dibData = DiabetesCalculatorData()

            var option = dibData.getDiabetesCodeData(binding.spAgeGroup.tag.toString())[binding.spAgeGroup.selectedItemPosition]
            answer = Answer(binding.spAgeGroup.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            option = dibData.getDiabetesCodeData(binding.spinnerGender.tag.toString())[binding.spinnerGender.selectedItemPosition]
            answer = Answer(binding.spinnerGender.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            var selectedId: Int = binding.rgOrigin.checkedRadioButtonId
            var radioButton = binding.rgOrigin.findViewById(selectedId) as RadioButton
            var position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgOrigin.tag.toString())[position]
            answer = Answer(binding.rgOrigin.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            selectedId = binding.rgDiabetes.checkedRadioButtonId
            radioButton = binding.rgDiabetes.findViewById(selectedId) as RadioButton
            position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgDiabetes.tag.toString())[position]
            answer = Answer(binding.rgDiabetes.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            selectedId = binding.rgBloodSugar.checkedRadioButtonId
            radioButton = binding.rgBloodSugar.findViewById(selectedId) as RadioButton
            position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgBloodSugar.tag.toString())[position]
            answer = Answer(binding.rgBloodSugar.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            selectedId = binding.rgBpMedication.checkedRadioButtonId
            radioButton = binding.rgBpMedication.findViewById(selectedId) as RadioButton
            position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgBpMedication.tag.toString())[position]
            answer = Answer(binding.rgBpMedication.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            selectedId = binding.rgSmoke.checkedRadioButtonId
            radioButton = binding.rgSmoke.findViewById(selectedId) as RadioButton
            position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgSmoke.tag.toString())[position]
            answer = Answer(binding.rgSmoke.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            selectedId = binding.rgFruits.checkedRadioButtonId
            radioButton = binding.rgFruits.findViewById(selectedId) as RadioButton
            position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgFruits.tag.toString())[position]
            answer = Answer(binding.rgFruits.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            selectedId = binding.rgExercise.checkedRadioButtonId
            radioButton = binding.rgExercise.findViewById(selectedId) as RadioButton
            position = if (radioButton.text.toString().equals(resources.getString(R.string.YES), ignoreCase = true)) 0 else 1
            option = dibData.getDiabetesCodeData(binding.rgExercise.tag.toString())[position]
            answer = Answer(binding.rgExercise.tag.toString(), option.code, option.score)
            answerArrayMap[answer.questionCode] = answer

            answer = Answer("WAISTMEASUREMENT", paramList[0].finalValue, ""
                        + calculateWaistScore(paramList[0].finalValue.toDouble()))
            answerArrayMap["WAISTMEASUREMENT"] = answer
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateWaistScore(waist: Double): Int {
        val `val` = waist // * 2.54
        if (answerArrayMap["GENDER"]!!.answerCode.equals(resources.getString(R.string.MALE), ignoreCase = true)) {
            if (answerArrayMap["ORIGIN"]!!.answerCode.equals("rdbAsianOriginYes", ignoreCase = true)) {
                if (`val` in 90.0..100.0) {
                    return 4
                } else if (`val` > 100) {
                    return 7
                }
            } else if (answerArrayMap["ORIGIN"]!!.answerCode.equals("rdbAsianOriginNo", ignoreCase = true)) {
                if (`val` in 102.0..110.0) {
                    return 4
                } else if (`val` > 110) {
                    return 7
                }
            }
        } else if (answerArrayMap["GENDER"]!!.answerCode.equals(resources.getString(R.string.FEMALE), ignoreCase = true)) {
            if (answerArrayMap["ORIGIN"]!!.answerCode.equals("rdbAsianOriginYes", ignoreCase = true)) {
                if (`val` in 80.0..90.0) {
                    return 4
                } else if (`val` > 90) {
                    return 7
                }
            } else if (answerArrayMap["ORIGIN"]!!.answerCode.equals("rdbAsianOriginNo", ignoreCase = true)) {
                if (`val` in 88.0..100.0) {
                    return 4
                } else if (`val` > 100) {
                    return 7
                }
            }
        }
        return 0
    }

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {
        val waistDialogManager = WaistDialogManager(requireContext(), this, parameterAdapter!!.paramList[0])
        waistDialogManager.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        waistDialogManager.show()
    }

    override fun onDialogValueListener(inch: String?, centimeter: String?, unit: String?) {
        paramList = parameterAdapter!!.paramList
        Timber.e("InchValue=>$inch")
        Timber.e("CmValue=>$centimeter")
        if (unit.equals("Inch", ignoreCase = true)) {
            paramList[0].value = inch!!
            Timber.e("Value=>$inch")
        } else {
            paramList[0].value = centimeter!!
            Timber.e("Value=>$centimeter")
        }
        paramList[0].unit = unit!!
        paramList[0].finalValue = centimeter!!
        parameterAdapter!!.notifyDataSetChanged()
        //parameterAdapter!!.updateList(paramList)
    }

}
