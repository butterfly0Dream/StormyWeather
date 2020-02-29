package com.fallgod.weather;

import android.app.Application;

import org.litepal.LitePal;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

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
        //初始化和风天气SDK
        HeConfig.init("HE2002291410011028","03b0ec8afc604519bf971978a0a2a240");
        //切换到免费节点
        HeConfig.switchToFreeServerNode();
    }
}
