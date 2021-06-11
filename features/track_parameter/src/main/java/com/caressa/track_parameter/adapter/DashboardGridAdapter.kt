package com.caressa.track_parameter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.os.Handler
import com.caressa.track_parameter.R
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities
import com.caressa.model.parameter.DashboardParamGridModel
import com.caressa.track_parameter.databinding.ItemTrackparamDashboardGridBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import java.util.*

class DashboardGridAdapter(
    private val mContext: Context,
    private val listener: ParameterSelectionListener,
    private val viewModel: DashboardViewModel) : RecyclerView.Adapter<DashboardGridAdapter.DashboardTrackParamGridViewHolder>() {

    private var parametersList: ArrayList<DashboardParamGridModel> = arrayListOf()
    private var appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardTrackParamGridViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_trackparam_dashboard_grid, parent, false)
        return DashboardTrackParamGridViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: DashboardTrackParamGridViewHolder, position: Int) {
        val parameter: DashboardParamGridModel = parametersList[position]
        holder.bindTo( parameter )

        holder.layoutParameter.setBackgroundColor(ContextCompat.getColor(mContext,R.color.white))
        ImageViewCompat.setImageTintList(holder.imgParam,ColorStateList.valueOf(ContextCompat.getColor(mContext,parameter.colorId)))
        holder.txtParamName.setTextColor(ContextCompat.getColor(mContext,R.color.textViewColor))
        holder.txtParamValue.setTextColor(ContextCompat.getColor(mContext,parameter.colorId))

        if (position == parametersList.size - 1) {
            holder.layoutParameter.setBackgroundColor(appColorHelper.primaryColor())
            holder.txtParamName.setTextColor(ContextCompat.getColor(mContext,parameter.colorId))
        }

        holder.layoutParameter.setOnTouchListener { v: View, event: MotionEvent ->
            if ( position != parametersList.size - 1 ) {
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    v.setBackgroundColor( appColorHelper.primaryColor() )
                    ImageViewCompat.setImageTintList(holder.imgParam,ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.white)))
                    holder.txtParamName.setTextColor(ContextCompat.getColor(mContext,R.color.white))
                    holder.txtParamValue.setTextColor(ContextCompat.getColor(mContext,R.color.white))
                }
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(mContext,R.color.white))
                    ImageViewCompat.setImageTintList(holder.imgParam,ColorStateList.valueOf(ContextCompat.getColor(mContext,parameter.colorId)))
                    holder.txtParamName.setTextColor(ContextCompat.getColor(mContext,R.color.textViewColor))
                    holder.txtParamValue.setTextColor(ContextCompat.getColor(mContext,parameter.colorId))
                }
            }
            false
        }

        holder.layoutParameter.setOnClickListener {
            listener.onSelection(parameter)
        }

    }

    fun updateData(list: List<DashboardParamGridModel>) {
        Utilities.printData("DashboardList",list.toMutableList())
        this.parametersList.clear()
        this.parametersList.addAll(list)
        this.notifyDataSetChanged()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.hideProgressBar()
        }, 1000)
    }

    interface ParameterSelectionListener {
        fun onSelection(paramGridModel: DashboardParamGridModel)
    }

    inner class DashboardTrackParamGridViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemTrackparamDashboardGridBinding.bind(view)
        var layoutParameter = binding.layoutParameter
        var imgParam = binding.imgParam
        var txtParamName = binding.txtParamName
        var txtParamValue = binding.txtParamValue

        fun bindTo(parameter : DashboardParamGridModel) {
            binding.parameter = parameter
        }
    }

}