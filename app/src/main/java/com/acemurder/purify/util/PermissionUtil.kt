package com.acemurder.purify.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by ：AceMurder
 * Created on ：2018/12/23
 * Created for : Purify.
 * Enjoy it !!!
 */
private var permissionCode = 0
data class _Permission(val granted: (Boolean) -> Unit)

object PermissionManager {
    val permissionList = HashMap<Int, _Permission>()
    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissionList.containsKey(requestCode)) {
            val granted = permissionList.remove(requestCode) ?: return
            val isTip = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])
            val isDenied = grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED
            if (isDenied) {
                if (isTip) {
                    val code = ++permissionCode
                    PermissionManager.permissionList[requestCode] = granted
                    ActivityCompat.requestPermissions(activity, permissions, code)
                } else {
                    granted.granted.invoke(false)
                }
            } else
                granted.granted.invoke(true)
        }
    }
}

fun Activity.storagePermission(granted: (Boolean) -> Unit) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) == PackageManager.PERMISSION_GRANTED)
        granted(true)
    else {
        val requestCode = ++permissionCode
        PermissionManager.permissionList[requestCode] = _Permission(granted)
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
    }
}





