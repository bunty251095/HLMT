package com.caressa.common.base

import com.caressa.common.constants.Constants
import org.json.JSONObject
import timber.log.Timber

object ClientConfiguration {

    private var appTemplateConfig = ""

    init {
        Timber.i("Inside Client Configuration")
        val template = JSONObject()

        template.put(Constants.PRIMARY_COLOR,"#00c8a1")
        template.put(Constants.TEXT_COLOR,"#3f4846")
        template.put(Constants.ICON_TINT_COLOR,"#00c8a1")
        template.put(Constants.LEFT_BUTTON_COLOR,"#f2f4f7")
        template.put(Constants.RIGHT_BUTTON_COLOR,"#00c8a1")
        template.put(Constants.LEFT_BUTTON_TEXT_COLOR,"#000000")
        template.put(Constants.RIGHT_BUTTON_TEXT_COLOR,"#ffffff")
        template.put(Constants.SELECTION_COLOR,"#00c8a1")
        template.put(Constants.DESELECTION_COLOR,"#f2f4f7")

/*        template.put(Constants.PRIMARY_COLOR, "#ff485d")
        template.put(Constants.TEXT_COLOR, "#3f4846")
        template.put(Constants.ICON_TINT_COLOR, "#ff485d")
        template.put(Constants.LEFT_BUTTON_COLOR, "#f2f4f7")
        template.put(Constants.RIGHT_BUTTON_COLOR, "#ff485d")
        template.put(Constants.LEFT_BUTTON_TEXT_COLOR, "#000000")
        template.put(Constants.RIGHT_BUTTON_TEXT_COLOR, "#ffffff")
        template.put(Constants.SELECTION_COLOR, "#ff485d")
        template.put(Constants.DESELECTION_COLOR, "#f2f4f7")*/

        appTemplateConfig = template.toString()
    }

    fun getAppTemplateConfig(): String {
        return appTemplateConfig;
    }

}