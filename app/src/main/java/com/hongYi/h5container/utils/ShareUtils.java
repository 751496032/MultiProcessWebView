package com.hongYi.h5container.utils;

import android.app.Activity;
import android.net.Uri;

import com.hongYi.h5container.business.Share2;
import com.hongYi.h5container.business.ShareContentType;

/**
 * Created by zhuangxiaozheng on 2021/10/19.
 */
public class ShareUtils {

    /**
     * 分享
     */
    public static void share(Activity activity, @ShareContentType String contentType, String title, String contentStr, Uri uri, int REQUEST_SHARE_FILE_CODE) {
        if (contentType.equals(ShareContentType.TEXT)) {
            new Share2.Builder(activity)
                    .setContentType(contentType)
                    .setTextContent(contentStr)
                    .setTitle(title)
                    // .forcedUseSystemChooser(false)
                    .build()
                    .shareBySystem();
        } else if (contentType.equals(ShareContentType.FILE)) {
            new Share2.Builder(activity)
                    .setContentType(contentType)
                    .setShareFileUri(uri)
                    .setTitle(title)
                    .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                    .build()
                    .shareBySystem();
        } else {
            new Share2.Builder(activity)
                    .setContentType(contentType)
                    .setShareFileUri(uri)
                    //.setShareToComponent("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI")
                    .setTitle(title)
                    .build()
                    .shareBySystem();
        }
    }

}
