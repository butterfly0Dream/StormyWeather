package com.fallgod.weather;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by JackPan on 2019/10/30
 * Describe:
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    //单例
    public static MyApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //赋值实例
        instance = this;
        LitePal.initialize(this);
    }
}
