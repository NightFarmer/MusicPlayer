package com.nightfarmer.musicplayer.mvp.base

import android.content.Context

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
}