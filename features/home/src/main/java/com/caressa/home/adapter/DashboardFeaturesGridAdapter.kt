package com.caressa.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ItemDashboardFeaturesBinding
import com.caressa.home.ui.HlmtDashboardFragment
import com.caressa.home.ui.ProfileAndFamilyMember.EditProfileImageBottomsheetFragment
import com.caressa.home.viewmodel.DashboardViewModel
import timber.log.Timber

class DashboardFeaturesGridAdapter(
    requireContext: Context,
    listener: OnItemSelectionListener,
    viewModel: DashboardViewModel
) : RecyclerView.Adapter<DashboardFeaturesGridAdapter.ObservationViewHolder>() {

    private var selectionListener:OnItemSelectionListener = listener
    private val dataList: MutableList<DataHandler.DashboardFeatureGrid> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ObservationViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_dashboard_features, parent, false
        )
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) =
        holder.bindTo(dataList[position])

    fun updateData(items: List<DataHandler.DashboardFeatureGrid>) {
        dataList.clear()
        dataList.addAll(items)
        Timber.i("Inside updateData " + dataList.size)
        this.notifyDataSetChanged()
    }

    fun setListener(item:OnItemSelectionListener){
        selectionListener = item
    }

    inner class ObservationViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemDashboardFeaturesBinding.bind(parent)

        fun bindTo(item: DataHandler.DashboardFeatureGrid) {
            if (item.code.equals("HRA",true)) {
                binding.icon.scaleType = ImageView.ScaleType.FIT_CENTER
                binding.txtData.setTextColor(item.color)
//                binding.txtData.textSize = binding.txtData.resources.getDimension(R.dimen._13sdp)
            }else{
                binding.txtData.setTextColor(ContextCompat.getColor(binding.txtData.context,R.color.colorAccent))
            }
            binding.icon.setImageResource(item.imageId)
            binding.title.text = item.title
            if(item.data.isNullOrEmpty()){
                binding.txtData.visibility = View.GONE
            }else{
                binding.txtData.text = item.data
                binding.txtData.visibility = View.VISIBLE
            }

            binding.cardTrackParameter.setOnClickListener {
                selectionListener.onItemSelected(item)
            }

        }

    }

    interface OnItemSelectionListener{
        fun onItemSelected(item: DataHandler.DashboardFeatureGrid)
    }

}