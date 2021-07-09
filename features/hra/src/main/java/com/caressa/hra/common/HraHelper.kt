package com.caressa.hra.common

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.view.Gravity
import android.view.View
import android.widget.*
import com.caressa.common.constants.Configuration
import com.caressa.common.constants.PreferenceConstants
import com.caressa.common.utils.*
import com.caressa.common.view.FlowLayout
import com.caressa.hra.R
import com.caressa.model.hra.Option
import com.caressa.hra.ui.HraQuesBmiFragment
import com.caressa.hra.views.CustomEditTextHra
import com.caressa.model.hra.LabRecordsModel.LabRecordDetails
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object HraHelper {

    fun showHeightDialog( height :Int,layHeight: CustomEditTextHra ,listener : HraQuesBmiFragment,context: Context ) {
        val data = ParameterDataModel()
        data.title = "Height"
        data.value = " - - "
        data.finalValue = height.toString()
        if (layHeight.getUnit().toLowerCase(Locale.ROOT).contains("cm")) {
            data.unit = "cm"
        } else {
            data.unit = "Feet/inch"
        }
        data.code = "HEIGHT"
        val heightWeightDialog = HeightWeightDialog(context,listener,"Height", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    fun showWeightDialog( weight :Int,layWeight: CustomEditTextHra ,listener : HraQuesBmiFragment,context: Context ) {
        val data = ParameterDataModel()
        data.title = "Weight"
        data.value = " - - "
        data.finalValue = weight.toString()

        if (layWeight.getUnit().toLowerCase(Locale.ROOT).contains("lbs")) {
            data.unit = "lbs"
        } else {
            data.unit = "Kg"
        }
        data.code = "WEIGHT"
        val heightWeightDialog = HeightWeightDialog(context,listener,"Weight", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    fun filterLabRecords(labRecords: List<LabRecordDetails>) : List<LabRecordDetails> {

        return labRecords.filter {
            it.ParameterCode.equals("DIAB_RA", ignoreCase = true)
                    || it.ParameterCode.equals("DIAB_FS", ignoreCase = true)
                    || it.ParameterCode.equals("DIAB_PM", ignoreCase = true)
                    || it.ParameterCode.equals("DIAB_HBA1C", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_TOTAL", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_HDL", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_LDL", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_TRY", ignoreCase = true)
                    || it.ParameterCode.equals("CHOL_VLDL", ignoreCase = true)
        }

    }

    fun getParamData(key: String?): ParameterDataModel {
        val param = ParameterDataModel()
        when (key) {
            "CHOL_TOTAL" -> {
                param.code = key
                param.minRange = 50.0
                param.maxRange = 1000.0
            }
            "CHOL_LDL" -> {
                param.code = key
                param.minRange = 10.0
                param.maxRange = 300.0
            }
            "CHOL_HDL" -> {
                param.code = key
                param.minRange = 10.0
                param.maxRange = 100.0
            }
            "CHOL_TRY" -> {
                param.code = key
                param.minRange = 20.0
                param.maxRange = 700.0
            }
            "CHOL_VLDL" -> {
                param.code = key
                param.minRange = 1.0
                param.maxRange = 200.0
            }
            "DIAB_FS" -> {
                param.code = key
                param.minRange = 1.0
                param.maxRange = 999.0
            }
            "DIAB_PM" -> {
                param.code = key
                param.minRange = 61.0
                param.maxRange = 999.0
            }
            "DIAB_RA" -> {
                param.code = key
                param.minRange = 61.0
                param.maxRange = 999.0
            }
            "DIAB_HBA1C" -> {
                param.code = key
                param.minRange = 1.0
                param.maxRange = 100.0
            }
        }
        return param
    }

    fun deselectExceptNone( flowLayout: FlowLayout ) {
        for (i in 0 until flowLayout.childCount ) {
            if ( i != 0 ) {
                val chk = flowLayout.getChildAt(i) as CheckBox
                if ( chk.isChecked ) {
                    chk.isChecked = false
                    Timber.e("${chk.text} is Unchecked")
                }
            }
        }
    }

    fun addButtonsMultiSelection(optionList: ArrayList<Option>, flowLayout: FlowLayout,
                                 listener: CompoundButton.OnCheckedChangeListener,
                                 noneListener : View.OnClickListener,
                                 context: Context) {
        try {
            flowLayout.removeAllViews()
            val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            par.setMargins(10, 10, 10,10)
            //if (mQuestion.getOptions().size() <= 5) btnViewMore.setVisibility(View.GONE)
            for (i in 0 until optionList.size) {
                val chk = CheckBox(context)
                val option = optionList[i]
                chk.apply {
                    id = i
                    tag = option.answerCode
                    text = option.description
                    layoutParams = par
                    buttonDrawable = null
                    setPadding(40, 25, 40, 25)
                    background = ViewUtils.getArcRbSelectorBgHra(true)
                    setTextColor( ViewUtils.getArcRbSelectorTxtColorHra(true) )

                    //background = ContextCompat.getDrawable(context, R.drawable.hra_option_selector_bg)
                    //setTextColor(ContextCompat.getColorStateList(context, R.color.hra_option_selector_color))
                }
                Timber.e("Option ::%s", option.description)
                //if (i >= 5) { chk.visibility = View.GONE }
                chk.setOnCheckedChangeListener(listener)
                if ( i == 0 ) {
                    chk.setOnClickListener(noneListener)
                }
                flowLayout.addView(chk)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addRadioButtonsSingleSelection(optionList: ArrayList<Option>, radioGroup: RadioGroup, listener: View.OnClickListener, context: Context) {
        try {
            radioGroup.removeAllViews()
            val par = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            par.setMargins(10, 10, 10,10)
            for( i in optionList.indices ) {
                val radioButton = RadioButton(context)
                val option = optionList[i]
                radioButton.apply {
                    id = i
                    tag = option.answerCode
                    text = option.description
                    layoutParams = par
                    gravity = Gravity.CENTER
                    buttonDrawable = null
                    setPadding(40, 25, 40, 25)
                    background = ViewUtils.getArcRbSelectorBgHra(true)
                    setTextColor( ViewUtils.getArcRbSelectorTxtColorHra(true) )

                    //background = ContextCompat.getDrawable(context, R.drawable.hra_option_selector_bg)
                    //setTextColor(ContextCompat.getColorStateList(context, R.color.hra_option_selector_color))
                }
                Timber.e("Option ::%s", option.description)
                radioButton.setOnClickListener(listener)
                radioGroup.addView(radioButton)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    fun writeResponseBodyToDisk(body: ResponseBody, context: Context): Boolean {
        try {
            if (RealPathUtil.isExternalStorageAvailable && RealPathUtil.isExternalStorageWritable) {
                //val path = context.getExternalFilesDir(null)
                val path = Environment.getExternalStorageDirectory()
                val file = File(path, RealPathUtil.generateUniqueFileName(Configuration.strAppIdentifier, ".pdf"))
                Timber.i("downloadReportPath: ----->$file")

                var inputStream: InputStream? = null
                var outputStream: OutputStream? = null

                try {
                    val fileReader = ByteArray(4096)
                    //val fileSize = body.contentLength()
                    var fileSizeDownloaded: Long = 0

                    inputStream = body.byteStream()
                    outputStream = FileOutputStream(file)

                    while (true) {
                        val read = inputStream!!.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read.toLong()
                        //  Timber.i("file download: $fileSizeDownloaded of $fileSize")
                    }
                    outputStream.flush()
                    openDownloadedFile(file.toString(), context)
                    return true
                } catch (e: Exception) {
                    Timber.i("Error..." + e.printStackTrace())
                    return false
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            } else {
                return false
            }
        } catch (e: Exception) {
            Timber.i("Error!!!" + e.printStackTrace())
            return false
        }
    }

    private fun openDownloadedFile(downloadedFilePath: String, context: Context) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val file = File(downloadedFilePath)
        val uri = Uri.fromFile(file)

        val intent = Intent(Intent.ACTION_VIEW)
        if (uri.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf")
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Utilities.toastMessageShort(context, context.resources.getString(R.string.no_application_available))
        } catch (e: Exception) {
            e.printStackTrace()
            Utilities.toastMessageShort(context, context.resources.getString(R.string.unable_to_open_file))
        }
    }

}