package vlntdds.com.mediasession.sample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import vlntdds.com.mediasession.sample.Constants
import vlntdds.com.mediasession.sample.R
import vlntdds.com.mediasession.sample.callbacks.PlayerCallbacks
import vlntdds.com.mediasession.sample.helpers.NotificationHelper

class PlayerActivity : AppCompatActivity(), ExoPlayer.EventListener {

    private lateinit var mMediaSession: MediaSessionCompat
    private lateinit var mPlaybackState: PlaybackStateCompat.Builder
    private lateinit var mExoPlayer: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        NotificationHelper.setupNotificationChannel(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mExoPlayer.isInitialized) {
            mExoPlayer.stop()
            mExoPlayer.release()
            mMediaSession.isActive = false
        }
    }

    private fun initializeMediaSession() {
        mMediaSession = MediaSessionCompat(this, Constants.MEDIA_SESSION_TAG)
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mMediaSession.setMediaButtonReceiver(null)

        mPlaybackState = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_FAST_FORWARD or
                    PlaybackStateCompat.ACTION_REWIND or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )

        mMediaSession.setPlaybackState(mPlaybackState.build())
        mMediaSession.setCallback(PlayerCallbacks(mExoPlayer))
        mMediaSession.isActive = true
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == ExoPlayer.STATE_READY && mExoPlayer.playWhenReady) {
            mPlaybackState.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.currentPosition, 1f)
        } else if (playbackState == ExoPlayer.STATE_READY && !mExoPlayer.playWhenReady) {
            mPlaybackState.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.currentPosition, 1f)
        }

        mMediaSession.setPlaybackState(mPlaybackState.build())
        NotificationHelper.showPlaybackNotification(mPlaybackState.build(), this, mMediaSession)
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        TODO("not needed in this sample")
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        TODO("not needed in this sample")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        TODO("not needed in this sample")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        TODO("not needed in this sample")
    }

    override fun onPositionDiscontinuity() {
        TODO("not needed in this sample")
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
        TODO("not needed in this sample")
    }
}
