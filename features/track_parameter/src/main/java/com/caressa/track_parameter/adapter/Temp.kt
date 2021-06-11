/*
package com.caressa.track_parameter.adapter

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.DecimalValueFilter
import com.caressa.track_parameter.R

class TrackParameterUpdateExpandableAdapter(
    internal var fragment: TrackHealthParameterUpdateFragment,
    internal var context: Context,
    listDataHeader: ArrayList<ArrayMap<String, String>>,
    internal var listDataChild: ArrayMap<String, ArrayList<ArrayMap<String, String>>>?
) : BaseExpandableListAdapter() {

    var profileInfoModelFromAdapter = ArrayList<ProfileInfoModel>()
        internal set
    var headerList: ArrayList<ArrayMap<String, String>>
        internal set
    internal var mSharedPreference: SharedPreference
    internal var isSpinnerTouched = false
    internal var myCalendar: Calendar
    internal var mHeight = 0f
    internal var mWeight = 0f
    internal var mWaist = 0f
    internal var mHip = 0f
    internal var mBPSystolic = 0f
    internal var mBPDiastolic = 0f
    internal var modifiedParameters: ArrayMap<String, Boolean>

    initBase {
        this.headerList = listDataHeader
        mSharedPreference = SharedPreference(context)

        fillObjectModelData()
    }

    fun fillObjectModelData() {
        try {
            modifiedParameters = ArrayMap<String, Boolean>()
            profileInfoModelFromAdapter.clear()
            for (i in headerList.indices) {
                val profileInfoModel = ProfileInfoModel()
                profileInfoModel.setPersonId(headerList[i].get(GlobalConstants.PERSON_ID))
                profileInfoModel.setFrom(headerList[i].get(GlobalConstants.FROM))
                profileInfoModel.setProfileCode(headerList[i].get(GlobalConstants.PROFILE_CODE))
                profileInfoModel.setProfileName(headerList[i].get(GlobalConstants.PROFILE_NAME))
                profileInfoModel.setRecordDate(headerList[i].get(GlobalConstants.RECORD_DATE))
                profileInfoModel.setValid(
                    headerList[i].get(GlobalConstants.IS_VALID)!!.equals(
                        "T",
                        ignoreCase = true
                    )
                )

                val parameterInfoModelList = ArrayList<ParameterInfoModel>()
                var parameterInfoModel = ParameterInfoModel()
                if (listDataChild != null) {
                    if (listDataChild!!.get(profileInfoModel.getProfileCode()) != null) {
                        //adding first dummy view for date;
                        parameterInfoModelList.add(parameterInfoModel)
                        for (j in 0 until listDataChild!!.get(profileInfoModel.getProfileCode())!!.size) {
                            parameterInfoModel = ParameterInfoModel()
                            val listDataChildArrayMap =
                                listDataChild!!.get(profileInfoModel.getProfileCode())!!.get(j)
                            parameterInfoModel.setParamId(listDataChildArrayMap.get(GlobalConstants.ID))
                            parameterInfoModel.setValue(listDataChildArrayMap.get(GlobalConstants.VALUE))
                            parameterInfoModel.setMinPermissibleValue(
                                listDataChildArrayMap.get(
                                    GlobalConstants.MIN_PERMISSIBLE_VALUE
                                )
                            )
                            parameterInfoModel.setMaxPermissibleValue(
                                listDataChildArrayMap.get(
                                    GlobalConstants.MAX_PERMISSIBLE_VALUE
                                )
                            )
                            parameterInfoModel.setStatus(listDataChildArrayMap.get(GlobalConstants.STATUS))
                            parameterInfoModel.setDescription(
                                listDataChildArrayMap.get(
                                    GlobalConstants.DESCRIPTION
                                )
                            )
                            parameterInfoModel.setCode(listDataChildArrayMap.get(GlobalConstants.CODE))
                            parameterInfoModel.setUnit(listDataChildArrayMap.get(GlobalConstants.UNIT))
                            parameterInfoModel.setFrom(listDataChildArrayMap.get(GlobalConstants.FROM))
                            parameterInfoModel.setProfileCode(
                                listDataChildArrayMap.get(
                                    GlobalConstants.PROFILE_CODE
                                )
                            )
                            parameterInfoModel.setProfileName(
                                listDataChildArrayMap.get(
                                    GlobalConstants.PROFILE_NAME
                                )
                            )
                            parameterInfoModel.setOriginalValue(
                                listDataChildArrayMap.get(
                                    GlobalConstants.ORIGINAL_VALUE
                                )
                            )
                            parameterInfoModel.setOwnerCode(
                                listDataChildArrayMap.get(
                                    GlobalConstants.OWNER_CODE
                                )
                            )
                            parameterInfoModel.setNeedToConvert(
                                listDataChildArrayMap.get(
                                    GlobalConstants.NEED_TO_CONVERT
                                )!!.equals("true", ignoreCase = true)
                            )
                            parameterInfoModelList.add(parameterInfoModel)
                            Log.d(
                                "UPDATE ADAPTER",
                                " adding ParameterInfoModel" + listDataChildArrayMap.get(
                                    GlobalConstants.CODE
                                )!!
                            )
                            Log.d(
                                "UPDATE ADAPTER",
                                " adding ParameterInfoModel VALUE" + listDataChildArrayMap.get(
                                    GlobalConstants.VALUE
                                )!!
                            )
                        }
                    } else {
                        Log.d("UPDATE ADAPTER", " listDataChild == null")
                    }
                }
                profileInfoModel.setParameterInfoModelList(parameterInfoModelList)
                profileInfoModelFromAdapter.add(profileInfoModel)
                Log.d("UPDATE ADAPTER", " listDataHeader$headerList")
                Log.d("UPDATE ADAPTER", " listDataHeader.size" + headerList.size)
                Log.d("UPDATE ADAPTER", " listDataChild" + listDataChild!!.toString())
                Log.d("UPDATE ADAPTER", " listDataChild.size" + listDataChild!!.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        //return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
        val parameterInfoModelArrayList =
            profileInfoModelFromAdapter[groupPosition].getParameterInfoModelList()
        return parameterInfoModelArrayList.get(childPosition)
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        //final HashMap<String, String> expandedListText = (HashMap<String, String>) getChild(listPosition, expandedListPosition);
        try {
            fragment.getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            val profileInfoModelData = getGroup(groupPosition) as ProfileInfoModel
            val parameterInfoModelData =
                getChild(groupPosition, childPosition) as ParameterInfoModel
            Log.d("UPDATE ADAPTER", "getChildView  " + parameterInfoModelData.toString())
            if (convertView == null) {
                val layoutInflater =
                    this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = layoutInflater.inflate(
                    R.layout.updateparameter_screen_fragement_expandable_childgroup_adapter,
                    null
                )
                val holder = ViewHolderChild(convertView!!)
                convertView!!.setTag(holder)
            }
            val holder = convertView!!.getTag() as ViewHolderChild

            if (childPosition == 0) {
                val strDate = DateHelper.formatDateValue(profileInfoModelData.getRecordDate())
                holder.txtDateChildELUP.setText(strDate)

                if (ClientConfiguration.strAppIdentifier.equals(GlobalConstants.OLIVECARE) && (profileInfoModelData.getProfileCode().equalsIgnoreCase(
                        "BMI"
                    ) || profileInfoModelData.getProfileCode().equalsIgnoreCase("WHR"))
                ) {
                    holder.spinnerUnit.setVisibility(View.VISIBLE)
                } else {
                    holder.spinnerUnit.setVisibility(View.GONE)
                }

                holder.rlParameterDate.setVisibility(View.VISIBLE)
                holder.lltUpdateValues.setVisibility(View.GONE)
                holder.lltUpdateValuesImperial.setVisibility(View.GONE)
            } else {
                holder.rlParameterDate.setVisibility(View.GONE)
                holder.spinnerUnit.setVisibility(View.GONE)
                holder.lltUpdateValues.setVisibility(View.VISIBLE)
                holder.lltUpdateValuesImperial.setVisibility(View.GONE)

                holder.edtChildValues.setText("")
                holder.txtChildLabel.setText(
                    parameterInfoModelData.getDescription() + "(" + Helper.convertUnitLabel(
                        mSharedPreference.getDefaultUnit(),
                        parameterInfoModelData.getCode(),
                        parameterInfoModelData.getUnit()
                    ) + ")"
                )

                if (parameterInfoModelData.getValue() != null) {

                    if (!Helper.isNullOrEmpty(parameterInfoModelData.getValue())) {
                        val valueNew = Helper.round(
                            java.lang.Double.valueOf(parameterInfoModelData.getValue()),
                            1
                        )
                        holder.edtChildValues.setText("" + valueNew)
                        println(parameterInfoModelData.getCode() + " " + parameterInfoModelData.getValue() + " EditValue l3: " + valueNew)

                    } else {
                        holder.edtChildValues.setText("")
                    }
                }
                Log.d(
                    "UPDATE ADAPTER ",
                    "parameterInfoModelData.getCode()  " + parameterInfoModelData.getCode() + " VALUE " + parameterInfoModelData.getValue()
                )

                when (parameterInfoModelData.getCode()) {
                    "BMI" -> {
                        //holder.edtChildValues.setTag(parameterInfoModelData.getCode());
                        holder.edtChildValues.setHint("")
                        holder.edtChildValues.setTextColor(-0x76000000)
                        if (!Helper.isNullOrEmpty(parameterInfoModelData.getValue())) {
                            Log.d("UPDATE ADAPTER ", "inside  " + parameterInfoModelData.getCode())
                            //String BMIstr = CalculateBMI(mSharedPreference.getDefaultUnit(), getChildValues(profileInfoModelData.getProfileCode(), "HEIGHT"), getChildValues(profileInfoModelData.getProfileCode(), "WEIGHT"));
                            val BMIstr = parameterInfoModelData.getValue()
                            if (!Helper.isNullOrEmpty(BMIstr)) {
                                val BMI = Helper.round(java.lang.Double.valueOf(BMIstr), 1)
                                holder.edtChildValues.setText(BMI + "")
                                holder.edtChildValues.setTextColor(Color.BLACK)
                            }
                        }
                    }
                    "WHR" -> {
                        //holder.edtChildValues.setTag(parameterInfoModelData.getCode());
                        holder.edtChildValues.setHint("")
                        holder.edtChildValues.setTextColor(-0x76000000)
                        if (!Helper.isNullOrEmpty(parameterInfoModelData.getValue())) {
                            val WHRstr = parameterInfoModelData.getValue()
                            //String WHRstr = CalculateWHR(mSharedPreference.getDefaultUnit(), getChildValues(profileInfoModelData.getProfileCode(), "WAIST"), getChildValues(profileInfoModelData.getProfileCode(), "HIP"));
                            if (!Helper.isNullOrEmpty(WHRstr)) {
                                val WHR = Helper.round(java.lang.Double.valueOf(WHRstr), 1)
                                holder.edtChildValues.setText(WHR + "")
                                holder.edtChildValues.setTextColor(Color.BLACK)
                            }
                        }
                    }
                    "AGRATIO" -> {
                        //holder.edtChildValues.setTag(parameterInfoModelData.getCode());
                        holder.edtChildValues.setHint("")
                        holder.edtChildValues.setTextColor(-0x76000000)
                        if (!Helper.isNullOrEmpty(parameterInfoModelData.getValue())) {
                            val agRatio = CalculateAGRatio(
                                getChildValues("LIVER", "ALBUMIN"),
                                getChildValues("LIVER", "GLOBULIN")
                            )
                            if (!Helper.isNullOrEmpty(agRatio)) {
                                holder.edtChildValues.setText(agRatio)
                                holder.edtChildValues.setTextColor(Color.BLACK)
                            }
                        }
                    }
                    "HEIGHT" ->

                        if (mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)) {
                            holder.lltUpdateValues.setVisibility(View.GONE)
                            holder.lltUpdateValuesImperial.setVisibility(View.VISIBLE)

                            var strValue = parameterInfoModelData.getValue()
                            if (parameterInfoModelData.getValue() != null) {
                                if (!strValue.contains("/")) {
                                    strValue = Helper.convertToImperialFormat(
                                        GlobalConstants.UNIT_IMPERIAL,
                                        "HEIGHT",
                                        parameterInfoModelData.getValue()
                                    )
                                }
                                if (!Helper.isNullOrEmpty(strValue)) {
                                    val separated = strValue.split("/".toRegex())
                                        .dropLastWhile({ it.isEmpty() }).toTypedArray()
                                    val first = separated[0]
                                    val second = separated[1]
                                    if (!Helper.isNullOrEmpty(first)) {
                                        holder.edtChildValuesHeightFeet.setText(first)
                                    } else {
                                        holder.edtChildValuesHeightFeet.setText("")
                                    }
                                    if (!Helper.isNullOrEmpty(second)) {
                                        holder.edtChildValuesHeightInch.setText(second)
                                    } else {
                                        holder.edtChildValuesHeightInch.setText("")
                                    }
                                } else {
                                    holder.edtChildValuesHeightFeet.setText("")
                                    holder.edtChildValuesHeightInch.setText("")
                                }
                            }


                        } else {
                            holder.lltUpdateValues.setVisibility(View.VISIBLE)
                            holder.lltUpdateValuesImperial.setVisibility(View.GONE)
                            holder.txtChildLabel.setText("HEIGHT (cm)")
                        }
                    else -> {
                        holder.edtChildValues.setText("")
                        holder.edtChildValues.setHint("Enter Values...")
                        if (parameterInfoModelData.getValue() != null) {
                            var valueStr = parameterInfoModelData.getValue()
                            Log.d(
                                "UPDATE ADAPTER",
                                " CODE " + parameterInfoModelData.getCode() + "  VALUE -- " + valueStr + " -- isNeedToConvert " + parameterInfoModelData.isNeedToConvert()
                            )
                            if (parameterInfoModelData.isNeedToConvert()) {
                                valueStr = Helper.convertToImperialFormat(
                                    mSharedPreference.getDefaultUnit(),
                                    parameterInfoModelData.getCode(),
                                    parameterInfoModelData.getValue()
                                )
                            }
                            if (!Helper.isNullOrEmpty(valueStr)) {
                                val valueNew = Helper.round(java.lang.Double.valueOf(valueStr), 1)
                                holder.edtChildValues.setText(valueNew.toString())
                                println(parameterInfoModelData.getCode() + " " + parameterInfoModelData.getValue() + " EditValue l2: " + valueNew)

                            }
                        } else {
                            holder.edtChildValues.setText("")
                        }
                    }
                }

                //Check Validation and show message
                val errorMessage = ValidateFieldErrorMsg(
                    holder.edtChildValues.getText().toString(),
                    profileInfoModelData.getProfileCode(),
                    parameterInfoModelData.getCode()
                )
                Log.d(
                    "UPDATE ADAPTER",
                    "--errorMessage for Parameter--" + parameterInfoModelData.getCode() + " - " + errorMessage
                )
                if (!Helper.isNullOrEmpty(errorMessage)) {
                    //holder.edtChildValues.setError(errorMessage);
                    Helper.showMessage(context, errorMessage)
                    holder.edtChildValues.setText("")
                } else {
                    //holder.edtChildValues.setError(null);
                }
            }

            holder.edtChildValues.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(v: View, hasFocus: Boolean) {
                    //Check Validation and show message
                    val errorMessage = ValidateFieldErrorMsg(
                        holder.edtChildValues.getText().toString(),
                        profileInfoModelData.getProfileCode(),
                        parameterInfoModelData.getCode()
                    )
                    Log.d(
                        "UPDATE ADAPTER",
                        "--errorMessage for Parameter--" + parameterInfoModelData.getCode() + " - " + errorMessage
                    )
                    if (!Helper.isNullOrEmpty(errorMessage)) {
                        //holder.edtChildValues.setError(errorMessage);
                        Helper.showMessage(context, errorMessage)
                        holder.edtChildValues.setText("")
                    } else {
                        //holder.edtChildValues.setError(null);
                    }
                }
            })

            if (childPosition != 0) {

                if (profileInfoModelData.getProfileCode().equalsIgnoreCase("BLOODPRESSURE")) {
                    val decimalValueFilter = DecimalValueFilter(false)
                    decimalValueFilter.setDigits(0)
                    holder.edtChildValues.setKeyListener(decimalValueFilter)
                } else {
                    val decimalValueFilter = DecimalValueFilter()
                    decimalValueFilter.setDigits(1)
                    holder.edtChildValues.setKeyListener(decimalValueFilter)
                }
                holder.edtChildValues.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(6)))

                if (parameterInfoModelData.getCode().equals("BMI") || parameterInfoModelData.getCode().equals(
                        "WHR"
                    ) || parameterInfoModelData.getCode().equals("AGRATIO")
                ) {
                    holder.edtChildValues.setEnabled(false)
                    if (parameterInfoModelData.getCode().equals("BMI")) {
                        holder.edtChildValues.setTextColor(Color.BLACK)
                    } else if (parameterInfoModelData.getCode().equals("WHR")) {
                        holder.edtChildValues.setTextColor(Color.BLACK)
                    } else if (parameterInfoModelData.getCode().equals("AGRATIO")) {
                        holder.edtChildValues.setTextColor(Color.BLACK)
                    } else {
                        holder.edtChildValues.setTextColor(-0x76000000)
                    }
                }

                Log.d(
                    "UPDATE ADAPTER",
                    "mSharedPreference.getDefaultUnit() " + mSharedPreference.getDefaultUnit()
                )

            }

            holder.spinnerUnit.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                    isSpinnerTouched = true
                    return false
                }
            })

            holder.spinnerUnit.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    position: Int,
                    l: Long
                ) {
                    //if (childPosition != 0) {
                    Log.d(
                        "UPDATE ADAPTER",
                        "  isSpinnerTouched setOnItemSelectedListener $isSpinnerTouched"
                    )
                    if (isSpinnerTouched) {
                        val strUnit = adapterView.getItemAtPosition(position) as String
                        */
