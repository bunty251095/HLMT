package com.caressa.track_parameter.adapter

import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.view.LayoutInflater
import com.caressa.track_parameter.R
import timber.log.Timber
import android.app.DatePickerDialog
import android.text.InputFilter
import android.text.InputType
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.DecimalValueFilter
import com.caressa.model.entity.TrackParameterMaster
import java.util.*


class ParamUpdateExpandableAdapter(val dataList: List<UpdateParamDataList>,
                                    internal var context: Context) :
    BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return dataList.get(groupPosition)
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val header = getGroup(groupPosition) as UpdateParamDataList
        Timber.i("Child List Size => "+header.childList.size)
        var cv = convertView
        if (cv == null) {
            val infalInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = infalInflater.inflate(R.layout.update_param_group_listitem, null)
        }

        val lblListHeader = cv?.findViewById(R.id.textNameParameterELUP) as TextView
        val lblCounter = cv?.findViewById(R.id.textCounterHeaderELUP) as TextView
        val lblDateTime = cv?.findViewById(R.id.textTimeParameterELUP) as TextView
        val imgArrow = cv?.findViewById(R.id.imgArrow) as AppCompatImageView
        lblCounter.text = (groupPosition+1).toString()
        lblListHeader.text = header.headerName
        lblDateTime.text = header.date
        if (isExpanded)
            imgArrow.setImageResource(R.drawable.ic_expand_less)
        else
            imgArrow.setImageResource(R.drawable.ic_expand_more)

        return cv!!
    }

    override fun getChildrenCount(groupPosition: Int): Int = dataList.get(groupPosition).childList.size

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return dataList.get(groupPosition).childList.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong();
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        val item = getChild(groupPosition, childPosition) as TrackParameterMaster.Parameter
        var cv = convertView
        if (cv == null) {
            val layoutInflater = this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = layoutInflater.inflate(R.layout.update_param_child_listitem, null)
        }

        val expandedListTextView = cv?.findViewById(R.id.editTextDateChildELUP) as EditText
        val dateLayout = cv?.findViewById(R.id.dateLayout) as RelativeLayout
        val imgCalendar = cv?.findViewById(R.id.imgCalender) as ImageView

        if(childPosition == 0) {
            dateLayout.visibility = View.VISIBLE
            expandedListTextView.setText(DateHelper.currentDateAsStringddMMMyyyy)
            expandedListTextView.setOnClickListener {v ->
                showDatePickerDialog(expandedListTextView, groupPosition)
            }
            imgCalendar.setOnClickListener { v ->
                Timber.i("sdfffffff")
                showDatePickerDialog(expandedListTextView, groupPosition)
            }
        }else {
            dateLayout.visibility = View.GONE
        }
        val txtLable = cv?.findViewById(R.id.txtChildLabel) as TextView
        val edtParamValue = cv?.findViewById(R.id.edtChildValues) as EditText
            if (showDecimalType(item.profileName.toString()))
            {
                edtParamValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
//                edtParamValue.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED) //for positive or negative values
            }else{
                edtParamValue.inputType = InputType.TYPE_CLASS_TEXT
            }
            txtLable.text = item.description

            DecimalValueFilter().setDigits(1)

            DecimalValueFilter(true).setDigits(1)
            edtParamValue.setText(item.paramValue)

        edtParamValue.setOnFocusChangeListener { v, hasFocus ->
            Timber.i("Updated Value :: "+edtParamValue.text)
        }
        return cv!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int = dataList.size

    fun showDecimalType(code: String): Boolean{
        if (code.equals("Urine Profile",true))
            return false
        else
            return true
    }

    /*fun addField(profileName:String, layout: LinearLayout) {
        val et = AppCompatEditText(ContextThemeWrapper(context, R.style.CustomEditText))
        val p = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        et.layoutParams = p
        et.setHint(profileName)
        et.inputType = InputType.TYPE_CLASS_NUMBER
        et.background = context.resources.getDrawable(R.drawable.edittext_border)
        layout.addView(et)
    }*/

    private fun showDatePickerDialog(
        expandedListTextView: EditText,
        groupPosition: Int
    ) {
        Timber.i("showDatePickerDialog :: "+groupPosition)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener {    view, year, monthOfYear, dayOfMonth ->

                val _monthOf = returnTwoDigitFromDate(monthOfYear + 1)
                val _monthOfDay = returnTwoDigitFromDate(dayOfMonth)

                val appointMentDate = "$year-$_monthOf-$_monthOfDay"
                val strDate = DateHelper.formatDateValue(appointMentDate)

                expandedListTextView.setText(strDate)

                dataList.get(groupPosition).date = strDate!!

            },year,month,day
        )
        dpd.setTitle("Pick record date")
        dpd.datePicker.setMaxDate(c.timeInMillis)
        dpd.show()
    }

    fun returnTwoDigitFromDate(date: Int): String {
        var twoDigit = "" + date
        if (date < 10) {
            twoDigit = "0$twoDigit"
        }
        return twoDigit
    }
}
