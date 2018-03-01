package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:nsh
 * @data:2018/3/1. 上午11:41
 */

public class Now {

    /**
     * cond : {"txt":"阴"}
     * tmp : -9
     */

    @SerializedName("cond")
    public More more;
    @SerializedName("tmp")
    public String temperature;

    public static class More {
        /**
         * txt : 阴
         */

        @SerializedName("txt")
        public String info;
    }
}
