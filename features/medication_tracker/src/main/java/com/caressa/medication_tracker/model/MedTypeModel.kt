package com.caressa.medication_tracker.model

import com.caressa.medication_tracker.R

data class MedTypeModel (
    var medTypeTitle:String = "",
    var medTypeCode: String = "",
    var medTypeImageId: Int = R.drawable.img_capsul
)