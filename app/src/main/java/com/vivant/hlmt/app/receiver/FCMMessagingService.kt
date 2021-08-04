package com.vivant.hlmt.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.caressa.common.constants.Constants
import com.caressa.common.constants.NavigationConstants
import com.caressa.common.utils.Utilities
import com.caressa.home.viewmodel.BackgroundCallViewModel
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.model.ReminderNotification
import com.caressa.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber
import java.util.*

class FCMMessagingService : FirebaseMessagingService(), LifecycleOwner, KoinComponent {

    private val TAG = "FCMMessagingService : "
    private val medNotification = ReminderNotification()
    private var lifeCycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private val viewModel: MedicineTrackerViewModel = get()
    private val backgroundApiCallviewModel: BackgroundCallViewModel = get()

    override fun getLifecycle(): Lifecycle = lifeCycleRegistry

    override fun onNewToken(token: String) {
        Timber.e(TAG + "Refreshed token--->" + token)
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        try {
            val data = remoteMessage.data
            Timber.i("onMessageReceived(FCMMessagingService): data =$data")
            if (viewModel.isUserLoggedIn()) {
                if (!Utilities.isNullOrEmpty(data.get("Screen")) && data.get("Screen")
                        .equals("MEDICATION_REMINDER", ignoreCase = true)
                ) {
                    showMedicineReminderNotification(data)
                } else if (!Utilities.isNullOrEmpty(data.get("Action")) && data.get("Action")
                        .equals("HEALTHTIPS", ignoreCase = true)
                ) {
                    showHealthTipNotification(this, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMedicineReminderNotification(data: Map<String, String>) {
        try {
            val screen = data.get("Screen")
            if (!Utilities.isNullOrEmpty(screen)) {
                Timber.e("Screen--->$screen")
                if (screen.equals("MEDICATION_REMINDER", ignoreCase = true)) {
                    val details = JSONObject(data.get("Body")!!)
                    medNotification.action = "MEDICATION"
                    medNotification.personID = details.getString("PersonID")
                    medNotification.medicineName = details.getString("Name")
                    medNotification.dosage = details.getString("Dosage")
                    medNotification.instruction = details.getString("Instruction")
                    medNotification.scheduleTime = details.getString("ScheduleTime")
                    medNotification.medicationID = details.getString("MedicationID")
                    medNotification.scheduleID = details.getString("ScheduleID")
                    medNotification.notificationDate =
                        details.getString("NotificationDate").split("T").toTypedArray()[0]
                    // For Self and Family Member also
                    viewModel.checkRelativeExistAndShowNotification(this, medNotification)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showHealthTipNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val title = data["title"]
            val message = data["text"]
            val imageURL = data["ImageURL"]
            Timber.e("Action--->$action")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val NOTIFICATION_ID = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val CHANNEL_ID = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Vivant Health Tips"
                val descriptionText = "Medicine Reminder Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }

            val OnClick = Intent()
            OnClick.action = action
            OnClick.putExtra(Constants.NOTIFICATION_ACTION, action)
            OnClick.putExtra(Constants.NOTIFICATION_TITLE, title)
            OnClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
            OnClick.setComponent(ComponentName(NavigationConstants.APPID, NavigationConstants.HOME))
            OnClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (!Utilities.isNullOrEmpty(imageURL)) {
                OnClick.putExtra(Constants.NOTIFICATION_URL, imageURL)
            }
            val pendingOnClickIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                OnClick,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.img_hlmt_logo_notification)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.img_hlmt_logo_notification
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Timber.e("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(NOTIFICATION_ID, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendRegistrationToServer(fcmToken: String) {
        //viewModel2.refreshFcmToken(fcmToken)
        if (viewModel.isUserLoggedIn()) {
            backgroundApiCallviewModel.callSaveCloudMessagingIdApi(fcmToken, true)
        }
    }

}