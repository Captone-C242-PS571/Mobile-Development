package com.project.sleepwell.utils.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.project.sleepwell.R
import java.util.*

class AlarmService : Service() {

    companion object {
        private const val REQUEST_CODE_BASE = 1000 // Base request code for multiple alarms

        fun scheduleAlarms(context: Context, alarmIntervals: List<AlarmInterval>, intent: Intent) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            cancelPreviousAlarms(context)

            alarmIntervals.forEachIndexed { index, alarmInterval ->
                val pendingIntent = PendingIntent.getService(
                    context,
                    REQUEST_CODE_BASE + index, // Unique request code for each alarm
                    intent.putExtra("alarmInterval", alarmInterval),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, alarmInterval.hour)
                    set(Calendar.MINUTE, alarmInterval.minute)
                    set(Calendar.SECOND, 0)
                }

                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }

        private fun cancelPreviousAlarms(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (i in 0..3) {
                val intent = Intent(context, AlarmService::class.java)
                val pendingIntent = PendingIntent.getService(
                    context,
                    REQUEST_CODE_BASE + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val alarmInterval = intent.getParcelableExtra<AlarmInterval>("alarmInterval")

        if (alarmInterval != null) {
            showNotification(alarmInterval.message) // Gunakan pesan dari AlarmInterval
        }

        return START_NOT_STICKY
    }


    private fun showNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_alarm_channel"

        // Create Notification Channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for daily alarm notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Check Notification Permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Reminder")
            .setContentText("It's time to Sleep!")
            .setSmallIcon(R.drawable.image_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}