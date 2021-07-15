package com.caressa.track_parameter.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities
import com.caressa.model.parameter.ParameterProfile
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.ItemSelectParametersBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import com.caressa.track_parameter.viewmodel.DashboardViewModel

class SelectParameterAdapter(private val mContext: Context,private  val viewModel: DashboardViewModel): RecyclerView.Adapter<SelectParameterAdapter.SelectParameterViewHolder>() {

    val dataList: MutableList<ParameterProfile> = mutableListOf()
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount() : Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SelectParameterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_select_parameters, parent, false))

    override fun onBindViewHolder(holder: SelectParameterViewHolder, position: Int) {
        val profile : ParameterProfile = dataList[position]
        holder.bindTo(profile)

        if( profile.isSelection) {
            holder.binding.layoutProfile.setBackgroundColor(appColorHelper.primaryColor())
            ImageViewCompat.setImageTintList(holder.binding.imgProfile, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)))
            holder.binding.txtProfile.setTextColor(ContextCompat.getColor(mContext, R.color.white))

        } else {
            holder.binding.layoutProfile.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
            ImageViewCompat.setImageTintList(holder.binding.imgProfile, ColorStateList.valueOf(appColorHelper.primaryColor()))
            holder.binding.txtProfile.setTextColor(appColorHelper.textColor)
        }

        holder.binding.layoutProfile.setOnClickListener {
            if ( profile.isSelection ) {
                updateSelection(position,false)
            } else {
                updateSelection(position,true)
            }
            viewModel.updateSelection(position)
        }

    }

    private fun updateSelection(position : Int, isSelected : Boolean ) {
        dataList[position].isSelection = isSelected
        this.notifyItemChanged(position)
    }

    fun updateData(list: List<ParameterProfile>) {
        Utilities.printData("AllProfilesList",list)
        this.dataList.clear()
        this.dataList.addAll(list)
        this.notifyDataSetChanged()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.hideProgressBar()
        }, 1000)
    }
    fun getSelectedParameterList(): ArrayList<ParameterProfile> {
        var list:ArrayList<ParameterProfile> = arrayListOf()
        for(item in dataList){
            if (item.isSelection){
                list.add(item)
            }
        }
        return list
    }

    class SelectParameterViewHolder(parent: View): RecyclerView.ViewHolder(parent) {

        val binding = ItemSelectParametersBinding.bind(parent)

        fun bindTo(profile: ParameterProfile) {
            binding.profile = profile
            binding.helper = TrackParameterHelper
        }

    }
}