package com.nightfarmer.musicplayer.mvp.main

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.MusicPlayService
import com.nightfarmer.musicplayer.MusicServiceConnection
import okhttp3.*
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Created by zhangfan on 2016/10/27 0027.
 */
class MainPresenter(val view: MainContract.View) : MainContract.Presenter {

    override fun onExist() {
        connection.unregister()
        view.getContext().unbindService(connection)
    }

    override fun pause() {
        MusicPlayService.pause(view.getContext())
    }

    override fun play(music: MusicInfo) {
        MusicPlayService.play(view.getContext(), music)
    }

    val handler = Handler(Looper.getMainLooper())

    private val connection = object : MusicServiceConnection() {
        override fun onStateChange(currentMusic: MusicInfo?, state: Int) {
            view.setPlayState(currentMusic, state)
        }
    }

    override fun start() {
        val intent = Intent(view.getContext(), MusicPlayService::class.java)
        view.getContext().bindService(intent, connection, AppCompatActivity.BIND_AUTO_CREATE)

        val okHttpClient = OkHttpClient()
        val builder = Request.Builder()
        val request = builder.url("http://www.5nd.com/")
                .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response?) {
                val bytes = response?.body()?.bytes()
                val html = Charset.forName("gbk").decode(ByteBuffer.wrap(bytes))
//                println(html)
                val numPattern = """<a target="_ting" href="([^"]+)" class="__playcolor" title="([^"]+)">[^<]*</a></span>\s*<span class="singer-name"><a target="_singer" href="[^"]*" title="([^"]*)">""".toRegex()
                val musicList = arrayListOf<MusicInfo>()
                for (matchResult in numPattern.findAll(html)) {
//                    println(matchResult.value)
//                    println(matchResult.groupValues[1] + "__" + matchResult.groupValues[2] + "__" + matchResult.groupValues[3])
                    val musicInfo = MusicInfo()
                    musicInfo.url = matchResult.groupValues[1]
                    musicInfo.name = matchResult.groupValues[2]
                    musicInfo.singer = matchResult.groupValues[3]
                    musicList.add(musicInfo)
                }
                println(musicList.size)
                println(Thread.currentThread().name)

                handler.post {
                    view.setDataList(musicList)
                }
            }
        })
    }
}