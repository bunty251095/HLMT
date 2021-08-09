package com.caressa.records_tracker.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.Constants
import com.caressa.common.utils.*
import com.caressa.model.entity.HealthDocument
import com.caressa.records_tracker.R
import com.caressa.records_tracker.common.DataHandler
import com.caressa.records_tracker.common.RecordSingleton
import com.caressa.records_tracker.databinding.ItemDocumentBinding
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import java.io.File
import java.util.ArrayList

class HealthRecordsAdapter(val context : Context, val viewModel : HealthRecordsViewModel,
                           private val activity: FragmentActivity, val from : String ) :
    RecyclerView.Adapter<HealthRecordsAdapter.HealthRecordsAdapterViewHolder>() {

    private var recordList: MutableList<HealthDocument> =  mutableListOf()
    var categoryCode = "ALL"
    var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green,
        R.color.vivant_dusky_blue)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthRecordsAdapterViewHolder =
        HealthRecordsAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false))

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: HealthRecordsAdapterViewHolder, position: Int) {
        val record = recordList[position]
        holder.bindTo(record)
        try {
            val code = record.Code!!
            val filename = record.Name!!
            val path = record.Path
            val documentId = record.Id.toString()
            val file = File(path , filename)
            //name = name.substring(0, name.lastIndexOf("."));
            var ext = ""
            if (!Utilities.isNullOrEmpty(filename)) {
                ext = RealPathUtil.getFileExt(filename)
            }

/*            if (!Utilities.isNullOrEmpty(code) && code.equals("LAB", ignoreCase = true)) {
                if (ext.equals("JPEG", ignoreCase = true) || ext.equals("jpg",
                        ignoreCase = true) || ext.equals("png", ignoreCase = true) || ext.equals("pdf",
                        ignoreCase = true)) {
                    holder.txtDigitize.visibility = View.VISIBLE
                } else {
                    holder.txtDigitize.visibility = View.GONE
                }
            } else {
                holder.txtDigitize.visibility = View.GONE
            }*/

            holder.txtDocName.text = record.Title
            holder.txtDocRelation.text = record.PersonName
            if (!Utilities.isNullOrEmpty(record.RecordDate)) {
                holder.txtDocDate.text = DateHelper.convertDateTimeValue(
                    record.RecordDate,
                    DateHelper.SERVER_DATE_YYYYMMDD,
                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW)
            } else {
                holder.txtDocDate.text = " -- "
            }

            if (!file.exists()) {
                holder.imgDownload.visibility = View.VISIBLE
            } else {
                holder.imgDownload.visibility = View.GONE
            }

            if (!Utilities.isNullOrEmpty(code)) {
                when {
                    code.equals("LAB", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[0]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_pathology))
                        holder.txtDocCategory.text = context.resources.getString(R.string.PATHOLOGY_REPORT)
                    }
                    code.equals("HOS", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[1]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_hospital_report))
                        holder.txtDocCategory.text = context.resources.getString(R.string.HOSPITAL_REPORT)
                    }
                    code.equals("PRE", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[2]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_prescription))
                        holder.txtDocCategory.text = context.resources.getString(R.string.DOCTOR_PRESCRIPTION)
                    }
                    code.equals("DIET_PLAN", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[3]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_diet_plan))
                        holder.txtDocCategory.text = context.resources.getString(R.string.DIET_PLAN)
                    }
                    code.equals("FIT_PLAN", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[4]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_fitness_plan))
                        holder.txtDocCategory.text = context.resources.getString(R.string.FITNESS_PLAN)
                    }
                    code.equals("OTR", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[5]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_other))
                        holder.txtDocCategory.text = context.resources.getString(R.string.OTHER_DOCUMENT)
                    }
                    code.equals("HRAREPORT", ignoreCase = true) -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[6]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_hra_report))
                        holder.txtDocCategory.text = context.resources.getString(R.string.HRA_REPORT)
                    }
                    else -> {
                        //holder.view.setBackgroundColor(ContextCompat.getColor(context,color[5]))
                        holder.imgCategory.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.img_other))
                        holder.txtDocCategory.text = DataHandler(context).getCategoryByCode(code)
                    }
                }
            }

            holder.imgDownload.setOnClickListener {
                performAction(Constants.DOWNLOAD,record)
            }

            holder.itemView.setOnClickListener {
                performAction(Constants.VIEW,record)
            }

            holder.imgShare.setOnClickListener {
                performAction(Constants.SHARE,record)
            }

            holder.txtDigitize.setOnClickListener {
                performAction(Constants.DIGITIZE,record)
            }

            holder.imgDelete.setOnClickListener {
                val dialogData = DefaultNotificationDialog.DialogData()
                dialogData.title = context.resources.getString(R.string.DELETE_RECORD)
                dialogData.message = context.resources.getString(R.string.MSG_DELETE_CONFIRMATION)
                dialogData.btnLeftName = context.resources.getString(R.string.NO)
                dialogData.btnRightName = context.resources.getString(R.string.YES)
                val defaultNotificationDialog =
                    DefaultNotificationDialog(activity, object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                            if (isButtonRight) {
                                val docPath = file.toString()
                                if ( !Utilities.isNullOrEmpty(docPath) ) {
                                    viewModel.deleteFileFromLocalSystem(docPath)
                                }
                                val deleteRecordIds: ArrayList<String> = ArrayList()
                                deleteRecordIds.add(documentId)
                                viewModel.callDeleteRecordsApi( deleteRecordIds )
                                //healthRecordsList.remove(document)
                                //recordList.removeAt(position)
                                //notifyDataSetChanged()
                            }
                        }
                    }, dialogData)
                defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                defaultNotificationDialog.show()
            }

        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun performAction(action: String, record : HealthDocument ) {
        val file = File(record.Path , record.Name!!)
        if (file.exists()) {
            when {
                action.equals(Constants.VIEW,ignoreCase = true) -> {
                    DataHandler(context).viewRecord(record)
                }
                action.equals(Constants.SHARE,ignoreCase = true) -> {
                    DataHandler(context).shareDataWithAppSingle(record,viewModel)
                }
                action.equals(Constants.DIGITIZE,ignoreCase = true) -> {
                    viewModel.callDigitizeDocumentApi(from,categoryCode,record)
                }
            }
        } else {
            RecordSingleton.getInstance()!!.setHealthRecord(record)
            viewModel.callDownloadRecordApi( action,record )
        }
    }

    fun updateList(list : MutableList<HealthDocument>, code: String) {
        recordList.clear()
        recordList.addAll(list)
        categoryCode = code
        notifyDataSetChanged()
    }

    class HealthRecordsAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemDocumentBinding.bind(view)
        val view = binding.view
        val txtDigitize = binding.txtDigitize
        val imgCategory = binding.imgCategory
        val txtDocCategory = binding.txtDocCategory
        val txtDocName = binding.txtDocName
        val txtDocRelation = binding.txtDocRelation
        val txtDocDate = binding.txtDocDate
        val imgDownload = binding.imgDownload
        val imgShare = binding.imgShare
        val imgDelete = binding.imgDelete

        fun bindTo(record: HealthDocument) {
            binding.record = record
        }
    }

}