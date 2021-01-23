package com.podoynikov.medhelpmobile

import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //Define sound URI

        val builder =  NotificationCompat.Builder(context!!, "medicalAppointment")
                .setSmallIcon(R.drawable.ic_baseline_schedule_24)
                .setContentTitle(intent?.getStringExtra("EXTRA_REMINDER_TITLE"))
                .setContentText(intent?.getStringExtra("EXTRA_REMINDER_TEXT"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

//        val v = (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
//        v.vibrate(VibrationEffect.createWaveform([2000, 1000], 1))

        val notificationManager  = NotificationManagerCompat.from(context)
        notificationManager.notify(201, builder.build())
    }
}