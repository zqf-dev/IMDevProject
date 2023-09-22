package com.zqf.imdevproject;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.gzdsy.faceauthsdk.utils.FaceCheckSDK;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FaceCheckSDK.init(this);
    }
}
