package com.hongYi.h5container.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hongYi.h5container.business.download.DownLoaderTask;
import com.hongYi.h5container.business.download.ZipExtractorTask;
import com.hongYi.h5container.business.utils.FileUtil;
import com.hongYi.h5container.business.utils.Utils;
import com.hongYi.h5container.ui.WebViewActivity;
import com.hongYi.h5container.utils.Constants;

import java.io.File;

/**
 * Created by zhuangxiaozheng on 2021/9/30.
 */
public class FileOperateActivity extends AppCompatActivity implements View.OnClickListener {
    private Button download_file, index_exist;

    private String downloadUrl = "https://github.com/751496032/hYi-sdk/archive/refs/heads/main.zip";
    String fileName;
    private String h5FileNamePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operate);
        initView();
    }

    private void initView() {
        download_file = findViewById(R.id.download_file);
        index_exist = findViewById(R.id.index_exist);
        index_exist.setOnClickListener(this);
        download_file.setOnClickListener(this);
        fileName = Utils.getAppName(this);
        h5FileNamePath = "file://" + FileUtil.getSDCardPath() + Utils.getAppName(this) + "/hYi-jssdk-main/test.html";
    }


    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.download_file) {
            DownLoaderTask downLoaderTask = new DownLoaderTask(downloadUrl, fileName, v.getContext());
            downLoaderTask.execute();
        } else if (vid == R.id.index_exist) {
            startActivity(WebViewActivity.class, h5FileNamePath, "测试网页");
        }
    }

    private void startActivity(Class<?> clazz, String url, String title) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra(Constants.URL, url);
        intent.putExtra(Constants.TITLE, title);
        intent.putExtra(Constants.JS_OBJECT_NAME, "hYi");
        intent.putExtra(Constants.CAN_NATIVE_REFRESH, false);
        startActivity(intent);
    }
}
