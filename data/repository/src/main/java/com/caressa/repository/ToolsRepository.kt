package com.caressa.repository

import android.content.Context
import com.caressa.remote.ToolsDatasource

interface ToolsRepository {

}

class ToolsRepositoryImpl(private val datasource: ToolsDatasource, private val context: Context) :
    ToolsRepository {

}