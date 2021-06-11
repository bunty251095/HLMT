package com.caressa.tools_calculators.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.ParamReportItemViewBinding
import com.caressa.tools_calculators.model.HeartAgeReport

class ParamDataReportAdapter(val paramList: MutableList<HeartAgeReport>, val context: Context)
    : RecyclerView.Adapter<ParamDataReportAdapter.ParamDataReportViewHolder>()  {

    override fun getItemCount(): Int = paramList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParamDataReportViewHolder =
        ParamDataReportViewHolder(LayoutInflater.from(parent.context).inflate(
                R.layout.param_report_item_view, parent, false))

    override fun onBindViewHolder(holder: ParamDataReportViewHolder, position: Int) {
        val parameter = paramList[position]
        holder.bindTo(parameter)
        if (parameter.title.toLowerCase().contains("smoke")) {
            ViewCompat.setBackgroundTintList(holder.mImg, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_dusty_orange)))
        } else if (parameter.title.toLowerCase().contains("blood pressure")) {
            holder.mImg.setImageResource(R.drawable.img_hypertension)
            ViewCompat.setBackgroundTintList(holder.mImg, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_watermelon)))
        } else {
            ViewCompat.setBackgroundTintList(holder.mImg, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorPrimary)))
        }
    }

    inner class ParamDataReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ParamReportItemViewBinding.bind(view)
        var mImg  = binding.img
        fun bindTo(parameter: HeartAgeReport) {
            binding.parameter = parameter
        }
    }

}