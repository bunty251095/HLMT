package com.caressa.blogs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caressa.blogs.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caressa.blogs.adapter.BlogAdapter
import com.caressa.blogs.databinding.FragmentBlogsDashboardBinding
import com.caressa.blogs.viewmodel.BlogViewModel
import com.caressa.common.base.BaseFragment
import com.caressa.common.base.BaseViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import com.caressa.blogs.adapter.BlogAdapter.OnBottomReachedListener

class BlogDashboardFragment : BaseFragment() {

    private val viewModel: BlogViewModel by viewModel()
    private lateinit var binding : FragmentBlogsDashboardBinding

    private var blogAdapter: BlogAdapter? = null
    private val visibleThreshold = 10
    private var currentPage = 0

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBlogsDashboardBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        startShimmer()
        initialise()
        configureRecyclerView()
        return binding.root
    }

    private fun initialise() {
        viewModel.callGetBlogsFromServerApi(visibleThreshold ,currentPage,this)
        binding.rvBlogs.addOnScrollListener( object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING ) {
                    binding.pbLoadBlogs.visibility = View.VISIBLE
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.pbLoadBlogs.visibility = View.GONE
                }
            }
        } )
    }

    private fun configureRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.rvBlogs.layoutManager = layoutManager
        blogAdapter = BlogAdapter( this, viewModel )
        binding.rvBlogs.adapter = blogAdapter
        blogAdapter!!.setOnBottomReachedListener(object : OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                currentPage += 10
                viewModel.callGetBlogsFromServerApi(visibleThreshold ,currentPage,this@BlogDashboardFragment)
            }
        })
        viewModel.blogList.observe( viewLifecycleOwner , Observer {})
    }

    private fun startShimmer() {
        binding.layoutShimmer.startShimmer()
        binding.layoutShimmer.visibility = View.VISIBLE
    }

    fun stopShimmer() {
        binding.layoutShimmer.stopShimmer()
        binding.layoutShimmer.visibility = View.GONE
    }

}
