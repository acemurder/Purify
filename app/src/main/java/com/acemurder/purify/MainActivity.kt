package com.acemurder.purify

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.acemurder.purify.util.PermissionManager
import com.acemurder.purify.util.storagePermission
import com.acemurder.purify.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import java.io.File


class MainActivity : AppCompatActivity() {

    val TAG = "Purify.MainActivity"
    private val urlEditText: EditText  by lazy { findViewById<EditText>(R.id.et_url) }
    private val searchButton: Button  by lazy { findViewById<Button>(R.id.btn_search) }
    private val coverImage: ImageView  by lazy { findViewById<ImageView>(R.id.iv_cover) }
    private val progressBar: ProgressBar by lazy { findViewById<ProgressBar>(R.id.pg_parse) }
    private val rootView: View by lazy { findViewById<View>(R.id.root) }

    private val viewModel: MainViewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideKeyboard()
        initView()
        observe()
    }

    private fun initView() {
        rootView.setOnClickListener {
            hideKeyboard()
        }
        searchButton.setOnClickListener {
            hideKeyboard()
            coverImage.visibility = View.GONE
            val url = urlEditText.text.toString()
            if (!TextUtils.isEmpty(url)) {
                viewModel.loadData(url)
            } else {
                Snackbar.make(rootView, "url不能为空", Snackbar.LENGTH_SHORT).show()
            }
        }
        coverImage.setOnClickListener {
            hideKeyboard()
            viewModel.result.value ?: let {
                return@setOnClickListener
            }
            val dialog = AlertDialog.Builder(this@MainActivity)
                    .setMessage("开始下载视频")
                    .setPositiveButton("下载") { dialog, _ ->
                        storagePermission { granted ->
                            if (granted) {
                                dialog.dismiss()
                                viewModel.downloadFile(viewModel.result.value!!.playUrl, VIDEO_DOWNLOAD_PATH)
                            } else {
                                dialog.dismiss()
                                Snackbar.make(rootView, "未获取到权限，无法开始下载", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNeutralButton("复制链接到剪切板") { dialog, _ ->
                        dialog.dismiss()
//                        startActivity(Intent(this, VideoPlayActivity::class.java)
//                                .apply { putExtra(KEY_VIDEO_URL, viewModel.result.value!!.playUrl) })
                        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val mClipData = ClipData.newPlainText("Purify", viewModel.result.value!!.playUrl)
                        cmb.primaryClip = mClipData
                        Snackbar.make(rootView, "已复制", Snackbar.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun observe() {
        with(viewModel) {
            result.observe(this@MainActivity, Observer {
                it?.let { video ->
                    coverImage.visibility = View.VISIBLE
                    if (video.coverUrl.endsWith(".gif")) {
                        Glide.with(this@MainActivity).asGif().load(video.coverUrl).into(coverImage)
                    } else {
                        Glide.with(this@MainActivity).asBitmap().load(video.coverUrl).into(coverImage)
                    }
                }
            })
            progressShow.observe(this@MainActivity, Observer {
                it?.let { show ->
                    progressBar.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
            errorInfo.observe(this@MainActivity, Observer {
                Snackbar.make(rootView, it!!, Snackbar.LENGTH_SHORT).show()
            })
            urlInfo.observe(this@MainActivity, Observer {
                val dialog = AlertDialog.Builder(this@MainActivity)
                        .setMessage("检查到URL,是否直接搜索\n$it")
                        .setPositiveButton("搜索") { _, _ ->
                            urlEditText.setText(it!!)
                            viewModel.loadData(it)
                        }
                        .setNegativeButton("取消") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            })
            downloadProgress.observe(this@MainActivity, Observer {
                if (it!! == 0f) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.max = 100
                    progressBar.progress = it.toInt()
                }

            })
            downloadFilePath.observe(this@MainActivity, Observer {
                progressBar.visibility = View.GONE
                val contentUri = Uri.fromFile(File(it))
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri)
                sendBroadcast(mediaScanIntent)
                Snackbar.make(rootView, "视频下载成功，路径: $it", Snackbar.LENGTH_LONG).show()
            })
        }
    }

    override fun onResume() {
        super.onResume()
        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val data = cm.primaryClip
        val content = data?.getItemAt(0)?.text?.toString()
        content?.let {
            Log.i(TAG, "识别到文字")
            viewModel.parse(content)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }
}
