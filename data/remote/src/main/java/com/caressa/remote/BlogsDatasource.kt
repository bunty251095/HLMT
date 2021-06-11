package com.caressa.remote

class BlogsDatasource(private val blogsService: ApiService ) {

    fun getBlogsResponse(data : String) = blogsService.downloadBlogs(data)

}