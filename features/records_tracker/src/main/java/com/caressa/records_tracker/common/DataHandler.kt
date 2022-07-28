package com.caressa.records_tracker.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Html
import androidx.core.content.FileProvider
import com.caressa.common.constants.FirebaseConstants
import com.caressa.common.utils.*
import com.caressa.common.view.SpinnerModel
import com.caressa.model.entity.HealthDocument
import com.caressa.model.entity.RecordInSession
import com.caressa.records_tracker.R
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import timber.log.Timber
import java.io.*
import java.util.*

class DataHandler(val context: Context) {

    private val fileUtils = FileUtils

    private fun openDownloadedFile(file : File, type :String) {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        try {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri,type)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            //val openIntent = Intent.createChooser(intent,"Open using")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Utilities.toastMessageShort(context,localResource.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF))
        } catch (e: Exception) {
            e.printStackTrace()
            Utilities.toastMessageShort(context,localResource.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
        }
    }

    fun getCategoryPositionByCode(code: String): Int {
        var position = 0
        position = if (code.equals("LAB", ignoreCase = true)) {
            1
        } else if (code.equals("HOS", ignoreCase = true)) {
            2
        } else if (code.equals("PRE", ignoreCase = true)) {
            3
        } else if (code.equals("DIET_PLAN", ignoreCase = true)) {
            4
        } else if (code.equals("FIT_PLAN", ignoreCase = true)) {
            5
        } else if (code.equals("OTR", ignoreCase = true)) {
            6
        } else if (code.equals("HRAREPORT", ignoreCase = true)) {
            7
        } else {
            0
        }
        return position
    }

    fun getCategoryByCode(code: String): String {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        return when {
            code.equals("LAB", ignoreCase = true) -> {
                localResource.getString(R.string.PATHOLOGY_REPORT)
            }
            code.equals("HOS", ignoreCase = true) -> {
                localResource.getString(R.string.HOSPITAL_REPORT)
            }
            code.equals("PRE", ignoreCase = true) -> {
                localResource.getString(R.string.DOCTOR_PRESCRIPTION)
            }
            code.equals("DIET_PLAN", ignoreCase = true) -> {
                localResource.getString(R.string.DIET_PLAN)
            }
            code.equals("FIT_PLAN", ignoreCase = true) -> {
                localResource.getString(R.string.FITNESS_PLAN)
            }
            code.equals("OTR", ignoreCase = true) -> {
                localResource.getString(R.string.OTHER_DOCUMENT)
            }
            code.equals("HRAREPORT", ignoreCase = true) -> {
                localResource.getString(R.string.HRA_REPORT)
            }
            else -> {
                localResource.getString(R.string.OTHER_DOCUMENT)
            }
        }
    }

