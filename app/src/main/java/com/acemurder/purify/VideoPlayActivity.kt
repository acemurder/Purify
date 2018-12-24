package com.acemurder.purify

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.MediaController
import android.widget.VideoView

class VideoPlayActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "Purify.VideoPlayActivity"
    }

    private val videoView: VideoView  by lazy { findViewById<VideoView>(R.id.vv_video) }
    private lateinit var videoUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        videoUrl = intent.getStringExtra(KEY_VIDEO_URL)
        videoView.setVideoPath(videoUrl)
        videoView.requestFocus()
        val controller =  MediaController(this)
        videoView.setMediaController(controller)
    }
}
