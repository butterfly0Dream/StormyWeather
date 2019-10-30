package com.fallgod.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by JackPan on 2019/10/30
 * Describe:省份信息实体类
 */
public class Province extends DataSupport {

    //实体类id
    private int id;
    //省的名字
    private String provinceName;
    //省的代号
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
