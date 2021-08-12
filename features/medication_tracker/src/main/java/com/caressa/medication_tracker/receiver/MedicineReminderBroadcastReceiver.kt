package com.caressa.medication_tracker.receiver

import android.app.NotificationManager
import android.content.*
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.NetworkUtility
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.common.MedicationSingleton
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import kotlin.coroutines.CoroutineContext

class MedicineReminderBroadcastReceiver : BroadcastReceiver(),LifecycleOwner,KoinComponent,CoroutineScope {

    private var lifeCycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    val viewModel: MedicineTrackerViewModel = get()

    override fun getLifecycle(): Lifecycle = lifeCycleRegistry

    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val action = intent!!.action
            val notificationID = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
            println("Action=>$action---notificationID--->$notificationID")
            if (action != null) {
                if (action.equals("MEDICATION", ignoreCase = true)) {
                    if (NetworkUtility.isOnline(context)) {
                        launchApp(context, notificationID, intent)
                    } else {
                        closeNotificationDrawer(context)
                        Utilities.toastMessageLong(context,context.resources.getString(R.string.MSG_NO_INTERNET_CONNECTION))
                    }
                } else if ( action.equals(Constants.TAKEN, ignoreCase = true) ) {
                    getMedicineDetails(context, intent)
                } else if ( action.equals(Constants.SKIPPED, ignoreCase = true) ) {
                    getMedicineDetails(context, intent)
                }
/*                else if ( action.equals(Constants.DATA, ignoreCase = true) ) {
                    observeLiveData()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMedicineDetails(context: Context, intent: Intent) {
        if (NetworkUtility.isOnline(context)) {
            showProgressNotification(context, intent)
            MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
            val medicationID = intent.getStringExtra(Constants.MEDICATION_ID)!!
            viewModel.getMedDetailsByMedicationIdApi(medicationID)
/*            viewModel.getMedicineDetailsByMedicationIdApi(medicationID,Constants.NOTIFICATION)
            viewModel.getMedicine.observe(this@MedicineReminderBroadcastReceiver, Observer {})
            viewModel.listMedicationInTake.observe(this@MedicineReminderBroadcastReceiver, Observer {})
            viewModel.addMedicineIntake.observe(this@MedicineReminderBroadcastReceiver, Observer {})*/
        } else {
            closeNotificationDrawer(context)
            Utilities.toastMessageLong(context,context.resources.getString(R.string.MSG_NO_INTERNET_CONNECTION))
        }
    }

    private fun showProgressNotification(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
        val remoteView = RemoteViews(context.packageName, R.layout.med_notification_progress)
        remoteView.setTextViewText(R.id.med_notification_title, intent.getStringExtra(Constants.MEDICINE_NAME))
        remoteView.setTextViewText(R.id.med_notification_subtext, intent.getStringExtra(Constants.SUB_TITLE))
        remoteView.setTextViewText(R.id.med_schedule_time, intent.getStringExtra(Constants.TIME))

        val customNotification = NotificationCompat.Builder(context, "fcm_medication_channel")
            .setSmallIcon(R.drawable.hl_pace_logo)
            .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setTicker(context.resources.getString(R.string.app_name))
            //.setProgress(100,0,true)
            .setCustomContentView(remoteView)
            .build()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, customNotification)
    }

    private fun launchApp(context: Context, notificationId:Int, intent: Intent) {
        try {
            val currentPersonId = viewModel.personId
            val personID: String = intent.getStringExtra(Constants.PERSON_ID)!!
            val medicationID: String = intent.getStringExtra(Constants.MEDICATION_ID)!!
            //val scheduleID: String = intent.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
            val scheduleTime: String = intent.getStringExtra(Constants.SCHEDULE_TIME)!!.substring(0, 5)
            //val medName: String = intent.getStringExtra(Constants.MEDICINE_NAME)!!
            println("currentPersonId=>$currentPersonId")
            println("PersonID=>$personID")
            println("MedicationID=>$medicationID")
            println("ScheduleTime=>$scheduleTime")

            if (currentPersonId.equals(personID, ignoreCase = true)) {
                MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                val launchIntent = Intent()
                launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                launchIntent.putExtra(Constants.DATE,intent.getStringExtra(Constants.DATE))
                context.startActivity(launchIntent)
            } else {
                println("Switch")
                MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                viewModel.checkRelativeExistAndLaunchApp(intent)
            }
            closeNotificationDrawer(context)
            cancelNotification(context, notificationId)

        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

/*    private fun observeLiveData() {
        viewModel.getMedicine.observe(this@MedicineReminderBroadcastReceiver, Observer {
            if ( it != null ) {
                val medication = it.medication
                Timber.e("medication--->$medication")
            }
        })
        viewModel.listMedicationInTake.observe(this@MedicineReminderBroadcastReceiver, Observer {})
        viewModel.addMedicineIntake.observe(this@MedicineReminderBroadcastReceiver, Observer {})
    }*/

    private fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    private fun closeNotificationDrawer(context: Context) {
        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
    }

    override val coroutineContext: CoroutineContext =
        Dispatchers.Main + SupervisorJob()

}