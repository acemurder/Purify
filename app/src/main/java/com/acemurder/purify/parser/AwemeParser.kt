package com.acemurder.purify.parser

import com.acemurder.purify.model.VideoInfo
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URI


/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */
class AwemeParser : HtmlParser<String, VideoInfo?> {
    private val okHttpClient: OkHttpClient by lazy { OkHttpClient() }

    override fun parse(data: String): VideoInfo? {
        var request = Request.Builder().url(data).get().build();
        var response = okHttpClient.newCall(request).execute()
        val path = response.request().url().toString()
        val uri = URI(path)
        val ids = uri.path.split("/").filter { !it.isEmpty() }
        val videoId = ids.last()
        request = Request.Builder()
                .url("https://api.amemv.com/aweme/v1/aweme/detail/?" +
                        "aweme_id=$videoId" +
                        "&app_name=aweme" +
                        "&version_code=390" +
                        "&version_name=3.9.0" +
                        "&device_platform=android" +
                        "&device_type=Mi%20Note%203"
                )
                .get()
                .build()
        response = okHttpClient.newCall(request).execute()
        val body = response.body() ?: return null
        val result = body.string()
        val resultObj = JSONObject(result)
        val videoObj = resultObj.optJSONObject("aweme_detail")?.optJSONObject("video")
        val coverUrl = videoObj?.optJSONObject("origin_cover")?.optJSONArray("url_list")?.getString(0)
        val playUrl = videoObj?.optJSONObject("play_addr")?.optJSONArray("url_list")?.getString(0)
        if (playUrl != null && coverUrl != null) {
            return VideoInfo(playUrl, coverUrl)
        }
        return null
    }
}