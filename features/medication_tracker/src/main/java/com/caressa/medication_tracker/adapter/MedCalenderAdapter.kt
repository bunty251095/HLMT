package com.caressa.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.model.MedCalender
import com.caressa.medication_tracker.ui.MedicineDashboardFragment
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import kotlinx.android.synthetic.main.item_medicine_calender.view.*

class MedCalenderAdapter(val viewModel : MedicineTrackerViewModel, val context: Context,
    pos: Int,val fragment:MedicineDashboardFragment) :
    RecyclerView.Adapter<MedCalenderAdapter.MedCalenderViewHolder>() {

    private var calenderDateList: MutableList<MedCalender> = mutableListOf()
    private var listener: OnDateClickListener = fragment
    private var selectedPos = pos
    private var todayPos = selectedPos

    override fun getItemCount(): Int = calenderDateList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedCalenderViewHolder =
        MedCalenderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_medicine_calender, parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MedCalenderViewHolder, position: Int) {
        try {
            val date = calenderDateList[position]
            if (date.IsToday) {
                holder.txtDate.text = context.resources.getString(R.string.TODAY)
            } else {
                holder.txtDate.text = date.Month + " " + date.DayOfMonth
            }
            holder.txtDayName.text = date.DayOfWeek
            holder.txtDay.text = date.DayOfMonth
            if (selectedPos == position) {
                //listener.onDateSelection(date,position);
                holder.layoutSelectedDay.background.setColorFilter(ContextCompat.getColor(context,R.color.vivant_heather), PorterDuff.Mode.SRC_ATOP)
                if (position == todayPos) {
                    holder.txtDate.setTextColor(ContextCompat.getColor(context,R.color.vivant_heather))
                } else {
                    holder.txtDate.setTextColor(ContextCompat.getColor(context,R.color.vivant_greyish))
                }
                holder.txtDayName.setTextColor(ContextCompat.getColor(context,R.color.white))
                holder.txtDay.setTextColor(ContextCompat.getColor(context,R.color.white))
                holder.imgDot.visibility = View.VISIBLE
            } else {
                holder.layoutSelectedDay.background.setColorFilter(ContextCompat.getColor(context,R.color.white), PorterDuff.Mode.SRC_ATOP)
                if ( date.IsToday ) {
                    holder.txtDate.setTextColor(ContextCompat.getColor(context,R.color.vivant_heather))
                } else {
                    holder.txtDate.setTextColor(ContextCompat.getColor(context,R.color.white))
                }
                holder.txtDayName.setTextColor(ContextCompat.getColor(context,R.color.textViewColor))
                holder.txtDay.setTextColor(ContextCompat.getColor(context,R.color.textViewColor))
                holder.imgDot.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                listener.onDateSelection(date, position)
                notifyItemChanged(selectedPos)
                selectedPos = position
                notifyItemChanged(selectedPos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateList(list : MutableList<MedCalender>) {
        calenderDateList.clear()
        calenderDateList.addAll(list)
        notifyDataSetChanged()
    }

    interface OnDateClickListener {
        fun onDateSelection(date: MedCalender, newDayPosition: Int)
    }

    inner class MedCalenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate = view.txt_date!!
        val txtDayName = view.txt_day_name!!
        val txtDay = view.txt_day!!
        val layoutSelectedDay = view.layout_selected_day!!
        val imgDot = view.img_dot!!
    }

}