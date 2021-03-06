package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtils;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public final String TAG = getClass().getSimpleName();

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        updateBingPic();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        8小时的毫秒数
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pi);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        LogUtils.d(TAG, "anHour 8小时的毫秒数为：" + anHour);
        LogUtils.d(TAG, "SystemClock.elapsedRealtime() 为：" + SystemClock.elapsedRealtime());
        LogUtils.d(TAG, "triggerAtTime 数为：" + triggerAtTime);
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {

        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                edit.putString("bing_pic", bingPic);
                edit.apply();
            }
        });
    }

    /**
     * 更新天气
     */
    private void updateWeather() {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
        String weatherInfo = preferences.getString("weather", null);
//        如果有缓存直接更新天气
        if (weatherInfo != null) {
            Weather weather = Utility.handleWeatherResponse(weatherInfo);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
            LogUtils.d(TAG, "通过此网址后台服务更新本地数据---" + weatherUrl);

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    String weatherId = weather.basic.weatherId;

                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString("weather", responseText);
                        edit.apply();
                    }
                }
            });


        }
    }
}
