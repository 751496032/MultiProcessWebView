package com.hongYi.h5container.business;

import android.graphics.Rect;
import android.os.Parcel;

import androidx.annotation.Nullable;

import com.previewlibrary.enitity.IThumbViewInfo;

/**
 * Created by zhuangxiaozheng on 2021/10/19.
 */
public class UserViewInfo implements IThumbViewInfo {
    //图片地址
    private String url;
    // 记录坐标
    private Rect mBounds;
    private String user = "用户字段";
    private String videoUrl;

    public UserViewInfo(String url) {
        this.url = url;
    }

    public UserViewInfo(String videoUrl, String url) {
        this.url = url;
        this.videoUrl = videoUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getUrl() {//将你的图片地址字段返回
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Rect getBounds() {//将你的图片显示坐标字段返回
        return mBounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.mBounds, flags);
        dest.writeString(this.user);
        dest.writeString(this.videoUrl);
    }

    protected UserViewInfo(Parcel in) {
        this.url = in.readString();
        this.mBounds = in.readParcelable(Rect.class.getClassLoader());
        this.user = in.readString();
        this.videoUrl = in.readString();
    }

    public static final Creator<UserViewInfo> CREATOR = new Creator<UserViewInfo>() {
        @Override
        public UserViewInfo createFromParcel(Parcel source) {
            return new UserViewInfo(source);
        }

        @Override
        public UserViewInfo[] newArray(int size) {
            return new UserViewInfo[size];
        }
    };
}
