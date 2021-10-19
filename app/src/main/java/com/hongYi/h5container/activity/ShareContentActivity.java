package com.hongYi.h5container.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hongYi.h5container.R;
import com.hongYi.h5container.business.ShareContentType;
import com.hongYi.h5container.utils.ShareUtils;

/**
 * Created by zhuangxiaozheng on 2021/10/19.
 */
public class ShareContentActivity extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 100;
    private static final int REQUEST_SHARE_FILE_CODE = 120;

    private static String FILE_TYPE = ShareContentType.IMAGE;

    private TextView tvShareFileUri;
    private Uri shareFileUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_content);
        tvShareFileUri = findViewById(R.id.tv_share_file_url);
    }

    public void handlerShare(View view) {
        switch (view.getId()) {
            case R.id.bt_choose_share_file:
                openFileChooser();
                break;
            case R.id.bt_share_text:
                ShareUtils.share(this, ShareContentType.TEXT, "Share Text", "This is a test message.", null, -1);
                break;
            case R.id.bt_share_image:
                ShareUtils.share(this, ShareContentType.IMAGE, "Share Image", "", getShareFileUri(), -1);
                break;
            case R.id.bt_share_audio:
                ShareUtils.share(this, ShareContentType.AUDIO, "Share Audio", "", getShareFileUri(), -1);
                break;
            case R.id.bt_share_video:
                ShareUtils.share(this, ShareContentType.VIDEO, "Share Video", "", getShareFileUri(), -1);
                break;
            case R.id.bt_share_file:
                ShareUtils.share(this, ShareContentType.FILE, "Share File", "", getShareFileUri(), REQUEST_SHARE_FILE_CODE);
                break;
            case R.id.bt_share:
                if (FILE_TYPE.equals(ShareContentType.FILE)) {
                    ShareUtils.share(this, FILE_TYPE, "Share File", "", getShareFileUri(), REQUEST_SHARE_FILE_CODE);
                } else {
                    ShareUtils.share(this, FILE_TYPE, "", "", getShareFileUri(), -1);
                }
                break;
            default:
                break;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), FILE_SELECT_CODE);
            overridePendingTransition(0, 0);
        } catch (Exception ex) {
            // Potentially direct the user to the Market with OnProgressChangeListener Dialog
            Toast.makeText(this, getString(R.string.please_install_filemanager), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("DemoActivity", "requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            shareFileUrl = data.getData();
            tvShareFileUri.setText(shareFileUrl.toString());

            if (data.getDataString().contains("image")) {
                FILE_TYPE = ShareContentType.IMAGE;
            } else if (data.getDataString().contains("video")) {
                FILE_TYPE = ShareContentType.VIDEO;
            } else if (data.getDataString().contains("audio")) {
                FILE_TYPE = ShareContentType.AUDIO;
            } else if (data.getDataString().contains("file")) {
                FILE_TYPE = ShareContentType.FILE;
            }
            // String filePath = FileUtil.getFileRealPath(this, shareFileUrl);
            // shareFileUrl = FileUtil.getFileUri(this, null, new File(filePath));
        } else if (requestCode == REQUEST_SHARE_FILE_CODE) {
            // todo share complete.
        }
    }

    public Uri getShareFileUri() {
        if (shareFileUrl == null) {
            Toast.makeText(this, "Please choose a file to share.", Toast.LENGTH_SHORT).show();
            tvShareFileUri.setText("Please choose a file to share.");
        }
        return shareFileUrl;
    }
}

