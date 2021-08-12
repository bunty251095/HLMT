package com.caressa.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.caressa.model.blogs.BlogModel
import com.caressa.remote.BlogsDatasource
import com.caressa.repository.utils.NetworkDataBoundResource
import com.caressa.repository.utils.Resource
import kotlinx.coroutines.Deferred

interface BlogsRepository {

    suspend fun downloadBlogs(
        forceRefresh: Boolean = false,
        data: String
    ): LiveData<Resource<List<BlogModel.Blog>>>

}

class BlogsRepositoryImpl(private val datasource: BlogsDatasource, private val context: Context) :
    BlogsRepository {

    override suspend fun downloadBlogs(
        forceRefresh: Boolean,
        data: String
    ): LiveData<Resource<List<BlogModel.Blog>>> {

        return object :
            NetworkDataBoundResource<List<BlogModel.Blog>, List<BlogModel.Blog>>(context) {
            override fun processResponse(response: List<BlogModel.Blog>): List<BlogModel.Blog> {
                return response
            }

            override fun createCallAsync(): Deferred<List<BlogModel.Blog>> {
                return datasource.getBlogsResponse(data)
            }

        }.build().asLiveData()

    }

}