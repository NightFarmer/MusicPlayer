package com.nightfarmer.musicplayer.mvp.playdetail

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.nightfarmer.musicplayer.MusicInfo
import com.nightfarmer.musicplayer.MusicPlayBinder
import com.nightfarmer.musicplayer.MusicPlayService
import com.nightfarmer.musicplayer.MusicServiceConnection

/**
 * Created by zhangfan on 2016/10/28 0028.
 */
class PlayDetailPresenter(val view: PlayDetailContract.View) : PlayDetailContract.Presenter {
    override fun setProgress(progress: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onExist() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun play(music: MusicInfo) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val connect = object : MusicServiceConnection() {

        override fun onStateChange(currentMusic: MusicInfo?, state: Int) {
            view.setPlayState(currentMusic, state)
        }

        override fun onConnectedToService(binder: MusicPlayBinder) {
            view.setPlayState(binder.currentMusic, binder.playState)
            if (binder.duration <= 0) return
//            view.setSeek((binder.currentPosition * 1.0 / binder.duration * 100).toInt())
        }
    }

    override fun start() {
        val intent = Intent(view.getContext(), MusicPlayService::class.java)
        view.getContext().bindService(intent, connect, AppCompatActivity.BIND_AUTO_CREATE)

    }
}