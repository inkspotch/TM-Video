package com.example.tm

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private val bandwidthMeter = DefaultBandwidthMeter()
    private val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
    private val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
    private lateinit var uri: Uri
    private var resumeWindow = C.INDEX_UNSET
    private var resumePosition = 0L

    companion object {
        const val EXTRA_URI = "uri"

        fun create(context: Context, uri: Uri): Intent {
            return Intent(context, VideoActivity::class.java)
                    .putExtra(EXTRA_URI, uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        uri = intent.getParcelableExtra(EXTRA_URI)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(EXTRA_URI, uri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        uri = savedInstanceState.getParcelable(EXTRA_URI)
    }

    override fun onResume() {
        super.onResume()
        val player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        video.player = player
        player.playWhenReady = true

        val dataSourceFactory = DefaultDataSourceFactory(this@VideoActivity, bandwidthMeter, DefaultHttpDataSourceFactory("tm-example-app", bandwidthMeter))
        val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

        val shouldResume = resumeWindow != C.INDEX_UNSET
        if (shouldResume) {
            player.seekTo(resumeWindow, resumePosition)
        }

        player.prepare(hlsMediaSource, !shouldResume, false)

        this.player = player
    }

    override fun onPause() {
        super.onPause()

        player?.let {
            resumeWindow = it.currentWindowIndex
            resumePosition = it.contentPosition

            it.release()
        }

        player = null
    }
}
