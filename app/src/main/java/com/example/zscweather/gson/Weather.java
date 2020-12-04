package com.example.zscweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 落忆枫音 on 2018/6/4.
 */

public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}