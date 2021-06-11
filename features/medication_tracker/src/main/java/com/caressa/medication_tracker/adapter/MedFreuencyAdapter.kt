package com.caressa.medication_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.databinding.ItemMedFrequencyBinding

class MedFreuencyAdapter(val context: Context, private var medFreuencyList : Array<String>,
                         val listener: OnMedFrequencyListener) :
    RecyclerView.Adapter<MedFreuencyAdapter.MedFreuencyViewHolder>() {

    private var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = medFreuencyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedFreuencyViewHolder =
        MedFreuencyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_med_frequency, parent, false))

    override fun onBindViewHolder(holder: MedFreuencyAdapter.MedFreuencyViewHolder, position: Int) {
        val medType = medFreuencyList[position]
        holder.bindTo( medType )

        if (selectedPos == position) {
            listener.onMedFrequencySelection(medFreuencyList[position], position)
            //holder.layoutFreq.background.setColorFilter(appColorHelper.primaryColor(),PorterDuff.Mode.SRC_ATOP)
            holder.layoutFreq.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.primaryColor(),BlendModeCompat.SRC_ATOP)
            holder.txtFrequency.setTextColor(ContextCompat.getColor(context,R.color.white))
        } else {
            //holder.layoutFreq.background.setColorFilter(ContextCompat.getColor(context,R.color.white),PorterDuff.Mode.SRC_ATOP)
            holder.layoutFreq.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(context,R.color.white),BlendModeCompat.SRC_ATOP)
            holder.txtFrequency.setTextColor(appColorHelper.primaryColor())
        }

        holder.itemView.setOnClickListener {
            listener.onMedFrequencySelection(medFreuencyList[position], position)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }

    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnMedFrequencyListener {
        fun onMedFrequencySelection(medFreq: String?, position: Int)
    }

    inner class MedFreuencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMedFrequencyBinding.bind(view)
        val txtFrequency = binding.txtFrequency
        val layoutFreq = binding.layoutFreq

        fun bindTo(freq : String ) {
            binding.freq = freq
        }
    }

}