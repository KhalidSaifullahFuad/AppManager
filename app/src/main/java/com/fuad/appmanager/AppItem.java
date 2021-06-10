package com.fuad.appmanager;

import android.graphics.drawable.Drawable;

import androidx.versionedparcelable.ParcelField;

import java.io.Serializable;
import java.util.Comparator;

public class AppItem implements Serializable {

    private Drawable appIcon;
    private String appName;
    private String appPackageName;
    private String appVersion;
    private int appSize;
    public boolean isSelected = false;

    public AppItem() {}

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getAppSize() {
        return appSize;
    }

    public void setAppSize(int appSize) {
        this.appSize = appSize;
    }

    static Comparator<AppItem> appTitleComparator = (o1, o2) -> o1.getAppName().compareTo(o2.getAppName());

    static Comparator<AppItem> appSizeComparator = (o1, o2) -> {
        int size1 = o1.getAppSize();
        int size2 = o2.getAppSize();
        return size2 - size1;
    };
}
