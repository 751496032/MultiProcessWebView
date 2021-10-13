package com.hongYi.h5container.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hongYi.h5container.business.download.DownLoaderTask;
import com.hongYi.h5container.business.download.ZipExtractorTask;
import com.hongYi.h5container.business.utils.FileUtil;
import com.hongYi.h5container.ui.WebViewActivity;
import com.hongYi.h5container.utils.Constants;

import java.io.File;

/**
 * Created by zhuangxiaozheng on 2021/9/30.
 */
public class FileOperateActivity extends AppCompatActivity implements View.OnClickListener {
    private Button save_file, unZipFolder_file, is_file_exist, index_exist;

    private String downloadUrl = "https://github.com/751496032/hYi-sdk/archive/refs/heads/main.zip";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operate);
        initView();
    }

    private void initView() {
        save_file = findViewById(R.id.save_file);
        unZipFolder_file = findViewById(R.id.unZipFolder_file);
        is_file_exist = findViewById(R.id.is_file_exist);
        index_exist = findViewById(R.id.index_exist);
        index_exist.setOnClickListener(this);
        save_file.setOnClickListener(this);
        unZipFolder_file.setOnClickListener(this);
        is_file_exist.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.save_file) {
            File srcDir = new File(FileUtil.getSDCardPath() + "html");
            if (!srcDir.exists()) {     //判断该文件夹是否存在，不存在则创建
                FileUtil.createFolder(FileUtil.getSDCardPath(), "html");
            }
            DownLoaderTask downLoaderTask = new DownLoaderTask(downloadUrl, FileUtil.getSDCardPath() + "html", v.getContext());
            downLoaderTask.execute();
        } else if (vid == R.id.unZipFolder_file) {
            File srcDir = new File(FileUtil.getSDCardPath() + "html/main.zip");
            if (srcDir.exists()) {  //判断该解压包文件是否存在
                File srcDir2 = new File(FileUtil.getSDCardPath() + "html");
                if (!srcDir2.exists()) {    //判断解压的路径（文件夹）是否存在，不存在则创建
                    FileUtil.createFolder(FileUtil.getSDCardPath(), "html");
                }
                ZipExtractorTask zipExtractorTask = new ZipExtractorTask(FileUtil.getSDCardPath() + "html/main.zip", FileUtil.getSDCardPath() + "html", v.getContext(), true);
                zipExtractorTask.execute();
            } else {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            }
        } else if (vid == R.id.is_file_exist) {
            File srcDir = new File(FileUtil.getSDCardPath() + "html/hYi-jssdk-main");
            if (srcDir.exists()) {  //判断需要用到的文件是否存在
                Toast.makeText(this, "文件存在", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            }
        } else if (vid == R.id.index_exist) {
            File srcDir = new File(FileUtil.getSDCardPath() + "html/hYi-jssdk-main/test.html");
            if (srcDir.exists()) {  //判断该网页文件是否存在
                startActivity(WebViewActivity.class, "file://" + FileUtil.getSDCardPath() + "html/hYi-jssdk-main/test.html", "测试网页");
            } else {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            }
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
