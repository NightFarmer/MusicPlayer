package com.nightfarmer.musicplayer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.music_list_item.view.*
import okhttp3.*
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Created by zhangfan on 2016/10/27 0027.
 */
class MainPageListAdapter(val mainActivity: MainActivity) : RecyclerView.Adapter<MainPageListAdapter.MusicHolder>() {
    var musicList = arrayListOf<MusicInfo>()
    override fun getItemCount(): Int {
        return musicList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MusicHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.music_list_item, parent, false)
        return MusicHolder(view)
    }

    override fun onBindViewHolder(holder: MusicHolder?, position: Int) {
        val musicInfo = musicList[position]
        holder?.bindData(musicInfo)
    }

    inner class MusicHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView?.setOnClickListener {
                val okHttpClient = OkHttpClient()
                val s = "http://www.5nd.com${musicInfo?.url}"
                println(s)
                val request = Request.Builder()
                        .url(s).build()
                okHttpClient.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                        e?.printStackTrace()
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                        val html = Charset.forName("gbk").decode(ByteBuffer.wrap(response?.body()?.bytes()))
//                        val regex = """<audio id="jp_audio_0" preload="metadata" src="([^"]*)"></audio>""".toRegex()
                        val regex = """<div class="songPlayBox"><div id="kuPlayer" data-play="([^"]*)"></div>""".toRegex()
                        val find = regex.find(html)
                        val url = find?.groupValues?.get(1)
                        println(url)
                        musicInfo?.let {
                            musicInfo?.playUrl = "http://mpge.5nd.com/$url"
                            mainActivity.playMusic(it)
                        }
                    }
                })
            }
        }

        private var musicInfo: MusicInfo? = null

        fun bindData(musicInfo: MusicInfo) {
            this.musicInfo = musicInfo
            itemView?.tv_name?.text = musicInfo.name
            itemView?.tv_singer?.text = musicInfo.singer
        }

    }
}