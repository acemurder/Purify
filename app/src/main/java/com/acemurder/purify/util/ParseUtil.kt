package com.acemurder.purify.util

import com.acemurder.purify.model.VideoInfo
import com.acemurder.purify.parser.EmptyParser
import com.acemurder.purify.parser.HtmlParser
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

    fun setParser(htmlParser: HtmlParser<String, VideoInfo?>) {
        this.htmlParser = htmlParser
    }

    suspend fun getVideoInfo(url: String): VideoInfo? {
        return withContext(Dispatchers.IO) {
            return@withContext htmlParser.parse(url)
        }
    }
}