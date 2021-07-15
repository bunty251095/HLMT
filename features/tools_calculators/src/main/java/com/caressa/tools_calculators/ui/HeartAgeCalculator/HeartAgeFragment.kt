package com.caressa.tools_calculators.ui.HeartAgeCalculator

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.FirebaseHelper
import com.caressa.common.utils.Utilities
import com.caressa.common.view.SpinnerAdapter
import com.caressa.common.view.SpinnerModel
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.ParameterAdapter
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentHeartAgeBinding
import com.caressa.tools_calculators.model.Answer
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.common.utils.ParameterDataModel
import com.caressa.model.toolscalculators.UserInfoModel
import com.caressa.tools_calculators.ui.HealthConditionDialog
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.caressa.common.utils.HeightWeightDialog
import com.caressa.tools_calculators.views.SystolicDiastolicDialogManager
import kotlinx.android.synthetic.main.dialog_input_parameter.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.*

class HeartAgeFragment : BaseFragment(),KoinComponent,ParameterAdapter.ParameterOnClickListener,
    SystolicDiastolicDialogManager.OnDialogValueListener,HeightWeightDialog.OnDialogValueListener,
    HealthConditionDialog.OnHealthConditionValueListener {

    private lateinit var binding: FragmentHeartAgeBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()
    private val dataHandler : DataHandler = get()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var genderAdapter : SpinnerAdapter? = null
    private var modelAdapter :SpinnerAdapter? = null
    private var parameterAdapter: ParameterAdapter? = null
    private var genderList = ArrayList<SpinnerModel>()
    private var modelList = ArrayList<SpinnerModel>()
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var userPreferenceModel = UserInfoModel()
    private var dialog: Dialog? = null
    private var dialogInput:Dialog? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                findNavController().navigate(R.id.action_heartAgeFragment_to_toolsCalculatorsDashboardFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHeartAgeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            quizID = calculatorDataSingleton!!.quizId
            participationID = calculatorDataSingleton!!.participantID
            Timber.e("QuizID,ParticipationID---> $quizID , $participationID")
            initialise()
            setClickable()
            loadUserData()
            FirebaseHelper.logScreenEvent(FirebaseConstants.HEART_AGE_CALCULATOR_SCREEN)
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        userPreferenceModel = UserInfoModel.getInstance()!!

        modelList = dataHandler.getModelList()
        modelAdapter = SpinnerAdapter(requireContext(), modelList)
        binding.modelSpinner.adapter = modelAdapter

        genderList = dataHandler.getGenderList()
        genderAdapter = SpinnerAdapter(requireContext(), genderList)
        binding.spinnerGender.adapter = genderAdapter

        dialog = Dialog(requireContext(), R.style.NoTitleDialog)
        dialog!!.setContentView(R.layout.dialog_vital_parameter)
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.currentFocus

        dialogInput = Dialog(requireContext(), R.style.NoTitleDialog)
        dialogInput!!.setContentView(R.layout.dialog_input_parameter)
        dialogInput!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogInput!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInput!!.window!!.currentFocus

        paramList = dataHandler.getParameterList("BMI", this)
        CalculatorDataSingleton.getInstance()!!.heartAgeModel = "BMI"
        parameterAdapter = ParameterAdapter(paramList,this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.paramRecyclerView.adapter = parameterAdapter

        var answer: Answer?
        answer = Answer("TOTAL_CHOL", "0", "0")
        answerArrayMap["TOTAL_CHOL"] = answer
        answer = Answer("HDL", "0", "0")
        answerArrayMap["HDL"] = answer
        answer = Answer("WEIGHT", "0", "0")
        answerArrayMap["WEIGHT"] = answer
        answer = Answer("HEIGHT", "0", "0")
        answerArrayMap["HEIGHT"] = answer

        viewModel.heartAgeSaveResp.observe( viewLifecycleOwner , {})
    }

    private fun loadUserData() {
        if (!Utilities.isNullOrEmpty(UserInfoModel.getInstance()!!.getAge())
            && !UserInfoModel.getInstance()!!.getAge().equals("0", ignoreCase = true)) {
            binding.edtAge.setText(UserInfoModel.getInstance()!!.getAge())
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

        binding.modelSpinner.onItemSelectedListener = object : OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Timber.i("Selected Item:: $position")
                if (position == 0) {
                    modelList[0].selection = true
                    modelList[1].selection = false
                    binding.txtModelSpinner.text = resources.getString(R.string.BMI)
                    paramList.clear()
                    paramList.addAll( dataHandler.getParameterList("BMI", this@HeartAgeFragment) )
                    //paramList = dataHandler.getParameterList("BMI", this@HeartAgeFragment)
                    parameterAdapter!!.updateList( paramList )
                    CalculatorDataSingleton.getInstance()!!.heartAgeModel = "BMI"
                } else {
                    modelList[0].selection = false
                    modelList[1].selection = true
                    binding.txtModelSpinner.text = resources.getString(R.string.LIPID)
                    paramList.clear()
                    paramList.addAll( dataHandler.getParameterList("LIPID", this@HeartAgeFragment) )
                    //paramList = dataHandler.getParameterList("LIPID", this@HeartAgeFragment)
                    parameterAdapter!!.updateList( paramList )
                    CalculatorDataSingleton.getInstance()!!.heartAgeModel = "LIPID"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.txtGender.setOnClickListener {
            binding.spinnerGender.performClick()
        }

        binding.txtModelSpinner.setOnClickListener {
            binding.modelSpinner.performClick()
        }

        binding.btnHealthConditionSelection.setOnClickListener {
            val healthConditionDialog = HealthConditionDialog(requireContext(),this)
            healthConditionDialog.show()
        }

        binding.btnCalculate.setOnClickListener {
            if (validateParameter()) {
                saveParameter()
                calculatorDataSingleton!!.answerArrayMap = answerArrayMap
                calculatorDataSingleton!!.userPreferences = userPreferenceModel
                viewModel.callHeartAgeCalculateApi("First",participationID,quizID,getAnswerList())
            }
/*            val bundle = Bundle()
            bundle.putString(Constants.FROM, "Home")
            it.findNavController().navigate(R.id.action_heartAgeFragment_to_heartSummaryFragment)*/
        }

    }

    private fun getAnswerList(): ArrayList<Answer> {
        val answers: ArrayList<Answer> = ArrayList()
        for ((_, value) in answerArrayMap) {
            answers.add(value)
        }
        return answers
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
                "TOTAL_CHOL" -> {
                    answer = Answer("TOTAL_CHOL", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["TOTAL_CHOL"] = answer
                }
                "HDL" -> {
                    answer = Answer("HDL", paramList[i].finalValue, paramList[i].value)
                    answerArrayMap["HDL"] = answer
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
        val selectedId3: Int = binding.rgDiabetic.checkedRadioButtonId

        val rgBpMed = binding.rgBpMedication.findViewById(selectedId1) as RadioButton
        val rgSmk = binding.rgSmoke.findViewById(selectedId2) as RadioButton
        val rgDib = binding.rgDiabetic.findViewById(selectedId3) as RadioButton

        answer = Answer("GENDER", binding.txtGender.text.toString(), "0")
        answerArrayMap["GENDER"] = answer
        answer = Answer("TRTHYPBP", rgBpMed.text.toString(), "0")
        answerArrayMap["TRTHYPBP"] = answer
        answer = Answer("DIABETIC", rgDib.text.toString(), "0")
        answerArrayMap["DIABETIC"] = answer
        answer = Answer("SMOKING", rgSmk.text.toString(), "0")
        answerArrayMap["SMOKING"] = answer
        answer = Answer("CHOL_LEVEL", "No", "0")
        answerArrayMap["CHOL_LEVEL"] = answer
        answer = Answer("AGE", binding.edtAge.text.toString(), "0")
        answerArrayMap["AGE"] = answer
        calculatorDataSingleton!!.personAge = binding.edtAge.text.toString()
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
                        saveUserPreference(paramList[i])
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(context, "Please fill height details.")
                        break
                    }
                } else if (paramList[i].title.equals("Weight", ignoreCase = true)) {
                    if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                        isValid = true
                        saveUserPreference(paramList[i])
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
                            saveUserPreference(paramList[i])
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

    @SuppressLint("SetTextI18n")
    private fun showInputDialog(param: ParameterDataModel, position: Int) {
        dialogInput!!.title_input.text = param.title
        dialogInput!!.txtMessage.text = "Enter your " + param.title.toLowerCase(Locale.ROOT) + " parameter value."

        dialogInput!!.inpLayout_input.hint = param.minRange.toString() + " - " + param.maxRange
        dialogInput!!.inpLayout_input.setText(param.finalValue)
        paramList = parameterAdapter!!.paramList

        dialogInput!!.btn_save_input.setOnClickListener {
            if (!Utilities.isNullOrEmpty(dialogInput!!.inpLayout_input.text.toString())) {
                val value: Double = dialogInput!!.inpLayout_input.text.toString().toDouble()
                if (value >= param.minRange && value <= param.maxRange) {
                    paramList[position].value = dialogInput!!.inpLayout_input.text.toString()
                    paramList[position].finalValue = dialogInput!!.inpLayout_input.text.toString()
                    parameterAdapter!!.notifyDataSetChanged()
                    //parameterAdapter!!.updateList(paramList)
                    dialogInput!!.dismiss()
                } else {
                    Utilities.toastMessageShort(context, "Please input value between " + param.minRange + " to " + param.maxRange)
                }
            } else {
                dialogInput!!.dismiss()
            }
        }
        dialogInput!!.img_close_input.setOnClickListener{
            dialogInput!!.dismiss()
        }
        dialogInput!!.show()
    }

    private fun saveUserPreference(parameterDataModel: ParameterDataModel) {
        when (parameterDataModel.code) {
            "HEIGHT" -> userPreferenceModel.setHeight(parameterDataModel.finalValue)
            "WEIGHT" -> userPreferenceModel.setWeight(parameterDataModel.finalValue)
            "TOTAL_CHOL" -> userPreferenceModel.setCholesterol(parameterDataModel.finalValue)
            "HDL" -> userPreferenceModel.setHdl(parameterDataModel.finalValue)
            "SYSTOLIC_BP" -> userPreferenceModel.setSystolicBp(parameterDataModel.finalValue)
            "DIASTOLIC_BP" -> userPreferenceModel.setDiastolicBp(parameterDataModel.finalValue)
        }
    }

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {
        binding.edtAge.clearFocus()
        if (parameterDataModel.title.equals("Height",ignoreCase = true)) {
            val heightWeightDialog = HeightWeightDialog(requireContext(), this@HeartAgeFragment, "Height", parameterAdapter!!.paramList[0])
            heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            heightWeightDialog.show()
        } else if (parameterDataModel.title.equals("Weight", ignoreCase = true)) {
            val heightWeightDialog = HeightWeightDialog(requireContext(), this@HeartAgeFragment, "Weight", parameterAdapter!!.paramList[2])
            heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            heightWeightDialog.show()
        } else if (parameterDataModel.code.equals("SYSTOLIC_BP", ignoreCase = true) || parameterDataModel.code.equals("DIASTOLIC_BP",ignoreCase = true)) {
            val customDialogManager = SystolicDiastolicDialogManager(requireContext(), this@HeartAgeFragment, parameterAdapter!!.paramList, position)
            customDialogManager.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            customDialogManager.show()
        } else {
            showInputDialog(parameterDataModel, position)
        }
    }

    override fun onDialogValueListener(systolic: String, diastolic: String) {
        paramList = parameterAdapter!!.paramList
        Timber.e("systolic,diastolic=>$systolic,$diastolic")
        if (!systolic.equals("", ignoreCase = true)) {
            paramList[1].value = systolic
            paramList[1].finalValue = systolic
        }
        if (!diastolic.equals("", ignoreCase = true)) {
            paramList[3].value = diastolic
            paramList[3].finalValue = diastolic
        }
        parameterAdapter!!.notifyDataSetChanged()
        //parameterAdapter!!.updateList(paramList)
    }

    override fun onDialogValueListener(dialogType: String, height: String, weight: String, unit: String, visibleValue: String) {
        viewModel.updateUserPreference(unit)
        paramList = parameterAdapter!!.paramList
        if (dialogType.equals("Height", ignoreCase = true)) {
            paramList[0].unit = unit
            paramList[0].value = visibleValue
            paramList[0].finalValue = height
        } else {
            paramList[2].unit = unit
            paramList[2].value = visibleValue
            paramList[2].finalValue = weight
        }
        parameterAdapter!!.notifyDataSetChanged()
        //parameterAdapter!!.updateList(paramList)
    }

    @SuppressLint("SetTextI18n")
    override fun onHealthConditionValueListener() {
        Timber.e("Selected :: ${calculatorDataSingleton!!.healthConditionSelection.size}")
        binding.txtSelection.text = "( You have selected ${calculatorDataSingleton!!.healthConditionSelection.size} health condition )"
    }

}
