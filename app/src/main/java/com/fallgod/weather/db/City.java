package com.fallgod.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by JackPan on 2019/10/30
 * Describe:
 */
public class City extends DataSupport {

    //实体类id
    private int id;
    //城市名
    private String cityName;
    //城市代码
    private int cityCode;
    //省份代码
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
