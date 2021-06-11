package com.caressa.home.adapter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.DefaultNotificationDialog
import com.caressa.common.utils.Utilities
import com.caressa.home.R
import com.caressa.home.common.DataHandler
import com.caressa.home.common.HomeSingleton
import com.caressa.home.ui.FamilyDoctor.FamilyDoctorListActivity
import com.caressa.home.viewmodel.FamilyDoctorViewModel
import com.caressa.model.home.FamilyDoctor
import com.caressa.model.home.FamilyDoctorsListModel
import kotlinx.android.synthetic.main.item_family_doctor.view.*
import java.util.ArrayList

class FamilyDoctorsAdapter(val  activity:FamilyDoctorListActivity, val viewModel:FamilyDoctorViewModel, val context: Context) :
    RecyclerView.Adapter<FamilyDoctorsAdapter.FamilyDoctorsViewHolder>() {

    private val familyDoctorsList: MutableList<FamilyDoctor> = mutableListOf()
    var color = intArrayOf(
        R.color.vivant_bright_sky_blue,
        R.color.vivant_watermelon,
        R.color.vivant_orange_yellow,
        R.color.vivant_bright_blue,
        R.color.vivant_soft_pink,
        R.color.vivant_nasty_green)

    override fun getItemCount(): Int = familyDoctorsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FamilyDoctorsViewHolder =
        FamilyDoctorsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_family_doctor, parent, false))

    override fun onBindViewHolder(holder: FamilyDoctorsAdapter.FamilyDoctorsViewHolder, position: Int) {
        val doctorDetail = familyDoctorsList[position]
        holder.view.setBackgroundColor(context.resources.getColor(getRandomColor(position)))
        holder.txt_doctor_name.setText(doctorDetail.firstName)
        holder.txt_specialization.setText(doctorDetail.specialty)

        if (!Utilities.isNullOrEmpty(doctorDetail.emailAddress)) {
            val emailText = "<u><a>" + doctorDetail.emailAddress + "</a></u>"
            holder.txt_email.setClickable(true)
            holder.txt_email.setText(Html.fromHtml(emailText))
        } else {
            holder.txt_email.setVisibility(View.GONE)
        }

        if (!Utilities.isNullOrEmpty(doctorDetail.primaryContactNo)) {
            holder.txt_contact.setText(doctorDetail.primaryContactNo)
        } else {
            holder.txt_contact.setVisibility(View.GONE)
        }

        if (!Utilities.isNullOrEmpty(doctorDetail.affiliatedTo)) {
            holder.txt_affiliated_to.setText("Affiliated To : " + doctorDetail.affiliatedTo)
        } else {
            holder.txt_affiliated_to.setVisibility(View.GONE)
        }

        holder.txt_email.setOnClickListener {
            DataHandler(context).showEmailClientIntent(doctorDetail.emailAddress)
        }

        holder.img_call_doctor.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + doctorDetail.primaryContactNo)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        holder.img_edit_doctor.setOnClickListener {
            HomeSingleton.getInstance()!!.setDoctorsDetails(doctorDetail)
            val intent = Intent()
            intent.putExtra("from","Update")
            intent.setComponent(ComponentName(NavigationConstants.APPID, NavigationConstants.FAMILY_DOCTOR_ADD_UPDATE))
            context.startActivity(intent)
        }

        holder.img_delete_doctor.setOnClickListener {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.DELETE_DOCTOR)
            dialogData.message = context.resources.getString(R.string.MSG_DELETE_DOCTOR_CONFIRMATION1) +
                    doctorDetail.firstName + context.resources.getString(R.string.MSG_DELETE_DOCTOR_CONFIRMATION2)
            dialogData.btnLeftName = context.resources.getString(R.string.NO)
            dialogData.btnRightName = context.resources.getString(R.string.YES)
            val defaultNotificationDialog =
                DefaultNotificationDialog(activity, object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                        if (isButtonRight) {
                            viewModel.callDeleteFamilyDoctorApi(context,doctorDetail.id)
                        }
                    }
                }, dialogData)
            defaultNotificationDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }

    }

    fun updateFamilyDoctorsList(items: List<FamilyDoctor>) {
        familyDoctorsList.clear()
        familyDoctorsList.addAll(items)
        //activity.stopShimmer()
    }

    private fun getRandomColor(position: Int): Int {
        return color[position % 7]
    }

    inner class FamilyDoctorsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = view.view
        val txt_doctor_name = view.txt_doctor_name
        val txt_specialization = view.txt_specialization
        val txt_contact = view.txt_contact
        val txt_email = view.txt_email
        val txt_affiliated_to = view.txt_affiliated_to
        val img_call_doctor = view.img_call_doctor
        val img_edit_doctor = view.img_edit_doctor
        val img_delete_doctor = view.img_delete_doctor
    }

}