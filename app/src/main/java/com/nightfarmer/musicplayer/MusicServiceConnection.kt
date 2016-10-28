package com.nightfarmer.musicplayer

import android.app.Service
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Created by zhangfan on 2016/10/27 0027.
 */
open class MusicServiceConnection : MusicPlayBinder.PlayCallBack(), ServiceConnection {
    override fun onServiceDisconnected(name: ComponentName?) {
        println("disconnect...")
        unregister()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        println("connnted....")
        binder = service as? MusicPlayBinder
        binder?.let {
            it.addListener(this)
            onConnectedToService(it)
//            onStateChange(it.currentMusic, it.playState)
        }
    }

    var binder: MusicPlayBinder? = null

    fun unregister() {
        println("remove..")
        binder?.removeListener(this)
    }
}