package com.caressa.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.model.medication.DrugsModel

class DrugsListAdapter(mContext: Context, private var dataList: List<DrugsModel.DrugsResponse.Drug>?) :
    ArrayAdapter<Any>(mContext, 0, dataList!!){

    override fun getCount(): Int {
        return dataList?.size!!
    }

    override fun getItem(position: Int): DrugsModel.DrugsResponse.Drug {
        return dataList!![position]
    }

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.auto_complete_textview_layout, parent, false)
        }
        val item = getItem(position)

        val strName = view!!.findViewById(R.id.text_item) as TextView

        if ( !Utilities.isNullOrEmpty(item.strength) ) {
            strName.text = item.name + " - " + item.strength
        } else {
            strName.text = item.name
        }

        return view
    }

    fun updateData(list: List<DrugsModel.DrugsResponse.Drug>){
        dataList = list
        notifyDataSetChanged()
    }
}



