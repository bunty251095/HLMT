package com.caressa.common.utils

import android.graphics.Color
import com.caressa.common.base.ClientConfiguration
import com.caressa.common.constants.Constants
import org.json.JSONException
import org.json.JSONObject

class AppColorHelper {

    private var primaryColor = Color.GREEN

    val textColor: Int
        get() {
            if (templateJSON.has(Constants.TEXT_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.TEXT_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val iconTintColor: Int
        get() {
            if (templateJSON.has(Constants.ICON_TINT_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.ICON_TINT_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val leftButtonColor: Int
        get() {
            if (templateJSON.has(Constants.LEFT_BUTTON_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.LEFT_BUTTON_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val rightButtonColor: Int
        get() {
            if (templateJSON.has(Constants.RIGHT_BUTTON_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.RIGHT_BUTTON_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val leftButtonTextColor: Int
        get() {
            if (templateJSON.has(Constants.LEFT_BUTTON_TEXT_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.LEFT_BUTTON_TEXT_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val rightButtonTextColor: Int
        get() {
            if (templateJSON.has(Constants.RIGHT_BUTTON_TEXT_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.RIGHT_BUTTON_TEXT_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val selectionColor: Int
        get() {
            if (templateJSON.has(Constants.SELECTION_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.SELECTION_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    val deselectionColor: Int
        get() {
            if (templateJSON.has(Constants.DESELECTION_COLOR)) {
                try {
                    primaryColor = Color.parseColor(templateJSON.getString(Constants.DESELECTION_COLOR))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return primaryColor
        }

    fun primaryColor(): Int {
        if (templateJSON.has(Constants.PRIMARY_COLOR)) {
            try {
                primaryColor = Color.parseColor(templateJSON.getString(Constants.PRIMARY_COLOR))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return primaryColor
    }

    val whiteColor: Int = Color.parseColor("#ffffff")

    companion object {
        var instance: AppColorHelper? = null
            get() {
                if (field == null) {
                    field = AppColorHelper()
                }
                try {
                    templateJSON = JSONObject(ClientConfiguration.getAppTemplateConfig())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return field
            }
            private set
        private var templateJSON = JSONObject()
    }
}
