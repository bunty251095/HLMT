package com.caressa.medication_tracker.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.caressa.common.constants.Constants
import com.caressa.common.utils.DateHelper
import com.caressa.common.utils.LocaleHelper
import com.caressa.common.utils.Utilities
import com.caressa.medication_tracker.R
import com.caressa.medication_tracker.model.InstructionModel
import com.caressa.medication_tracker.model.MedTypeModel
import com.caressa.common.view.SpinnerModel
import com.caressa.medication_tracker.model.ReminderNotification
import com.caressa.medication_tracker.receiver.MedicineReminderBroadcastReceiver
import com.caressa.model.entity.UserRelatives
import timber.log.Timber
import java.util.*

class MedicationTrackerHelper( val context: Context) {

    fun getFrequencyList(): ArrayList<String> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val frequencyList: ArrayList<String> = ArrayList()
        frequencyList.add(localResource.getString(R.string.EVERY_DAY))
        frequencyList.add(localResource.getString(R.string.FOR_X_DAYS))
        return frequencyList
    }

    fun getMedTypeList(): ArrayList<MedTypeModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val medTypeList: ArrayList<MedTypeModel> = ArrayList()
        medTypeList.add(MedTypeModel(localResource.getString(R.string.TABLET), "TAB", R.drawable.img_pill))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.CAPSULE), "CAP", R.drawable.img_capsul))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.SYRUP), "SYRUP", R.drawable.img_syrup))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.DROPS), "DROPS", R.drawable.img_drop))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.INJECTION), "INJ", R.drawable.img_syringe))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.GEL), "GEL", R.drawable.img_gel))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.TUBE), "TUBE", R.drawable.img_tube))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.SPRAY), "SPRAY", R.drawable.img_spray))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.MOUTHWASH), "MOUTHWASH", R.drawable.img_mouth_wash))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.SOLUTION), "SOL", R.drawable.img_solution))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.OINTMENT), "OINT", R.drawable.img_ointment))
        medTypeList.add(MedTypeModel(localResource.getString(R.string.OTHER), "OTHER", R.drawable.img_other_med))
        return medTypeList
    }

    fun getMedTypeImageByCode(code: String): Int {
        var image: Int = R.drawable.img_pill
        when {
            code.equals("TAB", ignoreCase = true) -> {
                image = R.drawable.img_pill
            }
            code.equals("CAP", ignoreCase = true) -> {
                image = R.drawable.img_capsul
            }
            code.equals("SYRUP", ignoreCase = true) -> {
                image = R.drawable.img_syrup
            }
            code.equals("DROPS", ignoreCase = true) -> {
                image = R.drawable.img_drop
            }
            code.equals("INJ", ignoreCase = true) -> {
                image = R.drawable.img_syringe
            }
            code.equals("GEL", ignoreCase = true) -> {
                image = R.drawable.img_gel
            }
            code.equals("TUBE", ignoreCase = true) -> {
                image = R.drawable.img_tube
            }
            code.equals("SPRAY", ignoreCase = true) -> {
                image = R.drawable.img_spray
            }
            code.equals("MOUTHWASH", ignoreCase = true) -> {
                image = R.drawable.img_mouth_wash
            }
            code.equals("SOL", ignoreCase = true) -> {
                image = R.drawable.img_solution
            }
            code.equals("OINT", ignoreCase = true) -> {
                image = R.drawable.img_ointment
            }
            code.equals("OTHER", ignoreCase = true) -> {
                image = R.drawable.img_other_med
            }
        }
        return image
    }

    fun getMedTypeByCode(code: String): Int {
        var type: Int = R.string.TABLET
        if ( !Utilities.isNullOrEmpty(code) ) {
            when {
                code.equals("TAB", ignoreCase = true) -> {
                    type = R.string.TABLET
                }
                code.equals("CAP", ignoreCase = true) -> {
                    type = R.string.CAPSULE
                }
                code.equals("SYRUP", ignoreCase = true) -> {
                    type = R.string.SYRUP
                }
                code.equals("DROPS", ignoreCase = true) -> {
                    type = R.string.DROPS
                }
                code.equals("INJ", ignoreCase = true) -> {
                    type = R.string.INJECTION
                }
                code.equals("GEL", ignoreCase = true) -> {
                    type = R.string.GEL
                }
                code.equals("TUBE", ignoreCase = true) -> {
                    type = R.string.TUBE
                }
                code.equals("SPRAY", ignoreCase = true) -> {
                    type = R.string.SPRAY
                }
                code.equals("MOUTHWASH", ignoreCase = true) -> {
                    type = R.string.MOUTHWASH
                }
                code.equals("SOL", ignoreCase = true) -> {
                    type = R.string.SOLUTION
                }
                code.equals("OINT", ignoreCase = true) -> {
                    type = R.string.OINTMENT
                }
                code.equals("OTHER", ignoreCase = true) -> {
                    type = R.string.OTHER
                }
            }
        }
        return type
    }

    fun getMedInstructionList(): ArrayList<InstructionModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val instructionList: ArrayList<InstructionModel> = ArrayList()
        instructionList.add(InstructionModel(localResource.getString(R.string.BEFORE_MEAL), R.drawable.img_before_meal))
        instructionList.add(InstructionModel(localResource.getString(R.string.WITH_MEAL), R.drawable.img_with_meal))
        instructionList.add(InstructionModel(localResource.getString(R.string.AFTER_MEAL), R.drawable.img_after_meal))
        instructionList.add(InstructionModel(localResource.getString(R.string.ANYTIME), R.drawable.img_anytime))
        return instructionList
    }

    fun getCategoryList(): ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel( localResource.getString(R.string.ONGOING), "",0,false))
        list.add(SpinnerModel( localResource.getString(R.string.COMPLETED), "",1,false))
        list.add(SpinnerModel( localResource.getString(R.string.ALL), "",2,false))
        return list
    }

    fun getMedInstructionByCode(code: String): String {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var instruction = localResource.getString(R.string.BEFORE_MEAL)
        when(code) {
            Constants.BEFORE_MEAL -> {
                instruction = localResource.getString(R.string.BEFORE_MEAL)
            }
            Constants.WITH_MEAL -> {
                instruction = localResource.getString(R.string.WITH_MEAL)
            }
            Constants.AFTER_MEAL -> {
                instruction = localResource.getString(R.string.AFTER_MEAL)
            }
            Constants.ANYTIME -> {
                instruction = localResource.getString(R.string.ANYTIME)
            }
        }
        return instruction
    }

    fun displayMedicineReminderNotification(context:Context,data:ReminderNotification,relative: UserRelatives) {
        try {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
            val instruction = getMedInstructionByCode(data.instruction)
            val strMessage: String = data.dosage + " " + localResource.getString(R.string.DOSE) + " , " + instruction
            val timeToDisplay: String = DateHelper.getTimeIn12HrFormatAmOrPm(data.scheduleTime)
            val firstName: String = relative.firstName.split(" ")[0]
            val personName = localResource.getString(R.string.FOR) + " $firstName"

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel" // The id of the channel.
            val name: CharSequence = "Medicine Reminders" // The user-visible name of the channel.

            //val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mNotificationManager = NotificationManagerCompat.from(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
                mNotificationManager.createNotificationChannel(mChannel)
            }

            // Onclick of Notification Intent
            val onClick = generateIntent(context,data,data.action,notificationId)
            onClick.putExtra(Constants.SUB_TITLE, strMessage)
            onClick.putExtra(Constants.TIME, timeToDisplay)
            val pendingOnClickIntent = PendingIntent.getBroadcast(context,notificationId,onClick,PendingIntent.FLAG_UPDATE_CURRENT)

            // Taken Action button Intent
            val takenIntent = generateIntent(context,data,Constants.TAKEN, notificationId)
            takenIntent.putExtra(Constants.SUB_TITLE, strMessage)
            takenIntent.putExtra(Constants.TIME, timeToDisplay)
            val pendingTakenIntent = PendingIntent.getBroadcast(context,notificationId,takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            // Skip Action button Intent
            val skipIntent = generateIntent(context,data,Constants.SKIPPED, notificationId)
            skipIntent.putExtra(Constants.SUB_TITLE, strMessage)
            skipIntent.putExtra(Constants.TIME, timeToDisplay)
            val pendingSkipIntent = PendingIntent.getBroadcast(context, notificationId, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            //Custom Notification Layout(Remote View) only supports android component,
            // not support custom view, androidx, android support library or any library
            // Do not change components of Remote views to androidx

            // Notification's Collapsed layout
            val remoteViewCollapsed = RemoteViews(context.packageName,R.layout.med_notification_collapsed)
            remoteViewCollapsed.setTextViewText(R.id.med_notification_title,data.medicineName)
            remoteViewCollapsed.setTextViewText(R.id.med_notification_subtext, strMessage)
            remoteViewCollapsed.setTextViewText(R.id.med_schedule_time, timeToDisplay)
            remoteViewCollapsed.setTextViewText(R.id.med_person_name, personName)
            remoteViewCollapsed.setTextViewText(R.id.lbl_med_reminder,localResource.getString(R.string.MEDICINE_REMINDER))
            remoteViewCollapsed.setOnClickPendingIntent(R.id.med_reminder_collapsed, pendingOnClickIntent)

            // Notification's Expanded layout
            val remoteViewExpanded = RemoteViews(context.packageName, R.layout.med_notification_expanded)
            remoteViewExpanded.setTextViewText(R.id.med_notification_title, data.medicineName)
            remoteViewExpanded.setTextViewText(R.id.med_notification_subtext, strMessage)
            remoteViewExpanded.setTextViewText(R.id.med_schedule_time, timeToDisplay)
            remoteViewExpanded.setTextViewText(R.id.med_person_name, personName)
            remoteViewExpanded.setTextViewText(R.id.lbl_med_reminder,localResource.getString(R.string.MEDICINE_REMINDER))
            remoteViewExpanded.setTextViewText(R.id.txt_taken,localResource.getString(R.string.TAKEN))
            remoteViewExpanded.setTextViewText(R.id.txt_skip,localResource.getString(R.string.SKIP))
            remoteViewExpanded.setOnClickPendingIntent(R.id.layout_taken, pendingTakenIntent)
            remoteViewExpanded.setOnClickPendingIntent(R.id.layout_skip, pendingSkipIntent)
            remoteViewExpanded.setOnClickPendingIntent(R.id.med_reminder_expanded, pendingOnClickIntent)

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val customNotification = NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.hl_pace_logo)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true) //.setDefaults(Notification.DEFAULT_SOUND)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(localResource.getString(R.string.app_name))
                //.setWhen(System.currentTimeMillis())
                .setContentIntent(pendingOnClickIntent)
                .setContent(remoteViewCollapsed)
                .setCustomContentView(remoteViewCollapsed)
                .setCustomBigContentView(remoteViewExpanded)
                .build()

            Timber.e("displaying Notification")
            mNotificationManager.notify(notificationId, customNotification)

        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun generateIntent(context:Context, data:ReminderNotification, action:String, notificationId:Int): Intent {
        val intent = Intent(context, MedicineReminderBroadcastReceiver::class.java)
        intent.action = action
        intent.putExtra(Constants.NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.NOTIFICATION_ACTION, action)
        intent.putExtra(Constants.PERSON_ID, data.personID)
        intent.putExtra(Constants.MEDICINE_NAME, data.medicineName)
        intent.putExtra(Constants.DOSAGE, data.dosage)
        intent.putExtra(Constants.INSTRUCTION, data.instruction)
        intent.putExtra(Constants.SCHEDULE_TIME, data.scheduleTime)
        intent.putExtra(Constants.MEDICATION_ID, data.medicationID)
        intent.putExtra(Constants.MEDICINE_IN_TAKE_ID, 0)
        intent.putExtra(Constants.SERVER_SCHEDULE_ID, data.scheduleID)
        intent.putExtra(Constants.DATE, data.notificationDate)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent
    }

}