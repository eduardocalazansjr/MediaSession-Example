package vlntdds.com.mediasession.sample.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_player.*
import vlntdds.com.mediasession.sample.Constants
import vlntdds.com.mediasession.sample.R
import vlntdds.com.mediasession.sample.callbacks.PlayerCallbacks
import vlntdds.com.mediasession.sample.helpers.NotificationHelper

class PlayerActivity : AppCompatActivity(), ExoPlayer.EventListener {

    companion object {
        @JvmStatic
        lateinit var mMediaSession: MediaSessionCompat
    }

    private var mExoPlayer: SimpleExoPlayer? = null
    private var mPlaybackState: PlaybackStateCompat.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        NotificationHelper.setupNotificationChannel(this)
        initializeMediaSession()
        setupPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mExoPlayer!!.stop()
        mExoPlayer!!.release()
        mExoPlayer = null
        mMediaSession.isActive = false
    }

    private fun setupPlayer() {
        if (mExoPlayer == null) {
            val trackSelector = DefaultTrackSelector()
            val loadControl = DefaultLoadControl()
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
            player_view.player = mExoPlayer
            mExoPlayer!!.addListener(this)
            val userAgent = Util.getUserAgent(this, "AndroidAPP")
            val mediaSource = ExtractorMediaSource(
                Uri.parse("https://freemusicarchive.org/file/music/Boston_Hassle/Mules/7/Mules_-_01_-_Shock_ya.mp3"),
                DefaultDataSourceFactory(this, userAgent),
                DefaultExtractorsFactory(),
                null,
                null
            )
            mMediaSession.setCallback(PlayerCallbacks(mExoPlayer!!))
            mExoPlayer!!.prepare(mediaSource)
            mExoPlayer!!.playWhenReady = true
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
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )

        mMediaSession.setPlaybackState(mPlaybackState!!.build())
        mMediaSession.isActive = true
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            mPlaybackState!!.setState(
                PlaybackStateCompat.STATE_PLAYING,
                mExoPlayer!!.currentPosition, 1f
            )
        } else if (playbackState == ExoPlayer.STATE_READY) {
            mPlaybackState!!.setState(
                PlaybackStateCompat.STATE_PAUSED,
                mExoPlayer!!.currentPosition, 1f
            )
        }
        PlayerActivity.mMediaSession.setPlaybackState(mPlaybackState!!.build())
        NotificationHelper.showPlaybackNotification(mPlaybackState!!.build(), this, mMediaSession)
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPositionDiscontinuity() {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {

    }

    class MediaReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent)
        }
    }
}
