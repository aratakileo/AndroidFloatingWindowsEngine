package com.pexty.studios.floating.windows.engine

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.VideoView
import androidx.cardview.widget.CardView
import pexty.utils.Utils

class MainActivityfake0: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val videoView = VideoView(this)
//        val videoViewXML = findViewById<VideoView>(R.id.videoView)
        findViewById<TableLayout>(R.id.tableLayout).addView(CardView(this).apply {
            radius = Utils.DPtoPixels(this@MainActivityfake0, 20)
            addView(videoView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.DPtoPixels(this, 200).toInt()))

        videoView.setVideoURI(Uri.parse("android.resource://${this.packageName}/${R.raw.video}"))
        videoView.start()
//        videoViewXML.setVideoURI(Uri.parse("android.resource://${this.packageName}/${R.raw.video}"))
//        videoViewXML.start()
    }
}