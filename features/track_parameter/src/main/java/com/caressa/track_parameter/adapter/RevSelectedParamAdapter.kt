package com.caressa.track_parameter.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.base.ClientConfiguration
import com.caressa.model.parameter.ParameterListModel
import com.caressa.track_parameter.R
import com.caressa.track_parameter.databinding.ItemSelectedParameterProfileBinding
import com.caressa.track_parameter.util.TrackParameterHelper
import org.json.JSONObject
import timber.log.Timber

class RevSelectedParamAdapter(private val showUrineProfile: Boolean) : RecyclerView.Adapter<RevSelectedParamAdapter.SelectParameterViewHolder>() {

    private val dataList: MutableList<ParameterListModel.SelectedParameter> = mutableListOf()
    var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SelectParameterViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_selected_parameter_profile, parent, false
        )
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: SelectParameterViewHolder, position: Int) =
        holder.bindTo(dataList[position])

    fun updateData(items: List<ParameterListModel.SelectedParameter>) {
        dataList.clear()
        dataList.addAll(filterList(items))
        Timber.i("Inside updateData " + dataList.size)
        this.notifyDataSetChanged()
    }

    private fun filterList(items: List<ParameterListModel.SelectedParameter>): Collection<ParameterListModel.SelectedParameter> {
        if (!showUrineProfile) {
            return items.filter { !it.profileCode.equals("URINE",true) }
        }else{
            return items
        }
    }

    inner class SelectParameterViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemSelectedParameterProfileBinding.bind(parent)

        fun bindTo(item: ParameterListModel.SelectedParameter) {
            binding.txtSelectedProfile.text = binding.txtSelectedProfile.context.getString(TrackParameterHelper.getProfileNameByProfileCode(item.profileCode))
            binding.imgSelectedProfile.setImageDrawable(
                binding.imgSelectedProfile.resources.getDrawable(
                    getIconDrawable(item.profileCode.toUpperCase())
                )
            )
            try {
                val templateJSON = JSONObject(ClientConfiguration.getAppTemplateConfig())
                val primaryColor = Color.parseColor(templateJSON.getString("primaryColor"))

                if (selectedPosition == adapterPosition) {
                    ImageViewCompat.setImageTintList(
                        binding.imgSelectedProfile,
                        ColorStateList.valueOf(primaryColor)
                    )
                    binding.txtSelectedProfile.setTextColor(primaryColor)
                    binding.viewSelectedParam.setVisibility(View.VISIBLE)
                } else {
                    ImageViewCompat.setImageTintList(
                        binding.imgSelectedProfile,
                        ColorStateList.valueOf(
                            binding.imgSelectedProfile.getResources().getColor(R.color.hlmt_warm_grey)
                        )
                    )
                    binding.txtSelectedProfile.setTextColor(
                        binding.imgSelectedProfile.getResources().getColor(R.color.textViewColor)
                    )
                    binding.viewSelectedParam.setVisibility(View.GONE)
                }
            }catch (e: Exception){e.printStackTrace()}

            binding.mainContainer.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(item)
                    selectedPosition = adapterPosition
                }
            }

        }

        private fun getIconDrawable(profileCode: String): Int {
            var name: Int = R.drawable.img_profile_bmi
            when (profileCode) {
                "BMI" -> return R.drawable.img_profile_bmi
                "BLOODPRESSURE" -> return R.drawable.img_profile_bp
                "DIABETIC" -> return R.drawable.img_profile_blood_sugar
                "HEMOGRAM" -> return R.drawable.img_profile_hemogram
                "KIDNEY" -> return R.drawable.img_profile_kidney
                "LIPID" -> return R.drawable.img_profile_lipid
                "LIVER" -> return R.drawable.img_profile_liver
                "THYROID" -> return R.drawable.img_profile_thyroid
                "WHR" -> return R.drawable.img_profile_whr
                "URINE" -> return R.drawable.img_profile_urine
            }
            return name
        }


    }

    //add these
    private var onItemClickListener: ((ParameterListModel.SelectedParameter) -> Unit)? = null

    fun setOnItemClickListener(listener: (ParameterListModel.SelectedParameter) -> Unit) {
        onItemClickListener = listener
    }
}