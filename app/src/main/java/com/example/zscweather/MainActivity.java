package com.example.zscweather;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.zscweather.util.HttpUtil;
import com.example.zscweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherId = prefs.getString("weather_id", null);
        if (weatherId != null) {  //如果已经选择了天气
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("weather_id", weatherId);
            startActivity(intent);
        }//if
    }

//        String test_url = "http://guolin.tech/api/china";
//        HttpUtil.sendOkHttpRequest(test_url,
//                new okhttp3.Callback(){
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Log.d(TAG,"onFailure");
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String strRes = response.body().string();
//                        Log.d(TAG,"onResponse" + strRes);
//                        Utility.handleProvinceResponse(strRes);
//                    }
//                });

}
