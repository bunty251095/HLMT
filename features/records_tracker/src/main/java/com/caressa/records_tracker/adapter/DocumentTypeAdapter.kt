package com.caressa.records_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.records_tracker.R
import com.caressa.records_tracker.databinding.ItemDocTypeBinding
import com.caressa.records_tracker.model.DocumentType
import com.caressa.records_tracker.ui.DocumentTypeFragment

class DocumentTypeAdapter(val fragment : DocumentTypeFragment, val context : Context, private val documentTypeList : List<DocumentType>) :
    RecyclerView.Adapter<DocumentTypeAdapter.DocumentTypeViewHolder>() {

    private var selectedPos = -1
    private val appColorHelper = AppColorHelper.instance!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentTypeAdapter.DocumentTypeViewHolder =
        DocumentTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_doc_type, parent, false))


    override fun getItemCount(): Int = documentTypeList.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: DocumentTypeViewHolder, position: Int) {
        val docType = documentTypeList[position]
        holder.bindTo(docType)

        holder.layoutOption.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        ImageViewCompat.setImageTintList(holder.imgOption, ColorStateList.valueOf(appColorHelper.primaryColor()))
        holder.txtTitle.setTextColor( appColorHelper.textColor )

        holder.layoutOption.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor( appColorHelper.primaryColor() )
                ImageViewCompat.setImageTintList(holder.imgOption, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
                holder.txtTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                ImageViewCompat.setImageTintList(holder.imgOption, ColorStateList.valueOf(appColorHelper.primaryColor()))
                holder.txtTitle.setTextColor( appColorHelper.textColor )
            }
            false
        }

        holder.layoutOption.setOnClickListener { view: View ->
            fragment.setDocTypeCode(docType.code)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
            fragment.performNextBtnClick(view)
        }

    }

    inner class DocumentTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemDocTypeBinding.bind(view)
        val layoutOption = binding.layoutOption
        val imgOption = binding.imgOption
        val txtTitle = binding.txtOption

        fun bindTo( docType : DocumentType) {
            binding.docType = docType
        }

    }
}