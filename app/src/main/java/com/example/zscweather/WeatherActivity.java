package com.example.zscweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.bumptech.glide.Glide;
import com.example.zscweather.gson.Forecast;
import com.example.zscweather.gson.Weather;
import com.example.zscweather.util.HttpUtil;
import com.example.zscweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private final static String MY_KEY = "90870e3946ff4dcbb823f90f317b61cc";
    private ScrollView weatherLayout;
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
    private String mWeatherId;
    //记录从bing获取的图片
    private ImageView bingPicImg;
    //下拉刷新
    public SwipeRefreshLayout swipeRefresh;
    //滑动菜单
    public DrawerLayout drawerLayout;
    private Button navButton;

    //添加部分的变量
    private TextView windScText;
    private TextView windSpdText;
    private TextView humText;
    private TextView pcpnText;
    private TextView presText;
    private TextView windDegText;
    private  TextView windDirText;
    private TextView qltyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        setContentView(R.layout.activity_weather);
        // 初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);

        //添加显示部分的初始化
        windScText = findViewById(R.id.wind_sc_text);
        windSpdText = findViewById(R.id.wind_spd_text);
        humText = findViewById(R.id.hum_text);
        pcpnText = findViewById(R.id.pcpn_text);
        presText = findViewById(R.id.pres_text);
        windDegText = findViewById(R.id.wind_deg_text);
        windDirText = findViewById(R.id.wind_dir_text);
        qltyText = findViewById(R.id.qlty_text);

        //初始化swipeRefresh，并且为其设置监听，请求新的天气信息
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        //如果不注释，那么就换不了天气了
//        if (weatherString != null) {
//            // 有缓存时直接解析天气数据
//            Weather weather = Utility.handleWeatherResponse(weatherString);
//            mWeatherId = weather.basic.weatherId;
//            showWeatherInfo(weather);
//        } //if
//        else
//            {
            // 无缓存时去服务器查询天气
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
//        }  //else

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //在其onCreate中为navButton设置监听，在其中打开侧滑菜单
        navButton = (Button) findViewById(R.id.nav_button);

        navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //请求背景图片
        //尝试从SharedPreferences中读取缓存的背景图片
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){ //有缓存的话，直接使用Glide来加载这张图片
            Glide.with(this).load(bingPic).into(bingPicImg);
        }//if
        else { //没有的话就调用loadBingPic()方法去请求今天的bing背景图
            loadBingPic();
        }//else
    } //end of onCreate

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=" + MY_KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather_id", weatherId);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        humText.setText("相对湿度：" + weather.now.hum + "%");
        pcpnText.setText("降水量：" + weather.now.pcpn + " mm");
        presText.setText("气压：" + weather.now.pres);

        windDirText.setText(weather.now.wind_dir);
        windDegText.setText("风向：" + weather.now.wind_deg + "°");
        windScText.setText("风向：" + weather.now.wind_deg + "°");
        windScText.setText("风力：" + weather.now.wind_sc);
        windSpdText.setText("风速：" + weather.now.wind_spd + " km/h");
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
//            Log.d("WeatherActivity",forecast.more.info);

            ImageView img = (ImageView)view.findViewById(R.id.info_img);
            if (forecast.more.info.equals("晴")) {
                img.setImageResource(R.drawable.qing);
            }
            else  if (forecast.more.info.equals("阴")) {
                img.setImageResource(R.drawable.yintian);
            }
            else  if (forecast.more.info.equals("多云")) {
                img.setImageResource(R.drawable.duoyun);
            }
            else  if (forecast.more.info.equals("雷阵雨")) {
                img.setImageResource(R.drawable.leizhenyu);
            }
            else  if (forecast.more.info.equals("小雨")) {
                img.setImageResource(R.drawable.xiaoyu);
            }
            else  if (forecast.more.info.equals("中雨")) {
                img.setImageResource(R.drawable.zhongyu);
            }
            else  if (forecast.more.info.equals("大雨")) {
                img.setImageResource(R.drawable.duoyun);
            }
            else  if (forecast.more.info.equals("阵雨")) {
                img.setImageResource(R.drawable.zhenyu);
            }

            maxText.setText(forecast.temperature.max + "℃");
            minText.setText(forecast.temperature.min + "℃");
            forecastLayout.addView(view);
        }//for
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            String qlty = weather.aqi.city.quality;
            if (qlty.equals("优")) {
                qltyText.setTextSize(50);
                qltyText.setTextColor(getResources().getColor(R.color.good));
            }
            else if (qlty.equals("良")) {
                qltyText.setTextSize(50);
                qltyText.setTextColor(getResources().getColor(R.color.良));
            }
            else if (qlty.charAt(0) == '轻') {
                qltyText.setTextSize(25);
                qltyText.setTextColor(getResources().getColor(R.color.轻));
            }
            else if (qlty.charAt(0) == '中') {
                qltyText.setTextSize(25);
                qltyText.setTextColor(getResources().getColor(R.color.中));
            }
            else if (qlty.charAt(0) == '重') {
                qltyText.setTextSize(25);
                qltyText.setTextColor(getResources().getColor(R.color.重));
            }
            else if (qlty.charAt(0) == '严') {
                qltyText.setTextSize(25);
                qltyText.setTextColor(getResources().getColor(R.color.严));
            }//else if
            qltyText.setText(qlty);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    /**
     * 加载必应每日一图
     * 用于从bing获取图片，并且加载到ImageView
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}

