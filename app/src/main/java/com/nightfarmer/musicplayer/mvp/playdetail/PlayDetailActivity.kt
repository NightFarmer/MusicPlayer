package com.nightfarmer.musicplayer.mvp.playdetail

import android.os.Bundle
import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.R
import com.nightfarmer.musicplayer.mvp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_play_detail.*

class PlayDetailActivity : BaseActivity(), PlayDetailContract.View {
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

    private var presenter: PlayDetailPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_detail)

        presenter = PlayDetailPresenter(this)
        presenter?.start()
    }
}