/*if (strUnit == null) {
                                    strUnit = GlobalConstants.UNIT_METRIC;
                                }*//*


                        mSharedPreference.setDefaultUnit(strUnit)
                        isSpinnerTouched = false
                        */
/*String valueTagUnitSpinner = holder.spinnerUnit.getTag().toString().trim();
                        String[] groupPart = valueTagUnitSpinner.split("-");
                        int groupPosition = Integer.parseInt(groupPart[1]);
                        //select group profile code
                        String profileCode = mapGroupHeader.get(GlobalConstants.PROFILE_CODE);*//*

                        // Check Locally for parameter
                        val mapGroupHeader = headerList[groupPosition]
                        val arrayMapChildList = returnChildListAsPerProfileCodeAndRecordDate(
                            profileInfoModelData.getProfileCode(),
                            profileInfoModelData.getRecordDate(),
                            "0"
                        )
                        mapGroupHeader.put(
                            GlobalConstants.RECORD_DATE,
                            profileInfoModelData.getRecordDate()
                        )
                        refreshExpandableListUnit(
                            groupPosition,
                            profileInfoModelData.getRecordDate(),
                            profileInfoModelData.getProfileCode(),
                            arrayMapChildList,
                            mapGroupHeader
                        )

                        */
/*for (int i = 0; i < listDataChild.get(profileInfoModelData.getProfileCode()).size(); i++) {
                                listDataChild.get(parameterInfoModelData.getCode()).get(i).put(GlobalConstants.UNIT, strUnit);
                            }
                            fillObjectModelData();
                            notifyDataSetChanged();*//*


                    }
                    //}
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                    Log.d(
                        "UPDATE ADAPTER",
                        "  isSpinnerTouched onNothingSelected $isSpinnerTouched"
                    )

                }
            })

            if (!isSpinnerTouched && !Helper.isNullOrEmpty(mSharedPreference.getDefaultUnit())) {
                //isSpinnerTouched = true;
                if (mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)) {
                    holder.spinnerUnit.setSelection(1)
                } else {
                    holder.spinnerUnit.setSelection(0)
                }
            }

            holder.txtDateChildELUP.setOnClickListener(object : View.OnClickListener {

                override fun onClick(v: View) {
                    myCalendar = Calendar.getInstance()
                    val dpd = DatePickerDialog.newInstance(
                        object : DatePickerDialog.OnDateSetListener() {
                            fun onDateSet(
                                view: DatePickerDialog,
                                year: Int,
                                monthOfYear: Int,
                                dayOfMonth: Int
                            ) {
                                val _monthOf = Helper.returnTwoDigitFromDate(monthOfYear + 1)
                                val _monthOfDay = Helper.returnTwoDigitFromDate(dayOfMonth)

                                val appointMentDate = "$year-$_monthOf-$_monthOfDay"
                                val strDate = DateHelper.formatDateValue(appointMentDate)

                                holder.txtDateChildELUP.setText(strDate)

                                val mapGroupHeader = headerList[groupPosition]
                                mapGroupHeader.put(GlobalConstants.RECORD_DATE, strDate)
                                mapGroupHeader.put(GlobalConstants.RECORD_DATE_TEMP, strDate)

                                if (strDate != null) {
                                    val profileCode =
                                        mapGroupHeader.get(GlobalConstants.PROFILE_CODE)
                                    val arrayMapChildList =
                                        returnChildListAsPerProfileCodeAndRecordDate(
                                            profileCode,
                                            strDate,
                                            "0"
                                        )
                                    mapGroupHeader.put(GlobalConstants.RECORD_DATE, strDate)
                                    refreshExpandableListUnit(
                                        groupPosition,
                                        strDate,
                                        profileCode,
                                        arrayMapChildList,
                                        mapGroupHeader
                                    )

                                } else {
                                    holder.txtDateChildELUP.setText("")
                                }
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.setAccentColor(context.getResources().getColor(R.color.vivantGreen))
                    dpd.setTitle("Pick record date")
                    dpd.setMaxDate(myCalendar)
                    dpd.setThemeDark(false)
                    dpd.vibrate(false)
                    dpd.dismissOnPause(false)
                    dpd.showYearPickerFirst(false)
                    dpd.show(fragment.getActivity().getFragmentManager(), "recorddate")

                }
            })

            holder.edtChildValues.setOnEditorActionListener(
                object : EditText.OnEditorActionListener {
                    override fun onEditorAction(
                        v: TextView,
                        actionId: Int,
                        event: KeyEvent
                    ): Boolean {
                        try {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN ||
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            ) {
                                Helper.hideKeyboard(v, context)
                                if (childPosition != 0) {
                                    //if ((!parameterInfoModelData.getCode().equals("BMI")) && (!parameterInfoModelData.getCode().equals("WHR")) && !parameterInfoModelData.getCode().equals("AGRATIO"))
                                    run {
                                        // ValidateFieldOnExit(holder.edtChildValues,groupPosition, childPosition, holder, listDataChild.get(profileInfoModelData.getProfileCode()).get(childPosition), parameterInfoModelData);
                                    }
                                }
                                return true // consume.
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return false // pass on to other listeners.
                    }
                })

            holder.edtChildValuesHeightFeet.setOnEditorActionListener(
                object : EditText.OnEditorActionListener {
                    override fun onEditorAction(
                        v: TextView,
                        actionId: Int,
                        event: KeyEvent
                    ): Boolean {
                        try {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN ||
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            ) {
                                Helper.hideKeyboard(v, context)
                                if (childPosition != 0) {
                                    //if ((!parameterInfoModelData.getCode().equals("BMI")) && (!parameterInfoModelData.getCode().equals("WHR")) && !parameterInfoModelData.getCode().equals("AGRATIO"))
                                    run {
                                        //ValidateFieldOnExit(holder.edtChildValuesHeightFeet, groupPosition, childPosition, holder, listDataChild.get(profileInfoModelData.getProfileCode()).get(childPosition), parameterInfoModelData);
                                    }
                                }
                                return true // consume.
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return false // pass on to other listeners.
                    }
                })

            holder.edtChildValuesHeightInch.setOnEditorActionListener(
                object : EditText.OnEditorActionListener {
                    override fun onEditorAction(
                        v: TextView,
                        actionId: Int,
                        event: KeyEvent
                    ): Boolean {
                        try {
                            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN ||
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            ) {
                                Helper.hideKeyboard(v, context)
                                if (childPosition != 0) {
                                    //if ((!parameterInfoModelData.getCode().equals("BMI")) && (!parameterInfoModelData.getCode().equals("WHR")) && !parameterInfoModelData.getCode().equals("AGRATIO"))
                                    run {
                                        // ValidateFieldOnExit(holder.edtChildValuesHeightInch, groupPosition, childPosition, holder, listDataChild.get(profileInfoModelData.getProfileCode()).get(childPosition), parameterInfoModelData);
                                    }
                                }
                                return true // consume.
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return false // pass on to other listeners.
                    }
                })

            */
