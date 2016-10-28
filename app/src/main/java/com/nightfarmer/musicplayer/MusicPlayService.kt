package com.nightfarmer.musicplayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.nightfarmer.musicplayer.test.HttpGetProxy
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MusicPlayService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    private val binder = MusicPlayBinder()
        get() {
            field.currentMusic = currentMusic
            field.playState = playState
            return field
        }


    private var currentMusic: MusicInfo? = null
    private var playState = 0//0stop 1playing 2paused -1加载中
        set(value) {
            field = value
            when (field) {
                -1 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(currentMusic, field)
//                            callBack.onStop()
                        }
                    }
                }
                0 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(currentMusic, field)
                            callBack.onStop()
                        }
                    }
                }
                1 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(currentMusic, field)
                            callBack.onStart(currentMusic)
                        }
                    }
                }
                2 -> {
                    if (binder.isBinderAlive) {
                        for (callBack in binder.callBackList) {
                            callBack.onStateChange(currentMusic, field)
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

    private var proxy: HttpGetProxy? = null


    override fun onCreate() {
        super.onCreate()
//        proxy = HttpGetProxy(9090)

        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!(mediaPlayer?.isPlaying ?: false)) return@subscribe
                    val duration = mediaPlayer?.duration ?: 0
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    if (binder.currentPosition != currentPosition || binder.duration != duration) {
                        binder.currentPosition = currentPosition
                        binder.duration = duration
                        if (binder.isBinderAlive) {
                            for (callBack in binder.callBackList) {
                                callBack.onProgress(currentPosition, duration)
                            }
                        }
                    }
                }

        try {
//            proxy?.startProxy()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        mediaPlayer ?: {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setOnBufferingUpdateListener { mediaPlayer, percent ->
                //                val currentProgress = seekBar.getMax() * mediaPlayer.currentPosition / mediaPlayer.duration
                //                Log.e(currentProgress + "% play", percent.toString() + " buffer");
            }
            mediaPlayer?.setOnPreparedListener {
                it.start()
//                println(mediaPlayer?.currentPosition.toString() + "__" + it.duration)
//                val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
//                simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+0:00");
//                val format = simpleDateFormat.format(0)
//                println(format)
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
                Thread({
                    try {
                        mediaPlayer?.reset()
//                        val url = proxy?.getLocalURL(it.playUrl)
                        val url = it.playUrl
                        println("proxy....$url")
                        Handler(Looper.getMainLooper()).post {
                            mediaPlayer?.setDataSource(url)
                            mediaPlayer?.prepareAsync()
                            currentMusic = it
                            playState = -1
                        }
                    } catch(e: Exception) {
                        e.printStackTrace()
                    }
                }).start()
            }
        } else if (OPER_PAUSE == intent?.getStringExtra("OPER")) {
            when (playState) {
                0 -> {
                    currentMusic?.let {
                        mediaPlayer?.start()
                        playState = 1
                    }
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

        } else if (OPER_PROGRESS == intent?.getStringExtra("OPER")) {
            val progress = (intent?.getIntExtra("progress", 0) ?: 0) / 100.0
            mediaPlayer?.seekTo(((mediaPlayer?.duration ?: 0) * progress).toInt())
        }
        return super.onStartCommand(intent, flags, startId)
    }


    companion object {

        val OPER_PAUSE = "pause"
        val OPER_PLAY = "play"
        val OPER_PROGRESS = "progress"
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

        fun progress(context: Context, progress: Int) {
            val intent = Intent(context, MusicPlayService::class.java)
            intent.putExtra("OPER", OPER_PROGRESS)
            intent.putExtra("progress", progress)
            context.startService(intent)
        }
    }


    fun initProxy() {
        var localSocket = ServerSocket(80, 0, InetAddress.getLocalHost())
        localSocket.soTimeout = 3000
        Thread({
            while (true) {
                val client = localSocket.accept() ?: continue
                println("client.....1")


            }
        }).start()
    }
}
