package com.acemurder.purify

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import android.util.Patterns
import com.acemurder.purify.model.VideoInfo
import com.acemurder.purify.parser.AwemeParser
import com.acemurder.purify.util.ParseUtil
import com.acemurder.purify.util.PurifyUtil
import kotlinx.coroutines.*


/**
 * Created by ：AceMurder
 * Created on ：2018/12/22
 * Created for : Purify.
 * Enjoy it !!!
 */
class MainViewModel : ViewModel() {

    private val TAG = "Purify.MainViewModel"
    private val _result = MutableLiveData<VideoInfo>()
    private val _progressShow = MutableLiveData<Boolean>()
    private val _errorInfo = MutableLiveData<String>()
    private val _urlInfo = MutableLiveData<String>()
    private val _downloadProgress = MutableLiveData<Float>()
    private val _downloadFilePath = MutableLiveData<String>()

    val result: LiveData<VideoInfo>
        get() = _result
    val progressShow: LiveData<Boolean>
        get() = _progressShow
    val errorInfo: LiveData<String>
        get() = _errorInfo
    val urlInfo: LiveData<String>
        get() = _urlInfo
    val downloadFilePath: LiveData<String>
        get() = _downloadFilePath
    val downloadProgress: LiveData<Float>
        get() = _downloadProgress


    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    init {
        ParseUtil.setParser(AwemeParser())
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun loadData(url: String) {
        _progressShow.value = true
        uiScope.launch {
            try {
                val v = ParseUtil.getVideoInfo(url)
                v?.let {
                    _result.value = it
                }
                v ?: let {
                    _errorInfo.value = "没有解析到视频信息，该地址支持解析"
                }
            } catch (e: Exception) {
                _errorInfo.value = "解析出错，请检查url"
            } finally {
                _progressShow.value = false
            }
        }
    }

    fun parse(text: String) {
        uiScope.launch {
            val url = withContext(Dispatchers.IO) {
                val pattern = Patterns.WEB_URL
                val matcher = pattern.matcher(text)
                var url: String? = null
                if (matcher.find()) {
                    url = matcher.group(0)
                }
                return@withContext url
            }
            if (url != _urlInfo.value) {
                url?.let {
                    Log.i(TAG, url)
                    _urlInfo.value = url
                }
            }

        }
    }

    fun downloadFile(url: String, fileDir: String) {
        uiScope.launch {
            try {
                val filePath = withContext(Dispatchers.IO) {
                    return@withContext PurifyUtil.downloadVideo(url, fileDir) {
                        _downloadProgress.postValue(it)
                    }
                }
                filePath?.let {
                    _downloadProgress.value = -0f
                    _downloadFilePath.value = filePath
                }
            } catch (e : Exception) {
                _downloadProgress.value = 0f
                _errorInfo.value = "下载失败，请重视"
            } finally {
                _downloadProgress.value = -0f
            }
        }
    }
}