package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:nsh
 * @data:2018/3/1. 上午10:57
 */

public class Basic {
    /**
     * city : 北京
     * id : CN101010100
     */
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        /**
         * loc : 2017-10-26 23:09
         */

        @SerializedName("loc")
        public String updateTime;
    }
}
