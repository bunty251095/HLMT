package com.caressa.common.base

import com.caressa.common.constants.Constants
import org.json.JSONObject
import timber.log.Timber

object ClientConfiguration {

    private var appTemplateConfig = ""

    init {
        Timber.i("Inside Client Configuration")
        val template = JSONObject()

        val hlmtBlue = "#002D62"
        val hlmtTxtColor = "#000000"

        template.put(Constants.PRIMARY_COLOR,hlmtBlue)
        template.put(Constants.TEXT_COLOR,hlmtTxtColor)
        template.put(Constants.ICON_TINT_COLOR,hlmtBlue)
        template.put(Constants.LEFT_BUTTON_COLOR,"#f2f4f7")
        template.put(Constants.RIGHT_BUTTON_COLOR,hlmtBlue)
        template.put(Constants.LEFT_BUTTON_TEXT_COLOR,hlmtTxtColor)
        template.put(Constants.RIGHT_BUTTON_TEXT_COLOR,"#ffffff")
        template.put(Constants.SELECTION_COLOR,hlmtBlue)
        template.put(Constants.DESELECTION_COLOR,"#f2f4f7")

        appTemplateConfig = template.toString()
    }

    fun getAppTemplateConfig(): String {
        return appTemplateConfig;
    }

}