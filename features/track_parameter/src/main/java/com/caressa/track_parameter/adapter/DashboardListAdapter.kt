package com.caressa.track_parameter.adapter

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.model.parameter.ParameterListModel
import com.caressa.model.parameter.TrackParamDashboardDataSet
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.DashboardListItemBinding
import com.caressa.track_parameter.databinding.SelectParamListItemBinding
import com.caressa.track_parameter.viewmodel.DashboardViewModel

class DashboardListAdapter(private val viewModel: DashboardViewModel): RecyclerView.Adapter<DashboardListAdapter.DashboardViewHolder>() {

    private val dataList: MutableList<TrackParamDashboardDataSet> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder =
        DashboardListAdapter.DashboardViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.dashboard_list_item, parent, false
            )
        )
    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bindTo(dataList.get(position),viewModel)
    }

    fun updateList(list: List<TrackParamDashboardDataSet>){
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    class DashboardViewHolder(parent: View): RecyclerView.ViewHolder(parent) {
        private val binding = DashboardListItemBinding.bind(parent)

        fun bindTo(item: TrackParamDashboardDataSet, viewModel: DashboardViewModel) {
            binding.txtProfileName.text = item.profileName
            binding.colorseekbarCommonprofile.visibility = View.GONE
            if (!item.isStartTracking) {
                binding.txtStartTracking.visibility = View.GONE
            }else{
                binding.txtStartTracking.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener {
                viewModel.openParameterDetails(item)
            }
        }
    }

}