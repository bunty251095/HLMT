package com.caressa.records_tracker.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.text.Html
import com.caressa.common.utils.RealPathUtil
import com.caressa.common.utils.Utilities
import com.caressa.common.view.SpinnerModel
import com.caressa.model.entity.HealthDocument
import com.caressa.records_tracker.R
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import timber.log.Timber
import java.io.*
import java.util.*

class DataHandler(val context: Context) {

    fun writeRecordToDisk(inputPath: String, outputPath: String, fileName : String): Boolean {
        try {
            val path = Environment.getExternalStorageDirectory()
            val file = File( outputPath ,  fileName )
            Timber.i("downloadDocPath: ----->" + file)

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)
                var fileSizeDownloaded: Long = 0

                inputStream = FileInputStream(File(inputPath))
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream!!.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    //  Timber.i("file download: $fileSizeDownloaded of $fileSize")
                }
                outputStream!!.flush()
                return true
            } catch (e: Exception) {
                Timber.i("Error..."+e.printStackTrace())
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            Timber.i("Error!!!"+e.printStackTrace())
            return false
        }
    }

    @Throws(IOException::class)
    fun saveBitampAsFile(bitmap: Bitmap?, outputPath: String, fileName : String): Boolean {
        var save = false

        if (bitmap != null ) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var byteArray: ByteArray? = stream.toByteArray() // convert camera photo to byte array
            val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
            // save it in your external storage.
            val fo = FileOutputStream(File(outputPath , fileName))
            fo.write(byteArray)
            fo.flush()
            fo.close()
            byteArray = null
            compressedBitmap.recycle()
            save = true
        } else {
            save = false
        }
        return save
    }

    fun openDownloadedFile( file : File , type :String ,  context: Context) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file),type)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        //val openIntent = Intent.createChooser(intent,"Open using")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF))
        } catch (e: Exception) {
            e.printStackTrace()
            Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
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

    fun getCategorByCode(code: String): String? {
        var Category = ""
        if (code.equals("LAB", ignoreCase = true)) {
            Category = "Pathology Report"
        } else if (code.equals("HOS", ignoreCase = true)) {
            Category = "Hospital Report"
        } else if (code.equals("PRE", ignoreCase = true)) {
            Category = "Doctor Prescription"
        } else if (code.equals("DIET_PLAN", ignoreCase = true)) {
            Category = "Diet Plan"
        } else if (code.equals("FIT_PLAN", ignoreCase = true)) {
            Category = "Fitness Plan"
        } else if (code.equals("OTR", ignoreCase = true)) {
            Category = "Other Document"
        } else if (code.equals("HRAREPORT", ignoreCase = true)) {
            Category = "HRA Report"
        } else {
            Category = "Other Document"
        }
        return Category
    }

    fun getCategoryList( context: Context ) : ArrayList<SpinnerModel> {
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(context.getResources().getString(R.string.all), "ALL", 0,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.pathology_report), "LAB", 1,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.hospital_report), "HOS", 2,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.prescription), "PRE", 3,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.diet_plan), "DIET_PLAN", 4,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.fitness_plan), "FIT_PLAN", 5,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.other_document), "OTR", 6,false))
        list.add(SpinnerModel(context.getResources().getString(R.string.hra_report), "HRAREPORT", 7,false))
        return list
    }

    fun viewRecord(recordData : HealthDocument) {
        val recordName = recordData.Name!!
        val recordPath = recordData.Path
        val recordType = recordData.Type
        var type = ""
        if (recordType.equals("IMAGE", ignoreCase = true)) {
            type = "image/*"
        } else if (recordType.equals("DOC", ignoreCase = true)) {
            type = "application/msword"
        } else if (recordType.equals("PDF", ignoreCase = true)) {
            type = "application/pdf"
        } else if (RealPathUtil.getFileExt(recordName!!).equals("txt", ignoreCase = true)) {
            type = "text/*"
        } else  {
            type = "application/pdf"
        }
        if (!type.equals("", ignoreCase = true)) {
            val file = File(recordPath , recordName!!)
            if (file.exists()) {
                DataHandler(context!!).openDownloadedFile(file , type , context!!)
            }
        }
    }

    fun shareDataWithAppSingle(recordData : HealthDocument,viewModel : HealthRecordsViewModel) {
        val completePath = recordData.Path + "/" + recordData.Name
        Timber.e("completePath--->$completePath")
        if (!Utilities.isNullOrEmpty(completePath)) {
            val file = File(completePath)
            val fileToShare = Uri.fromFile(file)
            generateShareIntent(context!!, fileToShare,viewModel)
        } else {
            Utilities.toastMessageShort(context, "Please download the document to share")
        }
    }

    fun generateShareIntent(context:Context, fileToShare:Uri,viewModel : HealthRecordsViewModel) {
        try
        {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileToShare)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, viewModel.firstName + " has shared Health Document")
            val himORher = if (viewModel.firstName.equals("1",ignoreCase = true)) " him " else " her "
            val hisORher = if (viewModel.firstName.equals("1",ignoreCase = true)) " his " else " her "
            val shareBody = ("Hello," + "\n\n" + viewModel.firstName + " has shared  health records with you." + "\n" +
                    Html.fromHtml("<br>" +
                            "Kindly review the records and guide" +
                            himORher +
                            "through" +
                            hisORher +
                            "health related queries." +
                            "</br>" +
                            "<br><br>" +
                            "We hope this electronic health record sharing feature has saved your precious time and provided you with the necessary information. " +
                            "If you've liked this feature, please share the Vivant portal " +
                            "<a href=\"https://portal.vivant.me\">portal.vivant.vivant.me</a> " +
                            "to connect with your other patients too!" + "</br></br>") +
                    Html.fromHtml(("<br><br>" + "Team Vivant</br><br>" +
                            "<a href=\"https://vivant.me\">www.vivant.vivant.me</a></br></br>")))

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