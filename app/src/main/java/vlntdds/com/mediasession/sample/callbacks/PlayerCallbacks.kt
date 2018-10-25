package vlntdds.com.mediasession.sample.callbacks

import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.google.android.exoplayer2.SimpleExoPlayer

internal class PlayerCallbacks(var mExoPlayer: SimpleExoPlayer) : MediaSessionCompat.Callback() {

    override fun onPlay() {
        super.onPlay()
        mExoPlayer.playWhenReady = true
        Log.i(this.javaClass.name, "onPlay")
    }

    override fun onPause() {
        super.onPause()
        mExoPlayer.playWhenReady = false
        Log.i(this.javaClass.name, "onPause")
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        mExoPlayer.seekTo(0)
        Log.i(this.javaClass.name, "onSkipToPrevious")

    }
}