/*holder.edtChildValues.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        String tagValue = holder.edtChildValues.getTag().toString().trim();
                        String[] groupPart = tagValue.split("-");
                        currentParamCode = groupPart[4];

//                                 Helper.showKeyboard(v, _context);
                        //TrackHealthParameterUpdateFragment.buttonSaveUp.setEnabled(false);
                        // TrackHealthParameterUpdateFragment.buttonSaveUp.setBackgroundResource(R.drawable.round_corner_right_top_bottom_grey_button);
//                                 editText.requestFocus();
//                                ValidateFieldOnExit(editText, map);
//                                editText.requestFocus();

                    } else {
//                                 Helper.hideKeyboard(v, _context);
                        //ValidateFieldOnExit(editText, map);
                    }
                }
            });*//*

            if (childPosition != 0) {
                if (!parameterInfoModelData.getCode().equalsIgnoreCase("BMI") &&
                    !parameterInfoModelData.getCode().equalsIgnoreCase("WHR") &&
                    !parameterInfoModelData.getCode().equalsIgnoreCase("AGRATIO")
                ) {
                    val finalConvertView = convertView
                    holder.edtChildValues.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            if (holder.lltUpdateValues.getVisibility() == View.VISIBLE) {
                                var editTextValue =
                                    holder.edtChildValues.getText().toString().trim({ it <= ' ' })
                                val profileCode = profileInfoModelData.getProfileCode()
                                val parameterCode = parameterInfoModelData.getCode()
                                var parameter: ArrayMap<String, String>? = null
                                try {
                                    parameter = getParametersByProfile(profileCode, parameterCode)
                                    parameter!!.put(GlobalConstants.VALUE, editTextValue)

                                    println("afterTextChanged: parameterCode:  $parameterCode  VALUE  $editTextValue")
                                    parameter!!.put(
                                        GlobalConstants.UNIT,
                                        parameterInfoModelData.getUnit()
                                    )
                                    if (!parameter!!.get(GlobalConstants.VALUE)!!.equals(
                                            parameter!!.get(
                                                GlobalConstants.ORIGINAL_VALUE
                                            )!!, ignoreCase = true
                                        )
                                    ) {
                                        modifiedParameters.put(parameterCode, true)
                                    } else {
                                        modifiedParameters.put(parameterCode, false)
                                    }
                                    println("modifiedParameters: " + modifiedParameters.toString())
                                    for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                        if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                            if (parameterCode.equals(
                                                    listDataChild!!.get(profileCode)!!.get(
                                                        i
                                                    ).get(GlobalConstants.CODE)!!, ignoreCase = true
                                                )
                                            ) {
                                                Log.d(
                                                    "UPDATE ADAPTER",
                                                    " afterTextChanged editTextValue main " + listDataChild!!.get(
                                                        profileCode
                                                    )!!.get(i).get(GlobalConstants.CODE) + " VALUE " + editTextValue
                                                )
                                                //String unitNew = listDataChild.get(profileCode).get(i).get(GlobalConstants.UNIT) + "|" + GlobalConstants.UNIT_METRIC;
                                                //listDataChild.get(profileCode).get(i).put(GlobalConstants.UNIT, unitNew);
                                                listDataChild!!.get(profileCode)!!.get(i)
                                                    .put(GlobalConstants.VALUE, editTextValue)
                                                if (parameterCode.equals(
                                                        "WEIGHT",
                                                        ignoreCase = true
                                                    ) || parameterCode.equals(
                                                        "WAIST",
                                                        ignoreCase = true
                                                    ) || parameterCode.equals(
                                                        "HIP",
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    listDataChild!!.get(profileCode)!!.get(i).put(
                                                        GlobalConstants.NEED_TO_CONVERT,
                                                        "false"
                                                    )
                                                }
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    parameter = null
                                }

                                // ValidateFieldOnExit(groupPosition, childPosition, holder, listDataChild.get(profileInfoModelData.getProfileCode()).get(childPosition), parameterInfoModelData);

                                when (profileCode) {
                                    "BMI" -> {
                                        */
