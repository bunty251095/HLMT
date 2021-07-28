package com.caressa.track_parameter.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.base.BaseViewModel
import com.caressa.common.utils.DateHelper
import com.caressa.model.entity.TrackParameterMaster
import com.caressa.model.parameter.DashboardParamGridModel
import com.caressa.model.parameter.ParameterListModel
import com.caressa.model.parameter.ParameterProfile
import com.caressa.track_parameter.R
import com.caressa.track_parameter.adapter.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import timber.log.Timber
import java.lang.Exception

@SuppressLint("StaticFieldLeak")
object ParameterBinding  {

    @BindingAdapter("app:dashboardList")
    @JvmStatic fun RecyclerView.setDashboardList( list: List<DashboardParamGridModel>? ) {
        with(this.adapter as DashboardGridAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            list?.let { updateData(it) }
        }
    }

    @BindingAdapter("app:allProfilesList")
    @JvmStatic fun RecyclerView.setAllProfilesList(resource: List<ParameterProfile>?) {
        with(this.adapter as SelectParameterAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 3)
            resource?.let { updateData(it) }
        }
    }


}