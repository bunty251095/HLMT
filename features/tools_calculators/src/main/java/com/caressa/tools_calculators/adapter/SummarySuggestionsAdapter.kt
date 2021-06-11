package com.caressa.tools_calculators.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caressa.tools_calculators.R
import kotlinx.android.synthetic.main.item_suggestion.view.*

class SummarySuggestionsAdapter(private val context : Context,
                                private val suggestionsList : List<String>, val color : Int ) :
    RecyclerView.Adapter<SummarySuggestionsAdapter.SummarySuggestionsViewHolder>() {

    override fun getItemCount(): Int = suggestionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummarySuggestionsViewHolder =
        SummarySuggestionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false))

    override fun onBindViewHolder(holder: SummarySuggestionsAdapter.SummarySuggestionsViewHolder, position: Int) {
        holder.txtSuggestion.text = suggestionsList[position]
        //holder.imgSuggestion.setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.MULTIPLY)
        holder.imgSuggestion.setBackgroundColor(ContextCompat.getColor(context,color))
        //holder.imgSuggestion.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN);
    }

    inner class SummarySuggestionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSuggestion = view.txt_suggestion!!
        var imgSuggestion = view.img_suggestion!!
    }

}