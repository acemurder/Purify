package com.acemurder.purify.viewmodel

import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Created by ：AceMurder
 * Created on ：2018/12/29
 * Created for : Purify.
 * Enjoy it !!!
 */
open class BaseViewModel(
        protected open val viewModelJob: Job = Job(),
        protected open val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob))
    : ViewModel(), CoroutineScope by scope