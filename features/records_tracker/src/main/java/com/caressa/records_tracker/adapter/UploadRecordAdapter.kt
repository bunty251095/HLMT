package com.caressa.records_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.RecordInSession
import com.caressa.records_tracker.R
import com.caressa.records_tracker.databinding.ItemUploadRecordBinding
import com.caressa.records_tracker.ui.UploadRecordFragment
import com.caressa.records_tracker.viewmodel.HealthRecordsViewModel
import timber.log.Timber

class UploadRecordAdapter(val fragment : UploadRecordFragment,val context: Context ,val viewModel: HealthRecordsViewModel ) :
    RecyclerView.Adapter<UploadRecordAdapter.UploadRecordViewHolder>() {

  internal var uploadRecordList: MutableList<RecordInSession> =  mutableListOf()
    var listener: ShowNoDataListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadRecordViewHolder =
        UploadRecordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_upload_record, parent, false))

    override fun getItemCount(): Int = uploadRecordList.size

    override fun onBindViewHolder(holder: UploadRecordViewHolder, position: Int) {
        val recordInSession = uploadRecordList[position]
        holder.bindTo(recordInSession)
        holder.imgCancelDoc.tag = position

        holder.imgCancelDoc.setOnClickListener {
            try {
                if ( uploadRecordList.size != 0 && uploadRecordList.size >= position) {
                    removeItem(position)
                    viewModel.deleteRecordInSession(recordInSession)
                }
                if (uploadRecordList.size > 0) {
                    fragment.setListVisibility(true)
                } else {
                    fragment.setListVisibility(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateList( list : MutableList<RecordInSession> ) {
        Utilities.printData("UploadDataList",list)
        this.uploadRecordList.clear()
        this.uploadRecordList.addAll(list)
        this.notifyDataSetChanged()
    }

    fun insertItem(item : RecordInSession,position :Int ) {
        Timber.e("Inserted_Item_At--->$position")
        this.uploadRecordList.add(item)
        this.notifyItemInserted(position)
    }

    private fun removeItem(position: Int) {
        Timber.e("Removed_Item_At--->$position")
        this.uploadRecordList.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyDataSetChanged()
    }

    inner class UploadRecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemUploadRecordBinding.bind(view)
        val imgCancelDoc = binding.imgCancelDoc

        fun bindTo( recordInSession : RecordInSession ) {
            binding.recordInSession = recordInSession
        }
    }

    interface ShowNoDataListener {
        fun showNoData(show: Boolean)
    }
}