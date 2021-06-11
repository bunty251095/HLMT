package com.caressa.tools_calculators.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.model.HypertensionResultPojo
import kotlinx.android.synthetic.main.item_tools_stress_listitem.view.*

class StressDetailReportAdapter(private val context : Context,
                                private val suggestionsList : List<HypertensionResultPojo> ) :
    RecyclerView.Adapter<StressDetailReportAdapter.StressDetailReportViewHolder>() {

    override fun getItemCount(): Int = suggestionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StressDetailReportViewHolder =
        StressDetailReportViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tools_stress_listitem, parent, false))

    override fun onBindViewHolder(holder: StressDetailReportViewHolder, position: Int) {
        holder.view.setBackgroundColor(ContextCompat.getColor(context,getRandomColor(position)))
        holder.txtTitle.text = suggestionsList[position].title
        holder.txtDesc.text = suggestionsList[position].description
    }

    inner class StressDetailReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtTitle = view.txt_title!!
        var txtDesc = view.txt_description!!
        var view = view.view!!
    }

    private fun getRandomColor(position: Int): Int {
        val color = intArrayOf(
            R.color.vivant_dusky_blue,
            R.color.vivant_nasty_green,
            R.color.vivant_orange_yellow,
            R.color.vivant_bright_blue,
            R.color.vivant_toolbar_green_blue)
        return color[position % 5]
    }

}