/*if (mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)) {
                                            String editTextValueFeet = getChildValues(profileCode, "HEIGHT").get(GlobalConstants.VALUE);
                                            String editTextValueInch = holder.edtChildValuesHeightInch.getText().toString().trim();
                                            String weightValue = getChildValues(profileCode, "WEIGHT").get(GlobalConstants.VALUE);
                                            Log.d("UPDATE ADAPTER", "BMI afterTextChanged editTextValueFeet  IMPERIAL  " + editTextValueFeet);
                                            Log.d("UPDATE ADAPTER", "BMI afterTextChanged editTextValueInch  IMPERIAL  " + editTextValueInch);
                                            Log.d("UPDATE ADAPTER", "BMI afterTextChanged weightValue  IMPERIAL  " + weightValue);
                                            if (!Helper.isNullOrEmpty(editTextValueFeet) && !Helper.isNullOrEmpty(editTextValueInch) && !Helper.isNullOrEmpty(weightValue)) {
                                                editTextValue = String.valueOf(CalculateParametersAndObservations.getBMIFromImperial(Double.parseDouble(editTextValueFeet), Double.parseDouble(editTextValueInch), Double.parseDouble(weightValue)));
                                            }
                                            Log.d("UPDATE ADAPTER", "BMI afterTextChanged final editTextValue  IMPERIAL  " + editTextValue);
                                        } else {
                                            editTextValue = CalculateBMI(parameterInfoModelData.getUnit(), getChildValues(profileCode, "HEIGHT"), getChildValues(profileCode, "WEIGHT"));
                                            Log.d("UPDATE ADAPTER", "BMI afterTextChanged final editTextValue  METRIC " + editTextValue);
                                        }*//*

                                        editTextValue = CalculateBMI(
                                            getChildValues(profileCode, "HEIGHT"),
                                            getChildValues(profileCode, "WEIGHT")
                                        )
                                        Log.d(
                                            "UPDATE ADAPTER",
                                            "BMI afterTextChanged final editTextValue $editTextValue"
                                        )

                                        for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                            if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                                if ("BMI".equals(
                                                        listDataChild!!.get(profileCode)!!.get(
                                                            i
                                                        ).get(GlobalConstants.CODE)!!,
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    Log.d(
                                                        "UPDATE ADAPTER",
                                                        " afterTextChanged editTextValue main in BMI " + listDataChild!!.get(
                                                            profileCode
                                                        )!!.get(i).get(GlobalConstants.CODE) + " VALUE " + editTextValue
                                                    )
                                                    listDataChild!!.get(profileCode)!!.get(i)
                                                        .put(GlobalConstants.VALUE, editTextValue)
                                                }
                                            }
                                        }
                                    }
                                    "WHR" -> {
                                        editTextValue = CalculateWHR(
                                            mSharedPreference.getDefaultUnit(),
                                            getChildValues(profileCode, "WAIST"),
                                            getChildValues(profileCode, "HIP")
                                        )

                                        Log.d(
                                            "UPDATE ADAPTER",
                                            "WHR afterTextChanged final editTextValue $editTextValue"
                                        )

                                        for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                            if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                                if ("WHR".equals(
                                                        listDataChild!!.get(profileCode)!!.get(
                                                            i
                                                        ).get(GlobalConstants.CODE)!!,
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    Log.d(
                                                        "UPDATE ADAPTER",
                                                        " afterTextChanged editTextValue main " + listDataChild!!.get(
                                                            profileCode
                                                        )!!.get(i).get(GlobalConstants.CODE) + " VALUE " + editTextValue
                                                    )
                                                    listDataChild!!.get(profileCode)!!.get(i)
                                                        .put(GlobalConstants.VALUE, editTextValue)
                                                }
                                            }
                                        }
                                    }
                                    "LIVER" -> {
                                        editTextValue = CalculateAGRatio(
                                            getChildValues(profileCode, "ALBUMIN"),
                                            getChildValues(profileCode, "GLOBULLIN")
                                        )

                                        Log.d(
                                            "UPDATE ADAPTER",
                                            "LIVER afterTextChanged final editTextValue $editTextValue"
                                        )

                                        for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                            if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                                if ("LIVER".equals(
                                                        listDataChild!!.get(profileCode)!!.get(
                                                            i
                                                        ).get(GlobalConstants.CODE)!!,
                                                        ignoreCase = true
                                                    )
                                                ) {
                                                    Log.d(
                                                        "UPDATE ADAPTER",
                                                        " afterTextChanged editTextValue main " + listDataChild!!.get(
                                                            profileCode
                                                        )!!.get(i).get(GlobalConstants.CODE) + " VALUE " + editTextValue
                                                    )
                                                    listDataChild!!.get(profileCode)!!.get(i)
                                                        .put(GlobalConstants.VALUE, editTextValue)
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                    }
                                }//holder.edtChildValues.setTextColor(0x8A000000);
                            }

                        }

                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                    holder.edtChildValuesHeightFeet.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            if (holder.lltUpdateValuesImperial.getVisibility() == View.VISIBLE) {
                                try {
                                    val editTextValueFeet =
                                        holder.edtChildValuesHeightFeet.getText().toString()
                                            .trim({ it <= ' ' })
                                    val editTextValueInch =
                                        holder.edtChildValuesHeightInch.getText().toString()
                                            .trim({ it <= ' ' })
                                    val profileCode = profileInfoModelData.getProfileCode()
                                    val weightValue = getChildValues(
                                        profileCode,
                                        "WEIGHT"
                                    ).get(GlobalConstants.VALUE)
                                    var finalEditTextValue = ""

                                    Log.d(
                                        "UPDATE ADAPTER",
                                        " edtChildValuesHeightFeet.addTextChangedListener editTextValueFeet $editTextValueFeet"
                                    )
                                    Log.d(
                                        "UPDATE ADAPTER",
                                        " edtChildValuesHeightFeet.addTextChangedListener editTextValueInch $editTextValueInch"
                                    )
                                    Log.d(
                                        "UPDATE ADAPTER",
                                        " edtChildValuesHeightFeet.addTextChangedListener weightValue " + weightValue!!
                                    )

                                    for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                        if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                            if ("HEIGHT".equals(
                                                    listDataChild!!.get(profileCode)!!.get(
                                                        i
                                                    ).get(GlobalConstants.CODE)!!, ignoreCase = true
                                                )
                                            ) {
                                                if (!Helper.isNullOrEmpty(editTextValueFeet) && !Helper.isNullOrEmpty(
                                                        editTextValueInch
                                                    )
                                                ) {
                                                    listDataChild!!.get(profileCode)!!.get(i).put(
                                                        GlobalConstants.VALUE,
                                                        Helper.convertFeetInchToCm(
                                                            editTextValueFeet,
                                                            editTextValueInch
                                                        )
                                                    )
                                                }
                                            }
                                            if ("WEIGHT".equals(
                                                    listDataChild!!.get(profileCode)!!.get(
                                                        i
                                                    ).get(GlobalConstants.CODE)!!, ignoreCase = true
                                                )
                                            ) {
                                                listDataChild!!.get(profileCode)!!.get(i)
                                                    .put(GlobalConstants.VALUE, weightValue)
                                            }
                                        }
                                    }

                                    when (profileCode) {
                                        "BMI" -> {
                                            if (!Helper.isNullOrEmpty(editTextValueFeet) && !Helper.isNullOrEmpty(
                                                    editTextValueInch
                                                ) && !Helper.isNullOrEmpty(weightValue)
                                            ) {
                                                finalEditTextValue = CalculateBMI(
                                                    getChildValues(
                                                        profileCode,
                                                        "HEIGHT"
                                                    ), getChildValues(profileCode, "WEIGHT")
                                                )
                                                //finalEditTextValue = String.valueOf(CalculateParametersAndObservations.getBMIFromImperial(Double.parseDouble(editTextValueFeet), Double.parseDouble(editTextValueInch), Double.parseDouble(weightValue)));
                                            }

                                            Log.d(
                                                "UPDATE ADAPTER",
                                                "BMI METRIC afterTextChanged final editTextValue $finalEditTextValue"
                                            )

                                            for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                                if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                                    if ("BMI".equals(
                                                            listDataChild!!.get(profileCode)!!.get(
                                                                i
                                                            ).get(GlobalConstants.CODE)!!,
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        Log.d(
                                                            "UPDATE ADAPTER",
                                                            " METRIC afterTextChanged editTextValue main " + listDataChild!!.get(
                                                                profileCode
                                                            )!!.get(i).get(GlobalConstants.CODE) + " VALUE " + finalEditTextValue
                                                        )
                                                        listDataChild!!.get(profileCode)!!.get(i)
                                                            .put(
                                                                GlobalConstants.VALUE,
                                                                finalEditTextValue
                                                            )
                                                    }
                                                }
                                            }
                                        }

                                        else -> {
                                        }
                                    }//holder.edtChildValues.setTextColor(0x8A000000);
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                        }

                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })

                    holder.edtChildValuesHeightInch.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable) {
                            if (holder.lltUpdateValuesImperial.getVisibility() == View.VISIBLE) {
                                try {
                                    val editTextValueFeet =
                                        holder.edtChildValuesHeightFeet.getText().toString()
                                            .trim({ it <= ' ' })
                                    val editTextValueInch =
                                        holder.edtChildValuesHeightInch.getText().toString()
                                            .trim({ it <= ' ' })
                                    val profileCode = profileInfoModelData.getProfileCode()
                                    val weightValue = getChildValues(
                                        profileCode,
                                        "WEIGHT"
                                    ).get(GlobalConstants.VALUE)
                                    var finalEditTextValue = ""

                                    Log.d(
                                        "UPDATE ADAPTER",
                                        " edtChildValuesHeightInch.addTextChangedListener editTextValueFeet $editTextValueFeet"
                                    )
                                    Log.d(
                                        "UPDATE ADAPTER",
                                        " edtChildValuesHeightInch.addTextChangedListener editTextValueInch $editTextValueInch"
                                    )
                                    Log.d(
                                        "UPDATE ADAPTER",
                                        " edtChildValuesHeightInch.addTextChangedListener weightValue " + weightValue!!
                                    )

                                    for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                        if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                            if ("HEIGHT".equals(
                                                    listDataChild!!.get(profileCode)!!.get(
                                                        i
                                                    ).get(GlobalConstants.CODE)!!, ignoreCase = true
                                                )
                                            ) {
                                                if (!Helper.isNullOrEmpty(editTextValueFeet) && !Helper.isNullOrEmpty(
                                                        editTextValueInch
                                                    )
                                                ) {
                                                    listDataChild!!.get(profileCode)!!.get(i).put(
                                                        GlobalConstants.VALUE,
                                                        Helper.convertFeetInchToCm(
                                                            editTextValueFeet,
                                                            editTextValueInch
                                                        )
                                                    )
                                                }
                                            }
                                            if ("WEIGHT".equals(
                                                    listDataChild!!.get(profileCode)!!.get(
                                                        i
                                                    ).get(GlobalConstants.CODE)!!, ignoreCase = true
                                                )
                                            ) {
                                                listDataChild!!.get(profileCode)!!.get(i)
                                                    .put(GlobalConstants.VALUE, weightValue)
                                            }
                                        }
                                    }

                                    when (profileCode) {
                                        "BMI" -> {
                                            if (!Helper.isNullOrEmpty(editTextValueFeet) && !Helper.isNullOrEmpty(
                                                    editTextValueInch
                                                ) && !Helper.isNullOrEmpty(weightValue)
                                            ) {
                                                finalEditTextValue = CalculateBMI(
                                                    getChildValues(
                                                        profileCode,
                                                        "HEIGHT"
                                                    ), getChildValues(profileCode, "WEIGHT")
                                                )
                                                //finalEditTextValue = String.valueOf(CalculateParametersAndObservations.getBMIFromImperial(Double.parseDouble(editTextValueFeet), Double.parseDouble(editTextValueInch), Double.parseDouble(weightValue)));
                                            }

                                            Log.d(
                                                "UPDATE ADAPTER",
                                                "BMI METRIC afterTextChanged final editTextValue $finalEditTextValue"
                                            )

                                            for (i in 0 until listDataChild!!.get(profileCode)!!.size) {
                                                if (listDataChild!!.get(profileCode)!!.get(i) != null) {
                                                    if ("BMI".equals(
                                                            listDataChild!!.get(profileCode)!!.get(
                                                                i
                                                            ).get(GlobalConstants.CODE)!!,
                                                            ignoreCase = true
                                                        )
                                                    ) {
                                                        Log.d(
                                                            "UPDATE ADAPTER",
                                                            " METRIC afterTextChanged editTextValue main " + listDataChild!!.get(
                                                                profileCode
                                                            )!!.get(i).get(GlobalConstants.CODE) + " VALUE " + finalEditTextValue
                                                        )
                                                        listDataChild!!.get(profileCode)!!.get(i)
                                                            .put(
                                                                GlobalConstants.VALUE,
                                                                finalEditTextValue
                                                            )
                                                    }
                                                }
                                            }
                                        }

                                        else -> {
                                        }
                                    }//holder.edtChildValues.setTextColor(0x8A000000);
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                        }

                        override fun beforeTextChanged(
                            s: CharSequence,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }
                    })
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        */
/* return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();*//*


        val parameterInfoModelArrayList =
            profileInfoModelFromAdapter[listPosition].getParameterInfoModelList()
        Log.d("UPDATE ADPATER", "getChildrenCount  " + parameterInfoModelArrayList.size)
        return parameterInfoModelArrayList.size
    }

    override fun getGroup(listPosition: Int): Any {
        return this.profileInfoModelFromAdapter[listPosition]
    }

    override fun getGroupCount(): Int {
        return this.profileInfoModelFromAdapter.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        //String listTitle = (String) getGroup(listPosition);
        val profileInfoModelData = getGroup(listPosition) as ProfileInfoModel
        Log.d("UPDATE ADAPTER", "getGroupView  " + profileInfoModelData.toString())
        if (convertView == null) {
            val layoutInflater =
                this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(
                R.layout.updateparameter_screen_fragement_expandable_headergroup_adapter,
                null
            )
            val holder = ViewHolderGroup(convertView!!)
            convertView!!.setTag(holder)
        }
        // Get the stored ViewHolder that also contains our views
        val holder = convertView!!.getTag() as ViewHolderGroup
        holder.textCounterHeaderELUP.setText("" + (listPosition + 1))
        // holder.textNameParameterELUP.setText(profileInfoModelData.getProfileName().replace("Cholesterol", "LIPID") + " PROFILE");
        holder.textNameParameterELUP.setText(
            profileInfoModelData.getProfileName().replace(
                "Cholesterol",
                "LIPID"
            )
        )
        if (profileInfoModelData.getRecordDate() != null) {
            if (profileInfoModelData.getRecordDate() != null && !profileInfoModelData.getRecordDate().equalsIgnoreCase(
                    ""
                ) && !profileInfoModelData.getRecordDate().equalsIgnoreCase("null")
            ) {
                holder.textTimeParameterELUP.setText(profileInfoModelData.getRecordDate())
            } else {
                holder.textTimeParameterELUP.setText("")
            }

        } else {
            holder.textTimeParameterELUP.setText("")
        }

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }

    fun getParametersByProfile(profileCode: String?): ArrayList<ArrayMap<String, String>>? {
        return if (listDataChild != null) {
            listDataChild!!.get(profileCode)
        } else ArrayList<ArrayMap<String, String>>()
    }

    fun getParametersByProfile(
        profileCode: String?,
        parameterCode: String?
    ): ArrayMap<String, String>? {
        val data = getParametersByProfile(profileCode)
        if (data != null) {
            for (i in data.indices) {
                if (data[i].get(GlobalConstants.CODE)!!.equals(
                        parameterCode!!,
                        ignoreCase = true
                    )
                ) {
                    return data[i]
                }
            }
        }
        return null
    }

    fun refreshExpandableListUnit(
        groupPosition: Int,
        recordDate: String?,
        profileCode: String?,
        arrayMapChildList: ArrayList<ArrayMap<String, String>>?,
        mapGroupHeader: ArrayMap<String, String>
    ) {
        var arrayMapChildList = arrayMapChildList
        val parameters = getParametersByProfile(profileCode)
        try {
            if (arrayMapChildList == null) {
                arrayMapChildList = ArrayList<ArrayMap<String, String>>()
            }

            for (j in parameters!!.indices) {
                var isFound = false
                parameters[j].put(GlobalConstants.NEED_TO_CONVERT, "true")
                for (k in arrayMapChildList.indices) {
                    val data = arrayMapChildList[k]
                    if (data.get(GlobalConstants.CODE)!!.equals(
                            parameters[j].get(GlobalConstants.CODE)!!,
                            ignoreCase = true
                        )
                    ) {
                        parameters[j].put(GlobalConstants.VALUE, data.get(GlobalConstants.VALUE))
                        //parameters.get(j).put(GlobalConstants.UNIT, data.get(GlobalConstants.UNIT));
                        parameters[j].put(
                            GlobalConstants.TEXT_VALUE,
                            data.get(GlobalConstants.TEXT_VALUE)
                        )
                        parameters[j].put(
                            GlobalConstants.OWNER_CODE,
                            data.get(GlobalConstants.OWNER_CODE)
                        )
                        parameters[j].put(
                            GlobalConstants.ORIGINAL_VALUE,
                            data.get(GlobalConstants.VALUE)
                        )
                        isFound = true
                        break
                    }
                }
                if (!isFound) {
                    parameters[j].put(GlobalConstants.VALUE, "")
                    //parameters.get(j).put(GlobalConstants.UNIT, parameters.get(j).get(GlobalConstants.UNIT));
                    parameters[j].put(GlobalConstants.TEXT_VALUE, "")
                    parameters[j].put(GlobalConstants.OWNER_CODE, "")
                    parameters[j].put(GlobalConstants.ORIGINAL_VALUE, "")
                }
                parameters[j].put(GlobalConstants.RECORD_DATE, recordDate)
            }
            headerList[groupPosition] = mapGroupHeader
            fillObjectModelData()
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun returnChildListAsPerProfileCodeAndRecordDate(
        profileCode: String?,
        recordDate: String?,
        value: String
    ): ArrayList<ArrayMap<String, String>> {

        val mapProfileSingle = ArrayMap<String, String>()
        mapProfileSingle.put(GlobalConstants.FROM, "updateParameterChild")
        mapProfileSingle.put(GlobalConstants.PROFILE_CODE, profileCode)
        mapProfileSingle.put(GlobalConstants.RECORD_DATE, recordDate)
        mapProfileSingle.put(
            GlobalConstants.PERSON_ID,
            SessionManager.GetSessionDetails().get(GlobalConstants.CURRENT_PERSON_ID)
        )
// Check Locally for parameter

        return HealthParametersDBHelper.getRecordsByProfileAndRecordDate(mapProfileSingle)
    }

    fun getChildValues(profileCode: String, parameterCode: String): ArrayMap<String, String> {
        if (listDataChild != null) {
            val vals = listDataChild!!.get(profileCode)
            if (vals != null) {
                for (i in vals!!.indices) {
                    if (vals!!.get(i).get(GlobalConstants.CODE)!!.equals(
                            parameterCode,
                            ignoreCase = true
                        )
                    ) {
                        return vals!!.get(i)
                    }
                }
            }
        }
        return ArrayMap<String, String>()
    }

    fun ValidateField(strValue: String, profileCode: String, paramCode: String): String {

        var parameter = getParametersByProfile(profileCode, paramCode)
        try {
            if (!Helper.isNullOrEmpty(strValue) && !Helper.isNullOrEmpty(
                    parameter!!.get(
                        GlobalConstants.MIN_PERMISSIBLE_VALUE
                    )
                )
                && !Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))
            ) {
                val textValue: Float?
                textValue = java.lang.Float.parseFloat(strValue)
                if (parameter!!.containsKey(GlobalConstants.MIN_PERMISSIBLE_VALUE) == false
                    || parameter!!.containsKey(GlobalConstants.MAX_PERMISSIBLE_VALUE) == false
                    || Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE))
                    || Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))
                ) {
                    //isValidParamterValue = false;
                    //  SaveTrackParameterValues.strValidationResult = "Some parameters are missing";
                } else {
                    */
