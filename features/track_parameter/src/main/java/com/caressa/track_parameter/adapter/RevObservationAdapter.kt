package com.caressa.track_parameter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.ItemParamRangesObsBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import timber.log.Timber

public class RevObservationAdapter : RecyclerView.Adapter<RevObservationAdapter.ObservationViewHolder>() {

    private val dataList: MutableList<TrackParameterMaster.TrackParameterRanges> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ObservationViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_param_ranges_obs, parent, false
        )
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) =
        holder.bindTo(dataList[position])

    fun updateData(items: List<TrackParameterMaster.TrackParameterRanges>) {
        dataList.clear()
        dataList.addAll(items)
        Timber.i("Inside updateData " + dataList.size)
        this.notifyDataSetChanged()
    }

    inner class ObservationViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemParamRangesObsBinding.bind(parent)

        fun bindTo(item: TrackParameterMaster.TrackParameterRanges) {
            binding.txtObservation.text = item.observation
//            binding.txtRange.text = Math.floor(item.minValue!!.toDouble()).toInt().toString() + " - "+Math.ceil(item.maxValue!!.toDouble()).toInt()
            binding.txtRange.text = item.minValue+" - "+item.maxValue
            binding.view.setBackgroundColor(
                binding.view.resources.getColor(
                    TrackParameterHelper.getObservationColor(
                        item.observation!!,
                        item.profileCode!!
                    )
                )
            )

        }

    }

}