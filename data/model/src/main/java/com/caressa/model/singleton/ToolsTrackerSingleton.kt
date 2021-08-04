package com.caressa.model.singleton

import com.caressa.model.entity.HRASummary
import com.caressa.model.entity.Users

class ToolsTrackerSingleton private constructor() {


    private object HOLDER {
        val INSTANCE = ToolsTrackerSingleton()
    }

    companion object {
        val instance: ToolsTrackerSingleton by lazy { HOLDER.INSTANCE }
    }

    var userDetails: Users = Users()

    var hraSummary: HRASummary = HRASummary()

}