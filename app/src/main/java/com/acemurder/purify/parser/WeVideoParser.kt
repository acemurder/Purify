package com.acemurder.purify.parser

import com.acemurder.purify.WE_VIDEO_URL
import com.acemurder.purify.model.VideoInfo
import com.acemurder.purify.purifyClient
import okhttp3.HttpUrl
import okhttp3.Request
import org.json.JSONObject
import java.net.URI

/**
 * Created by ：AceMurder
 * Created on ：2018/12/24
 * Created for : Purify.
 * Enjoy it !!!
 */
class WeVideoParser : HtmlParser<String, VideoInfo?> {
    override fun parse(data: String): VideoInfo? {
        val feedId = URI(data).path.split("/").filter { !it.isEmpty() }[2]
        val url = HttpUrl.parse(WE_VIDEO_URL)!!
                .newBuilder()
                .addQueryParameter("feedid", feedId)
                .build()
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        var response = purifyClient.newCall(request).execute()
        val body = response.body() ?: return null
        val resultObj = JSONObject(body.string())
        val feedObj = resultObj
                .optJSONObject("data")
                ?.optJSONArray("feeds")
                ?.getJSONObject(0)

        val playUrl = feedObj?.getString("video_url")
        val coverUrl = feedObj
                ?.getJSONArray("images")
                ?.getJSONObject(0)
                ?.getString("url")
        if (playUrl != null && coverUrl != null) {
            return VideoInfo(playUrl, coverUrl)
        }
        return null
    }
}