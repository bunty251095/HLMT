package com.caressa.home.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.home.R
import com.caressa.home.common.DataHandler.WellnessCentreDetails
import com.caressa.home.viewmodel.WebViewViewModel
import kotlinx.android.synthetic.main.item_wellness_centre.view.*

class WellnessCentreAdapter( val viewModel : WebViewViewModel , val context: Context) :
    RecyclerView.Adapter<WellnessCentreAdapter.WellnessCentreViewHolder>() {

    private val wellnessCentreList: MutableList<WellnessCentreDetails> = mutableListOf()
    var mOnWellnessCentreItemListener: OnWellnessCentreItemListener? = null

    fun setOnWellnessCentreItemListener( onWellnessCentreItemListener : OnWellnessCentreItemListener ) {
        this.mOnWellnessCentreItemListener = onWellnessCentreItemListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : WellnessCentreViewHolder =
        WellnessCentreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_wellness_centre, parent, false))

    override fun getItemCount(): Int = wellnessCentreList.size

    override fun onBindViewHolder(holder: WellnessCentreViewHolder, position: Int) {
        val wellnessCentreItem = wellnessCentreList[position]

        holder.wellnessCentreLayout.setBackgroundColor(wellnessCentreItem.color)
        holder.wellnessImg.setImageResource(wellnessCentreItem.img)
        holder.wellnessTitle.text = Html.fromHtml(wellnessCentreItem.title)
        holder.wellnessDesc.text = Html.fromHtml(wellnessCentreItem.desc)
/*        if (wellnessCentreItem.tag.equals("VIVANT", ignoreCase = true)) {
            holder.wellnessTitle.visibility = View.VISIBLE
            holder.wellnessImg.visibility = View.GONE
        } else {
            holder.wellnessTitle.visibility = View.GONE
        }*/
       // mOnWellnessCentreItemListener!!.OnWellnessCentreItemClicked(position)
    }

    fun updateWellnessCentreList(items: List<WellnessCentreDetails>) {
        wellnessCentreList.clear()
        wellnessCentreList.addAll(items)
    }

    inner class WellnessCentreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wellnessCentreLayout = view.item_wellness
        val wellnessImg = view.img_wellness_centre
        val wellnessTitle = view.txt_welness_title
        val wellnessDesc = view.txt_welness_desc
        val btnViewMore = view.btn_view_more
        init {
            view.setOnClickListener {
                mOnWellnessCentreItemListener!!.OnWellnessCentreItemClicked(adapterPosition)
            }
            btnViewMore.setOnClickListener {
                mOnWellnessCentreItemListener!!.OnWellnessCentreItemClicked(adapterPosition)
            }
        }
    }

    interface OnWellnessCentreItemListener {
       fun OnWellnessCentreItemClicked(position: Int)
    }

}


