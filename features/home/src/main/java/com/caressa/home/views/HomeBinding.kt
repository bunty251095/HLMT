package com.caressa.home.views

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.LocaleHelper
import com.caressa.common.utils.Utilities
import com.caressa.common.view.ArcTextView
import com.caressa.home.R
import com.caressa.home.adapter.*
import com.caressa.home.common.DataHandler
import com.caressa.home.common.DataHandler.WellnessCentreDetails
import com.caressa.home.common.DataHandler.FamilyRelationOption
import com.caressa.home.common.DataHandler.NavDrawerOption
import com.caressa.model.entity.HRASummary
import com.caressa.model.entity.UserRelatives
import com.caressa.repository.utils.Resource
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.util.*

object HomeBinding {

    fun AppCompatImageView.loadImageNew(url: String) {
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder)
            .into(this)
    }

    @BindingAdapter("app:loadImgUrl")
    @JvmStatic fun AppCompatImageView.setImgUrl( imgUrl: String) {
        try {
            if ( !Utilities.isNullOrEmpty(imgUrl) ) {
                Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .resize(6000,3000)
                    .onlyScaleDown()
                    .error(R.drawable.img_placeholder)
                    .into(this)
            } else {
                setImageResource(R.drawable.img_placeholder)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:imageUrl")
    @JvmStatic fun AppCompatImageView.loadImage( url: String) {
        Glide.with(context).load(url).apply(RequestOptions.circleCropTransform()).into(this)
    }

    @BindingAdapter("app:showWhenLoading")
    @JvmStatic
    fun <T>showWhenLoading(view: SwipeRefreshLayout, resource: Resource<T>?) {
        Timber.i("Resource--->$resource")
        if (resource != null) view.isRefreshing = resource.status == Resource.Status.LOADING
    }

    @BindingAdapter("android:loadImage")
    @JvmStatic fun AppCompatImageView.setImageView(resource: Int ) {
        //Picasso.get().load(resource).into(this)
        setImageResource(resource)
    }

    @BindingAdapter("android:showGender")
    @JvmStatic fun AppCompatTextView.setGender( gender : String? ) {
        try {
            if ( !Utilities.isNullOrEmpty(gender) && gender == "1" ) {
                text = context.resources.getString(R.string.MALE)
            } else if ( !Utilities.isNullOrEmpty(gender) && gender == "2" ) {
                text = context.resources.getString(R.string.FEMALE)
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("android:showAge")
    @JvmStatic fun AppCompatTextView.setAge( userRelatives: UserRelatives ) {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        if ( !Utilities.isNullOrEmpty(userRelatives.dateOfBirth) ) {
            val dob = userRelatives.dateOfBirth
            try {
                if ( !Utilities.isNullOrEmpty( dob ) ) {
                    var strAge = userRelatives.age.toDouble().toInt().toString()
                    val age = DateHelper.calculatePersonAge(dob)
                    if (!Utilities.isNullOrEmpty(userRelatives.age) && userRelatives.age.toDouble().toInt() != 0) {
                        strAge += if (userRelatives.age.equals("1", ignoreCase = true)) {
                            " ${localResource.getString(R.string.YEAR)}"
                        } else {
                            " ${localResource.getString(R.string.YEARS)}"
                        }
                    } else {
                        strAge = age
                    }
                    text = strAge
                }
            } catch ( e : Exception ) {
                e.printStackTrace()
            }
        }
    }

    @BindingAdapter("android:hraObsColor")
    @JvmStatic fun LinearLayout.setHraObsColor( hraSummary: HRASummary? ) {
        try {
            Timber.i("setHraScore--->$hraSummary")
            if ( hraSummary != null ) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }

                if ( !Utilities.isNullOrEmpty( currentHRAHistoryID) && !currentHRAHistoryID.equals("0", ignoreCase = true) ) {
                    when {
                        hraCutOff.equals("0", ignoreCase = true) -> {
                            background.setTint( ContextCompat.getColor(context,R.color.colorPrimary) )
                        }
                        wellnessScore in 0..15 -> {
                            background.setTint( ContextCompat.getColor(context,R.color.high_risk) )
                        }
                        wellnessScore in 16..45 -> {
                            background.setTint( ContextCompat.getColor(context,R.color.moderate_risk) )
                        }
                        wellnessScore in 46..85 -> {
                            background.setTint( ContextCompat.getColor(context,R.color.healthy_risk) )
                        }
                        wellnessScore > 85 -> {
                            background.setTint( ContextCompat.getColor(context,R.color.optimum_risk) )
                        }
                    }
                } else {
                    background.setTint( ContextCompat.getColor(context,R.color.colorPrimary) )
                }
            } else {
                background.setTint( ContextCompat.getColor(context,R.color.colorPrimary) )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:showHraScore")
    @JvmStatic fun AppCompatTextView.setHraScore( hraSummary: HRASummary? ) {
        try {
            if ( hraSummary != null ) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }
                if ( !Utilities.isNullOrEmpty( currentHRAHistoryID) && !currentHRAHistoryID.equals("0",ignoreCase = true) ) {
                    if (hraCutOff.equals("0", ignoreCase = true)) {
                        wellnessScore = 0
                    }
                } else {
                    wellnessScore = 0
                }
                text = wellnessScore.toString()
            }
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:showHraObservation")
    @JvmStatic fun AppCompatTextView.setHraObservation( hraSummary: HRASummary? ) {
        try {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
            if ( hraSummary != null ) {
                var wellnessScore = 0
                var hraCutOff = ""
                var currentHRAHistoryID = ""
                wellnessScore = hraSummary.scorePercentile.toInt()
                hraCutOff = hraSummary.hraCutOff
                currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                if (wellnessScore <= 0) {
                    wellnessScore = 0
                }
                if ( !Utilities.isNullOrEmpty( currentHRAHistoryID) && !currentHRAHistoryID.equals("0", ignoreCase = true) ) {
                    when {
                        hraCutOff.equals("0", ignoreCase = true) -> {
                            text = localResource.getString(R.string.COMPLETE_YOUR_HRA)
                        }
                        wellnessScore in 0..15 -> {
                            text = localResource.getString(R.string.HIGH_RISK)
                        }
                        wellnessScore in 16..45 -> {
                            text = localResource.getString(R.string.NEEDS_IMPROVEMENT)
                        }
                        wellnessScore in 46..85 -> {
                            text = localResource.getString(R.string.HEALTHY)
                        }
                        wellnessScore > 85 -> {
                            text = localResource.getString(R.string.OPTIMUM)
                        }
                    }
                } else {
                    text = localResource.getString(R.string.TAKE_ASSESSMENT)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:dashboardFeaturesGrid")
    @JvmStatic fun RecyclerView.setDashboardFeaturesList( list: List<DataHandler.DashboardFeatureGrid>? ) {
        with(this.adapter as DashboardFeaturesGridAdapter) {
            layoutManager = GridLayoutManager(context,3)
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            list?.let { updateData(it) }
        }
    }

    @BindingAdapter("app:drawerItems")
    @JvmStatic fun RecyclerView.setDrawerItems( list: List<NavDrawerOption>? ) {
        with(this.adapter as NavigationDrawerListAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateNavDrawerList(it) }
        }
    }

    @BindingAdapter("app:settingsItems")
    @JvmStatic fun RecyclerView.setSettingsItems( list: List<DataHandler.Option>?) {
        with(this.adapter as OptionSettingsAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateDashboardOptionsList(it) }
        }
    }

    @BindingAdapter("app:familyMembersList")
    @JvmStatic fun RecyclerView.setFamilyMembersList(list: List<UserRelatives>?) {
        with(this.adapter as FamilyMembersAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateFamilyMembersList(it) }
        }
    }

/*    @BindingAdapter("app:familyDoctorsList")
    @JvmStatic fun RecyclerView.setDoctorsList( list: List<FamilyDoctor>? ) {
        with(this.adapter as FamilyDoctorsAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateFamilyDoctorsList(it) }
        }
    }

    @BindingAdapter("app:familyMembers")
    @JvmStatic fun Spinner.setFamilyMembers( list: List<UserRelatives>? ) {
        with(this.adapter as FamilyProfileAdapter) {
            list?.let { updateRelativesList(it) }
        }
    }*/

    @BindingAdapter("app:relationListItems")
    @JvmStatic fun RecyclerView.setRelationListItems( list: List<FamilyRelationOption>? ) {
        with(this.adapter as FamilyRelationshipAdapter) {
            layoutManager = GridLayoutManager(context, 3)
            list?.let { updateRelationList(it) }
        }
    }

    @BindingAdapter("app:wellnessCentreItems")
    @JvmStatic fun RecyclerView.setWellnessCentreItems( list: List<WellnessCentreDetails>? ) {
        with(this.adapter as WellnessCentreAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateWellnessCentreList(it) }
        }
    }

    @BindingAdapter("app:loadAppImgUrl")
    @JvmStatic fun AppCompatImageView.setImgFromUrl( imgUrl: String?) {
        try {
            Timber.e("imgUrl--->$imgUrl")
            if ( !Utilities.isNullOrEmpty(imgUrl) ) {
                Picasso.get()
                    .load(imgUrl)
                    .placeholder(R.drawable.hl_pace_logo)
                    //.resize(6000,3000)
                    //.onlyScaleDown()
                    .error(R.drawable.hl_pace_logo)
                    .into(this)
            } else {
                setImageResource(R.drawable.hl_pace_logo)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    @BindingAdapter("app:setTextVisible")
    @JvmStatic fun ArcTextView.setVisible( forceUpdate: Boolean? ) {
        visibility = if(forceUpdate!!)
            View.GONE
        else
            View.VISIBLE
    }

    @BindingAdapter("app:languageList")
    @JvmStatic
    fun RecyclerView.setLanguageList(list: List<DataHandler.LanguageModel>?) {
        with(this.adapter as LanguageAdapter) {
            layoutManager = LinearLayoutManager(context)
            list?.let { updateList(it.toMutableList()) }
        }
    }

}