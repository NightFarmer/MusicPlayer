package com.nightfarmer.musicplayer.mvp.main

import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.mvp.base.BaseContract

/**
 * Created by zhangfan on 2016/10/27 0027.
 */
interface MainContract {

    interface View : BaseContract.View {

        fun setSeek(seek: Int)
        fun setPlayState(music: MusicInfo?, state: Int)
        fun setDataList(dataList: List<MusicInfo>)
    }

    interface Presenter : BaseContract.MusicPlayPresenter {
//        fun stop()
//        fun next()
    }
}