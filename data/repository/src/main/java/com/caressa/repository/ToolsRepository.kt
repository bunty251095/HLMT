package com.caressa.repository

import com.caressa.remote.ToolsDatasource

interface ToolsRepository {

}

class ToolsRepositoryImpl(
    private val datasource: ToolsDatasource
) : ToolsRepository {

}