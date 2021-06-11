package com.caressa.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ItemOptionBinding
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.item_drawer_list.view.*

class OptionSettingsAdapter(val viewModel : DashboardViewModel, val context: Context,val mSettingsOptionListener: SettingsOptionListener) :
    RecyclerView.Adapter<OptionSettingsAdapter.OptionSettingsViewHolder>() {

    private val settingsOptionsList: MutableList<DataHandler.Option> = mutableListOf()
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = settingsOptionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionSettingsViewHolder =
        OptionSettingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false))

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: OptionSettingsViewHolder, position: Int) {
        val settingsOption = settingsOptionsList[position]
        holder.bindTo( viewModel , settingsOption )

        ContextCompat.getColor(context,R.color.vivant_edit_textcolor)

        holder.layout_option.setOnTouchListener { v: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                holder.card_option.setBackgroundColor(appColorHelper.primaryColor())
                ImageViewCompat.setImageTintList(holder.img_option, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
                holder.txt_option.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                holder.card_option.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                ImageViewCompat.setImageTintList(holder.img_option, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_edit_textcolor)))
                holder.txt_option.setTextColor(appColorHelper.textColor)
            }
            false
        }

/*        holder.itemView.setOnClickListener {
            mSettingsOptionListener.OnSettingsOptionListener(position , settingsOption)
        }*/

    }

    fun updateDashboardOptionsList(items: List<DataHandler.Option>) {
        settingsOptionsList.clear()
        settingsOptionsList.addAll(items)
    }

    inner class OptionSettingsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemOptionBinding.bind(view)
        val img_option = binding.imgOption
        val txt_option = binding.txtOption
        val layout_option = binding.layoutOptionSetting
        val card_option = binding.cardOption


        fun bindTo( viewModel: DashboardViewModel , option : DataHandler.Option) {
            binding.viewModel = viewModel
            binding.option = option
        }

        init {
            view.setOnClickListener {
                mSettingsOptionListener.onSettingsOptionListener(adapterPosition,settingsOptionsList[adapterPosition])
            }
        }
    }

    interface SettingsOptionListener {
        fun onSettingsOptionListener(position:Int,option:DataHandler.Option)
    }

}