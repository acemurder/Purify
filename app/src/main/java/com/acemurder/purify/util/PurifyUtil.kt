package com.acemurder.purify.util

import android.text.TextUtils
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by ：AceMurder
 * Created on ：2018/12/22
 * Created for : Purify.
 * Enjoy it !!!
 */
object PurifyUtil {
    private val TAG = "Purify.PurifyUtil"
    val okHttpClient: OkHttpClient by lazy { OkHttpClient() }



    fun downloadVideo(url: String, filePath: String,
                      callback: ((Float) -> Unit)? = null): String? {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(filePath)) {
            return null
        }
        if (!File(filePath).exists()) {
            File(filePath).mkdirs()
        }
        val file = File(filePath, md5Encode(url) + ".mp4")
        val response = okHttpClient.newCall(Request.Builder().url(url).get().build()).execute()
        val responseBody = response.body()
        responseBody?.let {
            val length = it.contentLength().toFloat()
            val inputStream = it.byteStream()
            var curSize = 0.0
            val fos = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int = 0
            while ((inputStream.read(buffer).apply { size = this }) != -1) {
                fos.write(buffer, 0, size)
                callback?.let {
                    curSize += size
                    val progress = (curSize / length).toFloat()
                    Log.i(TAG, progress.toString())
                    callback(progress)
                }
            }
        }
        return file.absolutePath
    }

    fun md5Encode(text: String): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            val digest: ByteArray = instance.digest(text.toByteArray())
            var sb = StringBuffer()
            for (b in digest) {
                var i: Int = b.toInt() and 0xff
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    hexString = "0$hexString"
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }


}