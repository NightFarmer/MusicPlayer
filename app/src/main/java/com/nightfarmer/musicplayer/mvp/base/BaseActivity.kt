package com.nightfarmer.musicplayer.mvp.base

import android.content.Context
import android.support.v7.app.AppCompatActivity

/**
 * Created by zhangfan on 2016/10/27 0027.
 */

open class BaseActivity : AppCompatActivity(), BaseContract.View {

    override fun getContext(): Context {
        return this
    }

}
