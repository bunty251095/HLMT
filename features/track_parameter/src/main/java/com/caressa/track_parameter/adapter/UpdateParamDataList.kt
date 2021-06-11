package com.caressa.track_parameter.adapter

import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.ParameterListModel

data class UpdateParamDataList(var headerName:String ="", var date:String = "",var listPosition: Int = 0, var status: Boolean = false,
var childList:List<TrackParameterMaster.Parameter> = listOf()
)