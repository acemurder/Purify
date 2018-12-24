package com.acemurder.purify.util

import com.acemurder.purify.AWEME_SIGN
import com.acemurder.purify.WE_VIDEO_SIGN
import com.acemurder.purify.model.VideoInfo
import com.acemurder.purify.parser.AwemeParser
import com.acemurder.purify.parser.EmptyParser
import com.acemurder.purify.parser.HtmlParser
import com.acemurder.purify.parser.WeVideoParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */
object ParseUtil {
    private var htmlParser: HtmlParser<String, VideoInfo?> = EmptyParser()
    private val awemeParser: HtmlParser<String, VideoInfo?> by lazy { AwemeParser() }
    private val weVideoParser: HtmlParser<String, VideoInfo?> by lazy { WeVideoParser() }


    fun setParser(htmlParser: HtmlParser<String, VideoInfo?>) {
        this.htmlParser = htmlParser
    }

    suspend fun getVideoInfo(url: String): VideoInfo? {
        return withContext(Dispatchers.IO) {
            when {
                url.contains(AWEME_SIGN) -> awemeParser.parse(url)
                url.contains(WE_VIDEO_SIGN) -> weVideoParser.parse(url)
                else -> null
            }
        }
    }
}