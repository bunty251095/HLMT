package com.caressa.home.common

import android.app.Activity
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.LocaleHelper
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.model.entity.HRASummary
import java.util.*

class DataHandler(val context: Context) {

    object NavDrawer {
        const val HOME = "HOME"
        const val LINK = "Link"
        const val MY_PRO = "MY_PRO"
        const val FAMILY_PRO = "FAMILY_PRO"
        const val FAMILY_DOCTOR = "FAMILY_DOCTOR"
        const val CONTACT_US = "CONTACT_US"
        const val SPREAD_THE_WORD = "SPREAD_THE_WORD"
        const val REFER_AND_EARN = "REFER_AND_EARN"
        const val SETTINGS = "SETTINGS"
    }

    object ProfileImgOption {
        const val View = "View"
        const val Gallery = "Gallery"
        const val Photo = "Photo"
        const val Remove = "Remove"
    }

    fun getNavDrawerList(): List<NavDrawerOption> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<NavDrawerOption> = ArrayList()
        list.add(
            NavDrawerOption(
                R.drawable.img_drawer_home,
                localResource.getString(R.string.MENU_HOME),
                NavDrawer.HOME,
                ContextCompat.getColor(context, R.color.vivant_orange_yellow)
            )
        )
//        list.add(NavDrawerOption(R.drawable.img_link,localResource.getString(R.string.MENU_LINK),NavDrawer.LINK,ContextCompat.getColor(context,R.color.colorPrimary)))
        list.add(
            NavDrawerOption(
                R.drawable.img_drawer_my_profile,
                localResource.getString(R.string.MENU_MY_PROFILE),
                NavDrawer.MY_PRO,
                ContextCompat.getColor(context, R.color.vivant_soft_pink)
            )
        )
        list.add(
            NavDrawerOption(
                R.drawable.img_drawer_family,
                localResource.getString(R.string.MENU_FAMILY_MEMBERS),
                NavDrawer.FAMILY_PRO,
                ContextCompat.getColor(context, R.color.vivant_purple)
            )
        )
//        list.add(NavDrawerOption(R.drawable.img_doctor,localResource.getString(R.string.MENU_FAMILY_DOCTORS),NavDrawer.FAMILY_DOCTOR,ContextCompat.getColor(context,R.color.vivant_green_blue_two)))
        list.add(
            NavDrawerOption(
                R.drawable.img_drawer_contact_us,
                localResource.getString(R.string.MENU_CONTACT_US),
                NavDrawer.CONTACT_US,
                ContextCompat.getColor(context, R.color.vivant_nasty_green)
            )
        )
        list.add(
            NavDrawerOption(
                R.drawable.ic_menu_share,
                localResource.getString(R.string.MENU_SPREAD_THE_WORD),
                NavDrawer.SPREAD_THE_WORD,
                ContextCompat.getColor(context, R.color.vivant_bright_sky_blue)
            )
        )
//        list.add(NavDrawerOption(R.drawable.ic_menu_share,localResource.getString(R.string.REFER_AND_EARN),NavDrawer.REFER_AND_EARN,ContextCompat.getColor(context,R.color.vivant_bright_blue)))
        list.add(
            NavDrawerOption(
                R.drawable.ic_settings,
                localResource.getString(R.string.MENU_SETTINGS),
                NavDrawer.SETTINGS,
                ContextCompat.getColor(context, R.color.vivant_watermelon)
            )
        )
        return list
    }

    fun getSwitchProfileNavDrawerList(): List<NavDrawerOption> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<NavDrawerOption> = ArrayList()
        list.add(
            NavDrawerOption(
                R.drawable.img_drawer_home,
                localResource.getString(R.string.MENU_HOME),
                NavDrawer.HOME,
                ContextCompat.getColor(context, R.color.vivant_bright_blue)
            )
        )
