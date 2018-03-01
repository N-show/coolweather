package com.coolweather.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.activity.WeatherActivity;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtils;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author:nsh
 * @data:2018/2/26. 下午3:55
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    public final String TAG = getClass().getSimpleName();
    /**
     * 当前选中的省
     */
    Province selectProvince;
    /**
     * 当前选中的市
     */
    City selectCity;
    /**
     * 当前选中的县
     */
    County selectCounty;

    private TextView titleText;
    private Button backButton;
    private RecyclerView recyclerView;
    private List<String> dataList = new ArrayList<>();
    private AreaAdapter areaAdapter;
    private ProgressBar progressBar;
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
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area, container, false);

        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        progressBar = (ProgressBar) view.findViewById(R.id.pbNormal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        areaAdapter = new AreaAdapter(dataList);
        recyclerView.setAdapter(areaAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
                closeProgress();
            }
        });

        queryProvinces();
    }

    /**
     * 查询全国所有的省 优先从数据库查询 没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);
        LogUtils.d(TAG, "provinceList size:" + provinceList.size());

        if (provinceList.size() > 0) {
            dataList.clear();

            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            areaAdapter.notifyDataSetChanged();
            recyclerView.setSelected(true);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省的所有市 优先从数据库查询 没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceid=?", String.valueOf(selectProvince.getId())).find(City.class);
        LogUtils.d(TAG, "cityList size:" + cityList.size());

        if (cityList.size() > 0) {
            dataList.clear();

            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            areaAdapter.notifyDataSetChanged();
            recyclerView.setSelected(true);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中所有市内的所有县 优先从数据库查询 没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectCity.getCityName());

        backButton.setVisibility(View.VISIBLE);

        countyList = DataSupport.where("cityid=?", String.valueOf(selectCity.getId())).find(County.class);
        LogUtils.d(TAG, "countyList size:" + countyList.size());

        if (countyList.size() > 0) {
            dataList.clear();

            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            areaAdapter.notifyDataSetChanged();
            recyclerView.setSelected(true);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        LogUtils.d(TAG, "查询 " + type + " 信息 通过地址---" + address);
        showProgress();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            /**
             * 联网查询失败的回调方法
             * @param call
             * @param e
             */
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "查询 " + type + " 信息失败" + e);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            /**
             * 联网查询成功的回调方法
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.d(TAG, "查询 " + type + " 信息成功 返回数据---" + response);
                String responseText = response.body().string();
                boolean result = false;

                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectCity.getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    /**
     * 显示进度条
     */
    private void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 关闭进度条
     */
    private void closeProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * adapter
     */
    class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaHolder> {

        public final String TAG = getClass().getSimpleName();

        private List<String> mAreaList;

        public AreaAdapter(List<String> mAreaList) {
            this.mAreaList = mAreaList;
        }

        @Override
        public AreaAdapter.AreaHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View areaListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);
            AreaHolder areaHolder = new AreaHolder(areaListView);

            return areaHolder;
        }


        @Override
        public void onBindViewHolder(final AreaHolder holder, final int position) {
            String area = mAreaList.get(position);
            holder.areaItem.setText(area);

            holder.itemView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    String area = mAreaList.get(holder.getAdapterPosition());

                    if (currentLevel == LEVEL_PROVINCE) {
                        selectProvince = provinceList.get(position);
                        LogUtils.d(TAG, "选择了: " + area + " 省份级别  代码为: " + selectProvince.getProvinceCode());
                        queryCities();
                    } else if (currentLevel == LEVEL_CITY) {
                        selectCity = cityList.get(position);
                        LogUtils.d(TAG, "选择了: " + area + " 城市级别  代码为: " + selectCity.getCityCode());
                        queryCounties();
                    } else if (currentLevel == LEVEL_COUNTY) {
                        selectCounty = countyList.get(position);
                        String weatherId = selectCounty.getWeatherId();
                        LogUtils.d(TAG, "选择了: " + area + " 县城级别  天气代码为: " + weatherId);

//                        开启显示天气的activity
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAreaList.size();
        }

        class AreaHolder extends RecyclerView.ViewHolder {

            TextView areaItem;

            public AreaHolder(View itemView) {
                super(itemView);
                areaItem = (TextView) itemView.findViewById(R.id.area_list_item);
            }
        }
    }
}
