package com.caressa.common.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.caressa.common.R

class SpinnerAdapter(var context: Context, list: ArrayList<SpinnerModel>) : BaseAdapter() {

    var inflter: LayoutInflater? = null
    var dataList: ArrayList<SpinnerModel> = ArrayList()
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

    override fun getView(i: Int, v: View?, viewGroup: ViewGroup): View {
        var view = v
        try {
            view = inflter!!.inflate(R.layout.custom_spinner_item, null)
            val constraintLayout =
                view!!.findViewById<View>(R.id.spinner_container) as ConstraintLayout?
            val names = view.findViewById<View>(R.id.txt_spinner_name) as TextView?
            val view1 = view.findViewById(R.id.view1) as View?
            names!!.setText(dataList[i].name)
            if (selectedPos == i) {
                view1!!.visibility = View.VISIBLE
                constraintLayout!!.setBackgroundColor(context.resources.getColor(R.color.vivant_pale_grey))
            } else {
                view1!!.visibility = View.INVISIBLE
                constraintLayout!!.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view!!
    }

    fun updateList(list: ArrayList<SpinnerModel>) {
        dataList = list
        notifyDataSetChanged()
    }

    init {
        dataList = list
        inflter = LayoutInflater.from(context)
    }
}