    fun getCategoryList( context: Context ) : ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(localResource.getString(R.string.ALL), "ALL", 0,false))
        list.add(SpinnerModel(localResource.getString(R.string.PATHOLOGY_REPORT), "LAB", 1,false))
        list.add(SpinnerModel(localResource.getString(R.string.HOSPITAL_REPORT), "HOS", 2,false))
        list.add(SpinnerModel(localResource.getString(R.string.DOCTOR_PRESCRIPTION), "PRE", 3,false))
        list.add(SpinnerModel(localResource.getString(R.string.DIET_PLAN), "DIET_PLAN", 4,false))
        list.add(SpinnerModel(localResource.getString(R.string.FITNESS_PLAN), "FIT_PLAN", 5,false))
        list.add(SpinnerModel(localResource.getString(R.string.OTHER_DOCUMENT), "OTR", 6,false))
        list.add(SpinnerModel(localResource.getString(R.string.HRA_REPORT), "HRAREPORT", 7,false))
        return list
    }

    fun viewDocument(recordData : HealthDocument) {
        val recordName = recordData.Name!!
        val recordPath = recordData.Path
        val recordType = recordData.Type
        var type = ""
        when {
            recordType.equals("IMAGE", ignoreCase = true) -> {
                type = "image/*"
            }
            recordType.equals("DOC", ignoreCase = true) -> {
                type = "application/msword"
            }
            recordType.equals("PDF", ignoreCase = true) -> {
                type = "application/pdf"
            }
            fileUtils.getFileExt(recordName).equals("txt", ignoreCase = true) -> {
                type = "text/*"
            }
            else -> {
                type = "application/pdf"
            }
        }
        if (!type.equals("", ignoreCase = true)) {
            if (recordType.equals("IMAGE", ignoreCase = true)) {
                val completeFilePath = "$recordPath/$recordName"
                //val bitmap = RealPathUtil.decodeFile(completeFilePath)
                val bitmap = BitmapFactory.decodeFile(completeFilePath)
                Utilities.showFullImageWithBitmap(bitmap,context,true)
            } else {
                val file = File(recordPath , recordName)
                if (file.exists()) {
                    DataHandler(context).openDownloadedFile(file , type )
                }
            }
        }
    }

    fun viewRecord(recordData : RecordInSession) {
        val recordName = recordData.Name
        val recordPath = recordData.Path
        val recordType = recordData.Type
        var type = ""
        if (recordType.equals("IMAGE", ignoreCase = true)) {
            type = "image/*"
        } else if (recordType.equals("DOC", ignoreCase = true)) {
            type = "application/msword"
        } else if (recordType.equals("PDF", ignoreCase = true)) {
            type = "application/pdf"
        } else if (fileUtils.getFileExt(recordName).equals("txt", ignoreCase = true)) {
            type = "text/*"
        } else  {
            type = "application/pdf"
        }
        if (!type.equals("", ignoreCase = true)) {
            val file = File(recordPath , recordName)
            if (file.exists()) {
                DataHandler(context).openDownloadedFile(file , type)
            }
        }
    }

    fun shareDataWithAppSingle(recordData : HealthDocument,viewModel : HealthRecordsViewModel) {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val completePath = recordData.Path + "/" + recordData.Name
        Timber.e("completePath--->$completePath")
        if (!Utilities.isNullOrEmpty(completePath)) {
            val file = File(completePath)
            val fileToShare = Uri.fromFile(file)
            generateShareIntent(context, fileToShare,viewModel)
            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEALTH_RECORD_SHARED)
        } else {
            Utilities.toastMessageShort(context,localResource.getString(R.string.DOWNLOAD_DOCUMENT_TO_PROCEED))
        }
    }

    fun generateShareIntent(context:Context, fileToShare:Uri,viewModel : HealthRecordsViewModel) {
        try
        {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileToShare)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, viewModel.firstName + " has shared Health Document")
            val himORher = if (viewModel.gender.equals("1",ignoreCase = true)) " him " else " her "
            val hisORher = if (viewModel.gender.equals("1",ignoreCase = true)) " his " else " her "
            val shareBody = ("Hello," + "\n\n" + viewModel.firstName + " has shared  health records with you." + "\n" +
                    Html.fromHtml("<br>" +
                            "Kindly review the records and guide" +
                            himORher +
                            "through" +
                            hisORher +
                            "health related queries." +
                            "</br>" +
                            "<br><br>" +
                            "We hope this electronic health record sharing feature has saved your precious time and provided you with the necessary information. "
//                            +
//                            "If you've liked this feature, please share the Vivant portal " +
//                            "<a href=\"https://portal.vivant.me\">portal.vivant.vivant.me</a> " +
//                            "to connect with your other patients too!" + "</br></br>") +
//                    Html.fromHtml(("<br><br>" + "Team Vivant</br><br>" +
//                            "<a href=\"https://vivant.me\">www.vivant.vivant.me</a></br></br>"
                    ))

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            shareIntent.type = "*/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(shareIntent, " Share with .."))
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

}