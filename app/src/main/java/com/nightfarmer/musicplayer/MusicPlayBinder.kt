package com.nightfarmer.musicplayer

import android.os.Binder

/**
 * Created by zhangfan on 2016/10/27 0027.
 */
public class MusicPlayBinder : Binder() {

    val callBackList = hashSetOf<PlayCallBack>()

    var currentMusic: MusicInfo? = null
    var playState: Int = 0

    fun addListener(callBack: PlayCallBack?) {
        callBack?.let {
            callBackList.add(it)
        }
    }

    fun removeListener(callBack: PlayCallBack?) {
        callBackList.remove(callBack)
    }

    abstract class PlayCallBack {
//        open fun onConnectedToService(binder: MusicPlayBinder) {
//        }

        open fun onProgress() {
        }

        open fun onStart(currentMusic: MusicInfo?) {
        }

        open fun onStop() {
        }

        open fun onPause() {
        }

        open fun onStateChange(currentMusic: MusicInfo?, state: Int) {
        }
    }

}