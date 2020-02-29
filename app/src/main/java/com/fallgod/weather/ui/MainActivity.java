package com.fallgod.weather.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.fallgod.weather.R;
import com.fallgod.weather.util.SPUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String weatherId  = SPUtil.getString(this,SPUtil.SP_WEATHER_ID,"");
        if (!TextUtils.isEmpty(weatherId)){
            Intent intent = new Intent(this,WeatherActivity.class);
            intent.putExtra("weather_id",weatherId);
            startActivity(intent);
            finish();
        }
    }
}
