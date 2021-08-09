package com.caressa.track_parameter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.track_parameter.R

class ParameterSpinnerAdapter(var context: Context) : BaseAdapter() {

    var inflter: LayoutInflater? = null
    var dataList: List<TrackParameterMaster.History> = ArrayList()
    var selectedPos = 0

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(position: Int, v: View?, viewGroup: ViewGroup): View {
        var view = v
        try {
            view = inflter!!.inflate(com.caressa.common.R.layout.custom_spinner_item, null)
            val constraintLayout = view!!.findViewById<View>(com.caressa.common.R.id.spinner_container) as ConstraintLayout?
            val names = view.findViewById<View>(com.caressa.common.R.id.txt_spinner_name) as TextView?
            val view1 = view.findViewById(com.caressa.common.R.id.view1) as View?
            names!!.setText(dataList.get(position).description)
            if (selectedPos == position) {
                view1!!.visibility = View.VISIBLE
                constraintLayout!!.setBackgroundColor(context.resources.getColor(com.caressa.common.R.color.vivant_pale_grey))
            } else {
                view1!!.visibility = View.INVISIBLE
                constraintLayout!!.setBackgroundColor(context.resources.getColor(com.caressa.common.R.color.white))
            }
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
        return view!!
    }

    fun updateList(list: List<TrackParameterMaster.History>) {
        dataList = filterListData(list)
        notifyDataSetChanged()
    }

    private fun filterListData(list: List<TrackParameterMaster.History>): List<TrackParameterMaster.History> {
        val filterList:MutableList<TrackParameterMaster.History> = mutableListOf()
        filterList.addAll(list)
        lateinit var pulse:TrackParameterMaster.History
        var isPulse: Boolean = false
        for(item in list){
            if (item.parameterCode.equals("BP_SYS",true)){
                filterList.clear()
                val bpItem = item
                bpItem.description = context.resources.getString(R.string.BLOOD_PRESSURE)
                filterList.add(bpItem)
            }
            if (item.parameterCode.equals("BP_PULSE",true)){
                pulse = item
                isPulse = true
            }
        }
        if(isPulse)
            filterList.add(pulse)

        return filterList
    }

    init {
        inflter = LayoutInflater.from(context)
    }
}