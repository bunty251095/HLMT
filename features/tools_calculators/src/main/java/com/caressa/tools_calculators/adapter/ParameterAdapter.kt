package com.caressa.tools_calculators.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.ItemMesurementParameterBinding
import com.caressa.common.utils.ParameterDataModel

class ParameterAdapter(val list: MutableList<ParameterDataModel>, val listener: ParameterOnClickListener, val context: Context)
    : RecyclerView.Adapter<ParameterAdapter.ParameterViewHolder>()  {

    var paramList: MutableList<ParameterDataModel> = list

    override fun getItemCount(): Int = paramList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterViewHolder =
        ParameterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_mesurement_parameter, parent, false))

    override fun onBindViewHolder(holder: ParameterAdapter.ParameterViewHolder, position: Int) {
        val parameter = paramList[position]
        holder.bindTo(parameter)
        holder.binding.txtTitle.text = parameter.title
        holder.binding.txtValue.setTextColor(ContextCompat.getColor(context, parameter.color))
        holder.binding.txtValue.text = parameter.value
        holder.binding.txtUnit.text = parameter.unit
        holder.binding.txtUnit.setTextColor(ContextCompat.getColor(context, parameter.color))
        holder.binding.txtDescription.text = parameter.description
        holder.binding.img.setImageDrawable(setTint(ContextCompat.getDrawable(context, parameter.img)!!, parameter.color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.img.backgroundTintList = ContextCompat.getColorStateList(context, parameter.color)
        }

        holder.itemView.setOnClickListener {
            listener.onParameterClick(paramList[position],position)
        }

    }

    private fun setTint(drawable: Drawable, color: Int): Drawable {
        val newDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(newDrawable, color)
        return newDrawable
    }

    fun updateList(paramList: MutableList<ParameterDataModel>) {
/*        this.paramList.clear()
        this.paramList.addAll(paramList)*/
        this.paramList = paramList
        this.notifyDataSetChanged()
    }

    interface ParameterOnClickListener {
        fun onParameterClick(parameterDataModel: ParameterDataModel, position: Int)
    }

    inner class ParameterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMesurementParameterBinding.bind(view)

        fun bindTo(parameter: ParameterDataModel) {
            binding.parameter = parameter
        }

    }
}