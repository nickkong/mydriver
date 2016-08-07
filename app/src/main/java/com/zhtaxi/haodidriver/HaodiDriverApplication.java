package com.zhtaxi.haodidriver;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.nickkong.commonlibrary.service.LocationService;

import cn.jpush.android.api.JPushInterface;

/**
 * 全局Application
 * Created by NickKong on 16/7/2.
 */
public class HaodidriverApplication extends Application {

    public LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化百度地图定位
        locationService = new LocationService(this);
        //百度地图，在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //初始化极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //初始化科大讯飞语音合成
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=57a74009");

    }

}