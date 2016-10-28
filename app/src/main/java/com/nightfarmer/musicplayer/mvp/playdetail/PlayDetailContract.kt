package com.nightfarmer.musicplayer.mvp.playdetail

import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.mvp.base.BaseContract

/**
 * Created by zhangfan on 2016/10/28 0028.
 */
interface PlayDetailContract {
    interface View : BaseContract.View {

        fun setPlayState(music: MusicInfo?, state: Int)
    }

    interface Presenter : BaseContract.MusicPlayPresenter {

    }
}