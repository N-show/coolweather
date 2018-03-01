package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @author:nsh
 * @data:2018/3/1. 下午2:48
 */

public class Suggestion {

    /**
     * air : {"brf":"良","txt":"气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。"}
     * comf : {"brf":"极不舒适","txt":"白天气温低、风力较强，并伴有雨雪天气，这种天气会使您感觉十分寒冷，极不舒适，请注意保暖。"}
     * cw : {"brf":"不宜","txt":"不宜洗车，未来24小时内有雪，如果在此期间洗车，雪水和路上的泥水可能会再次弄脏您的爱车。"}
     * sport : {"brf":"较不宜","txt":"有降雪，推荐您在室内进行低强度运动；若坚持户外运动，请选择合适运动并注意保暖。"}
     */

    @SerializedName("air")
    public Air air;
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    @SerializedName("sport")
    public Sport sport;

    public class Air {
        /**
         * brf : 良
         * txt : 气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。
         */

        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String infoText;
    }

    public class Comfort {
        /**
         * brf : 极不舒适
         * txt : 白天气温低、风力较强，并伴有雨雪天气，这种天气会使您感觉十分寒冷，极不舒适，请注意保暖。
         */

        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String infoText;
    }

    public class CarWash {
        /**
         * brf : 不宜
         * txt : 不宜洗车，未来24小时内有雪，如果在此期间洗车，雪水和路上的泥水可能会再次弄脏您的爱车。
         */

        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String infoText;
    }

    public class Sport {
        /**
         * brf : 较不宜
         * txt : 有降雪，推荐您在室内进行低强度运动；若坚持户外运动，请选择合适运动并注意保暖。
         */

        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String infoText;
    }
}
