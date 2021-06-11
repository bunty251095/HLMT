package com.caressa.home.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.databinding.ItemRelationshipBinding
import com.caressa.home.ui.ProfileAndFamilyMember.SelectRelationshipFragment
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel

class FamilyRelationshipAdapter( val fragment : SelectRelationshipFragment , val viewModel : ProfileFamilyMemberViewModel, val context: Context) : RecyclerView.Adapter<FamilyRelationshipAdapter.FamilyRelationshipViewHolder>() {

    private val familyRelationList: MutableList<DataHandler.FamilyRelationOption> = mutableListOf()
    private var selectedPos = -1
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = familyRelationList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyRelationshipViewHolder =
        FamilyRelationshipViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_relationship, parent, false))

    override fun onBindViewHolder(holder: FamilyRelationshipViewHolder, position: Int) {
        holder.bindTo( familyRelationList[position] , viewModel)
        holder.layoutRelation.tag = position
/*        holder.relation.setTextColor(context.getResources().getColor(R.color.vivant_track_param_textcolor))
        holder.relationIcon.setBackgroundColor(context.resources.getColor(R.color.white))
        ImageViewCompat.setImageTintList(holder.relationIcon,
            ColorStateList.valueOf(context.resources.getColor(R.color.vivant_track_param_textcolor)))*/

        if (selectedPos == position) {
            // view is selected
            holder.relation.setTextColor(ContextCompat.getColor(context,R.color.white))
            holder.layoutRelation.setBackgroundColor(appColorHelper.primaryColor())
            ImageViewCompat.setImageTintList(holder.relationIcon,ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
        } else {
            // view not selected
            holder.relation.setTextColor(appColorHelper.textColor)
            holder.layoutRelation.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
            ImageViewCompat.setImageTintList(holder.relationIcon,ColorStateList.valueOf(appColorHelper.primaryColor()))
        }

    }

    fun updateRelationList(items: List<DataHandler.FamilyRelationOption>) {
        familyRelationList.clear()
        familyRelationList.addAll(items)
    }

   inner class FamilyRelationshipViewHolder(view: View ) : RecyclerView.ViewHolder(view)  {

       private val binding = ItemRelationshipBinding.bind(view)
       var relationIcon = binding.imgRelation
       var relation = binding.txtRelation
       var layoutRelation = binding.layoutRelativeView

       init {
               layoutRelation.setOnClickListener {

                   val memberDetail = familyRelationList[adapterPosition]
                   fragment.setRelationShipCode(memberDetail.relationshipCode)
                   fragment.setRelation(memberDetail.relation)
                   fragment.setGender(memberDetail.gender)

                   notifyItemChanged(selectedPos)
                   selectedPos = layoutPosition
                   notifyItemChanged(selectedPos)
           }
       }

       fun bindTo(familyRelationOption: DataHandler.FamilyRelationOption, viewModel: ProfileFamilyMemberViewModel) {
           binding.relationOption = familyRelationOption
           binding.viewModel = viewModel
       }
   }
}