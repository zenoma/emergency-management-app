package es.udc.emergencyapp.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import es.udc.emergencyapp.MainActivity
import es.udc.emergencyapp.R
import es.udc.emergencyapp.ui.DrawerBadgeState

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val route = data["route"] ?: "myteam"
        val title = data["title"] ?: message.notification?.title ?: getString(R.string.app_name)
        val body = data["body"] ?: message.notification?.body ?: getString(R.string.notice_sent)

        DrawerBadgeState.refreshTrigger++

        showNotification(title, body, route)
    }

    private fun showNotification(title: String, body: String, route: String) {
        val channelId = "assignments_channel"
        ensureChannel(channelId)

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_ROUTE, route)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            route.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(route.hashCode(), notification)
    }

    private fun ensureChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(channelId) == null) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        getString(R.string.assignments_channel_name),
                        NotificationManager.IMPORTANCE_HIGH
                    )
                )
            }
        }
    }
}
