package com.hongYi.h5container.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hongYi.h5container.R
import com.hongYi.h5container.ui.WebViewActivity
import com.hongYi.h5container.utils.ActivityHelper
import com.hongYi.h5container.utils.Constants

class MainActivity : AppCompatActivity() {

    private  val REQUEST_WRITE_STORAGE_PERMISSION = 101

    var data = arrayOf("百度", "豆瓣", "京东商城", "腾讯视频","demo.html", "优化前WebView", "文件下载、解压、展示", "图片预览", "分享")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.list_view)

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listView.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

           val targetClass = ActivityHelper.INSTANCE.getSmallActivity<WebViewActivity>(context = this)

            when (position) {
                0 -> startActivity(targetClass, "https://www.baidu.com", data[position])
                1 -> startActivity(targetClass, "https://www.douban.com", data[position])
                2 -> startActivity(targetClass, "https://m.jd.com", data[position])
                3 -> startActivity(targetClass, "https://v.qq.com/?ptag=qqbsc", data[position])
                4 -> startActivity(targetClass, Constants.ANDROID_ASSET_URI + "demo.html", data[position])
                5 -> startActivity(WebViewTestActivity::class.java, "http://lgmy.hmeshop.cn/default.aspx?ReferralId=100831&go=1", data[position])
                6 -> startActivity(FileOperateActivity::class.java, "", "")
                7 -> startActivity(TestFunctionActivity::class.java, "", "")
                8 -> startActivity(ShareContentActivity::class.java, "", "")
            }
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE_PERMISSION)
            } else {
                Toast.makeText(this, "缺少文件读写权限，可能会造成无法分享文件", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "缺少文件读写权限，可能会造成无法分享文件", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startActivity(clazz: Class<*>, url: String, title: String) {
        val intent = Intent(this, clazz)
        intent.putExtra(Constants.URL, url)
        intent.putExtra(Constants.TITLE, title)
        intent.putExtra(Constants.JS_OBJECT_NAME, "hYi")
        intent.putExtra(Constants.CAN_NATIVE_REFRESH, false)
        startActivity(intent)



        // 外部调用案例
//        val command = CommandHelper.INSTANCE.getCommand<CommandLogin>("login")
//        command?.registerCommandMonitor(object : ICommandMonitor {
//            override fun onMonitor(parameters: Map<*, *>, callback: ICallbackFromMainToWebInterface) {
//                Toast.makeText(App.INSTANCE, "登录中...", Toast.LENGTH_LONG).show()
//
//            }
//
//        })






    }

    override fun onDestroy() {
        super.onDestroy()
//        CommandHelper.INSTANCE.getCommand<CommandLogin>("login")?.unregisterCommandMonitor();
    }



}