/* Float MinPermisibleVal = Float.parseFloat(parameter.get(GlobalConstants.MIN_PERMISSIBLE_VALUE));
                    Float MaxPermisibleVal = Float.parseFloat(parameter.get(GlobalConstants.MAX_PERMISSIBLE_VALUE));*//*

                    var MinPermisibleVal: Float? = 0f
                    var MaxPermisibleVal: Float? = 0f
                    if (!paramCode.equals(
                            "HEIGHT",
                            ignoreCase = true
                        ) && mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                    ) {
                        MinPermisibleVal = java.lang.Float.parseFloat(
                            Helper.convertToImperialFormat(
                                mSharedPreference.getDefaultUnit(),
                                paramCode,
                                parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE)
                            )
                        )
                        MaxPermisibleVal = java.lang.Float.parseFloat(
                            Helper.convertToImperialFormat(
                                mSharedPreference.getDefaultUnit(),
                                paramCode,
                                parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE)
                            )
                        )
                    } else {

                        MinPermisibleVal =
                            java.lang.Float.parseFloat(parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE)!!)
                        MaxPermisibleVal =
                            java.lang.Float.parseFloat(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE)!!)
                    }

                    if (paramCode.equals("HEIGHT", ignoreCase = true)) {
                        mHeight = textValue
                    }

                    if (paramCode.equals("WEIGHT", ignoreCase = true)) {
                        mWeight = textValue
                    }

                    if (paramCode.equals("WAIST", ignoreCase = true)) {
                        mWaist = textValue
                    }

                    if (paramCode.equals("HIP", ignoreCase = true)) {
                        mHip = textValue
                    }

                    if (paramCode.equals("BP_SYS", ignoreCase = true)) {
                        mBPSystolic = textValue
                    }

                    if (paramCode.equals("BP_DIA", ignoreCase = true)) {
                        mBPDiastolic = textValue
                    }


                    if (textValue < MinPermisibleVal || textValue > MaxPermisibleVal) {
                        println(MaxPermisibleVal)
                        println(MinPermisibleVal)
                        TrackHealthParameterUpdateFragment.isValidParamterValue = false
                        if (paramCode.equals(
                                "HEIGHT",
                                ignoreCase = true
                            ) && mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                        ) {
                            TrackHealthParameterUpdateFragment.strValidationResult =
                                "Please enter " + parameter!!.get(GlobalConstants.DESCRIPTION) + " values between 4 feet - 7 feet"
                        } else {
                            TrackHealthParameterUpdateFragment.strValidationResult =
                                "Please enter " + parameter!!.get(GlobalConstants.DESCRIPTION) + " values between " + MinPermisibleVal + " - " + MaxPermisibleVal
                        }
                    } else if ((profileCode.equals(
                            "BP",
                            ignoreCase = true
                        ) || profileCode.equals("BLOODPRESSURE", ignoreCase = true))
                        && mBPSystolic > 0 && mBPDiastolic > 0 && mBPSystolic < mBPDiastolic
                    ) {
                        TrackHealthParameterUpdateFragment.isValidParamterValue = false
                        TrackHealthParameterUpdateFragment.strValidationResult =
                            "Systolic BP should not less than diastolic BP"
                    }
                }
            } else {
                if (paramCode == "HEIGHT" || paramCode == "WEIGHT" || paramCode == "WAIST" || paramCode == "HIP" || paramCode == "BP_SYS" || paramCode == "BP_DIA") {
                    TrackHealthParameterUpdateFragment.isValidParamterValue = false
                    TrackHealthParameterUpdateFragment.strValidationResult =
                        "Please Enter Value for " + parameter!!.get(GlobalConstants.DESCRIPTION)!!
                } else {
                    //                    TrackHealthParameterUpdateFragment.isValidParamterValue = true;
                }
            }//            else if(!Helper.isNullOrEmpty(strValue) && !Helper.isNullOrEmpty(parameter.get(GlobalConstants.MIN_PERMISSIBLE_VALUE))
            //                    && !Helper.isNullOrEmpty(parameter.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))){}
            //            else if(!Helper.isNullOrEmpty(strValue) && !Helper.isNullOrEmpty(parameter.get(GlobalConstants.MIN_PERMISSIBLE_VALUE))
            //                    && !Helper.isNullOrEmpty(parameter.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))){}
            //            else if(!Helper.isNullOrEmpty(strValue) && !Helper.isNullOrEmpty(parameter.get(GlobalConstants.MIN_PERMISSIBLE_VALUE))
            //                    && !Helper.isNullOrEmpty(parameter.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))){}
        } catch (e: Exception) {
            e.printStackTrace()
            println("Validation: $e")
        } finally {
            parameter = null
        }

        return TrackHealthParameterUpdateFragment.strValidationResult

    }

    fun ValidateFieldErrorMsg(strValue: String, profileCode: String, paramCode: String): String? {
        var errorMessage: String? = null
        var parameter = getParametersByProfile(profileCode, paramCode)
        try {
            if (!Helper.isNullOrEmpty(strValue) && !Helper.isNullOrEmpty(
                    parameter!!.get(
                        GlobalConstants.MIN_PERMISSIBLE_VALUE
                    )
                )
                && !Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))
            ) {
                val textValue: Float?
                textValue = java.lang.Float.parseFloat(strValue)
                if (!parameter!!.containsKey(GlobalConstants.MIN_PERMISSIBLE_VALUE)
                    || !parameter!!.containsKey(GlobalConstants.MAX_PERMISSIBLE_VALUE)
                    || Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE))
                    || Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))
                ) {
                } else {
                    var MinPermisibleVal: Float? = 0f
                    var MaxPermisibleVal: Float? = 0f
                    if (!paramCode.equals(
                            "HEIGHT",
                            ignoreCase = true
                        ) && mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                    ) {
                        MinPermisibleVal = java.lang.Float.parseFloat(
                            Helper.convertToImperialFormat(
                                mSharedPreference.getDefaultUnit(),
                                paramCode,
                                parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE)
                            )
                        )
                        MaxPermisibleVal = java.lang.Float.parseFloat(
                            Helper.convertToImperialFormat(
                                mSharedPreference.getDefaultUnit(),
                                paramCode,
                                parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE)
                            )
                        )
                    } else {

                        MinPermisibleVal =
                            java.lang.Float.parseFloat(parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE)!!)
                        MaxPermisibleVal =
                            java.lang.Float.parseFloat(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE)!!)
                    }

                    if (paramCode.equals("BP_SYS", ignoreCase = true)) {
                        mBPSystolic = textValue
                    }

                    if (paramCode.equals("BP_DIA", ignoreCase = true)) {
                        mBPDiastolic = textValue
                    }

                    if (textValue < MinPermisibleVal || textValue > MaxPermisibleVal) {
                        println(MaxPermisibleVal)
                        println(MinPermisibleVal)
                        if (paramCode.equals(
                                "HEIGHT",
                                ignoreCase = true
                            ) && mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                        ) {
                            errorMessage =
                                "Please enter " + parameter!!.get(GlobalConstants.DESCRIPTION) + " values between 4 feet - 7 feet"
                        } else {
                            errorMessage =
                                "Please enter " + parameter!!.get(GlobalConstants.DESCRIPTION) + " values between " + MinPermisibleVal + " - " + MaxPermisibleVal
                        }
                    } else if ((profileCode.equals(
                            "BP",
                            ignoreCase = true
                        ) || profileCode.equals("BLOODPRESSURE", ignoreCase = true))
                        && mBPSystolic > 0 && mBPDiastolic > 0 && mBPSystolic < mBPDiastolic
                    ) {
                        errorMessage = "Systolic BP should not less than diastolic BP"
                    }
                }
            } else {

            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Validation: $e")
        } finally {
            parameter = null
        }

        return errorMessage

    }

    fun ValidateFieldOnExit(
        edtChildValues: EditText,
        groupPosition: Int,
        childPosition: Int,
        holder: ViewHolderChild,
        paramMap: ArrayMap<String, String>,
        parameterInfoModelData: ParameterInfoModel
    ): Boolean {
        //TrackHealthParameterUpdateFragment.buttonAddParameterUP.setVisibility(View.GONE);
        var editTextValue: String? = edtChildValues.getText().toString()


        //String tagValue = holder.edtChildValues.getTag().toString().trim();
        //String[] groupPart = tagValue.split("-");
        //int groupPosition = Integer.parseInt(groupPart[1]);
        //int childPosition = Integer.parseInt(groupPart[2]);
        val profileCode = paramMap.get(GlobalConstants.PROFILE_CODE)
        val parameterCode = paramMap.get(GlobalConstants.CODE)

        var parameter = getParametersByProfile(profileCode, parameterCode)
        val paramCode = parameter!!.get(GlobalConstants.CODE)
        val strUnit = parameter!!.get(GlobalConstants.UNIT)
        var isValid = true
        var finalValue = ""
        try {
            println("ValidateFieldOnExit  " + editTextValue!!)
            if (paramCode!!.equals("HEIGHT", ignoreCase = true)) {
                if (parameterInfoModelData.getUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)) {
                    editTextValue = Helper.convertFeetInchToCm(
                        holder.edtChildValuesHeightFeet.getText().toString(),
                        holder.edtChildValuesHeightFeet.getText().toString()
                    )
                }
            }
            if (editTextValue != null && !editTextValue.equals("", ignoreCase = true)) {
                val textValue: Float?
                //childValues.put(GlobalConstants.MAX_PERMISSIBLE_VALUE,"");
                try {
                    textValue = java.lang.Float.parseFloat(editTextValue)
                    //  parameter.put(GlobalConstants.VALUE, editTextValue);

                    //System.out.println(editTextValue);
                    //if (editTextValue != null && !editTextValue.equalsIgnoreCase("")) {
                    try {
                        //int textValue = (int) Float.parseFloat(editTextValue);
                        finalValue = editTextValue
                        parameter!!.put(GlobalConstants.VALUE, editTextValue)
                        parameter!!.put(GlobalConstants.UNIT, paramMap.get(GlobalConstants.UNIT))
                        headerList[groupPosition].put(
                            GlobalConstants.UNIT,
                            paramMap.get(GlobalConstants.UNIT)
                        )
                        if (!parameter!!.get(GlobalConstants.VALUE)!!.equals(
                                parameter!!.get(
                                    GlobalConstants.ORIGINAL_VALUE
                                )!!, ignoreCase = true
                            )
                        ) {
                            modifiedParameters.put(parameterCode, true)
                        } else {
                            modifiedParameters.put(parameterCode, false)
                        }
                        //////////mapParameterChild.set(groupPosition, mapParameterChildLocal);
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {

                    }

                    if (!parameter!!.containsKey(GlobalConstants.MIN_PERMISSIBLE_VALUE)
                        || !parameter!!.containsKey(GlobalConstants.MAX_PERMISSIBLE_VALUE)
                        || Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE))
                        || Helper.isNullOrEmpty(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE))
                    ) {
                        headerList[groupPosition].put(GlobalConstants.IS_VALID, "F")
                        isValid = false
                        //TrackHealthParameterUpdateFragment.buttonSaveUp.setEnabled(false);
                        //TrackHealthParameterUpdateFragment.buttonSaveUp.setBackgroundResource(R.drawable.round_corner_right_top_bottom_grey_button);
                        //DialogHelper.showNoticeDialog("Unable to process", "There seems some error validating the entry. Please logout, login and try again", 1, fragment.getActivity(), "");
                    } else {
                        */
