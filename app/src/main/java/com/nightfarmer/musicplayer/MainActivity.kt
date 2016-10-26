package com.nightfarmer.musicplayer

import android.media.AudioManager
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setOnBufferingUpdateListener { mediaPlayer, percent ->
//                val currentProgress = seekBar.getMax() * mediaPlayer.currentPosition / mediaPlayer.duration
//                Log.e(currentProgress + "% play", percent.toString() + " buffer");
            }
            mediaPlayer.setOnPreparedListener {
                it.start();
                Log.e("mediaPlayer", "onPrepared");
            }
            mediaPlayer.setOnCompletionListener {
                Log.e("mediaPlayer", "end." + it)
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource("http://mpge.5nd.com/2016/2016-9-29/74119/1.mp3")
            mediaPlayer.prepare()
        }, 3000)
    }
}
