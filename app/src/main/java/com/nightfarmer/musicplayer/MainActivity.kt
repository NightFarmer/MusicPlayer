package com.nightfarmer.musicplayer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private var mainPageListAdapter: MainPageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mainPageListAdapter = MainPageListAdapter(this)
        recyclerView.adapter = mainPageListAdapter

        parseMainPage()

        val intent = Intent(this, MusicPlayService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)

        tv_play_pause.setOnClickListener {
            MusicPlayService.pause(this)
        }
    }

    private fun parseMainPage() {
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
                    println(matchResult.groupValues[1] + "__" + matchResult.groupValues[2] + "__" + matchResult.groupValues[3])
                    val musicInfo = MusicInfo()
                    musicInfo.url = matchResult.groupValues[1]
                    musicInfo.name = matchResult.groupValues[2]
                    musicInfo.singer = matchResult.groupValues[3]
                    musicList.add(musicInfo)
                }
                println(musicList.size)
                println(Thread.currentThread().name)
                runOnUiThread {
                    mainPageListAdapter?.musicList = musicList
                    mainPageListAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    private val connection = object : MusicServiceConnection() {
        override fun onStart(currentMusic: MusicInfo?) {
            println("onstart.....!!")
            tv_jindu.text = "${currentMusic?.name}-${currentMusic?.singer} playing..."
        }

        override fun onConnectedToService(binder: MusicPlayBinder) {
            val music = binder.currentMusic
            tv_jindu.text = "${music?.name}-${music?.singer} ${binder.playState}.........@@@"
        }

        override fun onStateChange(state: Int) {
            when (state) {
                1 -> tv_play_pause.text = "暂停"
                2 -> tv_play_pause.text = "播放"
            }

        }
    }


    fun playMusic(music: MusicInfo) {
        runOnUiThread { tv_jindu.text = "${music.name}-${music.singer} 准备中" }

        MusicPlayService.play(this, music)
    }

    fun stop() {
        stopService(Intent(this, MusicPlayService::class.java))
    }

    override fun onDestroy() {
        connection.unregister()
        unbindService(connection)
        super.onDestroy()
    }
}
