package com.acemurder.purify

import android.os.Environment
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */

val VIDEO_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().absolutePath.toString() + "/Purify"

const val BUFFER_SIZE = 1024
const val TIMEOUT = 60L
const val DEFAULT_VIDEO_SUFFIX = ".mp4"
const val AWEME_URL = "https://api.amemv.com/aweme/v1/aweme/detail/"
const val WE_VIDEO_URL = "https://h5.weishi.qq.com/webapp/json/weishi/WSH5GetPlayPage"
const val AWEME_SIGN = "douyin.com"
const val WE_VIDEO_SIGN = "weishi.qq.com"
const val KEY_VIDEO_URL = "video_url"

val purifyClient: OkHttpClient by lazy {
    val client = OkHttpClient.Builder()
            .callTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    client
}