//        list.add(NavDrawerOption(R.drawable.img_doctor,localResource.getString(R.string.MENU_FAMILY_DOCTORS),NavDrawer.FAMILY_DOCTOR,ContextCompat.getColor(context,R.color.vivant_green_blue_two)))
        list.add(
            NavDrawerOption(
                R.drawable.img_drawer_contact_us,
                localResource.getString(R.string.MENU_CONTACT_US),
                NavDrawer.CONTACT_US,
                ContextCompat.getColor(context, R.color.vivant_nasty_green)
            )
        )
        list.add(
            NavDrawerOption(
                R.drawable.ic_menu_share,
                localResource.getString(R.string.MENU_SPREAD_THE_WORD),
                NavDrawer.SPREAD_THE_WORD,
                ContextCompat.getColor(context, R.color.vivant_bright_sky_blue)
            )
        )
//        list.add(NavDrawerOption(R.drawable.ic_menu_share,localResource.getString(R.string.REFER_AND_EARN),NavDrawer.REFER_AND_EARN,ContextCompat.getColor(context,R.color.vivant_bright_blue)))
        list.add(
            NavDrawerOption(
                R.drawable.ic_settings,
                localResource.getString(R.string.MENU_SETTINGS),
                NavDrawer.SETTINGS,
                ContextCompat.getColor(context, R.color.vivant_watermelon)
            )
        )
        return list
    }

    fun getDashboardListUpper(
        hraSummary: HRASummary?,
        stepsData: String
    ): List<DashboardFeatureGrid> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<DashboardFeatureGrid> = ArrayList()

        if (hraSummary == null) {

            list.add(
                DashboardFeatureGrid(
                    imageId = R.drawable.dash_hra,
                    title = localResource.getString(R.string.HRA),
                    color = ContextCompat.getColor(context, R.color.colorAccent),
                    data = localResource.getString(R.string.TAKE_ASSESSMENT),
                    code = "HRA"
                )
            )
        } else {
            try {
                var hraObservation = " -- "
                if (hraSummary!!.hraCutOff.equals("1")) {
                    hraObservation = "${hraSummary!!.scorePercentile.toInt()}"
                } else {
                    hraObservation = localResource.getString(R.string.TAKE_ASSESSMENT)
                }
                var color = ContextCompat.getColor(context, R.color.colorAccent)

                if (hraSummary != null) {
                    var wellnessScore = 0
                    var hraCutOff = ""
                    var currentHRAHistoryID = ""
                    wellnessScore = hraSummary.scorePercentile.toInt()
                    hraCutOff = hraSummary.hraCutOff
                    currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                    if (wellnessScore <= 0) {
                        wellnessScore = 0
                    }

                    if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals(
                            "0",
                            ignoreCase = true
                        )
                    ) {
                        when {
                            hraCutOff.equals("0", ignoreCase = true) -> {
                                color = ContextCompat.getColor(context, R.color.colorPrimary)
                            }
                            wellnessScore in 0..15 -> {
                                color = ContextCompat.getColor(context, R.color.high_risk)
                            }
                            wellnessScore in 16..45 -> {
                                color = ContextCompat.getColor(context, R.color.moderate_risk)
                            }
                            wellnessScore in 46..85 -> {
                                color = ContextCompat.getColor(context, R.color.healthy_risk)
                            }
                            wellnessScore > 85 -> {
                                color = ContextCompat.getColor(context, R.color.optimum_risk)
                            }
                        }
                    } else {
                        color = ContextCompat.getColor(context, R.color.colorPrimary)
                    }
                } else {
                    ContextCompat.getColor(context, R.color.colorPrimary)
                }

                list.add(
                    DashboardFeatureGrid(
                        imageId = R.drawable.dash_hra,
                        title = localResource.getString(R.string.HRA),
                        color = color,
                        data = hraObservation,
                        code = "HRA"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                list.add(
                    DashboardFeatureGrid(
                        imageId = R.drawable.dash_hra,
                        title = localResource.getString(R.string.HRA),
                        color = ContextCompat.getColor(context, R.color.colorAccent),
                        data = " -- ",
                        code = "HRA"
                    )
                )
            }

        }
        if (stepsData.isNullOrEmpty()) {
            list.add(
                DashboardFeatureGrid(
                    imageId = R.drawable.dash_step_counter,
                    title = localResource.getString(R.string.DASH_ACTIVITY_TRACKER),
                    color = ContextCompat.getColor(context, R.color.colorAccent),
                    data = " -- ",
                    code = "STEP"
                )
            )
        } else {
            list.add(
                DashboardFeatureGrid(
                    imageId = R.drawable.dash_step_counter,
                    title = localResource.getString(R.string.DASH_ACTIVITY_TRACKER),
                    color = ContextCompat.getColor(context, R.color.colorAccent),
                    data = stepsData,
                    code = "STEP"
                )
            )
        }

        return list
    }

    fun getSwitchProfileDashboardListUpper(
        hraSummary: HRASummary?,
        stepsData: String
    ): List<DashboardFeatureGrid> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<DashboardFeatureGrid> = ArrayList()

        if (hraSummary == null) {

            list.add(
                DashboardFeatureGrid(
                    imageId = R.drawable.dash_hra,
                    title = localResource.getString(R.string.HRA),
                    color = ContextCompat.getColor(context, R.color.colorAccent),
                    data = localResource.getString(R.string.TAKE_ASSESSMENT),
                    code = "HRA"
                )
            )
        } else {
            try {
                var hraObservation = " -- "
                if (hraSummary!!.hraCutOff.equals("1")) {
                    hraObservation = "${hraSummary!!.scorePercentile.toInt()}"
                } else {
                    hraObservation = localResource.getString(R.string.TAKE_ASSESSMENT)
                }
                var color = ContextCompat.getColor(context, R.color.colorAccent)

                if (hraSummary != null) {
                    var wellnessScore = 0
                    var hraCutOff = ""
                    var currentHRAHistoryID = ""
                    wellnessScore = hraSummary.scorePercentile.toInt()
                    hraCutOff = hraSummary.hraCutOff
                    currentHRAHistoryID = hraSummary.currentHRAHistoryID.toString()
                    if (wellnessScore <= 0) {
                        wellnessScore = 0
                    }

                    if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && !currentHRAHistoryID.equals(
                            "0",
                            ignoreCase = true
                        )
                    ) {
                        when {
                            hraCutOff.equals("0", ignoreCase = true) -> {
                                color = ContextCompat.getColor(context, R.color.colorPrimary)
                            }
                            wellnessScore in 0..15 -> {
                                color = ContextCompat.getColor(context, R.color.high_risk)
                            }
                            wellnessScore in 16..45 -> {
                                color = ContextCompat.getColor(context, R.color.moderate_risk)
                            }
                            wellnessScore in 46..85 -> {
                                color = ContextCompat.getColor(context, R.color.healthy_risk)
                            }
                            wellnessScore > 85 -> {
                                color = ContextCompat.getColor(context, R.color.optimum_risk)
                            }
                        }
                    } else {
                        color = ContextCompat.getColor(context, R.color.colorPrimary)
                    }
                } else {
                    ContextCompat.getColor(context, R.color.colorPrimary)
                }

                list.add(
                    DashboardFeatureGrid(
                        imageId = R.drawable.dash_hra,
                        title = localResource.getString(R.string.HRA),
                        color = color,
                        data = hraObservation,
                        code = "HRA"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                list.add(
                    DashboardFeatureGrid(
                        imageId = R.drawable.dash_hra,
                        title = localResource.getString(R.string.HRA),
                        color = ContextCompat.getColor(context, R.color.colorAccent),
                        data = " -- ",
                        code = "HRA"
                    )
                )
            }

        }

        return list
    }

    fun getDashboardList(hraSummary: HRASummary?, stepsData: String): List<DashboardFeatureGrid> {

        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<DashboardFeatureGrid> = ArrayList()

        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_track_parameter,
                title = localResource.getString(R.string.DASH_TRACK_PARAMETER),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "PARAM"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_tools_calculators,
                title = localResource.getString(R.string.DASH_TOOLS_TRACKER),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "CAL"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_medicine_tracker,
                title = localResource.getString(R.string.DASH_MEDICINE_TRACKER),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "MED"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_store_records,
                title = localResource.getString(R.string.DASH_STORE_HEALTH_RECORD),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "RECORD"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_rewards,
                title = localResource.getString(R.string.DASH_REWARD),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "REW"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_health_library,
                title = localResource.getString(R.string.DASH_HEALTH_LIBRARY),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "BLOG"
            )
        )
        return list
    }

    fun getSwitchProfileDashboardList(
        hraSummary: HRASummary?,
        stepsData: String
    ): List<DashboardFeatureGrid> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<DashboardFeatureGrid> = ArrayList()

        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_track_parameter,
                title = localResource.getString(R.string.DASH_TRACK_PARAMETER),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "PARAM"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_tools_calculators,
                title = localResource.getString(R.string.DASH_TOOLS_TRACKER),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "CAL"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_medicine_tracker,
                title = localResource.getString(R.string.DASH_MEDICINE_TRACKER),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "MED"
            )
        )
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_store_records,
                title = localResource.getString(R.string.DASH_STORE_HEALTH_RECORD),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "RECORD"
            )
        )
