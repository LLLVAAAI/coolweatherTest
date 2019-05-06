package com.coolweathertest1.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * daily_forecast 返回的JSON数据是一个数组，数组中的每一项都代表着未来一天的天气信息
 * 只需要定义出单日天气的实体类就可以了，然后在声明实体类引用的时候使用集合类型来进行声明
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;

        public String min;

    }

    public class More {

        @SerializedName("txt_d")
        public String info;

    }

}
