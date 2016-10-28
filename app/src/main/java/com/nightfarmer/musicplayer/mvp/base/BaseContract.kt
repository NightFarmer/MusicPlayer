package com.nightfarmer.musicplayer.mvp.base

import android.content.Context
import com.nightfarmer.musicplayer.MusicInfo

/**
 * Created by zhangfan on 2016/10/27 0027.
 */
interface BaseContract {

    interface View {
        fun getContext(): Context
    }

    interface Presenter {
        fun start()
    }

    interface MusicPlayPresenter : Presenter {
        fun onExist()
        fun play(music: MusicInfo)
        fun pause()
        fun setProgress(progress: Int)
    }
}