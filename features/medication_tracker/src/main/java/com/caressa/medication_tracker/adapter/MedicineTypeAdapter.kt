package com.caressa.medication_tracker.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.databinding.ItemMedicineTypeBinding
import com.caressa.medication_tracker.model.MedTypeModel

class MedicineTypeAdapter( val context: Context,private var medTypeList : List<MedTypeModel>,val listener: OnMedTypeListener) :
    RecyclerView.Adapter<MedicineTypeAdapter.MedicineTypeViewHolder>() {

    private var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = medTypeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_medicine_type, parent, false))

    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        val medType = medTypeList[position]
        holder.bindTo( medType )

        if (selectedPos == position) {
            listener.onMedTypeSelection(position, medType)
            holder.layoutMedType.setBackgroundColor(appColorHelper.primaryColor())
            ImageViewCompat.setImageTintList(holder.imgMedType, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
        } else {
            holder.layoutMedType.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            ImageViewCompat.setImageTintList(holder.imgMedType, ColorStateList.valueOf(appColorHelper.primaryColor()))
        }

        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }

    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnMedTypeListener {
        fun onMedTypeSelection(position: Int, medType: MedTypeModel)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMedicineTypeBinding.bind(view)
        val imgMedType = binding.imgMedType
        //val txtMedType = binding.txtMedType
        val layoutMedType = binding.layoutMedType

        fun bindTo(MedType : MedTypeModel ) {
            binding.medType = MedType
        }

    }

}