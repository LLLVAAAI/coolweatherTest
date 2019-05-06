package com.coolweathertest1.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweathertest1.android.gson.Weather;
import com.coolweathertest1.android.util.HttpUtil;
import com.coolweathertest1.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 后台自动更新天气
 * 创建一个长期在后天运行的定时任务
 * 我们设定一个时间，然后在该时间到来时，AlarmManager为我们广播一个我们设定的Intent,通常我们使用 PendingIntent，
 */

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();               //更新天气
        updateBingPic();               //更新背景图片

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 1000 * 60 * 30;            //30分钟更新一次数据

        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;           //方法的含义为系统启动到现在的时间
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);

        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        //设置一次性闹钟，第一个参数表示闹钟类型（5种），第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
        //ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);

        if (weatherString != null){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                    weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseText = response.body().string();

                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);       //更新缓存中的数据
                        editor.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }
}
