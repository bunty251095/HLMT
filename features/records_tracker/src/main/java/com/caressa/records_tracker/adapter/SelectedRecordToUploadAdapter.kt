package com.caressa.records_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.Utilities
import com.caressa.model.entity.RecordInSession
import com.caressa.records_tracker.R
import com.caressa.records_tracker.databinding.ItemSelectedRecordBinding
import com.caressa.records_tracker.ui.SelectRelationFragment
import timber.log.Timber

class SelectedRecordToUploadAdapter(val fragment : SelectRelationFragment, val context : Context ) :
    RecyclerView.Adapter<SelectedRecordToUploadAdapter.SelectedRecordToUploadViewHolder>() {

    var documentList: MutableList<RecordInSession> =  mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedRecordToUploadViewHolder =
        SelectedRecordToUploadViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_record, parent, false))

    override fun getItemCount(): Int = documentList.size

    override fun onBindViewHolder(holder: SelectedRecordToUploadViewHolder, position: Int) {

        val recordInSession = documentList[position]
        holder.bindTo(recordInSession)
        holder.imgCancel.tag = position

        holder.imgCancel.setOnClickListener {
            try {
                if ( !documentList.isNullOrEmpty() && documentList.size >= position) {
                    fragment.deleteRecordInSession(recordInSession)
                    Timber.e("Removed_Item_At--->$position")
                    documentList.removeAt(position)
                    notifyDataSetChanged()
                }
                if ( !documentList.isNullOrEmpty() ) {
                    fragment.setNoDataView(false)
                } else {
                    fragment.setNoDataView(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateData( list: MutableList<RecordInSession> ) {
        this.documentList.clear()
        this.documentList.addAll(list)
        Utilities.printData("RecordToUploadList",list)
        this.notifyDataSetChanged()
    }

/*    private fun removeItem(position: Int) {
        Timber.e("Removed_Item_At--->$position")
        this.documentList.removeAt(position)
        this.notifyItemRemoved(position)
        this.notifyDataSetChanged()
    }*/

    inner class SelectedRecordToUploadViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemSelectedRecordBinding.bind(view)
        val imgCancel = binding.imgCancelDoc

        fun bindTo( recordInSession : RecordInSession ) {
            binding.recordInSession = recordInSession
        }
    }
}