/* Float MinPermisibleVal = Float.parseFloat(parameter.get(GlobalConstants.MIN_PERMISSIBLE_VALUE));
                        Float MaxPermisibleVal = Float.parseFloat(parameter.get(GlobalConstants.MAX_PERMISSIBLE_VALUE));*//*

                        var MinPermisibleVal: Float? = 0f
                        var MaxPermisibleVal: Float? = 0f
                        if (!paramCode!!.equals(
                                "HEIGHT",
                                ignoreCase = true
                            ) && !Helper.isNullOrEmpty(mSharedPreference.getDefaultUnit())
                            && mSharedPreference.getDefaultUnit().equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                        ) {
                            MinPermisibleVal = java.lang.Float.parseFloat(
                                Helper.convertToImperialFormat(
                                    mSharedPreference.getDefaultUnit(),
                                    paramCode,
                                    parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE)
                                )
                            )
                            MaxPermisibleVal = java.lang.Float.parseFloat(
                                Helper.convertToImperialFormat(
                                    mSharedPreference.getDefaultUnit(),
                                    paramCode,
                                    parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE)
                                )
                            )
                        } else {
                            MinPermisibleVal =
                                java.lang.Float.parseFloat(parameter!!.get(GlobalConstants.MIN_PERMISSIBLE_VALUE)!!)
                            MaxPermisibleVal =
                                java.lang.Float.parseFloat(parameter!!.get(GlobalConstants.MAX_PERMISSIBLE_VALUE)!!)
                        }

                        if (textValue >= MinPermisibleVal && textValue <= MaxPermisibleVal) {


                            //if (parameter.containsKey(GlobalConstants.VALUE) &&
                            //		!parameter.get(GlobalConstants.VALUE).equalsIgnoreCase(editTextValue)) {

                            */
