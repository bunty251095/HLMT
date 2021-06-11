package com.caressa.medication_tracker.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.DateHelper
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.model.MedStatusModel
import kotlinx.android.synthetic.main.item_status.view.*
import java.text.SimpleDateFormat
import java.util.*

class MedStatusAdapter(val context:Context, private var pos:Int, private var statusList:List<MedStatusModel>,
                       private var selectedPos: Int, var time : String, private var selectedDate : String,
                       private var listener : OnStatusChangeListener, private var futureListener : OnFutureListener) :
    RecyclerView.Adapter<MedStatusAdapter.MedTimeStatusViewHolder>() {

    override fun getItemCount(): Int = statusList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedTimeStatusViewHolder =
        MedTimeStatusViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false))

    override fun onBindViewHolder(holder: MedTimeStatusViewHolder, position: Int) {
        try {
            val statusDetails = statusList[position]
            holder.txtStatus.text = statusDetails.statusTitle
            holder.imgStatus.setImageResource(statusDetails.imageId)

            val sdf =
                SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val strDate = sdf.parse(selectedDate)
            val currentDate = sdf.parse(DateHelper.currentDateAsStringyyyyMMdd)

            val dateFormat =
                SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            val currentTime = dateFormat.parse(dateFormat.format(Date()))
            val medTime = dateFormat.parse(time)
            val differenceInMinutes =
                (currentTime!!.time - medTime!!.time) / (60 * 1000)

            if (strDate == currentDate) {
                // To check whether given time is earlier than current time
                //Also it will be Enabled to Update 60 Minitues before Current Time
                //if( medTime.before(currentTime) ) {
                if (medTime.before(currentTime) || differenceInMinutes >= -60) {
                    if (selectedPos == position) { holder.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.white))
                        ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
                        holder.layoutStatus.background = ResourcesCompat.getDrawable(context.resources,R.drawable.btn_oval_fill_round,null)
                        holder.layoutStatus.background.setColorFilter(ContextCompat.getColor(context,statusDetails.color), PorterDuff.Mode.SRC_ATOP)
                    } else {
                        holder.txtStatus.setTextColor(ContextCompat.getColor(context,statusDetails.color))
                        ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,statusDetails.color)))
                        holder.layoutStatus.background = ResourcesCompat.getDrawable(context.resources,R.drawable.border_fill_oval,null)
                        holder.layoutStatus.background.setColorFilter(ContextCompat.getColor(context,statusDetails.color), PorterDuff.Mode.SRC_ATOP)
                    }
                } else {
                    // Disable Medicine times from Current time
                    futureListener.onFutureChange(pos, true)
                    holder.layoutStatus.isEnabled = false
                    holder.layoutStatus.background.setColorFilter(context.getResources().getColor(R.color.vivant_disabled), PorterDuff.Mode.SRC_ATOP)
                    ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_disabled)))
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.vivant_disabled))
                }
            } else if (strDate!!.before(currentDate)) {
                if (selectedPos == position) {
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.white))
                    ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
                    holder.layoutStatus.background = ResourcesCompat.getDrawable(context.resources,R.drawable.btn_oval_fill_round,null)
                    holder.layoutStatus.background.setColorFilter(ContextCompat.getColor(context,statusDetails.color), PorterDuff.Mode.SRC_ATOP)
                } else {
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context,statusDetails.color))
                    ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,statusDetails.color)))
                    holder.layoutStatus.background = ResourcesCompat.getDrawable(context.resources,R.drawable.border_fill_oval,null)
                    holder.layoutStatus.background.setColorFilter(ContextCompat.getColor(context,statusDetails.color), PorterDuff.Mode.SRC_ATOP)
                }
            } else {
                // Disable Medicine times for future Dates
                futureListener.onFutureChange(pos, true)
                holder.layoutStatus.isEnabled = false
                holder.layoutStatus.background.setColorFilter(ContextCompat.getColor(context,R.color.vivant_disabled), PorterDuff.Mode.SRC_ATOP)
                ImageViewCompat.setImageTintList(holder.imgStatus, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.vivant_disabled)))
                holder.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.vivant_disabled))
            }

            holder.itemView.setOnClickListener {
                listener.onStatusChange(pos, statusDetails.statusTitle)
                notifyItemChanged(selectedPos)
                selectedPos = position
                notifyItemChanged(selectedPos)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateSelectedPosStatus(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnStatusChangeListener {
        fun onStatusChange(position: Int, status: String)
    }

    interface OnFutureListener {
        fun onFutureChange(position: Int, isFuture: Boolean)
    }

    inner class MedTimeStatusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtStatus = view.txt_status!!
        val imgStatus = view.img_status!!
        val layoutStatus = view.layout_status!!
    }

}