package com.caressa.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.AppColorHelper
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.common.DataHandler.NavDrawerOption
import com.caressa.home.databinding.NavHeaderHomeMainBinding
import com.caressa.home.ui.HomeMainActivity
import com.caressa.home.viewmodel.DashboardViewModel
import com.caressa.model.entity.UserRelatives
import kotlinx.android.synthetic.main.item_drawer_list.view.*
import timber.log.Timber

class NavigationDrawerListAdapter(val viewModel:DashboardViewModel,val activity:HomeMainActivity,val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var familyProfileSelected = ""
    var mIsSpinnerTouched: Boolean = false
    var isProfileUpdate: Boolean = false
    private val navDrawerList: MutableList<NavDrawerOption> = mutableListOf()
    private val userRelativesList: MutableList<UserRelatives> = mutableListOf()
    private var drawerClickListener: DrawerClickListener = activity
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            TYPE_HEADER -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.nav_header_home_main, parent, false)
                return HeaderViewHolder(view)
            }
            TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_list, parent, false)
                return NavDrawerViewHolder(view)
            }
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.drawer_list_item, parent, false)
        return NavDrawerViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NavDrawerViewHolder) {
            val navDrawerItem = getNavDrawerItem(position)
            holder.view.setBackgroundColor(navDrawerItem.color)
            holder.navIcon.setImageResource( navDrawerItem.imageId )
            holder.navTitle.text = navDrawerItem.title

            holder.layoutDrawer.setOnTouchListener { v: View, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN ||
                    event.action == MotionEvent.ACTION_MOVE) {
                    v.setBackgroundColor(navDrawerItem.color)
                    ImageViewCompat.setImageTintList(holder.navIcon,
                        ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
                    holder.navTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
                }
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                    ImageViewCompat.setImageTintList(holder.navIcon,
                        ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorAccent)))
                    holder.navTitle.setTextColor(ContextCompat.getColor(context,R.color.textViewColor))
                }
                false
            }

        } else if (holder is HeaderViewHolder) {
            holder.bindTo(viewModel)
        }
    }

    override fun getItemCount(): Int = navDrawerList.size+1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    private fun getNavDrawerItem(position: Int): NavDrawerOption {
        return navDrawerList[position-1]
    }

    fun updateNavDrawerList(items: List<NavDrawerOption>) {
        navDrawerList.clear()
        navDrawerList.addAll(items)
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    inner class NavDrawerViewHolder( view: View ) : RecyclerView.ViewHolder(view)  {
        var layoutDrawer = view.layout_drawer_item!!
        var view = view.view_drawer!!
        var navIcon = view.img_drawer_item!!
        var navTitle = view.txt_drawer_item_title!!

        init {
            layoutDrawer.setOnClickListener {
                drawerClickListener.onDrawerClick(navDrawerList[adapterPosition-1])
            }
        }
    }

    inner class HeaderViewHolder(view: View ) : RecyclerView.ViewHolder(view)  {
        private val binding = NavHeaderHomeMainBinding.bind(view)

        @SuppressLint("ClickableViewAccessibility")
        fun bindTo(viewModel: DashboardViewModel) {
            binding.viewModel = viewModel

            if ( Constants.strEnableFamilyProfile ) {

                binding.llMainUser.visibility = View.GONE
                binding.layoutSpinner.visibility = View.VISIBLE
                binding.spFamilyprofile.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    appColorHelper.primaryColor(), BlendModeCompat.SRC_ATOP)
                try {
                    viewModel.userRelativesList.observe( activity , Observer {
                        if (!isProfileUpdate) {
                            isProfileUpdate = true
                            if (it != null && it.isNotEmpty()) {
                                Timber.i("RelativesList--->$it")
                                userRelativesList.clear()
                                userRelativesList.addAll(it)
                                val familyProfileAdapter = FamilyProfileAdapter(viewModel,userRelativesList,binding.spFamilyprofile,context)
                                binding.spFamilyprofile.adapter = familyProfileAdapter
                                keepDefaultFamilyProfileSelected()
                            }
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    binding.spFamilyprofile.setOnTouchListener { _, _ ->
                        mIsSpinnerTouched = true
                        false
                    }
                    binding.spFamilyprofile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                            try {
                                if (mIsSpinnerTouched) {
                                    mIsSpinnerTouched = false
                                    val familyProfileData = adapterView.selectedItem as UserRelatives
                                    if (familyProfileData != null) {
                                        val relativeID = familyProfileData.relativeID
                                        if (!Utilities.isNullOrEmpty(relativeID) && !relativeID.equals(viewModel.personId, ignoreCase = true)) {
                                            changeFamilyProfile(familyProfileData, position)
                                        } else {
                                            keepDefaultFamilyProfileSelected()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onNothingSelected(adapterView: AdapterView<*>) { }
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }

            } else {
                viewModel.getLoggedInPersonDetails()
                binding.llMainUser.visibility = View.VISIBLE
                binding.layoutSpinner.visibility = View.GONE
                viewModel.userDetails.observe( activity , Observer {
                    if ( it != null ) {
                        if ( it.gender.equals("1",ignoreCase = true) ) {
                            binding.navigationUserImage.setImageResource(R.drawable.icon_father)
                        } else if( it.gender.equals("2",ignoreCase = true) ) {
                            binding.navigationUserImage.setImageResource(R.drawable.icon_sister)
                        }
                        binding.navigationUserName.text = it.firstName
                        binding.navigationUserEmail.text = it.emailAddress
                    }
                })
            }

            binding.llMainUser.setOnClickListener {
                binding.spFamilyprofile.performClick()
            }

        }

        fun keepDefaultFamilyProfileSelected() {
            var strFamilyProfileSelected =  getSelectedFamilyProfile(userRelativesList, viewModel.personId)
            if (Utilities.isNullOrEmpty(strFamilyProfileSelected.toString())) {
                strFamilyProfileSelected = 0
            }
            Timber.i("strFamilyProfileSelected--->$strFamilyProfileSelected")
            if (!mIsSpinnerTouched) {
                binding.spFamilyprofile.setSelection(strFamilyProfileSelected)
            }
        }

        private fun getSelectedFamilyProfile(userRelativesList: MutableList<UserRelatives>, personId: String): Int {
            Timber.i("personId--->$personId")
            for ((index, value) in userRelativesList.withIndex()){
                if (value.relativeID == personId){
                    return index
                }
            }
            return 0
        }

        private fun changeFamilyProfile(userRelative: UserRelatives, position:Int) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.SWITCH_PROFILE)
            dialogData.message = context.resources.getString(R.string.MSG_SWITCH_PROFILE_CONFIRMATION1) + " " + userRelative.firstName + "."+context.resources.getString(R.string.MSG_SWITCH_PROFILE_CONFIRMATION2)
            dialogData.btnLeftName = context.resources.getString(R.string.NO)
            dialogData.btnRightName = context.resources.getString(R.string.CONFIRM)
            val defaultNotificationDialog =
                DefaultNotificationDialog(activity, object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            viewModel.switchProfile(userRelative)
                            familyProfileSelected = position.toString()
                            activity.refreshView()
                        }
                        if (isButtonLeft) {
                            keepDefaultFamilyProfileSelected()
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }

    }

    interface DrawerClickListener{
        fun onDrawerClick(item: NavDrawerOption)
    }
}