/*childValues.put(GlobalConstants.CHANGEORNOT, "true");*//*

                            //mapParameterChildLocal.set(childPosition, childValues);
                            /////////mapParameterChild.set(groupPosition, mapParameterChildLocal);

                            //mapGroupHeader.put(GlobalConstants.CHANGEORNOT, GlobalConstants.TRUE);
                            //mapProfileHeader.set(groupPosition, mapGroupHeader);

                            //}
                            TrackHealthParameterUpdateFragment.buttonSaveUp.setEnabled(true)
                            //TrackHealthParameterUpdateFragment.buttonSaveUp.setBackgroundResource(R.drawable.round_corner_right_top_bottom_button);
                            //                            holder.edtChildValues.setError(null);
                            headerList[groupPosition].put(GlobalConstants.IS_VALID, "T")
                            isValid = true
                        } else {

                            //((EditText) v).getText().clear();
                            println(MaxPermisibleVal)
                            println(MinPermisibleVal)
                            //
                            headerList[groupPosition].put(GlobalConstants.IS_VALID, "F")
                            //TrackHealthParameterUpdateFragment.buttonSaveUp.setEnabled(false);
                            //TrackHealthParameterUpdateFragment.buttonSaveUp.setBackgroundResource(R.drawable.round_corner_right_top_bottom_grey_button);

                            //DialogHelper.showNoticeDialog("Invalid Value", "Please Enter Values between " + MinPermisibleVal + " - " + MaxPermisibleVal, 1, _context, "");
                            //holder.edtChildValues.setError("Please Enter Values between " + MinPermisibleVal + " - " + MaxPermisibleVal);
                            isValid = false
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                if (paramCode == "HEIGHT" || paramCode == "WEIGHT" || paramCode == "WAIST" || paramCode == "HIP") {
                    headerList[groupPosition].put(GlobalConstants.IS_VALID, "F")
                    isValid = false
                    //TrackHealthParameterUpdateFragment.buttonSaveUp.setEnabled(false);
                    //TrackHealthParameterUpdateFragment.buttonSaveUp.setBackgroundResource(R.drawable.round_corner_right_top_bottom_grey_button);
                    //DialogHelper.showNoticeDialog("Invalid Value", , 1, _context, "");
                    //                    holder.edtChildValues.setError(null);
                    //  editText.setError("Please Enter Value for " + parameter.get(GlobalConstants.DESCRIPTION));

                } else {

                }
                //	((EditText) v).setText("");
            }

            println(" ValidateFieldOnExit $profileCode profileCod: $paramCode")
            if (profileCode == "BMI") {
                val BMI = CalculateBMI(
                    getChildValues(profileCode, "HEIGHT"),
                    getChildValues(profileCode, "WEIGHT")
                )
                finalValue = BMI
                if (!Helper.isNullOrEmpty(BMI)) {
                    if (edtChildValues != null) {
                        //holder.edtChildValues.setText(BMI);
                        //holder.edtChildValues.setTextColor(Color.BLACK);
                        var summParameter = getParametersByProfile("BMI", "BMI")
                        if (summParameter != null) {
                            summParameter!!.put(
                                GlobalConstants.VALUE,
                                edtChildValues.getText().toString()
                            )
                        }
                        summParameter = null
                    }

                }
            } else if (profileCode == "WHR") {
                val whr = CalculateWHR(
                    strUnit,
                    getChildValues(profileCode, "WAIST"),
                    getChildValues(profileCode, "HIP")
                )
                finalValue = whr
                if (!Helper.isNullOrEmpty(whr)) {
                    if (edtChildValues != null) {
                        //holder.edtChildValues.setText(whr);
                        //holder.edtChildValues.setTextColor(Color.BLACK);
                        */
/*parameterInfoModelData.setValue(finalValue);
                        notifyDataSetChanged();*//*

                        var summParameter = getParametersByProfile("WHR", "WHR")
                        if (summParameter != null) {
                            summParameter!!.put(
                                GlobalConstants.VALUE,
                                edtChildValues.getText().toString()
                            )
                        }
                        summParameter = null
                    }
                }
            } else if (profileCode == "LIVER") {
                val liver = CalculateAGRatio(
                    getChildValues(profileCode, "ALBUMIN"),
                    getChildValues(profileCode, "GLOBULLIN")
                )
                finalValue = liver
                if (!Helper.isNullOrEmpty(liver)) {
                    if (edtChildValues != null) {
                        //holder.edtChildValues.setText(liver);
                        //holder.edtChildValues.setTextColor(Color.BLACK);
                        */
/*parameterInfoModelData.setValue(finalValue);
                        notifyDataSetChanged();*//*

                        var summParameter = getParametersByProfile("LIVER", "AGRATIO")
                        if (summParameter != null) {
                            summParameter!!.put(
                                GlobalConstants.VALUE,
                                edtChildValues.getText().toString()
                            )
                        }
                        summParameter = null
                    }
                }
            }

            //TrackHealthParameterUpdateFragment.buttonAddParameterUP.setVisibility(View.GONE);
        } finally {
            parameter = null
        }
        */
