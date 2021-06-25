package com.caressa.track_parameter.adapter

import android.graphics.PorterDuff
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.ItemParamResultBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import timber.log.Timber

public class RevSavedParamAdapter(): RecyclerView.Adapter<RevSavedParamAdapter.SavedParamViewHolder>() {

    private val dataList: MutableList<TrackParameterMaster.History> = mutableListOf()
    var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SavedParamViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_param_result, parent, false
        )
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: SavedParamViewHolder, position: Int) =
        holder.bindTo(dataList[position])

    fun updateData(items: List<TrackParameterMaster.History>) {

        dataList.clear()
        if (items.size>0 && items.get(0).profileCode.equals("BLOODPRESSURE",true)){
            dataList.addAll(updateBloodPressureObservation(items))
        }else{
            dataList.addAll(items)
        }
        Timber.i("Inside updateData " + dataList.size)
        Timber.i("Data " + dataList)

        this.notifyDataSetChanged()

    }

    fun updateBloodPressureObservation(bpList: List<TrackParameterMaster.History>): List<TrackParameterMaster.History> {
        var systolic: Int = 0
        var diastolic: Int = 0
        var list :MutableList<TrackParameterMaster.History> = mutableListOf()
        lateinit var sys: TrackParameterMaster.History
        lateinit var dia: TrackParameterMaster.History
        lateinit var pulse: TrackParameterMaster.History
        for(item in bpList){
            if (item.parameterCode.equals("BP_SYS")){
                sys = item
                systolic = item.value!!.toInt()
            }else if (item.parameterCode.equals("BP_DIA")){
                dia = item
                diastolic = item.value!!.toInt()
            }else{
                pulse = item
            }
        }
        if (bpList.isNotEmpty() && bpList.size>1){
            list.add(sys)
            list.add(dia)
            if (bpList.size>2)
                list.add(pulse)
        }
        val observation = TrackParameterHelper.getBPObservation(systolic,diastolic)
        for(item in list){
            if(!item.parameterCode.equals("BP_PULSE")) {
                item.observation = observation
                item.textValue = "" + systolic + "/" + diastolic
            }
        }
        return list
    }

    inner class SavedParamViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemParamResultBinding.bind(parent)

        fun bindTo(item: TrackParameterMaster.History) {
            var color:Int = R.color.textViewColor

            if(!item.description.isNullOrEmpty()){
                binding.txtParamTitle.text = item.description
            }else{
                binding.txtParamTitle.text = "- -"
            }
            binding.txtParamValue.text = item.value.toString()
            if(!item.unit.isNullOrEmpty()){
                binding.txtParamUnit.text = item.unit
            }else{
                binding.txtParamUnit.text = "- -"
            }

            if (!item.observation.isNullOrEmpty()){
                color = TrackParameterHelper.getObservationColor(
                    item.observation!!,
                    item.profileCode!!
                )
                binding.txtParamValue.setTextColor(binding.txtParamValue.context.resources.getColor(color))
            }else{
                binding.txtParamValue.setTextColor(color)
            }
            if (adapterPosition == selectedPosition) {
                //holder.layout_param.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_selected));
                val drawable = DrawableCompat.wrap(
                    ContextCompat.getDrawable(
                        binding.layoutParam.context,
                        R.drawable.border_selected
                    )!!
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DrawableCompat.setTint(
                        drawable, binding.layoutParam.context.resources.getColor(
                            R.color.colorPrimary
                        )
                    )
                } else {
                    drawable.mutate()
                        .setColorFilter(
                            binding.layoutParam.context.resources.getColor(R.color.colorPrimary),
                            PorterDuff.Mode.SRC_IN
                        )
                }
                ViewCompat.setBackground(binding.layoutParam, drawable)
            } else {
                binding.layoutParam.setBackground(
                    ContextCompat.getDrawable(
                        binding.layoutParam.context,
                        R.drawable.border_unselected
                    )
                )
            }

            if (adapterPosition == selectedPosition - 1) {
                binding.view.setBackgroundColor(
                    binding.view.context.resources.getColor(R.color.transparent)
                )
            } else {
                binding.view.setBackgroundColor(binding.view.resources.getColor(R.color.colorPrimary))
            }

            if (!item.profileCode.isNullOrEmpty()){
                binding.imgParam.setImageResource(TrackParameterHelper.getProfileImageByProfileCode(item.profileCode!!))
            }

            binding.layoutParam.setOnClickListener {
                onItemClickListener?.let { click ->
                    selectedPosition = adapterPosition
                    click(item)
                }
            }

        }

    }

    //add these
    private var onItemClickListener: ((TrackParameterMaster.History) -> Unit)? = null

    fun setOnItemClickListener(listener: (TrackParameterMaster.History) -> Unit) {
        onItemClickListener = listener
    }
}