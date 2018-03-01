package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:nsh
 * @data:2018/3/1. 下午3:25
 */

public class Forecast {


    /**
     * cond : {"txt_d":"小雪"}
     * date : 2018-03-01
     * tmp : {"max":"-8","min":"-20"}
     */

    @SerializedName("cond")
    public More more;
    @SerializedName("date")
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;

    public class More {
        /**
         * txt_d : 小雪
         */

        @SerializedName("txt_d")
        public String infoText;
    }

    public class Temperature {
        /**
         * max : -8
         * min : -20
         */

        @SerializedName("max")
        public String max;
        @SerializedName("min")
        public String min;
    }
}
