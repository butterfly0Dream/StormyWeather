package com.fallgod.weather;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by JackPan on 2019/10/30
 * Describe:
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    private MyApplication(){}

    //单例
    public static MyApplication getInstance(){
        if (instance == null){
            synchronized (MyApplication.class){
                if (instance == null){
                    instance = new MyApplication();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
