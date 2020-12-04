package com.example.zscweather.db;

import org.litepal.crud.DataSupport;

public class County  extends DataSupport {
    private int id;  //数据库表必须的默认字段
    private String countyName;
    private String weatherId;  //weatherId 是用于请求天气的id值
    private int cityId;   //其中cityId是当前县所属市的id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}