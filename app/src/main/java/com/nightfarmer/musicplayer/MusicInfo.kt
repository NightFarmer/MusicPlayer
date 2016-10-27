package com.nightfarmer.musicplayer

import java.io.Serializable

/**
 * Created by zhangfan on 2016/10/27 0027.
 */

class MusicInfo() : Serializable {
    var url: String = ""
    var playUrl: String = ""
    var name: String = ""
    var singer: String = ""
}
