package com.fallgod.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.bumptech.glide.Glide;
import com.fallgod.weather.ui.WeatherActivity;
import com.fallgod.weather.util.HttpUtil;
import com.fallgod.weather.util.LogUtil;
import com.fallgod.weather.util.SPUtil;

import interfaces.heweather.com.interfacesmodule.bean.alarm.Alarm;

public class AutoUpdateService extends Service {
    private static final String TAG = "AutoUpdateService";
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;//八小时的毫秒数
        long triggertAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggertAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        //更新天气数据，保存数据文本到sp
    }

    private void updateBingPic(){
        HttpUtil.sendRequset(WeatherActivity.BING_IMG, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String url) {
                LogUtil.d(TAG,"today bing image url:"+url);
                SPUtil.putString(AutoUpdateService.this,SPUtil.SP_BING_IMG,url);
            }

            @Override
            public void onError(Exception e) {
                LogUtil.d(TAG,"Bing image request error" + e.toString());
            }
        });
    }
}
