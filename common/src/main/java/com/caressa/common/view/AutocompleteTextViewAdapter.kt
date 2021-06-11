package com.caressa.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.caressa.common.R

class AutocompleteTextViewAdapter(private val mContext: Context, private var dataList: List<AutocompleteTextViewModel>?) :
    ArrayAdapter<Any>(mContext, 0, dataList!!){

    override fun getCount(): Int {
        return dataList?.size!!
    }

    override fun getItem(position: Int): AutocompleteTextViewModel? {
        return dataList!![position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.auto_complete_textview_layout, parent, false)
        }
        val item = getItem(position)

        val strName = view!!.findViewById(R.id.text_item) as TextView
        strName.setText(item!!.name)
        return view
    }

    fun updateData(list: List<AutocompleteTextViewModel>){
        dataList = list
        notifyDataSetChanged()
    }
}