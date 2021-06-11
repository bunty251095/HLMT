package com.caressa.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.home.R
import com.caressa.home.common.DataHandler.DashboardFeature
import com.caressa.home.common.DataHandler.NavDrawerOption
import com.caressa.home.databinding.ItemDashboardFeatureBinding
import com.caressa.home.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.item_dashboard_feature.view.*

class DashboardFeaturesAdapter( val viewModel : DashboardViewModel , val context: Context) :
    RecyclerView.Adapter<DashboardFeaturesAdapter.DashboardFeaturesViewHolder>() {

    private val dashboardOptionsList: MutableList<DashboardFeature> = mutableListOf()
    var mOnDashboardFeaturesListener: OnDashboardFeaturesListener? = null

    fun setOnDashboardFeaturesListener( onDashboardFeaturesListener : OnDashboardFeaturesListener ) {
        this.mOnDashboardFeaturesListener = onDashboardFeaturesListener
    }

    override fun getItemCount(): Int = dashboardOptionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardFeaturesViewHolder =
        DashboardFeaturesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_dashboard_feature, parent, false))

    override fun onBindViewHolder(holder: DashboardFeaturesViewHolder, position: Int) {
        val featureDetail = dashboardOptionsList[position]
        holder.bindTo( viewModel , featureDetail )
    }

    fun updateDashboardOptionsList(items: List<DashboardFeature>) {
        dashboardOptionsList.clear()
        dashboardOptionsList.addAll(items)
    }

    inner class DashboardFeaturesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemDashboardFeatureBinding.bind(view)

        fun bindTo( viewModel: DashboardViewModel , dashboardFeature : DashboardFeature ) {
            binding.viewModel = viewModel
            binding.dashboardFeature = dashboardFeature
        }
        init {
            view.setOnClickListener {
                mOnDashboardFeaturesListener!!.OnDashboardFeatureClicked(adapterPosition , it)
            }
        }
    }

    interface OnDashboardFeaturesListener {
        fun OnDashboardFeatureClicked(position: Int , view: View)
    }

}