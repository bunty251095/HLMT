package com.caressa.tools_calculators.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.AppColorHelper
import com.caressa.tools_calculators.R
import com.caressa.tools_calculators.databinding.ExpandableListItemBinding
import com.caressa.tools_calculators.model.ExpandModel
import com.caressa.tools_calculators.model.SubSectionModel
import com.caressa.tools_calculators.views.ExpandableLinearLayout
import java.util.*

class DiabetesReportExpandableAdapter(val recyclerView: RecyclerView,
                                      private val expandableListDetail: ArrayMap<String, ArrayList<SubSectionModel>>,
                                      val data: MutableList<ExpandModel>, val context: Context) :
    RecyclerView.Adapter<DiabetesReportExpandableAdapter.DiabetesReportExpandableViewHolder>() {

    private var lastExpandedCardPosition = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = expandableListDetail.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiabetesReportExpandableViewHolder =
        DiabetesReportExpandableViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.expandable_list_item, parent, false))

    override fun onBindViewHolder(holder: DiabetesReportExpandableViewHolder, position: Int) {

        holder.txtTitleCount.setBackgroundColor( appColorHelper.primaryColor() )
        holder.txtTitleCount.text = (position+1).toString()
        holder.mTitle.text = expandableListDetail.keyAt(position)

        val subSectionModelArrayList = expandableListDetail[expandableListDetail.keyAt(position)]
        holder.mContainer.removeAllViews()
        var textView: TextView
        val li = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val descParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (subSectionModelArrayList != null) {
            for (i in subSectionModelArrayList.indices) {
                textView = TextView(context)
                textView.setLineSpacing(11f, 1f)
                if (subSectionModelArrayList[i].type.equals("description",ignoreCase = true)) {
                    textView = li.inflate(R.layout.textview_description, null) as TextView
                    textView.text = subSectionModelArrayList[i].text
                    textView.setTextAppearance(context, R.style.VivantDescription)
                    if (i + 1 == subSectionModelArrayList.size) {
                        textView.setPadding(0, 0, 0, 30)
                    }
                    descParam.setMargins(10, 10, 10, 20)
                    textView.layoutParams = descParam
                    holder.mContainer.addView(textView)
                } else if (subSectionModelArrayList[i].type.equals("title",ignoreCase = true)) {
                    textView = li.inflate(R.layout.textview_title, null) as TextView
                    textView.text = subSectionModelArrayList[i].text
                    textView.setTextAppearance(context, R.style.VivantTitle)
                    descParam.setMargins(10, 10, 10, 10)
                    textView.layoutParams = descParam
                    holder.mContainer.addView(textView)
                } else if (subSectionModelArrayList[i].type.equals("ListItem",ignoreCase = true)) {
                    for (j in 0 until subSectionModelArrayList[i].bulletPointList.size) {
                        textView = li.inflate(R.layout.textview_bullete, null) as TextView
                        setTextViewDrawableColor(textView,appColorHelper.primaryColor())
                        textView.text = subSectionModelArrayList[i].bulletPointList[j]
                        textView.setTextAppearance(context, R.style.VivantDescription)
                        descParam.setMargins(30, 10, 10, 10)
                        textView.layoutParams = descParam
                        holder.mContainer.addView(textView)
                    }
                }
            }
        }

        if (data[position].isExpanded) {
            holder.expandableView.visibility = View.VISIBLE
            holder.expandableView.setExpanded(true)
        } else {
            holder.expandableView.visibility = View.GONE
            holder.expandableView.setExpanded(false)
        }

    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawablesRelative) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
    }


/*    fun addItem(i: Int) {
        data.add(i, ExpandModel())
        if (i <= lastExpandedCardPosition) lastExpandedCardPosition++
        notifyDataSetChanged()
    }

    fun deleteItem(i: Int) {
        data.removeAt(i)
        notifyDataSetChanged()
    }

    fun refreshList(i: Int) {
        notifyItemChanged(i)
    }*/

    inner class DiabetesReportExpandableViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ExpandableListItemBinding.bind(view)

        var mTitle  = binding.txtSuggestion
        var txtTitleCount  = binding.txtSuggestionNo
        var expandableView  = binding.expandableView
        var mContainer  = binding.layContainer
        private var mImgExpand  = binding.imageView

        private var expandListener = object : ExpandableLinearLayout.ExpandListener {

            override fun onExpandComplete() {
                if (lastExpandedCardPosition != adapterPosition && recyclerView.findViewHolderForAdapterPosition(
                        lastExpandedCardPosition) != null) {
                    (recyclerView.findViewHolderForAdapterPosition(lastExpandedCardPosition)!!.itemView.findViewById<View>(
                        R.id.expandableView) as ExpandableLinearLayout).setExpanded(false)
                    data[lastExpandedCardPosition].isExpanded = false
                    (recyclerView.findViewHolderForAdapterPosition(lastExpandedCardPosition)!!.itemView.findViewById<View>(
                        R.id.expandableView) as ExpandableLinearLayout).toggle()
                    (recyclerView.findViewHolderForAdapterPosition(lastExpandedCardPosition)!!.itemView.findViewById<View>(
                        R.id.imageView) as ImageView).setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_add))
                } else if (lastExpandedCardPosition != adapterPosition && recyclerView.findViewHolderForAdapterPosition(
                        lastExpandedCardPosition) == null) {
                    data[lastExpandedCardPosition].isExpanded = false
                }
                lastExpandedCardPosition = adapterPosition
                //refreshList(lastExpandedCardPosition);
            }

            override fun onCollapseComplete() {}
        }

        init {
            expandableView.setExpandListener(expandListener)

            itemView.setOnClickListener {
                if (expandableView.isExpanded()) {
                    expandableView.setExpanded(false)
                    expandableView.toggle()
                    data[adapterPosition].isExpanded = false
                    ContextCompat.getDrawable(context, R.drawable.ic_remove)
                    mImgExpand.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.add))
                } else {
                    expandableView.setExpanded(true)
                    data[adapterPosition].isExpanded = true
                    expandableView.toggle()
                    mImgExpand.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_remove))
                }
            }

        }
    }

}