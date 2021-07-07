package com.caressa.track_parameter.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import com.caressa.common.constants.Constants
import com.caressa.common.utils.DateHelper
import com.caressa.model.parameter.ParameterListModel
import com.caressa.track_parameter.adapter.RevInputParamAdapter
import com.caressa.track_parameter.adapter.RevSelectedParamAdapter
import com.caressa.track_parameter.databinding.RevUpdateParameterFragmentBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import com.caressa.track_parameter.viewmodel.UpdateParamViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class RevUpdateParameterFragment : BaseFragment(){

    private val viewModel: UpdateParamViewModel by viewModel()
    private lateinit var binding: RevUpdateParameterFragmentBinding
    override fun getViewModel(): BaseViewModel = viewModel
    val args: RevUpdateParameterFragmentArgs by navArgs()
    var serverDate = ""
    var profileCode = "BMI"
    lateinit var dpd:DatePickerDialog
    var isValidateSuccess = false
    var validationMessage = ""
    var isFirstTime = true
//    private var from = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RevUpdateParameterFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        initialise()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // callback to Handle back button event

//        arguments?.let {
//            from = it.getString(Constants.FROM,"")!!
//            Timber.e("from,selectedDate--->$from")
//        }
//        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                performBackClick()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

//    fun performBackClick() {
//        val bundle = Bundle()
//        if ( from.equals("DashboardBP",ignoreCase = true) || from.equals("DashboardBMI") )  {
//            requireActivity().finish()
//        }
//    }

    private fun initialise() {
//        if(!from.isNullOrEmpty()){
//            if(from.equals("DashboardBP",true)) {
//                profileCode = "BLOODPRESSURE"
//            }else{
//                profileCode = "BMI"
//            }
//        }else {
            profileCode = args.profileCode
//        }
        val profileAdapter = RevSelectedParamAdapter(true)
        binding.rvSelectedParameters.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        binding.rvSelectedParameters.adapter = profileAdapter

        var paramAdapter = RevInputParamAdapter(profileCode)
        binding.rvInputParameters.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding.rvInputParameters.adapter = paramAdapter

        serverDate = DateHelper.currentDateAsStringddMMMyyyy
        binding.edtDate.setText(DateHelper.currentDateAsStringddMMMyyyy)

        profileAdapter.setOnItemClickListener {
            Timber.i("Position: "+it.iconPosition+" :: "+it.profileName)
            paramAdapter = RevInputParamAdapter(it.profileCode)
            binding.rvInputParameters.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL,false)
            binding.rvInputParameters.adapter = paramAdapter
            viewModel.getParameterByProfileCodeAndDate(it.profileCode,serverDate)
            paramAdapter.profileCode = it.profileCode
            viewModel.refreshSelectedParamList(args.showAllProfile)
            profileCode = it.profileCode
        }

        viewModel.getParameterByProfileCodeAndDate(profileCode,serverDate)
        viewModel.refreshSelectedParamList(args.showAllProfile)

        // Date picker Dialog
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date
            val selectedDate:String = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year.toString()
            if (!selectedDate.isNullOrEmpty()) {
                serverDate = DateHelper.convertDateTimeValue(selectedDate,DateHelper.DISPLAY_DATE_DDMMYYYY,DateHelper.DISPLAY_DATE_DDMMMYYYY).toString()
                binding.edtDate.setText(serverDate)
                viewModel.getParameterByProfileCodeAndDate(profileCode,serverDate)
            }

        }, year, month, day)

        binding.layoutDate.setOnClickListener(View.OnClickListener { view: View? ->
            if (fragmentManager != null) {
                dpd.show()
            }
        })

        binding.edtDate.setOnClickListener(View.OnClickListener { view: View? ->
            if (fragmentManager != null) {
                dpd.show()
            }
        })

        // Observer
        viewModel.saveParam.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
          Timber.i("Inside SAVE API Call Response: "+it)
        })
        viewModel.selectedParameter.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            var counter: Int = 0
            if(it.isNotEmpty()){
                if(isFirstTime) {
                    for (item in it) {
                        if (item.profileCode.equals(profileCode, true)) {
                            break
                        } else {
                            counter++
                        }
                    }
                    profileAdapter.selectedPosition = counter
                    profileAdapter.updateData(it)

                    paramAdapter = RevInputParamAdapter(profileCode)
                    binding.rvInputParameters.layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.VERTICAL, false
                    )
                    binding.rvInputParameters.adapter = paramAdapter
                    viewModel.getParameterByProfileCodeAndDate(profileCode, serverDate)
                    paramAdapter.profileCode = profileCode
                    binding.rvSelectedParameters.scrollToPosition(profileAdapter.selectedPosition)
                }else{
                    profileAdapter.updateData(it)
                }
            }
        })

        binding.btnSaveUpdateParameters.setOnClickListener(View.OnClickListener { view: View? ->
            try {
                var IS_BMI = false
                var IS_WHR:Boolean = false
                var IS_BP:Boolean = false
                val updateDateTime: String = DateHelper.currentDateTime
                var recordDateSync: String? = ""

                isValidateSuccess = paramAdapter.validateFields(paramAdapter.dataList)
                validationMessage = paramAdapter.validationMassage
                if (isValidateSuccess) {
                    var parameters: ArrayList<ParameterListModel.InputParameterModel>? = null
                    parameters = paramAdapter.getUpdatedParamList()
                    val recordDate = serverDate
                    var strUnit: String? = null
                    if (!profileCode.isNullOrEmpty() && profileCode.equals(
                            "LIPID",
                            ignoreCase = true
                        ) && strUnit.isNullOrEmpty()) {
                        strUnit = "mg/dL"
                    }
                    if (profileCode.equals("WHR", ignoreCase = true)) {
                        IS_WHR = true
                        recordDateSync = recordDate
                    } else if (profileCode.equals("BMI", ignoreCase = true)) {
                        IS_BMI = true
                        recordDateSync = recordDate
                    } else if (profileCode.equals(
                            "BP",
                            ignoreCase = true
                        ) || profileCode.equals("BLOODPRESSURE", ignoreCase = true)) {
                        IS_BP = true
                        recordDateSync = recordDate
                    }
                    val isParameterEmpty: Boolean = checkParameterEmpty(parameters)
                    if (!isParameterEmpty) {
                        viewModel.saveParameter(paramAdapter.dataList!! as ArrayList<ParameterListModel.InputParameterModel>,recordDate)
                    } else {
                        isValidateSuccess = false
                        validationMessage = "Please enter at least one value for $profileCode"
                    }
//                    checkIfDiastolicIsGreaterThanSystolic()
                    if (isValidateSuccess) {
                        if (IS_WHR || IS_BMI || IS_BP) {
                            /*val parameterToSync = ArrayMap<String, String>()
                            parameterToSync[GlobalConstants.RECORD_DATE] = recordDateSync
                            parameterToSync[GlobalConstants.PROFILE_CODE] = profileCode
                            parameterToSync[GlobalConstants.PERSON_ID] =
                                SessionManager.GetSessionDetails()
                                    .get(GlobalConstants.CURRENT_PERSON_ID)
                            HealthParametersDBHelper.markRecordToSync(parameterToSync)*/
                        }
                        //Synchronization calls
                        /*if (profileCode == "BMI") {
                            SyncManager.initiatePushDataSyncAsync("BMI")
                        } else if (profileCode == "WHR") {
                            SyncManager.initiatePushDataSyncAsync("WHR")
                        } else if (profileCode.equals(
                                "BP",
                                ignoreCase = true
                            ) || profileCode.equals("BLOODPRESSURE", ignoreCase = true)
                        ) {
                            SyncManager.initiatePushDataSyncAsync("BP")
                        } else {
                            SyncManager.initiatePushDataSyncAsync("LABPARAMETER")
                        }*/
                    }
                }else{
                    viewModel.showMessage(validationMessage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        binding.layoutHistory.setOnClickListener {
            viewModel.navigateParam(RevUpdateParameterFragmentDirections.actionUpdateFragmentToDetailHistoryFragment())
        }
    }

    /**
     * check ParameterEmpty
     *
     * @param parameters
     * @return
     */
    private fun checkParameterEmpty(parameters: ArrayList<ParameterListModel.InputParameterModel>?): Boolean {
        var isParameterEmpty = false
        var counter = 0
        for (j in parameters!!.indices) {
            val parameter: ParameterListModel.InputParameterModel = parameters.get(j)
            val strValue: String? = parameter.parameterVal
            val strTextValue: String? = parameter.parameterTextVal
            if (TrackParameterHelper.isNullOrEmptyOrZero(strValue) && strTextValue.isNullOrEmpty()) {
                counter = counter + 1
            }
        }
        isParameterEmpty = if (counter == parameters.size) {
            true
        } else {
            false
        }
        return isParameterEmpty
    }
}