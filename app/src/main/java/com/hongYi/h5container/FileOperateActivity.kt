package com.hongYi.h5container

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hongYi.h5container.business.download.DownLoaderTask
import com.hongYi.h5container.business.utils.FileUtil
import com.hongYi.h5container.business.utils.Utils
import com.hongYi.h5container.ui.WebViewActivity
import com.hongYi.h5container.utils.Constants

/**
 * Created by zhuangxiaozheng on 2021/9/30.
 */
class FileOperateActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = "FileOperateActivity"
    lateinit var download_file: Button
    lateinit var index_exist: Button
    private val downloadUrl = "https://github.com/751496032/hYi-sdk/archive/refs/heads/main.zip"
    lateinit var fileName: String
    lateinit var h5FileNamePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_operate)
        initView()
    }

    private fun initView() {
        download_file = findViewById(R.id.download_file)
        index_exist = findViewById(R.id.index_exist)
        index_exist.setOnClickListener(this)
        download_file.setOnClickListener(this)
        fileName = Utils.getAppName(this)
        h5FileNamePath = "file://" + FileUtil.getSDCardPath() + Utils.getAppName(this) + "/hYi-jssdk-main/test.html"
    }

    override fun onClick(v: View) {
        val vid = v.id
        if (vid == R.id.download_file) {
            DownLoaderTask.getInstance().downLoaderTaskFunction(downloadUrl, fileName, v.context)
                    .setDownloadFileCallBackListener(object : DownLoaderTask.DownloadFileCallBack {
                        override fun downloadFileSuccess(path: String?) {
                            Log.d(TAG, "下载成功:路径——>$path")
                        }

                        override fun downloadFileFail() {
                            Log.d(TAG, "下载失败")
                        }

                    })
                    .setZipFolderFileCallBackListener(object : DownLoaderTask.ZipFolderCallBack {
                        override fun unZipFolderSuccess(path: String?) {
                            Log.d(TAG, "解压成功:路径——>$path")
//                            startActivity(WebViewActivity::class.java, "file://$path/hYi-jssdk-main/test.html", "测试网页")
                        }

                        override fun unZipFolderFail() {
                            Log.d(TAG, "解压失败")
                        }

                    }).execute()
        } else if (vid == R.id.index_exist) {
            startActivity(WebViewActivity::class.java, h5FileNamePath, "测试网页")
        }
    }

    private fun startActivity(clazz: Class<*>, url: String?, title: String) {
        val intent = Intent(this, clazz)
        intent.putExtra(Constants.URL, url)
        intent.putExtra(Constants.TITLE, title)
        intent.putExtra(Constants.JS_OBJECT_NAME, "hYi")
        intent.putExtra(Constants.CAN_NATIVE_REFRESH, false)
        startActivity(intent)
    }
}
