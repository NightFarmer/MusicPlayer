package com.nightfarmer.musicplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MusicPlayService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    private val binder = MusicPlayBinder()

    private var currentMusic: MusicInfo? = null
    private var playState = 0//0stop 1playing 2paused
        set(value) {
            field = value
            when (field) {
                0 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(playState)
                            callBack.onStop()
                        }
                    }
                }
                1 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(playState)
                            callBack.onStart(currentMusic)
                        }
                    }
                }
                2 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(playState)
                            callBack.onPause()
                        }
                    }
                }
            }
        }

    override fun onBind(intent: Intent): IBinder? {
        println("onbind...")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer ?: {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setOnBufferingUpdateListener { mediaPlayer, percent ->
                //                val currentProgress = seekBar.getMax() * mediaPlayer.currentPosition / mediaPlayer.duration
                //                Log.e(currentProgress + "% play", percent.toString() + " buffer");
            }
            mediaPlayer?.setOnPreparedListener {
                it.start()
                playState = 1
                Log.e("mediaPlayer", "onPrepared");
            }
            mediaPlayer?.setOnCompletionListener {
                playState = 0
                Log.e("mediaPlayer", "end." + it)
            }
        }()

    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer == null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (OPER_PLAY == intent?.getStringExtra("OPER")) {
            val music = intent?.getSerializableExtra("music") as? MusicInfo
            music?.let {
                try {
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(it.playUrl)
                    mediaPlayer?.prepareAsync()
                    currentMusic = it
                    binder.currentMusic = currentMusic
                    binder.playState = playState;
                } catch(e: Exception) {
                }
            }
        } else if (OPER_PAUSE == intent?.getStringExtra("OPER")) {
            when (playState) {
                0 -> {

                }
                1 -> {
                    mediaPlayer?.pause()
                    playState = 2
                }
                2 -> {
                    mediaPlayer?.start()
                    playState = 1
                }
            }
        } else if (OPER_NEXT == intent?.getStringExtra("OPER")) {

        }
        return super.onStartCommand(intent, flags, startId)
    }


    companion object {

        val OPER_PAUSE = "pause"
        val OPER_PLAY = "play"
        val OPER_NEXT = "next"

        fun play(context: Context, music: MusicInfo) {
            val intent = Intent(context, MusicPlayService::class.java)
            intent.putExtra("music", music)
            intent.putExtra("OPER", OPER_PLAY)
            context.startService(intent)
        }

        fun pause(context: Context) {
            val intent = Intent(context, MusicPlayService::class.java)
            intent.putExtra("OPER", OPER_PAUSE)
            context.startService(intent)
        }

        fun next(context: Context) {
            val intent = Intent(context, MusicPlayService::class.java)
            intent.putExtra("OPER", OPER_NEXT)
            context.startService(intent)
        }
    }
}
