package com.opennotes.notes.presentation.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val noteId = inputData.getInt("NOTE_ID", -1)
        val noteTitle = inputData.getString("NOTE_TITLE") ?: "OpenNotes Reminder"
        val noteContent = inputData.getString("NOTE_CONTENT") ?: ""

        if (noteId != -1) {
            showNotification(noteId, noteTitle, noteContent)
        }
        return Result.success()
    }

    private fun showNotification(noteId: Int, title: String, content: String) {
        val channelId = "note_reminders_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Note Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for note reminder notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("opennotes://note/$noteId?noteColor=-1")
        ).apply {
            `package` = applicationContext.packageName
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(com.opennotes.R.drawable.ic_launcher_monochrome)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(noteId, notification)
    }
}
