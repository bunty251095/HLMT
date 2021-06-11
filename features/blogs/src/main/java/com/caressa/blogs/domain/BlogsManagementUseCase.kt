package com.caressa.blogs.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.caressa.model.blogs.BlogModel
import com.caressa.repository.BlogsRepository
import com.caressa.repository.utils.Resource

class BlogsManagementUseCase( private val blogRepository : BlogsRepository ) {

    suspend fun invokeDownloadBlog(isForceRefresh : Boolean, data: String): LiveData<Resource<List<BlogModel.Blog>>> {
        return Transformations.map(
            blogRepository.downloadBlogs(isForceRefresh,data)) {
            it
        }
    }

}