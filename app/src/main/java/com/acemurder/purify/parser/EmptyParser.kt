package com.acemurder.purify.parser

import com.acemurder.purify.model.VideoInfo

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */
class EmptyParser : HtmlParser<String, VideoInfo?> {
    override fun parse(data: String): VideoInfo? {
        return null
    }
}