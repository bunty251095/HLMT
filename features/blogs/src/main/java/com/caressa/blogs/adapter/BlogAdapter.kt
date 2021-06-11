package com.caressa.blogs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.blogs.R
import androidx.recyclerview.widget.RecyclerView
import com.caressa.blogs.databinding.ItemBlogNewBinding
import com.caressa.model.blogs.BlogItem
import com.caressa.blogs.ui.BlogDashboardFragment
import com.caressa.blogs.viewmodel.BlogViewModel

class BlogAdapter(private val fragment : BlogDashboardFragment,
                  private val viewModel : BlogViewModel) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val blogList: MutableList<BlogItem> =  mutableListOf()
    private var mOnBottomReachedListener: OnBottomReachedListener? = null

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {
        this.mOnBottomReachedListener = onBottomReachedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder =
        BlogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_blog_new, parent, false))

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        try {
            val blogItem = blogList[position]
            holder.bindTo( blogItem,viewModel)

            if (position == blogList.size - 1) {
                mOnBottomReachedListener!!.onBottomReached(position)
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun updateData(blogList: List<BlogItem>?) {
            this.blogList.addAll(blogList!!)
            notifyDataSetChanged()
            fragment.stopShimmer()
    }

    inner class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //private val binding = ItemBlogBinding.bind(view)
        private val binding = ItemBlogNewBinding.bind(view)

        fun bindTo(blog : BlogItem, viewModel:BlogViewModel) {
            binding.blog = blog
            binding.viewModel = viewModel
        }

    }

    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

}

