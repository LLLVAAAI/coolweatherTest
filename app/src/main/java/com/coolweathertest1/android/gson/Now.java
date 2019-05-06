package com.coolweathertest1.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * JSON返回内容：
 * "now":{
 *     "tmp":"29",
 *     "cond":{
 *         "text":"阵雨"
 *     }
 * }
 *
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