//        list.add(DashboardFeatureGrid(imageId = R.drawable.dash_rewards,title = localResource.getString(R.string.DASH_REWARD),color = ContextCompat.getColor(context,R.color.vivant_bright_blue),data = "",code = "REW"))
        list.add(
            DashboardFeatureGrid(
                imageId = R.drawable.dash_health_library,
                title = localResource.getString(R.string.DASH_HEALTH_LIBRARY),
                color = ContextCompat.getColor(context, R.color.vivant_bright_blue),
                data = "",
                code = "BLOG"
            )
        )
        return list
    }

    fun getSettingsOptionList(language: String): List<Option> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<Option> = ArrayList()
        list.add(
            Option(
                R.drawable.ic_star_border,
                localResource.getString(R.string.RATE_US),
                ContextCompat.getColor(context, R.color.vivant_soft_pink),
                "RATE_US"
            )
        )
//        if(language.isNullOrEmpty()) {
//            list.add(
//                Option(
//                    R.drawable.ic_language,
//                    localResource.getString(R.string.LANGUAGE) + " (English)",
//                    ContextCompat.getColor(context, R.color.colorPrimary),
//                    "LANGUAGE"
//                )
//            )
//        }else{
//            list.add(
//                Option(
//                    R.drawable.ic_language,
//                    localResource.getString(R.string.LANGUAGE)+" ("+language+")",
//                    ContextCompat.getColor(context, R.color.colorPrimary),
//                    "LANGUAGE"
//                )
//            )
//        }
//        list.add(Option(R.drawable.ic_feedback,localResource.getString(R.string.FEEDBACK),ContextCompat.getColor(context,R.color.vivant_bright_sky_blue),"FEEDBACK"))
        //list.add(Option(R.drawable.img_password,localResource.getString(R.string.CHANGE_PASSWORD),ContextCompat.getColor(context,R.color.vivant_orange_yellow),"CHANGE_PASSWORD"))
        list.add(
            Option(
                R.drawable.img_setting_language,
                localResource.getString(R.string.LANGUAGE),
                ContextCompat.getColor(context, R.color.vivant_nasty_green),
                "LANGUAGE"
            )
        )
        list.add(
            Option(
                R.drawable.img_drawer_logout,
                localResource.getString(R.string.LOGOUT),
                ContextCompat.getColor(context, R.color.vivant_nasty_green),
                "LOGOUT"
            )
        )
        return list
    }

    fun getHasProfileImageOptionList(): List<Option> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<Option> = ArrayList()
        list.add(
            Option(
                R.drawable.img_view,
                localResource.getString(R.string.VIEW),
                R.color.vivantCyan,
                ProfileImgOption.View
            )
        )
        list.add(
            Option(
                R.drawable.ic_upload_gallery,
                localResource.getString(R.string.OPEN_GALLERY),
                R.color.purple,
                ProfileImgOption.Gallery
            )
        )
        list.add(
            Option(
                R.drawable.ic_upload_photo,
                localResource.getString(R.string.TAKE_PHOTO),
                R.color.green,
                ProfileImgOption.Photo
            )
        )
        list.add(
            Option(
                R.drawable.img_delete,
                localResource.getString(R.string.REMOVE_PHOTO),
                R.color.vivant_watermelon,
                ProfileImgOption.Remove
            )
        )
        return list
    }

    fun getNotProfileImageOptionList(): List<Option> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<Option> = ArrayList()
        list.add(
            Option(
                R.drawable.ic_upload_gallery,
                localResource.getString(R.string.OPEN_GALLERY),
                R.color.purple,
                ProfileImgOption.Gallery
            )
        )
        list.add(
            Option(
                R.drawable.ic_upload_photo,
                localResource.getString(R.string.TAKE_PHOTO),
                R.color.green,
                ProfileImgOption.Photo
            )
        )
        return list
    }

    fun getFamilyRelationListMale(): List<FamilyRelationOption> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FamilyRelationOption> = ArrayList()
        list.add(
            FamilyRelationOption(
                R.drawable.icon_father,
                localResource.getString(R.string.FATHER),
                "FAT",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_mother,
                localResource.getString(R.string.MOTHER),
                "MOT",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_son,
                localResource.getString(R.string.SON),
                "SON",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_daughter,
                localResource.getString(R.string.DAUGHTER),
                "DAU",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_brother,
                localResource.getString(R.string.BROTHER),
                "BRO",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_sister,
                localResource.getString(R.string.SISTER),
                "SIS",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_gf,
                localResource.getString(R.string.GRAND_FATHER),
                "GRF",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_gm,
                localResource.getString(R.string.GRAND_MOTHER),
                "GRM",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_wife,
                localResource.getString(R.string.WIFE),
                "WIF",
                localResource.getString(R.string.FEMALE)
            )
        )
        return list
    }

    fun getFamilyRelationListFemale(): List<FamilyRelationOption> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<FamilyRelationOption> = ArrayList()
        list.add(
            FamilyRelationOption(
                R.drawable.icon_father,
                localResource.getString(R.string.FATHER),
                "FAT",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_mother,
                localResource.getString(R.string.MOTHER),
                "MOT",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_son,
                localResource.getString(R.string.SON),
                "SON",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_daughter,
                localResource.getString(R.string.DAUGHTER),
                "DAU",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_brother,
                localResource.getString(R.string.BROTHER),
                "BRO",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_sister,
                localResource.getString(R.string.SISTER),
                "SIS",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_gf,
                localResource.getString(R.string.GRAND_FATHER),
                "GRF",
                localResource.getString(R.string.MALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_gm,
                localResource.getString(R.string.GRAND_MOTHER),
                "GRM",
                localResource.getString(R.string.FEMALE)
            )
        )
        list.add(
            FamilyRelationOption(
                R.drawable.icon_husband,
                localResource.getString(R.string.HUSBAND),
                "HUS",
                localResource.getString(R.string.MALE)
            )
        )
        return list
    }

    fun getFamilyRelationOptionMale(
        relationshipCode: String,
        desiredLocale: Locale
    ): FamilyRelationOption {
        val localResource = LocaleHelper.getLocalizedResources(context, desiredLocale)!!
        when (relationshipCode) {
            "SELF" -> {
                return FamilyRelationOption(
                    R.drawable.icon_father,
                    localResource.getString(R.string.MYSELF),
                    "SELF",
                    localResource.getString(R.string.MALE)
                )
            }
            "FAT" -> {
                return FamilyRelationOption(
                    R.drawable.icon_father,
                    localResource.getString(R.string.FATHER),
                    "FAT",
                    localResource.getString(R.string.MALE)
                )
            }
            "MOT" -> {
                return FamilyRelationOption(
                    R.drawable.icon_mother,
                    localResource.getString(R.string.MOTHER),
                    "MOT",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "SON" -> {
                return FamilyRelationOption(
                    R.drawable.icon_son,
                    localResource.getString(R.string.SON),
                    "SON",
                    localResource.getString(R.string.MALE)
                )
            }
            "DAU" -> {
                return FamilyRelationOption(
                    R.drawable.icon_daughter,
                    localResource.getString(R.string.DAUGHTER),
                    "DAU",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "BRO" -> {
                return FamilyRelationOption(
                    R.drawable.icon_brother,
                    localResource.getString(R.string.BROTHER),
                    "BRO",
                    localResource.getString(R.string.MALE)
                )
            }
            "SIS" -> {
                return FamilyRelationOption(
                    R.drawable.icon_sister,
                    localResource.getString(R.string.SISTER),
                    "SIS",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "GRF" -> {
                return FamilyRelationOption(
                    R.drawable.icon_gf,
                    localResource.getString(R.string.GRAND_FATHER),
                    "GRF",
                    localResource.getString(R.string.MALE)
                )
            }
            "GRM" -> {
                return FamilyRelationOption(
                    R.drawable.icon_gm,
                    localResource.getString(R.string.GRAND_MOTHER),
                    "GRM",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "WIF" -> {
                return FamilyRelationOption(
                    R.drawable.icon_wife,
                    localResource.getString(R.string.WIFE),
                    "WIF",
                    localResource.getString(R.string.FEMALE)
                )
            }
            else -> {
                return FamilyRelationOption(
                    R.drawable.icon_wife,
                    localResource.getString(R.string.WIFE),
                    "WIF",
                    localResource.getString(R.string.FEMALE)
                )
            }
        }
    }

    fun getFamilyRelationOptionFemale(
        relationshipCode: String,
        desiredLocale: Locale
    ): FamilyRelationOption {
        val localResource = LocaleHelper.getLocalizedResources(context, desiredLocale)!!
        when (relationshipCode) {
            "SELF" -> {
                return FamilyRelationOption(
                    R.drawable.icon_sister,
                    localResource.getString(R.string.MYSELF),
                    "SELF",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "FAT" -> {
                return FamilyRelationOption(
                    R.drawable.icon_father,
                    localResource.getString(R.string.FATHER),
                    "FAT",
                    localResource.getString(R.string.MALE)
                )
            }
            "MOT" -> {
                return FamilyRelationOption(
                    R.drawable.icon_mother,
                    localResource.getString(R.string.MOTHER),
                    "MOT",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "SON" -> {
                return FamilyRelationOption(
                    R.drawable.icon_son,
                    localResource.getString(R.string.SON),
                    "SON",
                    localResource.getString(R.string.MALE)
                )
            }
            "DAU" -> {
                return FamilyRelationOption(
                    R.drawable.icon_daughter,
                    localResource.getString(R.string.DAUGHTER),
                    "DAU",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "BRO" -> {
                return FamilyRelationOption(
                    R.drawable.icon_brother,
                    localResource.getString(R.string.BROTHER),
                    "BRO",
                    localResource.getString(R.string.MALE)
                )
            }
            "SIS" -> {
                return FamilyRelationOption(
                    R.drawable.icon_sister,
                    localResource.getString(R.string.SISTER),
                    "SIS",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "GRF" -> {
                return FamilyRelationOption(
                    R.drawable.icon_gf,
                    localResource.getString(R.string.GRAND_FATHER),
                    "GRF",
                    localResource.getString(R.string.MALE)
                )
            }
            "GRM" -> {
                return FamilyRelationOption(
                    R.drawable.icon_gm,
                    localResource.getString(R.string.GRAND_MOTHER),
                    "GRM",
                    localResource.getString(R.string.FEMALE)
                )
            }
            "HUS" -> {
                return FamilyRelationOption(
                    R.drawable.icon_husband,
                    localResource.getString(R.string.HUSBAND),
                    "HUS",
                    localResource.getString(R.string.MALE)
                )
            }
            else -> {
                return FamilyRelationOption(
                    R.drawable.icon_husband,
                    localResource.getString(R.string.HUSBAND),
                    "HUS",
                    localResource.getString(R.string.MALE)
                )
            }
        }
    }

    fun getWellnessCentreList(): List<WellnessCentreDetails> {
        val localResource =
            LocaleHelper.getLocalizedResources(
                context,
                Locale(LocaleHelper.getLanguage(context))
            )!!
        val list: ArrayList<WellnessCentreDetails> = ArrayList()
        //list.add(WellnessCentreDetails(localResource.getString(R.string.TITLE_TOOLS_AND_TRACKER), "VIVANT" , localResource.getString(R.string.DESC_TOOLS_TRACKER) , R.drawable.img_tools_and_cals,ContextCompat.getColor(context,R.color.vivant_watermelon)))
        //list.add(WellnessCentreDetails(localResource.getString(R.string.TITLE_MEDICINES_HEALTHCARE), "MedLife" , localResource.getString(R.string.DESC_MED_LIFE) , R.drawable.img_medicines_healthcare,ContextCompat.getColor(context,R.color.vivant_nasty_green)))
        //list.add(WellnessCentreDetails(localResource.getString(R.string.TITLE_SEARCH_HEALTH_PACKAGE), "VIVANT" , localResource.getString(R.string.DESC_HEALTH_PACKAGE) , R.drawable.img_health_package,ContextCompat.getColor(context,R.color.vivant_orange_yellow)))
        return list
    }

    fun goToPlayStore(context: Context) {
        try {
            //Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            val uri =
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
            val goToPlayStore = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to take back to our application, we need to add following flags to intent.
            goToPlayStore.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            context.startActivity(goToPlayStore)
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageName)
                )
            )
        }
    }

    fun showEmailClientIntent(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Select App..."))
        }
    }

    fun logout(viewModel: BackgroundCallViewModel, context: Context) {
        viewModel.logoutUser()
        //Utilities.exportdatabase(this)

        Utilities.clearStepsData(context)

        //Dismiss all notifications from Notification tray,
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        val intent = Intent()
        intent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.LOGIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        (context as Activity).finish()
    }

    // DoctorDetailsModel
    data class Option(val imageId: Int, val title: String, val color: Int, val code: String)

    //    data class DashboardFeature(val  imageId: Int, val title: String , val color : Int )
    data class DashboardFeatureGrid(
        val imageId: Int,
        val title: String,
        val color: Int,
        val data: String,
        val code: String
    )

    data class NavDrawerOption(
        val imageId: Int,
        val title: String,
        val id: String,
        val color: Int
    )

    data class WellnessCentreDetails(
        val title: String,
        val tag: String,
        val desc: String,
        val img: Int,
        val color: Int
    )

    data class FamilyRelationOption(
        val relationImgId: Int,
        val relation: String,
        val relationshipCode: String,
        val gender: String
    )
    //data class DoctorDetails(val  relationImgId: Int, val relation: String , val relationshipCode: String , val gender: String )

    data class LanguageModel(
        val language: String = "",
        val languageCode: String = "",
        var selectionStatus: Boolean = false
    )
}
