package com.caressa.home.adapter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.solver.widgets.Helper
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.model.entity.UserRelatives
import kotlinx.android.synthetic.main.rowitem_familyprofile.view.*

class FamilyProfileAdapter(val viewModel:DashboardViewModel,
                           private val relativeList:MutableList<UserRelatives>,
                           private val sp_familyprofile: Spinner, val context: Context) : BaseAdapter() {
//class FamilyProfileAdapter(val viewModel:DashboardViewModel,val sp_familyprofile: Spinner,val context: Context) : BaseAdapter() {

     val mInflater: LayoutInflater = LayoutInflater.from(context)
     private val mRowItemSize = intArrayOf(15, 30, 30, 30)
     private val userRelativesList: MutableList<UserRelatives> = mutableListOf()

     override fun getCount(): Int = relativeList.size + 1

     override fun getItemId(position: Int): Long = position.toLong()

     override fun getItem(position: Int): Any {
         return relativeList[position]
     }

     private fun getRelative(position: Int): UserRelatives {
         return relativeList[position]
     }

     fun updateRelativesList(items: List<UserRelatives>) {
         userRelativesList.clear()
         userRelativesList.addAll(items)
         //Timber.i("userRelativesList"+userRelativesList)
     }

/*     override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
         return super.getDropDownView(position, recycledView, parent)
     }*/

     override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View? {
         if (position == relativeList.lastIndex+1) {

             if(viewModel.isSelfUser()) {
                 return addFamilyMemberButton()
             }else{
                 val view = TextView(context)
                 view.visibility = View.GONE
                 return view
             }
         } else {
             return super.getDropDownView(position, recycledView, parent)
         }
     }

/*     override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
         var mConvertView = convertView
         val viewHolder: FamilyProfileViewHolder
         val relative = getRelative(position)
         mConvertView = mInflater.inflate(R.layout.rowitem_familyprofile, parent, false)
         viewHolder = FamilyProfileViewHolder(mConvertView)
         if (relative != null) {
             val relativeImgId = Utilities.getRelationImgIdWithGender(relative.relationshipCode , relative.gender)
             viewHolder.relativeImage.setImageResource(relativeImgId)
             viewHolder.relativeName.text = relative.firstName
             viewHolder.relation.text = relative.relationship
         }
         return mConvertView
     }*/

     override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
         val view: View
         val vh: FamilyProfileViewHolder
         if (convertView == null) {
             view = mInflater.inflate(R.layout.rowitem_familyprofile, parent, false)
             vh = FamilyProfileViewHolder(view)
             view?.tag = vh
         } else {
             view = convertView
             vh = view.tag as FamilyProfileViewHolder
         }
         val relative = getRelative(position)
         val relativeImgId = Utilities.getRelationImgIdWithGender(relative.relationshipCode , relative.gender)
         vh.relativeImage.setImageResource(relativeImgId)
         vh.relativeName.text = relative.firstName
         vh.relation.text = relative.relationship
         return view
     }

     inner class FamilyProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         //private val binding = RowitemFamilyprofileBinding.bind(view)
         var relativeImage = view.img_relative!!
         var relativeName = view.txt_relative_name!!
         var relation = view.txt_relation!!

/*         fun bindTo( user:UserRelatives,viewModel: DashboardViewModel) {
             binding.userRelative = user
             binding.viewModel = viewModel
             //app:familyMembers="@{viewModel.userRelativesList}"
         }*/
     }

/*     private fun addFamilyMemberButton(): View {
         val view = TextView(context)
         view.text = "    "+context.resources.getString(R.string.ADD_NEW_MEMBER)
         view.textSize = 18f
         view.setTypeface(Typeface.DEFAULT_BOLD, Typeface.NORMAL)
         view.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_plus), null, null, null)
         view.setCompoundDrawableTintList(ColorStateList.valueOf(Color.WHITE))
         view.setPadding(mRowItemSize[0], mRowItemSize[1], mRowItemSize[2], mRowItemSize[3])
         view.gravity = Gravity.CENTER
         view.setTextColor(Color.WHITE)
         view.setBackgroundColor(context.getResources().getColor(R.color.green))
         view.setOnClickListener {
             Utilities.detachSpinnerWindow(sp_familyprofile)
             val intent = Intent()
             intent.setComponent(ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE))
             context.startActivity(intent)
         }
         return view
     }*/

    private fun addFamilyMemberButton(): View? {
        val convertView: View
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (inflater != null) {
            convertView = inflater.inflate(R.layout.btn_add_new_family_member, null)
            convertView.setOnClickListener {
                Utilities.detachSpinnerWindow(sp_familyprofile)
                val intent = Intent()
                intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_PROFILE)
                context.startActivity(intent)
            }
            return convertView
        }
        return null
    }

     private fun addTransparentView(): View {
         val view = View(context)
         view.setPadding(mRowItemSize[0], mRowItemSize[1], mRowItemSize[2], mRowItemSize[3])
         view.setBackgroundColor(context.resources.getColor(R.color.transparent))
         return view
     }

}