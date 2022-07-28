package com.caressa.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.LocaleHelper
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ItemLanguageBinding

class LanguageAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<LanguageAdapter.ItemViewsHolder>() {

    private var selectedPos = -1
    private var dataList: MutableList<DataHandler.LanguageModel> = mutableListOf()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageAdapter.ItemViewsHolder = ItemViewsHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false))

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ItemViewsHolder, position: Int) {
        val language = dataList[position].language

        holder.txtRelative.text = language

//        if ( selectedPos == -1 ) {
//            holder.layoutRelative.background = ContextCompat.getDrawable(holder.layoutRelative.context,R.drawable.border_button_white)
//        } else {
        if ( selectedPos == position || dataList[position].selectionStatus) {
            holder.layoutRelative.background = ContextCompat.getDrawable(holder.layoutRelative.context,R.drawable.border_button_selected)
            holder.imgSelect.setImageResource(R.drawable.img_tick_green)
        } else {
            holder.layoutRelative.background = ContextCompat.getDrawable(holder.layoutRelative.context,R.drawable.border_button_transparant)
            holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
        }
//        }

        holder.itemView.setOnClickListener {
            listener.onItemSelection(position,dataList[position])
            filterListForSelection()
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyDataSetChanged()
        }

    }

    private fun filterListForSelection() {
        for(item in dataList){
            if(item.selectionStatus){
                item.selectionStatus = false
            }
        }
    }

    fun updateList(items: MutableList<DataHandler.LanguageModel>) {
        dataList.clear()
        dataList.addAll(items)
        this.notifyDataSetChanged()
    }

    inner class ItemViewsHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemLanguageBinding.bind(view)
        val layoutRelative = binding.layoutLanguage
        val imgSelect:ImageView = binding.imgSelect
        val txtRelative:TextView = binding.txtLanguage

        //val layoutRelative = view.layout_relative!!
        //val imgSelect = view.img_select!!
        //val txtRelative = view.txt_relative!!
    }

    interface OnItemClickListener {
        fun onItemSelection(position: Int, data: DataHandler.LanguageModel)
    }

}