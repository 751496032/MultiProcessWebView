package com.hongYi.h5container;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hongYi.h5container.ui.WebViewActivity;
import com.hongYi.h5container.utils.Constants;

public class MainActivity extends AppCompatActivity {

    String[] data = {"百度", "控价系统H5", "商城H5", "demo.html","优化前WebView"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.list_view);

        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        startActivity(WebViewActivity.class, "https://www.baidu.com", data[position]);
                        break;
                    case 1:
                        startActivity(WebViewActivity.class, "https://demonewh5.hyxmt.cn/", data[position]);
                        break;
                    case 2:
                        startActivity(WebViewActivity.class, "http://lgmy.hmeshop.cn/default.aspx?ReferralId=100831&go=1", data[position]);
                        break;
                    case 3:
                        startActivity(WebViewActivity.class, Constants.ANDROID_ASSET_URI + "demo.html", data[position]);
                        break;
                    case 4:
                        startActivity(WebViewTestActivity.class, "http://lgmy.hmeshop.cn/default.aspx?ReferralId=100831&go=1", data[position]);
                        break;
                }
            }
        });

    }


    private void startActivity(Class<?> clazz, String url, String title) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(Constants.URL, url);
        intent.putExtra(Constants.TITLE, title);
        intent.putExtra(Constants.JS_OBJECT_NAME,"hYi");
        startActivity(intent);
    }
}
