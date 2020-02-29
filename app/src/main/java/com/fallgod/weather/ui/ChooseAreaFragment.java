package com.fallgod.weather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fallgod.weather.MyApplication;
import com.fallgod.weather.R;
import com.fallgod.weather.db.City;
import com.fallgod.weather.db.County;
import com.fallgod.weather.db.Province;
import com.fallgod.weather.util.HttpUtil;
import com.fallgod.weather.util.LogUtil;
import com.fallgod.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by JackPan on 2019/10/30
 * Describe: 地址选择Fragment
 */
public class ChooseAreaFragment extends Fragment {
    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //服务器地址
    public static final String ADDRESS = "http://guolin.tech/api/china";

    private TextView titleText;
    private Button backButton;
    private ProgressBar progressBar;
    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvicne;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        progressBar = view.findViewById(R.id.progress_bar);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener((AdapterView<?> parent,View view,int position,long id)->{
            if (currentLevel == LEVEL_PROVINCE){
                selectedProvicne = provinceList.get(position);
                queryCities();
            }else if (currentLevel == LEVEL_CITY){
                selectedCity = cityList.get(position);
                queryCounties();
            }else if (currentLevel == LEVEL_COUNTY){
                String weatherId = countyList.get(position).getWeatherId();
                Intent intent = new Intent(getActivity(),WeatherActivity.class);
                intent.putExtra("weather_id",weatherId);
                startActivity(intent);
                if (getActivity() != null){
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener((View v)->{
            if (currentLevel == LEVEL_COUNTY){
                queryCities();
            }else if (currentLevel == LEVEL_CITY){
                queryProvinces();
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国的省份信息，优先从数据库查询，没有则去服务器查询
     */
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(ADDRESS,"province");
        }
    }

    /**
     * 查询选中省份的所有城市信息，优先从数据库查询，没有则去服务器查询
     */
    private void queryCities(){
        titleText.setText(selectedProvicne.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.findAll(City.class);
        if (cityList.size() > 0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(ADDRESS + "/" + selectedProvicne.getId(),"city");
        }
    }

    /**
     * 查询选中城市的所有县，优先从数据库查询，没有则去服务器查询
     */
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.findAll(County.class);
        if (countyList.size() > 0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(ADDRESS + "/" + selectedProvicne.getProvinceCode() + "/" + selectedCity.getCityCode(),"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * @param address 地址
     * @param type 类型
     */
    private void queryFromServer(String address,String type){
        LogUtil.d(TAG,"address:" + address + "   type:" + type);
        showProgressBar();
        HttpUtil.sendOkHttpResquest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(()->{
                    hideProgressBar();
                    Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                switch (type){
                    case "province":
                        result = Utility.handleProvinceResponse(responseText);
                        break;
                    case "city":
                        result = Utility.handleCityResponse(responseText,selectedProvicne.getId());
                        break;
                    case "county":
                        result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                        break;
                }
                if (result){
                    getActivity().runOnUiThread(()->{
                        hideProgressBar();
                        switch (type){
                            case "province":
                                queryProvinces();
                                break;
                            case "city":
                                queryCities();
                                break;
                            case "county":
                                queryCounties();
                                break;
                        }
                    });
                }
            }
        });
    }

    //显示进度条
    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    //隐藏进度条
    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
}
