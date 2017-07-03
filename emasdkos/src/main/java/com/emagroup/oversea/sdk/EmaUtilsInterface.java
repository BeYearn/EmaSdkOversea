package com.emagroup.oversea.sdk;

import android.content.Intent;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 */
public interface EmaUtilsInterface {

    /**
     * 无延迟，立刻初始化的
     * 用来初始化一些严格要求在oncreate中的语句（比如应用宝的YSDKApi.onCreate(this)严格眼球）
     * @param listener
     */
    void immediateInit(EmaSDKListener listener);

    void realInit(EmaSDKListener listener, JSONObject data);

    void realLogin(EmaSDKListener listener, String userid, String deviceId);

    void doPayPre(EmaSDKListener listener);

    void realPay(EmaSDKListener listener, EmaPayInfo emaPayInfo);

    void logout();

    void swichAccount();

    void doShowToolbar();

    void doHideToobar();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void onBackPressed(EmaBackPressedAction action);

    void submitGameRole(Map<String, String> data);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onNewIntent(Intent intent);

    void onRestart();

}