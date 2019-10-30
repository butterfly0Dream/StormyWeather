package com.fallgod.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by JackPan on 2019/10/30
 * Describe:
 */
public class County extends DataSupport {

    //实体类id
    private int id;
    //县名称
    private String countyName;
    //天气id
    private int weatherId;
    //市代码
    private int cityId;

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

    public int getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
