package com.caressa.tools_calculators.adapter

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
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.ItemToolsDashboardBinding
import com.caressa.tools_calculators.model.TrackerDashboardModel

class TrackersDashboardAdapter(private val context: Context,
                               private val listener: OnCalculatorClickListener) :
    RecyclerView.Adapter<TrackersDashboardAdapter.TrackersDashboardViewHolder>()  {

    private val trackersList: MutableList<TrackerDashboardModel> = mutableListOf()

    override fun getItemCount(): Int = trackersList.size

    var color = intArrayOf(
        R.color.vivantNavy,
        R.color.vivantGreen,
        R.color.vivant_nasty_green,
        R.color.vivantOrange,
        R.color.vivantCyan,
        R.color.vivantRed)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackersDashboardViewHolder =
        TrackersDashboardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tools_dashboard, parent, false))

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TrackersDashboardViewHolder, position: Int) {
        val tracker = trackersList[position]
        holder.bindTo(tracker)

        holder.layoutTracker.setOnTouchListener { _: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE) {
                holder.cardTracker.setBackgroundColor(ContextCompat.getColor(context,getColor(position)))
                holder.view.setBackgroundColor(ContextCompat.getColor(context,getColor(position)))
                ImageViewCompat.setImageTintList(holder.imgTracker, ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)))
                holder.txtTrackerName.setTextColor(ContextCompat.getColor(context,R.color.white))
                holder.txtTrackerDesc.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                holder.cardTracker.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                holder.view.setBackgroundColor(ContextCompat.getColor(context,getColor(position)))
                ImageViewCompat.setImageTintList(holder.imgTracker, ColorStateList.valueOf(ContextCompat.getColor(context,getColor(position))))
                holder.txtTrackerName.setTextColor(ContextCompat.getColor(context,R.color.textViewColorSecondary))
                holder.txtTrackerDesc.setTextColor(ContextCompat.getColor(context,R.color.vivant_edit_textcolor))
            }
            false
        }

    }

    fun updateTrackersList(items: MutableList<TrackerDashboardModel>) {
        trackersList.clear()
        trackersList.addAll(items)
    }

    private fun getColor(position: Int): Int {
        return color[position]
    }

    inner class TrackersDashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemToolsDashboardBinding.bind(view)

        val cardTracker = binding.cardTracker
        val layoutTracker = binding.layoutTracker
        val view = binding.view
        val imgTracker = binding.imgTracker
        val txtTrackerName = binding.txtTrackerName
        val txtTrackerDesc = binding.txtTrackerDesc

        fun bindTo(trackerItem: TrackerDashboardModel) {
            binding.trackerItem = trackerItem
        }

        init {
            view.setOnClickListener {
                listener.onCalculatorSelection(trackersList[adapterPosition], it)
            }
        }
    }

    interface OnCalculatorClickListener {
        fun onCalculatorSelection(trackerDashboardModel: TrackerDashboardModel, view: View)
    }

}