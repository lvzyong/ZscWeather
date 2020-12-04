package com.example.zscweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.zscweather.db.City;
import com.example.zscweather.db.County;
import com.example.zscweather.db.Province;
import com.example.zscweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        //判断传入的数据是否为空
        if (!TextUtils.isEmpty(response)) {
            try {
                //省数据的根元素是数组，所以是JSONArray
                JSONArray allProvinces = new JSONArray(response);

                //遍历的预处理
                JSONObject provinceObject;
                Province province;
                //遍历数组
                for (int i = 0; i < allProvinces.length(); i++) {
                    //得到期中的JSONObject
                    provinceObject = allProvinces.getJSONObject(i);
                    //下面根据key 得到value
                    //并且把数据设置给Province，最后保存到数据库
                    province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }//for
                return true;
            }//try
            catch (JSONException e) {
                e.printStackTrace();
            }//catch
        }//if
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {//判断传入的数据是否为空
            try {
                JSONArray allCity = new JSONArray(response);//市数据的根元素是数组，所以是JSONArray

                //遍历的预处理
                JSONObject cityObject;
                City city;

                for (int i = 0; i < allCity.length(); i++) {//遍历数组
                    cityObject = allCity.getJSONObject(i);//得到其中的JSONObject
                    //下面根据key 得到value
                    //并且把数据设置给City，最后保存到数据库
                    city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.save();
                }//for
                return true;
            }//try
            catch (JSONException e) {
                e.printStackTrace();
            }//catch
        }//if
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {//判断传入的数据是否为空
            try {
                JSONArray allCounty = new JSONArray(response);//县城数据的根元素是数组，所以是JSONArray

                //遍历的预处理
                JSONObject countyObject;
                County county;

                for (int i = 0; i < allCounty.length(); i++) {//遍历数组
                    countyObject = allCounty.getJSONObject(i);//得到其中的JSONObject
                    //下面根据key 得到value
                    //并且把数据设置给county，最后保存到数据库
                    county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                }//for
                return true;
            }//try
            catch (JSONException e) {
                e.printStackTrace();
            }//catch
        }//if
        return false;
    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);  //用请求得到的字符串常见一个JSONObject
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");   //在得到的JSONObject中，得到键为HeWeather的值
            String weatherContent = jsonArray.getJSONObject(0).toString();  //得到jsonArray的第0号元素，他也是一个JSONObject
            return new Gson().fromJson(weatherContent, Weather.class);  //用了Gson().fromJson解析
        } //try
        catch (Exception e) {
            e.printStackTrace();
        }//catch
        return null;
    }
}