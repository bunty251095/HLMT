package com.caressa.tools_calculators.ui.HypertensionCalculator

import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.Utilities
import com.caressa.common.view.SpinnerAdapter
import com.caressa.common.view.SpinnerModel
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.ParameterAdapter
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentHypertensionRecalculateBinding
import com.caressa.tools_calculators.model.Answer
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.common.utils.ParameterDataModel
import com.caressa.model.toolscalculators.UserInfoModel
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.caressa.common.utils.HeightWeightDialog
import com.caressa.tools_calculators.views.SystolicDiastolicDialogManager
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.ArrayList

class HypertensionRecalculateFragment : BaseFragment(), KoinComponent,ParameterAdapter.ParameterOnClickListener,
    SystolicDiastolicDialogManager.OnDialogValueListener, HeightWeightDialog.OnDialogValueListener {

    private lateinit var binding: FragmentHypertensionRecalculateBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()
    private val dataHandler : DataHandler = get()
    private  val calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!

    private var quizID = calculatorDataSingleton.quizId
    private var participationID = calculatorDataSingleton.participantID
    private var genderAdapter : SpinnerAdapter? = null
    private var parameterAdapter: ParameterAdapter? = null
    private var genderList = ArrayList<SpinnerModel>()
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var userPreferenceModel = UserInfoModel()
    private var dialogInput: Dialog? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        arguments?.let {
            quizID = requireArguments().getString("QuizId","")!!
            participationID = requireArguments().getString("ParticipationID","")!!
        }*/
        Timber.i("QuizID,ParticipationID---> $quizID , $participationID")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHypertensionRecalculateBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        setClickable()
        loadUserData()
        return binding.root
    }

    private fun initialise() {
        userPreferenceModel = UserInfoModel.getInstance()!!

        genderList = dataHandler.getGenderList()
        genderAdapter = SpinnerAdapter(requireContext(), genderList)
        binding.spinnerGender.adapter = genderAdapter

        binding.edtAge.getBackground().setColorFilter(ContextCompat.getColor(requireContext(),R.color.textViewColor), PorterDuff.Mode.SRC_IN)

        dialogInput = Dialog(requireContext(), R.style.NoTitleDialog)
        dialogInput!!.setContentView(R.layout.dialog_input_parameter)
        dialogInput!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogInput!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInput!!.window!!.currentFocus

        paramList = dataHandler.getParameterList("BMI", this)
        parameterAdapter = ParameterAdapter(paramList,this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.paramRecyclerView.adapter = parameterAdapter

        var answer: Answer = Answer("WEIGHT", "0", "0")
        answerArrayMap["WEIGHT"] = answer
        answer = Answer("HEIGHT", "0", "0")
        answerArrayMap["HEIGHT"] = answer

        viewModel.hypertensionSaveResp.observe( viewLifecycleOwner , Observer {})
    }

    private fun loadUserData() {
        if ( !Utilities.isNullOrEmptyOrZero(userPreferenceModel.getAge()) ) {
            binding.edtAge.setText(UserInfoModel.getInstance()!!.getAge())
        }
        if (UserInfoModel.getInstance()!!.isMale) {
            binding.spinnerGender.setSelection(0)
        } else {
            binding.spinnerGender.setSelection(1)
        }
    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap) {
            answers.add(value)
        }
        return answers
    }

    private fun setClickable() {

        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                Timber.i("Selected Item:: $position")
                binding.txtGender.text = genderList[position].name
                for (i in genderList.indices) {
                    genderList[i].selection = i == position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtGender.setOnClickListener {
            binding.spinnerGender.performClick()
        }

        binding.btnCalculate.setOnClickListener {
            if (validateParameter()) {
                saveParameter()
                calculatorDataSingleton.answerArrayMap = answerArrayMap
                calculatorDataSingleton.userPreferences = userPreferenceModel
                viewModel.callHypertensionSaveResponseApi("",participationID,quizID,getAnswerList())
            }
        }

    }

    private fun saveParameter() {
        var answer: Answer
        for (i in paramList.indices) {
            when (paramList[i].code) {
                "HEIGHT" -> {
                    answer = Answer("HEIGHT", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["HEIGHT"] = answer
                }
                "WEIGHT" -> {
                    answer = Answer("WEIGHT", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["WEIGHT"] = answer
                }
                "SYSTOLIC_BP" -> {
                    answer = Answer("SYSTOLIC_BP", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["SYSTOLIC_BP"] = answer
                }
                "DIASTOLIC_BP" -> {
                    answer = Answer("DIASTOLIC_BP", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["DIASTOLIC_BP"] = answer
                }
            }
        }
        val selectedId1: Int = binding.rgBpMedication.checkedRadioButtonId
        val selectedId2: Int = binding.rgSmoke.checkedRadioButtonId

        val rgBpMed = binding.rgBpMedication.findViewById(selectedId1) as RadioButton
        val rgSmk = binding.rgSmoke.findViewById(selectedId2) as RadioButton

        answer = Answer("GENDER", binding.txtGender.text.toString(), "0")
        answerArrayMap["GENDER"] = answer
        answer = Answer("TRTHYPBP", rgBpMed.text.toString(), "0")
        answerArrayMap["TRTHYPBP"] = answer
        answer = Answer("SMOKING", rgSmk.text.toString(), "0")
        answerArrayMap["SMOKING"] = answer
        answer = Answer("AGE", binding.edtAge.text.toString(), "0")
        answerArrayMap["AGE"] = answer

        calculatorDataSingleton.personAge = binding.edtAge.text.toString()
        userPreferenceModel.isMale = !binding.txtGender.text.toString().equals("female", ignoreCase = true)
    }

    private fun validateParameter(): Boolean {
        var isValid = false
        if (binding.edtAge.text.toString().equals("", ignoreCase = true)) {
            Utilities.toastMessageShort(context, "Please provide age")
            isValid = false
        } else if (!binding.edtAge.text.toString().equals("", ignoreCase = true)) {
            try {
                val age: Double = binding.edtAge.text.toString().toDouble()
                if (age > 17 && age <= 74) {
                    isValid = true
                    userPreferenceModel.setAge("" + age.toInt())
                } else {
                    isValid = false
                    Utilities.toastMessageShort(context, "Enter Age between 18 and 74  years")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (isValid) {
            for (i in paramList.indices) {
                if (paramList[i].title.equals("Height", ignoreCase = true)) {
                    if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                        isValid = true
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(context, "Please fill height details.")
                        break
                    }
                } else if (paramList[i].title.equals("Weight", ignoreCase = true)) {
                    if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                        isValid = true
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(context, "Please fill weight details.")
                        break
                    }
                } else if (!paramList[i].title.equals("Height", ignoreCase = true)
                    && !paramList[i].title.equals("Weight", ignoreCase = true)) {
                    if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                        Timber.e("paramList=>%s", paramList[i].finalValue)
                        val `val`: Double = paramList[i].finalValue.toDouble()
                        if (`val` >= paramList[i].minRange && `val` <= paramList[i].maxRange) {
                            isValid = true

                        } else {
                            isValid = false
                            Utilities.toastMessageShort(context, paramList[i].title + " should be between "
                                    + paramList[i].minRange + " to " + paramList[i].maxRange)
                            break
                        }
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(context, "Please fill " + paramList[i].title)
                        break
                    }
                }
            }
        }
        return isValid
    }

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {

    }

    override fun onDialogValueListener(systolic: String, diastolic: String) {

    }

    override fun onDialogValueListener(dialogType: String, height: String, weight: String, unit: String, visibleValue: String) {

    }

}