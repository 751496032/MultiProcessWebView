package com.hongYi.h5container.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.qrcode.encoder.QRCode;
import com.hongYi.h5container.R;
import com.hongYi.h5container.business.ImageLoader;
import com.hongYi.h5container.business.UserViewInfo;
import com.hongYi.h5container.business.utils.PictureUtils;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.ZoomMediaLoader;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuangxiaozheng on 2021/10/19.
 */
public class TestFunctionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView show_image;
    private TextView image_select, scan;
    private int GALLERY_REQUEST_CODE = 1;
    private int QRCode_REQUEST_CODE = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        show_image = findViewById(R.id.show_image);
        image_select = findViewById(R.id.image_select);
        scan = findViewById(R.id.scan);
        show_image.setOnClickListener(this);
        image_select.setOnClickListener(this);
        scan.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.show_image) {
            List<UserViewInfo> stringList = new ArrayList<>();
            stringList.add(new UserViewInfo("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F18%2F08%2F24%2F05dbcc82c8d3bd356e57436be0922357.jpg&refer=http%3A%2F%2Fbpic.588ku.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1637215554&t=9009ef54f8723e97a48cecb7ce390c3c"));
            PictureUtils.picturePreview(this, stringList, 0);
        } else if (vid == R.id.image_select) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
        } else if (vid == R.id.scan) {
            Intent intent = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent, QRCode_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (data != null) {
                image_select.setText("图片地址——>" + data.getDataString());
                Log.d("zxz", data.getDataString());
            }
        } else if (requestCode == QRCode_REQUEST_CODE) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                scan.setText("二维码返回结果为：" + content);
                Log.d("zxz", "二维码返回结果为：" + content);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
