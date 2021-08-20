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
import android.widget.RadioButton
import androidx.recyclerview.widget.GridLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.Utilities
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.adapter.ParameterAdapter
import com.caressa.tools_calculators.common.DataHandler
import com.caressa.tools_calculators.databinding.FragmentHeartAgeRecalculateBinding
import com.caressa.tools_calculators.model.Answer
import com.caressa.tools_calculators.model.CalculatorDataSingleton
import com.caressa.common.utils.ParameterDataModel
import com.caressa.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.caressa.common.utils.HeightWeightDialog
import com.caressa.model.toolscalculators.UserInfoModel
import com.caressa.tools_calculators.views.SystolicDiastolicDialogManager
import kotlinx.android.synthetic.main.dialog_input_parameter.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.*

class HeartAgeRecalculateFragment : BaseFragment(), KoinComponent, ParameterAdapter.ParameterOnClickListener,
    SystolicDiastolicDialogManager.OnDialogValueListener, HeightWeightDialog.OnDialogValueListener {

    private lateinit var binding: FragmentHeartAgeRecalculateBinding
    private val viewModel: ToolsCalculatorsViewModel by viewModel()
    private val dataHandler : DataHandler = get()
    private var calculatorDataSingleton : CalculatorDataSingleton? = null

    private var quizID = ""
    private var participationID = ""
    private var parameterAdapter: ParameterAdapter? = null
    private var paramList: MutableList<ParameterDataModel> = mutableListOf()
    private var answerArrayMap = ArrayMap<String, Answer>()
    private var dialogInput: Dialog? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHeartAgeRecalculateBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        quizID = calculatorDataSingleton!!.quizId
        participationID = calculatorDataSingleton!!.participantID
        Timber.e("QuizID,ParticipationID---> $quizID , $participationID")
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        binding.txtHeartAgeValue.text = calculatorDataSingleton!!.heartAge
        binding.txtHeartRiskValue.text = calculatorDataSingleton!!.riskScorePercentage

        paramList = dataHandler.getParameterList(calculatorDataSingleton!!.heartAgeModel, this)
        parameterAdapter = ParameterAdapter(paramList,this, requireContext())
        binding.paramRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.paramRecyclerView.adapter = parameterAdapter

        dialogInput = Dialog(requireContext(), R.style.NoTitleDialog)
        dialogInput!!.setContentView(R.layout.dialog_input_parameter)
        dialogInput!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogInput!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInput!!.window!!.currentFocus

        viewModel.heartAgeSaveResp.observe( viewLifecycleOwner , {})
    }

    private fun setClickable() {

        binding.btnCalculate.setOnClickListener {
            if (validateParameter()) {
                saveParameter()
                viewModel.callHeartAgeCalculateApi("",participationID,quizID,getAnswerList())
/*                val bundle = Bundle()
                bundle.putString(Constants.FROM, "Home")
                it.findNavController().navigate(R.id.action_heartAgeRecalculateFragment_to_heartSummaryFragment)*/
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
        var answerCodes:String = "NONE"
        if(calculatorDataSingleton?.healthConditionSelection.isNullOrEmpty()){
            answer = Answer("HHILL", answerCodes, "")
            answerArrayMap["HHILL"] = answer
        }else{
            answerCodes = ""
            for (item in calculatorDataSingleton!!.healthConditionSelection){
                answerCodes = answerCodes+item+","
            }
            answer = Answer("HHILL", answerCodes, "")
            answerArrayMap["HHILL"] = answer
        }

        val selectedId1: Int = binding.rgBpMedication.checkedRadioButtonId
        val selectedId2: Int = binding.rgSmoke.checkedRadioButtonId
        val selectedId3: Int = binding.rgDiabetic.checkedRadioButtonId

        val rgBpMed = binding.rgBpMedication.findViewById(selectedId1) as RadioButton
        val rgSmk = binding.rgSmoke.findViewById(selectedId2) as RadioButton
        val rgDib = binding.rgDiabetic.findViewById(selectedId3) as RadioButton

        if (UserInfoModel.getInstance()!!.isMale) {
            answer = Answer("GENDER", "Male", "0")
        } else {
            answer = Answer("GENDER", "Female", "0")
        }
        answerArrayMap["GENDER"] = answer
        answer = Answer("TRTHYPBP", rgBpMed.text.toString(), "0")
        answerArrayMap["TRTHYPBP"] = answer
        answer = Answer("DIABETIC", rgDib.text.toString(), "0")
        answerArrayMap["DIABETIC"] = answer
        answer = Answer("SMOKING", rgSmk.text.toString(), "0")
        answerArrayMap["SMOKING"] = answer

        answer = Answer("CHOL_LEVEL", "No", "0")
        answerArrayMap["CHOL_LEVEL"] = answer

        if (!Utilities.isNullOrEmpty(UserInfoModel.getInstance()!!.getAge())
            && !UserInfoModel.getInstance()!!.getAge().equals("0", ignoreCase = true)) {
            answer = Answer("AGE", UserInfoModel.getInstance()!!.getAge(), "0")
            answerArrayMap["AGE"] = answer
        }
    }

    private fun validateParameter(): Boolean {
        var isValid = false
        for (i in paramList.indices) {
            if (paramList[i].title.equals(resources.getString(R.string.HEIGHT), ignoreCase = true)) {
                if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                    isValid = true
                } else {
                    isValid = false
                    Utilities.toastMessageShort(context, "${resources.getString(R.string.PLEASE_FILL_HEIGHT_DETAILS)}.")
                    break
                }
            } else if (paramList[i].title.equals(resources.getString(R.string.WEIGHT), ignoreCase = true)) {
                if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                    isValid = true
                } else {
                    isValid = false
                    Utilities.toastMessageShort(context, "${resources.getString(R.string.PLEASE_FILL_WEIGHT_DETAILS)}.")
                    break
                }
            } else if (!paramList[i].title.equals(resources.getString(R.string.HEIGHT), ignoreCase = true)
                && !paramList[i].title.equals(resources.getString(R.string.WEIGHT), ignoreCase = true)) {

                if (!paramList[i].finalValue.equals("", ignoreCase = true)) {
                    Timber.e("paramList=>%s", paramList[i].finalValue)
                    val `val`: Double = paramList[i].finalValue.toDouble()
                    if (`val` >= paramList[i].minRange && `val` <= paramList[i].maxRange) {
                        isValid = true
                    } else {
                        isValid = false
                        Utilities.toastMessageShort(context, paramList[i].title + " ${resources.getString(R.string.SHOULD_BE_BETWEEN)} "
                                + paramList[i].minRange + " ${resources.getString(R.string.TO)} " + paramList[i].maxRange)
                        break
                    }
                } else {
                    isValid = false
                    Utilities.toastMessageShort(context, "${resources.getString(R.string.PLEASE_FILL)} " + paramList[i].title)
                    break
                }
            }
        }
        return isValid
    }

    @SuppressLint("SetTextI18n")
    private fun showInputDialog(param: ParameterDataModel, position: Int) {
        dialogInput!!.title_input.text = param.title
        dialogInput!!.txtMessage.text = "${resources.getString(R.string.ENTER_YOUR)} " + param.title.toLowerCase(Locale.ROOT) + " ${resources.getString(R.string.PARAMETER_VALUE)}."

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
                    Utilities.toastMessageShort(context, "${resources.getString(R.string.PLEASE_INPUT_VALUE_BETWEEN)} "
                            + param.minRange + " ${resources.getString(R.string.TO)} " + param.maxRange)
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

    override fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int) {
        if (parameterDataModel.title.equals(resources.getString(R.string.HEIGHT), ignoreCase = true)) {
            val heightWeightDialog = HeightWeightDialog(
                requireContext(),
                this@HeartAgeRecalculateFragment,
                "Height",
                parameterAdapter!!.paramList[0])
            heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            heightWeightDialog.show()
        } else if (parameterDataModel.title.equals(resources.getString(R.string.WEIGHT), ignoreCase = true)) {
            val heightWeightDialog = HeightWeightDialog(
                requireContext(),
                this@HeartAgeRecalculateFragment,
                "Weight",
                parameterAdapter!!.paramList[2])
            heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            heightWeightDialog.show()
        } else if (parameterDataModel.code.equals("SYSTOLIC_BP", ignoreCase = true)
            || parameterDataModel.code.equals(
                "DIASTOLIC_BP", ignoreCase = true)) {
            val customDialogManager = SystolicDiastolicDialogManager(
                requireContext(),
                this@HeartAgeRecalculateFragment,
                parameterAdapter!!.paramList,
                position)
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

}
