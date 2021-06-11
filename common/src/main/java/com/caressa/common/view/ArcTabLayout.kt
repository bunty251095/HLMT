package com.caressa.common.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.caressa.common.R
import com.caressa.common.base.ClientConfiguration
import com.caressa.common.constants.Constants
import com.google.android.material.tabs.TabLayout
import org.json.JSONObject
import timber.log.Timber

class ArcTabLayout : TabLayout {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        styleTab(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        styleTab(context, attrs)
    }

    private fun styleTab(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTemplate)
        val isTemplate = a.getBoolean(R.styleable.CustomTemplate_isTemplate, false)
        if (isTemplate) {
            try {
                val templateJSON = JSONObject(ClientConfiguration.getAppTemplateConfig())
                if (templateJSON.has(Constants.SELECTION_COLOR)) {
                    val selectionColor = Color.parseColor(templateJSON.getString(Constants.SELECTION_COLOR))
                    val deselectionColor = Color.parseColor(templateJSON.getString(Constants.DESELECTION_COLOR))

/*                    val stateListDrawable = StateListDrawable()
                    stateListDrawable.addState(intArrayOf(android.R.attr.state_selected),ColorDrawable(selectionColor))
                    stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed),ColorDrawable(selectionColor))
                    stateListDrawable.addState(intArrayOf(android.R.attr.state_checked),ColorDrawable(selectionColor))
                    stateListDrawable.addState(intArrayOf(),ColorDrawable(deselectionColor))
                    background = stateListDrawable*/

                    addOnTabSelectedListener(object : OnTabSelectedListener {
                        override fun onTabSelected(tab: Tab) {
                            try {
                                Timber.d("Selected: ${tab.position}")
                                tab.customView?.setBackgroundColor(selectionColor)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onTabUnselected(tab: Tab) {
                            try {
                                Timber.d("Deselected: ${tab.position}")
                                tab.customView?.setBackgroundColor(deselectionColor)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onTabReselected(tab: Tab) {}
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}