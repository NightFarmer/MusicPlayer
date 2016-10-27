package com.nightfarmer.musicplayer.mvp.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.MusicPlayService
import com.nightfarmer.musicplayer.R
import com.nightfarmer.musicplayer.mvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.View {


    private var mainPageListAdapter: MainPageListAdapter? = null

    private var presenter: MainPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mainPageListAdapter = MainPageListAdapter(this)
        recyclerView.adapter = mainPageListAdapter

        presenter = MainPresenter(this)
        presenter?.start()

        tv_play_pause.setOnClickListener {
            presenter?.pause()
        }
    }

    override fun setPlayState(music: MusicInfo?, state: Int) {
        when (state) {
            0 -> tv_play_pause.text = "播放"
            1 -> tv_play_pause.text = "暂停"
            2 -> tv_play_pause.text = "播放"
        }

        music?.let {
            tv_name_playing.text = "${it.name}-${it.singer}".toString()
        }

        when (state) {
            -1 -> tv_jindu.text = "加载中.."
            0 -> tv_jindu.text = "已停止"
            1 -> tv_jindu.text = "播放中"
            2 -> tv_jindu.text = "已暂停"
        }
    }

    override fun setDataList(dataList: List<MusicInfo>) {
        mainPageListAdapter?.musicList = dataList
        mainPageListAdapter?.notifyDataSetChanged()
    }

    fun playMusic(music: MusicInfo) {
        runOnUiThread { setPlayState(music, -1) }
        presenter?.play(music)
    }

    fun stop() {
        stopService(Intent(this, MusicPlayService::class.java))
    }

    override fun onDestroy() {
        presenter?.onExist()
        super.onDestroy()
    }
}
