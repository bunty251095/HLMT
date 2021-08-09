package com.caressa.records_tracker.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.UserRelatives
import com.caressa.records_tracker.R
import com.caressa.records_tracker.databinding.ItemRelationshipRecordBinding
import com.caressa.records_tracker.ui.SelectRelationFragment
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel

class SelectRelationshipAdapter( val  fragment : SelectRelationFragment ,val viewModel: HealthRecordsViewModel , val context: Context) :
    RecyclerView.Adapter<SelectRelationshipAdapter.SelectRelationshipViewHolder>() {

    val relativesList: MutableList<UserRelatives> = mutableListOf()
    var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    init {
        //automaticallySelectedSlef()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectRelationshipViewHolder =
        SelectRelationshipViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_relationship_record, parent, false))

    override fun getItemCount(): Int {
       return relativesList.size
    }

    override fun onBindViewHolder(holder: SelectRelationshipViewHolder, position: Int) {
        val relative = relativesList[position]
        holder.bindTo(relative)

/*        val gender = relative.gender
        val relativeImgId = Utilities.getRelationImgIdWithGender(relative.relationshipCode , gender)
        holder.relativeImg.setImageResource(relativeImgId)"*/

        if (selectedPos == position) {
            // view is selected
            holder.txtRelation.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.txtName.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.layoutRelativeView.setBackgroundColor(appColorHelper.primaryColor())
            ImageViewCompat.setImageTintList(holder.relativeImg,ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
        } else {
            // view not selected
            holder.txtRelation.setTextColor(appColorHelper.textColor)
            holder.txtName.setTextColor(appColorHelper.textColor)
            holder.layoutRelativeView.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            ImageViewCompat.setImageTintList(holder.relativeImg,ColorStateList.valueOf(appColorHelper.primaryColor()))
        }

    }

    fun updateRelativesList(items: List<UserRelatives>) {
        relativesList.clear()
        relativesList.addAll(items)
        automaticallySelectedSelf()
        this.notifyDataSetChanged()
        //fragment.stopShimmer()
    }

    private fun automaticallySelectedSelf() {
        if( relativesList.size > 0 ) {
            val personDetail  = relativesList[0]
            val personID = personDetail.relativeID
            val relation = personDetail.relationship
            val personName = personDetail.firstName
            fragment.setPersonID(personID)
            fragment.setRelativeID(personID)
            fragment.setPersonRel(relation)
            fragment.setPersonName(personName)
        }
    }

    inner class SelectRelationshipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemRelationshipRecordBinding.bind(view)
        val layoutRelativeView = binding.layoutRelativeView
        val layoutRelative = binding.layoutRelative
        val relativeImg = binding.imgRelative
        val txtName = binding.txtName
        val txtRelation = binding.txtRelation

        fun bindTo( userRelatives : UserRelatives) {
            binding.relative = userRelatives
            binding.utilities = Utilities
        }

        init {
            layoutRelative.setOnClickListener {

                val personDetail = relativesList[adapterPosition]
                val personID = personDetail.relativeID
                val personRel = personDetail.relationship.trim { it <= ' ' }
                val personName = personDetail.firstName.trim { it <= ' ' }
                fragment.setPersonID(personID)
                fragment.setRelativeID(personID)
                fragment.setPersonRel(personRel)
                fragment.setPersonName(personName)

                notifyItemChanged(selectedPos)
                selectedPos = layoutPosition
                notifyItemChanged(selectedPos)
            }
        }
    }
}