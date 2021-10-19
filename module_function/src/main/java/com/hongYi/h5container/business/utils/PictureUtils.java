package com.hongYi.h5container.business.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import com.previewlibrary.GPreviewBuilder;

import java.util.List;


/**
 * Created by zhuangxiaozheng on 2021/10/19.
 */
public class PictureUtils {

    private int GALLERY_REQUEST_CODE = 1;

    /**
     * 图片预览
     */
    public static void picturePreview(Activity context, List list, int position) {
        GPreviewBuilder.from(context)
                .setData(list)
                .setCurrentIndex(position)
                .setSingleFling(true)//是否在黑屏区域点击返回
                .setDrag(true)//是否禁用图片拖拽返回
                .setType(GPreviewBuilder.IndicatorType.Number)//指示器类型
                .start();//启动
    }

    /**
     * 图片选择
     */
    public static void pictureSelector() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
    }

}
