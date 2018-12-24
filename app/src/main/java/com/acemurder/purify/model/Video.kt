package com.acemurder.purify.model

import com.google.gson.annotations.SerializedName

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */
data class VideoInfo(var playUrl: String, var coverUrl: String)

data class AwemeResponse(@SerializedName("status_code") var statusCode: Int,
                         @SerializedName("aweme_detail") var awemeDetail: AwemeDetail)

data class AwemeDetail(@SerializedName("video") var videoDetail: VideoDetail)

class VideoDetail
