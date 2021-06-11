package com.caressa.hra.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.hra.R
import com.caressa.hra.databinding.ItemLabtestBinding
import com.caressa.model.hra.HraLabTest
import com.caressa.hra.ui.HraSummaryActivity
import com.caressa.hra.viewmodel.HraSummaryViewModel


class HraLabTestsAdapter(
    private val labTestsList: List<HraLabTest>,
    val viewModel: HraSummaryViewModel,
    val context: Context,
    val activity: HraSummaryActivity) :
    RecyclerView.Adapter<HraLabTestsAdapter.HraLabTestsHolder>() {

    private val appColorHelper = AppColorHelper.instance!!

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HraLabTestsHolder =
        HraLabTestsHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_labtest, parent, false))

    override fun getItemCount(): Int = labTestsList.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: HraLabTestsHolder, position: Int) {
        val labTest = labTestsList[position]
        holder.bindTo( labTest, viewModel)

        holder.labTestName.text = labTest.LabTestName
        if ( !labTest.Frequency.equals("As suggested by your doctor",ignoreCase = true) ) {
            holder.labTestFrequency.visibility = View.VISIBLE
            holder.labTestFrequency.text = labTest.Frequency
        } else {
            holder.labTestFrequency.visibility = View.GONE
        }

        holder.itemView.setOnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                v.setBackgroundColor(appColorHelper.primaryColor())
                holder.labTestName.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.labTestFrequency.setTextColor(ContextCompat.getColor(context, R.color.white))
/*                if (!labTest.get(GlobalConstants.Frequency).equals("As suggested by your doctor", ignoreCase = true)) {
                    holder.labTestFrequency.setTextColor(context.resources.getColor(R.color.white))
                }*/
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                holder.labTestName.setTextColor(ContextCompat.getColor(context, R.color.vivant_gunmetal))
                holder.labTestFrequency.setTextColor(ContextCompat.getColor(context, R.color.vivant_gunmetal))
/*                if (!labTest.get(GlobalConstants.Frequency).equals("As suggested by your doctor", ignoreCase = true)) {
                    holder.labTestFrequency.setTextColor(appColorHelper.getTextColor())
                }*/
            }
            false
        }

        holder.itemView.setOnClickListener {
            activity.showLabTestDetailsDialog( labTestsList[position] )
        }
    }

   inner class HraLabTestsHolder(view: View) : RecyclerView.ViewHolder(view)  {

       private val binding = ItemLabtestBinding.bind(view)
       val labTestName = binding.txtLabTest
       val labTestFrequency =binding.txtFrequency

       fun bindTo(hraLabTest: HraLabTest, viewModel: HraSummaryViewModel) {
           binding.hraLabtest = hraLabTest
           binding.viewModel = viewModel
       }

    }
}