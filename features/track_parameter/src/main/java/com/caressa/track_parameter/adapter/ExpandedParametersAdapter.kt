package com.caressa.track_parameter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.caressa.track_parameter.R
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.ParentProfileModel
import com.caressa.track_parameter.databinding.ItemExpandableParameterBinding
import com.caressa.track_parameter.ui.RevDetailHistoryFragment
import com.caressa.track_parameter.util.TrackParameterHelper
import java.util.*

class ExpandedParametersAdapter(private val mContext: Context, private val fragment: RevDetailHistoryFragment,
    private var parametersList: MutableList<TrackParameterMaster.History>) : RecyclerView.Adapter<ExpandedParametersAdapter.ExpandedParametersViewHolder>() {

    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandedParametersViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_expandable_parameter, parent, false)
        return ExpandedParametersViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ExpandedParametersViewHolder, position: Int) {
        try {
            val parameter = parametersList[position]
            var color: Int = ContextCompat.getColor(mContext, R.color.textViewColor)
            if (!Utilities.isNullOrEmpty(parameter.observation)) {
                color = ContextCompat.getColor(mContext, TrackParameterHelper.getObservationColor(
                    Objects.requireNonNull(parameter.observation!!), parameter.profileCode!!))
            }
            holder.txtParamTitle.text = Utilities.getParameterNameByCode(mContext,parameter.parameterCode,parameter.description!!)
            holder.bindTo( parameter,color )

            if( parameter.profileCode.equals("URINE",ignoreCase = true) ) {
                if ( !Utilities.isNullOrEmpty(parameter.textValue) ) {
                    holder.txtParamValue.text = parameter.textValue
                }
            } else {
                if ( !Utilities.isNullOrEmpty(parameter.value.toString()) ) {
                    holder.txtParamValue.text = parameter.value.toString()
                }
            }

            if ( !Utilities.isNullOrEmpty(parameter.observation)  ) {
                if ( !parameter.observation.equals("NA",ignoreCase = true) ) {
                    holder.txtParamObs.text = parameter.observation
                }
            }

            holder.layoutParent.setOnTouchListener { v: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    v.setBackgroundColor(appColorHelper.primaryColor())
                    holder.txtParamTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    holder.txtParamDate.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    holder.txtParamValue.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    holder.txtParamUnit.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    holder.txtParamObs.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                }
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
                    holder.txtParamTitle.setTextColor(ContextCompat.getColor(mContext, R.color.textViewColor))
                    holder.txtParamDate.setTextColor(ContextCompat.getColor(mContext, R.color.vivant_charcoal_grey_55))
                    holder.txtParamValue.setTextColor(color)
                    holder.txtParamUnit.setTextColor(ContextCompat.getColor(mContext, R.color.vivant_charcoal_grey_55))
                    holder.txtParamObs.setTextColor(color)
                }
                false
            }

            holder.itemView.setOnClickListener {
                fragment.showDetailsDialog( parameter,color )
            }

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    inner class ExpandedParametersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemExpandableParameterBinding.bind(view)
        var layoutParent = binding.layoutParent
        var txtParamTitle = binding.txtParamTitle
        var txtParamObs = binding.txtParamObs
        var txtParamValue = binding.txtParamValue
        var txtParamUnit = binding.txtParamUnit
        var txtParamDate = binding.txtParamDate
        var view = binding.viewExpand
        var viewExpand = binding.viewLast

        fun bindTo(history : TrackParameterMaster.History , color :Int) {
            binding.history = history
            binding.color = color
        }
    }
}