/*ProfileInfoModel profileInfoModel = (ProfileInfoModel) getGroup(groupPosition);
        parameterInfoModelData.setValue(finalValue);
        profileInfoModel.getParameterInfoModelList().get(getChild(groupPosition, childPosition));*//*

        try {
            */
/*for (int i = 0; i < listDataChild.get(profileCode).size(); i++) {
                if (parameterCode.equalsIgnoreCase(listDataChild.get(parameterCode).get(i).get(GlobalConstants.CODE))) {
                    Log.d("UPDATE ADAPTER", " ValidateFieldOnExit inside loop CODE " + listDataChild.get(parameterCode).get(i).get(GlobalConstants.CODE) + " VALUE " + finalValue);
                    listDataChild.get(parameterCode).get(i).put(GlobalConstants.VALUE, finalValue);
                }
            }*//*

            fillObjectModelData()
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //((EditText) v).setText("");
        return isValid
    }

    private fun CalculateBMI(
        heightParam: ArrayMap<String, String>,
        weightParam: ArrayMap<String, String>
    ): String {
        var height: String? = ""
        var weight: String? = ""
        var bmi: Double? = 0.0
        var bmiAsString = ""
        val strUnit = mSharedPreference.getDefaultUnit()
        try {

            height = heightParam.get(GlobalConstants.VALUE)
            Log.d("UPDATE ADAPTER", "  CalculateBMI strUnit $strUnit")
            Log.d("UPDATE ADAPTER", "  CalculateBMI height " + height!!)
            weight = weightParam.get(GlobalConstants.VALUE)
            Log.d("UPDATE ADAPTER", "  CalculateBMI before weight " + weight!!)
            if (weightParam.get(GlobalConstants.NEED_TO_CONVERT)!!.equals(
                    "false",
                    ignoreCase = true
                )
            ) {
                if (strUnit.equals(GlobalConstants.UNIT_IMPERIAL, ignoreCase = true)) {
                    //                height = Helper.convertToMetricFormat(strUnit, "HEIGHT", heightParam.get(GlobalConstants.VALUE));
                    weight = Helper.convertToMetricFormat(
                        strUnit,
                        "WEIGHT",
                        weightParam.get(GlobalConstants.VALUE)
                    )
                }
            }
            Log.d("UPDATE ADAPTER", "  CalculateBMI weight after" + weight!!)

            if (Helper.isNullOrEmpty(height)) {
                return ""
            } else if (Helper.isNullOrEmpty(weight)) {
                return ""
            } else {
                try {
                    val weightValue = java.lang.Float.parseFloat(weight!!)
                    val heightValue = java.lang.Float.parseFloat(height!!)
                    */
/* if ((!Helper.isNullOrEmpty(strUnit) && strUnit.equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                            && heightValue >= 121 && heightValue <= 214 && weightValue >= 66 && weightValue <= 551)
                            || (!Helper.isNullOrEmpty(strUnit) && strUnit.equalsIgnoreCase(GlobalConstants.UNIT_METRIC)
                            && heightValue >= 121 && heightValue <= 214 && weightValue >= 30 && weightValue <= 250)) {*//*

                    if (weightValue >= GlobalConstants.WEIGHT_MIN_METRIC && weightValue <= GlobalConstants.WEIGHT_MAX_METRIC && heightValue >= GlobalConstants.HEIGHT_MIN && heightValue <= GlobalConstants.HEIGHT_MAX) {
                        bmi = weightValue / Math.pow((heightValue / 100).toDouble(), 2.0)
                        bmiAsString = DecimalFormat("##.##").format(bmi).toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            height = null
            weight = null
        }
        return bmiAsString
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun CalculateWHR(
        strUnit: String?,
        waistParam: ArrayMap<String, String>,
        hipParam: ArrayMap<String, String>
    ): String {
        var waist: String? = ""
        var hip: String? = ""
        var whr = 0f
        var whrAsString = ""

        try {
            if (strUnit!!.equals(GlobalConstants.UNIT_IMPERIAL, ignoreCase = true)) {
                waist = Helper.convertToMetricFormat(
                    strUnit,
                    "WAIST",
                    waistParam.get(GlobalConstants.VALUE)
                )
                hip = Helper.convertToMetricFormat(
                    strUnit,
                    "HIP",
                    hipParam.get(GlobalConstants.VALUE)
                )
            } else {
                waist = waistParam.get(GlobalConstants.VALUE)
                hip = hipParam.get(GlobalConstants.VALUE)
            }

            if (Helper.isNullOrEmpty(waist)) {
                return ""
            } else if (Helper.isNullOrEmpty(hip)) {
                return ""
            } else {
                try {
                    val waistValue = java.lang.Float.parseFloat(waist!!)
                    val hipValue = java.lang.Float.parseFloat(hip!!)
                    if (waistValue >= 63.5 && waistValue <= 165.1 && hipValue >= 63.5 && hipValue <= 165.1) {
                        whr = waistValue / hipValue
                        val nm = NumberFormat.getNumberInstance()
                        //whrAsString=String.valueOf(whr);
                        //whrAsString=(nm.format(whr));
                        whrAsString = DecimalFormat("##.##").format(whr.toDouble()).toString()
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            waist = null
            hip = null
        }
        return whrAsString
    }

    private fun CalculateAGRatio(
        albuminParam: ArrayMap<String, String>,
        globulinParam: ArrayMap<String, String>
    ): String {
        var albumin: String? = ""
        var globulin: String? = ""
        var agratio = 0f
        var agratioAsString = ""

        try {
            albumin = albuminParam.get(GlobalConstants.VALUE)
            globulin = globulinParam.get(GlobalConstants.VALUE)

            if (Helper.isNullOrEmpty(albumin)) {
                return ""
            } else if (Helper.isNullOrEmpty(globulin)) {
                return ""
            } else {
                try {
                    agratio = Math.round(
                        java.lang.Float.parseFloat(albumin!!) / java.lang.Float.parseFloat(globulin!!)
                    ).toFloat()
                    val nm = NumberFormat.getNumberInstance()
                    agratioAsString = DecimalFormat("##.##").format(agratio.toDouble()).toString()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            albumin = null
            globulin = null
        }
        println("agratioAsString: $agratioAsString")
        return agratioAsString
    }

    fun updateListExpandable(map: ArrayMap<String, String>) {
        */
/*String[] groupPart = valueTagDateEditText.split("-");
        int groupPosition = Integer.parseInt(groupPart[1]);
        ArrayMap<String, String> mapGroupHeader = (ArrayMap<String, String>) this.getGroup(groupPosition);

        String profileCode = mapGroupHeader.get(GlobalConstants.PROFILE_CODE);
        // creating new map to fatch data from database
        //
        ArrayList<ArrayMap<String, String>> arrayMapChildList = returnChildListAsPerProfileCodeAndRecordDate(profileCode, map.get(GlobalConstants.RECORD_DATE), "1");
        mapGroupHeader.put(GlobalConstants.RECORD_DATE, map.get(GlobalConstants.RECORD_DATE));
        //refreshExpandableList(groupPosition, map.get(GlobalConstants.RECORD_DATE), profileCode, arrayMapChildList, mapGroupHeader);
        refreshExpandableListUnit(groupPosition, map.get(GlobalConstants.RECORD_DATE), profileCode, arrayMapChildList, mapGroupHeader, strUnit);*//*

    }

    internal inner class ViewHolderGroup(convertView: View) {
        var textCounterHeaderELUP: TextView
        var textNameParameterELUP: TextView
        var textTimeParameterELUP: TextView

        initBase {
            textCounterHeaderELUP = convertView.findViewById(R.id.textCounterHeaderELUP) as TextView
            textNameParameterELUP = convertView.findViewById(R.id.textNameParameterELUP) as TextView
            textTimeParameterELUP = convertView.findViewById(R.id.textTimeParameterELUP) as TextView
            textCounterHeaderELUP.setTypeface(TypeFace.Medium)
            textNameParameterELUP.setTypeface(TypeFace.Medium)
            textTimeParameterELUP.setTypeface(TypeFace.Medium)
        }
    }

    internal inner class ViewHolderChild(convertView: View) {
        var rlParameterDate: RelativeLayout
        var txtChildLabel: TextView
        var txtDateChildELUP: TextView
        var edtChildValuesHeightFeet: EditText
        var edtChildValuesHeightInch: EditText
        var edtChildValues: EditText
        var spinnerUnit: Spinner
        var lltUpdateValues: LinearLayout
        var lltUpdateValuesImperial: LinearLayout

        initBase {
            rlParameterDate = convertView.findViewById(R.id.rlParameterDate) as RelativeLayout
            lltUpdateValues = convertView.findViewById(R.id.lltUpdateValues) as LinearLayout
            lltUpdateValuesImperial =
                convertView.findViewById(R.id.lltUpdateValuesImperial) as LinearLayout
            txtChildLabel = convertView.findViewById(R.id.txtChildLabel) as TextView
            txtDateChildELUP = convertView.findViewById(R.id.txtDateChildELUP) as TextView
            edtChildValues = convertView.findViewById(R.id.edtChildValues) as EditText
            edtChildValuesHeightFeet =
                convertView.findViewById(R.id.edtChildValuesHeightFeet) as EditText
            edtChildValuesHeightInch =
                convertView.findViewById(R.id.edtChildValuesHeightInch) as EditText
            spinnerUnit = convertView.findViewById(R.id.spn_unit) as Spinner
        }
    }
}*/
