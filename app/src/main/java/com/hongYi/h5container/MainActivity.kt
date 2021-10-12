package com.hongYi.h5container

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.hongYi.h5container.ui.WebViewActivity
import com.hongYi.h5container.utils.Constants

class MainActivity : AppCompatActivity() {
    var data = arrayOf("百度", "控价系统H5", "商城H5", "demo.html", "优化前WebView")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        listView.onItemClickListener = OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            when (position) {
                0 -> startActivity(WebViewActivity::class.java, "https://www.baidu.com", data[position])
                1 -> startActivity(WebViewActivity::class.java, "https://demonewh5.hyxmt.cn/", data[position])
                2 ->
                    // https://book.douban.com/subject/10785583/
                    // http://lgmy.hmeshop.cn/default.aspx?ReferralId=100831&go=1
                    startActivity(WebViewActivity::class.java, "https://m.douban.com/subject/10785583/", data[position])
                3 -> startActivity(WebViewActivity::class.java, Constants.ANDROID_ASSET_URI + "demo.html", data[position])
                4 -> startActivity(WebViewTestActivity::class.java, "http://lgmy.hmeshop.cn/default.aspx?ReferralId=100831&go=1", data[position])
            }
        }
    }

    private fun startActivity(clazz: Class<*>, url: String, title: String) {
        val intent = Intent(this, clazz)
        intent.putExtra(Constants.URL, url)
        intent.putExtra(Constants.TITLE, title)
        intent.putExtra(Constants.JS_OBJECT_NAME, "hYi")
        intent.putExtra(Constants.CAN_NATIVE_REFRESH,false)
        startActivity(intent)
    }
}
