package com.caressa.tools_calculators.model

import com.caressa.tools_calculators.R

class TrackerDashboardModel(
    name: String,
    description: String,
    imageId: Int,
    color: Int,
    code: String) {
    var name = ""
    var description = ""
    var imageId = 0
    var color: Int = R.color.textViewColor
    var code = "NA"

    init {
        this.name = name
        this.description = description
        this.imageId = imageId
        this.color = color
        this.code = code
    }
}