package com.acemurder.purify.parser

import com.acemurder.purify.model.VideoInfo
import org.jsoup.Jsoup

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */

class AwemeWebParser : HtmlParser<String, VideoInfo?> {
    override fun parse(data: String): VideoInfo? {

        val doc = Jsoup.connect(data).get()
        val elements = doc.getElementsByTag("script")

        val e = elements[elements.size - 1]
        val script = e.data()
        if (script == null || script.isEmpty()) {
            return null
        }
        if (!script.contains("playAddr")) {
            return null
        }

        val result = script.split("\"")
        if (result.size < 4) {
            return null
        }
        val playUrl = result[1]
        val coverUrl = result[3]
        if (playUrl.startsWith("http") && coverUrl.startsWith("http")) {
            return VideoInfo(playUrl, coverUrl)
        }
        return null
    }
}
