package com.caressa.medication_tracker.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.databinding.ItemMealTimeBinding
import com.caressa.medication_tracker.model.InstructionModel

class MealTimeAdapter(val context: Context, private var mealTimeList : List<InstructionModel>,
                      val listener: OnInstructionClickListener) :
    RecyclerView.Adapter<MealTimeAdapter.MealTimeViewHolder>() {

    private var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = mealTimeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealTimeViewHolder =
        MealTimeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_meal_time, parent, false))

    override fun onBindViewHolder(holder: MealTimeViewHolder, position: Int) {
        val instruction = mealTimeList[position]
        holder.bindTo( instruction )

        if (selectedPos == position) {
            listener.onInstructionSelection(position, instruction)
            holder.layoutMeal.background = ResourcesCompat.getDrawable(context.resources,R.drawable.border_filled,null)
            holder.layoutMeal.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.primaryColor(),
                BlendModeCompat.SRC_ATOP)
            ImageViewCompat.setImageTintList(holder.imgMeal, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
            holder.txtMeal.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.layoutMeal.background = ResourcesCompat.getDrawable(context.resources,R.drawable.border_normal,null)
            ImageViewCompat.setImageTintList(holder.imgMeal, ColorStateList.valueOf(appColorHelper.primaryColor()))
            holder.txtMeal.setTextColor(appColorHelper.textColor)
        }

        holder.itemView.setOnClickListener {
            listener.onInstructionSelection(position, instruction)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }

    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnInstructionClickListener {
        fun onInstructionSelection(position: Int, instruction: InstructionModel)
    }

    inner class MealTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMealTimeBinding.bind(view)
        val txtMeal = binding.txtMeal
        val imgMeal = binding.imgMeal
        val layoutMeal = binding.layoutMeal

        fun bindTo(instruction : InstructionModel ) {
            binding.instruction = instruction
        }
    }

}