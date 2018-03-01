package com.coolweather.android.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtils;
import com.coolweather.android.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    private ImageView bingPicImg;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiDegree;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView airBrfText;
    private TextView airText;
    private TextView comfortBrfText;
    private TextView comfortText;
    private TextView cwBrfText;
    private TextView cwText;
    private TextView sportBrfText;
    private TextView sportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();

//        设置状态栏和背景图融合
        if (Build.VERSION.SDK_INT > 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherInfo = preferences.getString("weather", "");
//        如果本地存储有json文件就解析本地json数据 显示
        if (!TextUtils.isEmpty(weatherInfo)) {
            LogUtils.d(TAG, "通过解析本地json文件 显示数据");

            Weather weather = Utility.handleWeatherResponse(weatherInfo);
            showWeatherInfo(weather);
        } else {
//          去服务器查询天气
            LogUtils.d(TAG, "通过解析网络获取的json文件 显示数据" + weatherInfo);

            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

//        加载bing网站的图片 如果存储有图片地址
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);

        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);

        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        aqiDegree = (TextView) findViewById(R.id.aqi_degree);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);

        airBrfText = (TextView) findViewById(R.id.air_brf_text);
        airText = (TextView) findViewById(R.id.air_text);
        comfortBrfText = (TextView) findViewById(R.id.comfort_brf_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        cwBrfText = (TextView) findViewById(R.id.cw_brf_text);
        cwText = (TextView) findViewById(R.id.cw_text);
        sportBrfText = (TextView) findViewById(R.id.sport_brf_text);
        sportText = (TextView) findViewById(R.id.sport_text);
    }

    /**
     * 加载网络bing的每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(WeatherActivity.this, "每日一图加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                LogUtils.d(TAG, "获取到的bing每日一图地址为:" + bingPic);

                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edit.putString("bing_pic", bingPic);
                edit.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 根据天气id请求城市天气
     *
     * @param weatherId
     */
    private void requestWeather(String weatherId) {
        final String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        LogUtils.d(TAG, weatherUrl);

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d(TAG, "查询失败 通过网址---" + weatherUrl);
                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                LogUtils.d(TAG, "查询成功 通过网址---" + weatherUrl);
                LogUtils.d(TAG, "返回数据:" + responseText);

                final Weather weather = Utility.handleWeatherResponse(responseText);
                LogUtils.d(TAG, "解析json数据 返回具体weather类:" + weather.status);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (weather != null && "ok".equals(weather.status)) {

                            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            edit.putString("weather", responseText);
                            edit.apply();

                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        loadBingPic();
    }

    /**
     * 处理并展示weather实体类中的数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime;

        String qlty = weather.aqi.city.qlty;
        String aqi = weather.aqi.city.aqi;
        String pm25 = weather.aqi.city.pm25;

        String temperature = weather.now.temperature;
        String info = weather.now.more.info;

        String airBrfContent = weather.suggestion.air.brf;
        String airTextContent = weather.suggestion.air.infoText;
        String comfortBrfTextContent = weather.suggestion.comfort.brf;
        String comfortTextContent = weather.suggestion.comfort.infoText;
        String cwBrfTextContent = weather.suggestion.carWash.brf;
        String cwTextContent = weather.suggestion.carWash.infoText;
        String sportBrfTextContent = weather.suggestion.sport.brf;
        String sportTextContent = weather.suggestion.sport.infoText;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);

        aqiDegree.setText(qlty);
        aqiText.setText(aqi);
        pm25Text.setText(pm25);

        degreeText.setText(temperature + "℃");
        weatherInfoText.setText(info);

        forecastLayout.removeAllViews();
        List<Forecast> forecastList = weather.forecastList;
        for (Forecast forecast : forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView mixText = (TextView) view.findViewById(R.id.mix_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.infoText);
            mixText.setText(forecast.temperature.min);
            maxText.setText(forecast.temperature.max);

            forecastLayout.addView(view);
        }

        airBrfText.setText(airBrfContent);
        airText.setText(airTextContent);
        comfortBrfText.setText(comfortBrfTextContent);
        comfortText.setText(comfortTextContent);
        cwBrfText.setText(cwBrfTextContent);
        cwText.setText(cwTextContent);
        sportBrfText.setText(sportBrfTextContent);
        sportText.setText(sportTextContent);

        weatherLayout.setVisibility(View.VISIBLE);

    }
}
