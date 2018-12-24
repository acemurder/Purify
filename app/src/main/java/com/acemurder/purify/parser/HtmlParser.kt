package com.acemurder.purify.parser

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */
interface HtmlParser<T, V> {
    fun parse(data : T) : V
}