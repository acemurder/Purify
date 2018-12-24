package com.acemurder.purify.parser

import com.acemurder.purify.AWEME_URL
import com.acemurder.purify.model.VideoInfo
import com.acemurder.purify.purifyClient
import okhttp3.HttpUrl
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

    override fun parse(data: String): VideoInfo? {
        var request = Request.Builder().url(data).get().build();
        var response = purifyClient.newCall(request).execute()
        val path = response.request().url().toString()
        val uri = URI(path)
        val ids = uri.path.split("/").filter { !it.isEmpty() }
        val videoId = ids.last()
        val url = HttpUrl.parse(AWEME_URL)!!.newBuilder()
                .addQueryParameter("aweme_id", videoId)
                .addQueryParameter("app_name", "aweme")
                .addQueryParameter("version_code", "380")
                .addQueryParameter("version_name", "3.8.0")
                .addQueryParameter("device_platform", "android")
                .addQueryParameter("device_type", "Mi%20Note%202")
                .build()
        request = Request.Builder()
                .url(url)
                .get()
                .build()
        response = purifyClient.newCall(request).execute()
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