package com.caressa.track_parameter.adapter

import android.content.ClipData
import android.text.Html
import android.util.ArrayMap
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.utils.OnSwipeTouchListener
import com.caressa.common.utils.Utilities
import com.caressa.track_parameter.R
import com.caressa.track_parameter.util.CalculateParametersAndObservations
import com.caressa.track_parameter.viewmodel.DashboardViewModel
import com.gc.materialdesign.views.Slider
import com.rtugeek.android.colorseekbar.ColorSeekBar
import java.util.ArrayList

class TrackHealthParametersDashboardAdapter(private val viewModel: DashboardViewModel){


    abstract class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view: View? = null
        var currentItem: ClipData.Item? = null
        init {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class BMIViewHolder(v: View) : ViewHolder(v) {
        internal var bmiView: TextView
        internal var BMIObservationView: TextView
        internal var colorSeekBar: ColorSeekBar
        internal var cardview: CardView
        internal var bmiWeightView: TextView
        internal var bmi: Double = 0.toDouble()
        internal var bmiWeight: Double = 0.toDouble()
        //TextView progressText;
        private val slider: Slider? = null

        init {
            //this.temp = (TextView) v.findViewById(R.id.temp);
            view = v
            view!!.setOnClickListener(View.OnClickListener {
//                redirectToProfilesFragment(
//                    view,
//                    "BMI",
//                    "BMI"
//                )
            })
            view!!.setOnTouchListener(object : OnSwipeTouchListener(view!!.getContext()) {
//                fun onSwipeRight() {
//                    redirectToProfilesFragment(view, "BMI", "BMI")
//                }
//
//                fun onSwipeLeft() {
//                    redirectToProfilesFragment(view, "BMI", "BMI")
//                }
            })


            this.cardview = v.findViewById(R.id.cardview) as CardView
            this.bmiWeightView = v.findViewById(R.id.bmiWeight) as TextView
            bmiView = v.findViewById(R.id.bmiView) as TextView
            colorSeekBar = v.findViewById(R.id.colorSlider) as ColorSeekBar
            colorSeekBar.maxValue = 100
            colorSeekBar.setBarHeight(10f)
            colorSeekBar.setThumbHeight(30f)

            colorSeekBar.setColors(R.array.seekbar_colors)
            this.BMIObservationView = v.findViewById(R.id.BMIObservationView) as TextView
//            bmiWeightView.typeface = TypeFace.Medium
            //bmiView.setTypeface(TypeFace.Medium);
            var parameters: ArrayMap<String, ArrayMap<String, String>>? = null
            var inputParameters: ArrayMap<String, String>? = ArrayMap()
            val parametersObservation: ArrayList<ArrayMap<String, String>>? = null
            val bmiObservation = ""
            try {
                // Mayuresh
//                inputParameters!![CalculateParametersAndObservations.GlobalConstants.PERSON_ID] =
//                    SessionManager.GetSessionDetails().get(CalculateParametersAndObservations.GlobalConstants.CURRENT_PERSON_ID)
//                inputParameters[CalculateParametersAndObservations.GlobalConstants.PROFILE_CODE] = "BMI"
//
//                parameters = HealthParametersDBHelper.getLatestParameters(inputParameters)


                /* parametersObservation = HealthParametersDBHelper.getLatestParametersObservation();
                if ((parametersObservation != null)) {
                    for (int i = 0; i < parametersObservation.size(); i++) {
                        bmiObservation = parametersObservation.get(i).get(GlobalConstants.PROFILE_OBSERVATION);
                    }
                }*/

    // Mayuresh
                /*if (parameters != null && parameters!!.containsKey("BMI")) {
                    bmi =
                        java.lang.Double.parseDouble(parameters!!["BMI"]!![CalculateParametersAndObservations.GlobalConstants.VALUE]!!)
                    bmi = Utilities.roundOffPrecision(bmi, 1)
                    bmiWeight =
                        java.lang.Double.parseDouble(parameters!!["WEIGHT"]!![CalculateParametersAndObservations.GlobalConstants.VALUE]!!)
                    bmiWeight = Utilities.roundOffPrecision(bmiWeight, 1)
                    bmiWeightView.text =
                        bmiWeight.toString() + " " + parameters!!["WEIGHT"]!![CalculateParametersAndObservations.GlobalConstants.UNIT]
                    colorSeekBar.setColorBarValue(bmi.toInt())
                    // bmiView.setText(Html.fromHtml("<big>" + String.valueOf(bmi) + "</big><small><Font color=\"#B3B3B3\"> kg/m<sup>2</sup></small>"));
                    bmiView.text = Html.fromHtml(bmi.toString())

                    val bmiObservationMap =
                        CalculateParametersAndObservations.getBMIObservation(bmi)
                    if (bmiObservationMap != null && bmiObservationMap!!.size > 0) {
                        BMIObservationView.setText(bmiObservationMap!!.get(
                            CalculateParametersAndObservations.GlobalConstants.BMI_OBSERVATION_DESCRIPTION))
                        //BMIObservationView.setTextColor(Color.parseColor(bmiObservationMap.get(GlobalConstants.BMI_OBSERVATION_COLOR)));
                        //bmiView.setTextColor(Color.parseColor(bmiObservationMap.get(GlobalConstants.BMI_OBSERVATION_COLOR)));
                    }
                }*/
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                inputParameters = null
                parameters = null
            }


            colorSeekBar.setOnTouchListener { view, motionEvent -> true }

            colorSeekBar.isClickable = false
        }
    }
}