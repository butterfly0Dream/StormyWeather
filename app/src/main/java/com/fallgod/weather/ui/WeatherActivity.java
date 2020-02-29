package com.fallgod.weather.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fallgod.weather.R;
import com.fallgod.weather.util.HttpUtil;
import com.fallgod.weather.util.LogUtil;
import com.fallgod.weather.util.SPUtil;
import com.google.gson.Gson;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";

    private static final String BING_IMG = "http://guolin.tech/api/bing_pic";

    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;

    private Weather weatherInfo;
    private ImageView weatherBg;
    private ScrollView weatherLayout;
    private Button navButton;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置透明状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        // 初始化各控件
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        weatherBg = findViewById(R.id.weather_bg);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);

//        String weatherStr  = SPUtil.getString(this,SPUtil.SP_WEATHER,"");
        String weatherId = getIntent().getStringExtra("weather_id");
        weatherLayout.setVisibility(View.VISIBLE);
        //将数据缓存
        SPUtil.putString(this,SPUtil.SP_WEATHER_ID,weatherId);
        LogUtil.d(TAG,"weatherId："+weatherId);
        requestWeather(weatherId);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //添加下拉刷新事件
        swipeRefreshLayout.setOnRefreshListener(()->requestWeather(weatherId));

        //添加导航栏按钮点击事件
        navButton.setOnClickListener((View v)->drawerLayout.openDrawer(GravityCompat.START));

        //更新背景图片,优先从sp取
        String url = SPUtil.getString(WeatherActivity.this,SPUtil.SP_BING_IMG,"");
        if (TextUtils.isEmpty(url)){
            loadBingImage();
        }else {
            Glide.with(WeatherActivity.this).load(url).into(weatherBg);
        }
    }

    /**
     * 请求必应每日一图的图片资源
     */
    private void loadBingImage(){
        HttpUtil.sendRequset(BING_IMG, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String url) {
                LogUtil.d(TAG,"today bing image url:"+url);
                SPUtil.putString(WeatherActivity.this,SPUtil.SP_BING_IMG,url);
                runOnUiThread(()->Glide.with(WeatherActivity.this).load(url).into(weatherBg));
            }

            @Override
            public void onError(Exception e) {
                LogUtil.d(TAG,"Bing image request error" + e.toString());
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     * @param weatherId 城市id
     */
    public void requestWeather(final String weatherId){
        HeWeather.getWeather(this, weatherId, new HeWeather.OnResultWeatherDataListBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                LogUtil.e(TAG, "onError: getWeather" + throwable);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(Weather weather) {
                LogUtil.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(weather));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if ( Code.OK.getCode().equalsIgnoreCase(weather.getStatus()) ){
                    //此时返回数据
                    weatherInfo = weather;
                    showWeatherInfo();
                } else {
                    //在此查看返回数据失败的原因
                    String status = weather.getStatus();
                    Code code = Code.toEnum(status);
                    LogUtil.i(TAG, "failed code: " + code);
                }
                //确保每次都是最新的图片
                loadBingImage();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 处理并展示天气数据
     */
    private void showWeatherInfo(){

        Basic basic = weatherInfo.getBasic();
        NowBase nowBase = weatherInfo.getNow();
        List<ForecastBase> forecastBaseList = weatherInfo.getDaily_forecast();
        List<LifestyleBase> lifestyleBaseList = weatherInfo.getLifestyle();

        String cityName = basic.getLocation();
        String updateTime = weatherInfo.getUpdate().getLoc();
        String degree = nowBase.getTmp() + "°C";
        String weatherText = nowBase.getCond_txt();
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherText);
        forecastLayout.removeAllViews();
        for (ForecastBase forecastBase:forecastBaseList){//添加多个item，分别设置每天天气
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dataText.setText(forecastBase.getDate());
            infoText.setText(forecastBase.getCond_txt_d());
            maxText.setText(forecastBase.getTmp_max());
            minText.setText(forecastBase.getTmp_min());
            forecastLayout.addView(view);
        }
//        aqiText;
//        pm25Text;
        for (LifestyleBase lifestyleBase:lifestyleBaseList){
            String text;
            if (lifestyleBase.getType().equals("comf")){
                text = "舒适度：" + lifestyleBase.getTxt();
                comfortText.setText(text);
            }else if (lifestyleBase.getType().equals("cw")){
                text = "洗车指数:" + lifestyleBase.getTxt();
                carWashText.setText(text);
            }else if (lifestyleBase.getType().equals("sport")){
                text = "运动指数：" + lifestyleBase.getTxt();
                sportText.setText(text);
            }
        }
    }
}
