package com.nightfarmer.musicplayer.mvp.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.SeekBar
import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.MusicPlayService
import com.nightfarmer.musicplayer.R
import com.nightfarmer.musicplayer.mvp.base.BaseActivity
import com.nightfarmer.musicplayer.mvp.playdetail.PlayDetailActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.View {
    override fun setSeek(seek: Int) {
        seekBar.progress = seek
    }


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
        tv_name_playing.setOnClickListener {
            startActivity(Intent(this, PlayDetailActivity::class.java))
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                println("${seekBar?.progress}")
                presenter?.setProgress(seekBar?.progress ?: 0)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
        })
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
