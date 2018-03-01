package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:nsh
 * @data:2018/3/1. 上午11:38
 */

public class AQI {

    /**
     * city : {"aqi":"30","qlty":"优","pm25":"11"}
     */

    @SerializedName("city")
    public AqiCity city;

    public class AqiCity {
        /**
         * aqi : 30
         * qlty : 优
         * pm25 : 11
         */

        @SerializedName("aqi")
        public String aqi;
        @SerializedName("qlty")
        public String qlty;
        @SerializedName("pm25")
        public String pm25;
    }
}
