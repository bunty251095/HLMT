package com.caressa.home.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.databinding.ItemRelativeBinding
import com.caressa.home.ui.ProfileAndFamilyMember.FamilyMembersListFragment
import com.caressa.home.viewmodel.ProfileFamilyMemberViewModel
import com.caressa.model.entity.UserRelatives

class FamilyMembersAdapter( val  fragment : FamilyMembersListFragment , val viewModel : ProfileFamilyMemberViewModel, val context: Context) :
    RecyclerView.Adapter<FamilyMembersAdapter.FamilyMembersViewHolder>() {

    private val familyMembersList: MutableList<UserRelatives> = mutableListOf()
    var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green,
        R.color.vivant_dusky_blue)

    override fun getItemCount(): Int = familyMembersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :  FamilyMembersViewHolder =
        FamilyMembersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_relative, parent, false))

    override fun onBindViewHolder(holder: FamilyMembersViewHolder, position: Int) {
        holder.bindTo( familyMembersList[position] , viewModel)
        holder.view.setBackgroundColor(context.resources.getColor(getRandomColor(position)))
        if ( !Utilities.isNullOrEmpty(familyMembersList[position].dateOfBirth) ) {
            val dob = familyMembersList[position].dateOfBirth
            try {
                if ( !Utilities.isNullOrEmpty( dob ) ) {
                    var strAge = familyMembersList[position].age.toDouble().toInt().toString()
                    val age = DateHelper.calculatePersonAge(dob)
                    if (!Utilities.isNullOrEmpty(familyMembersList[position].age)
                        && familyMembersList[position].age.toDouble().toInt() != 0) {
                        if (familyMembersList[position].age.equals("1", ignoreCase = true)) {
                            strAge = strAge + " ${context.resources.getString(R.string.YEAR)}"
                        } else {
                            strAge = strAge + " ${context.resources.getString(R.string.YEARS)}"
                        }
                    } else {
                        strAge = age
                    }
                    holder.familyMemberAge.text = "(" + strAge + ")"
                }
            } catch ( e : Exception ) {
                e.printStackTrace()
            }
        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.RELATIVE_ID,familyMembersList[position].relativeID)
            bundle.putString(Constants.RELATION_SHIP_ID,familyMembersList[position].relationShipID)
            bundle.putString(Constants.RELATION_CODE,familyMembersList[position].relationshipCode)
            bundle.putString(Constants.RELATION,familyMembersList[position].relationship)
            it.findNavController().navigate(R.id.action_familyMembersListFragment2_to_editFamilyMemberDetailsFragment2,bundle)
        }
    }

    fun updateFamilyMembersList(items: List<UserRelatives>) {
        familyMembersList.clear()
        familyMembersList.addAll(items)
        fragment.stopShimmer()
    }

    private fun getRandomColor(position: Int): Int {
        return color[position % 7]
    }

    inner class FamilyMembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemRelativeBinding.bind(view)
        val view = binding.view5
        val familyMemberAge = binding.textFamilyMemberAge

        fun bindTo(UserRelative : UserRelatives, viewModel: ProfileFamilyMemberViewModel) {
            binding.familyMember = UserRelative
            binding.viewModel = viewModel
        }
    }
}