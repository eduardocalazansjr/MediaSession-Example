package vlntdds.com.mediasession.sample.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import vlntdds.com.mediasession.sample.Constants
import vlntdds.com.mediasession.sample.R
import vlntdds.com.mediasession.sample.ui.PlayerActivity

class NotificationHelper {
    companion object {
        @JvmStatic
        fun setupNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val mChannel = NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    Constants.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
                mChannel.description = Constants.NOTIFICATION_CHANNEL_DESC
                mChannel.setShowBadge(false)
                mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                mNotificationManager.createNotificationChannel(mChannel)
            }
        }

        @JvmStatic
        fun showPlaybackNotification(playbackState: PlaybackStateCompat, context: Context, mediaSessionCompat: MediaSessionCompat) {
            val icon: Int
            val playPause: String

            when (playbackState.state) {
                PlaybackStateCompat.STATE_PLAYING -> {
                    icon = R.drawable.exo_controls_pause
                    playPause = context.getString(R.string.exo_controls_pause_description)
                }
                else -> {
                    icon = R.drawable.exo_controls_play
                    playPause = context.getString(R.string.exo_controls_play_description)
                }
            }

            val playPauseAction = NotificationCompat.Action(
                icon, playPause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_PLAY_PAUSE)
            )

            val restartAction = NotificationCompat.Action(
                R.drawable.exo_controls_previous, context.getString(R.string.exo_controls_previous_description),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            )

            val nextAction = NotificationCompat.Action(
                R.drawable.exo_controls_next, context.getString(R.string.exo_controls_next_description),
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            )

            val contentPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, PlayerActivity::class.java), 0)

            val notificationStyle = androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSessionCompat.sessionToken)
                .setShowActionsInCompactView(0, 1)

            val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .addAction(nextAction)
                .setStyle(notificationStyle)

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(Constants.NOTIFICATION_TAG, Constants.NOTIFICATION_ID, notification.build())

        }
    }

}