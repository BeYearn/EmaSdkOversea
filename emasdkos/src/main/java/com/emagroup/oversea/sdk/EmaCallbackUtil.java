package com.emagroup.oversea.sdk;

import android.app.Activity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by beyearn on 2017/7/3.
 */

public class EmaCallbackUtil {

    private static EmaCallbackUtil mInstance;
    private EmaSDKListener mPayListener;
    private Timer mTimer;

    private EmaCallbackUtil() {
    }

    private EmaSDKListener mInitLoginListener;

    public void setmInitLoginListener(EmaSDKListener mInitLoginListener) {
        this.mInitLoginListener = mInitLoginListener;
    }

    public static EmaCallbackUtil getInstance() {
        if (mInstance == null) {
            mInstance = new EmaCallbackUtil();
        }
        return mInstance;
    }

    public void onInitLoginCallback(int msgCode, String msgStr) {

        switch (msgCode) {
            case EmaCallBackConst.LOGINSUCCESS:
                EmaUser.getInstance().setIsLogin(true);
                tokenCheck();
                break;
            case EmaCallBackConst.LOGOUTSUCCESS:
                EmaUser.getInstance().setIsLogin(false);
                stopCheckToken();
                break;
        }
        mInitLoginListener.onCallBack(msgCode, msgStr);

    }

    public void setPayListener(EmaSDKListener payListener) {
        this.mPayListener = payListener;
    }

    public void onPayCallBack(int msgCode, String msgStr) {

        mPayListener.onCallBack(msgCode, msgStr);

    }

    /**
     * 心跳验证token
     */
    private void tokenCheck() {
        mTimer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                ThreadUtil.runInSubThread(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = EmaSdk.getInstance().getActivity();
                        UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(activity.getApplicationContext());
                        if (null != userLoginInfo) {
                            HashMap<String, String> urlParams = new HashMap<>();
                            urlParams.put("client_id", EmaSdk.getInstance().getClientId());
                            urlParams.put("account", userLoginInfo.getAccount());
                            urlParams.put("op_id", ResourceManager.getOpId(activity));
                            urlParams.put("game_id", ResourceManager.getGameId(activity));
                            urlParams.put("device_id", ComUtils.getDEVICE_ID(activity.getApplicationContext()));
                            urlParams.put("token", userLoginInfo.getAccessToken());
                            urlParams.put("type", userLoginInfo.getType());
                            urlParams.put("timestamp", ComUtils.getTimestamp());
                            urlParams.put("sign", ComUtils.getSign(urlParams));

                            try {
                                String checkResult = new HttpRequestor().doPost(Url.checkTokenUrl(), urlParams);
                                JSONObject jsonObject = new JSONObject(checkResult);
                                int code = jsonObject.getInt("code");
                                if (code == 0) {
                                    //token有效
                                } else {
                                    //登录过期
                                    mInitLoginListener.onCallBack(EmaCallBackConst.LOGINEXPIRED, "login expired");

                                    //非要把逻辑设计到这里  过期后logout 然后login
                                    ToastHelper.toast(activity,"We detected abnormal activity on your account, please relogin.");
                                    EmaSdk.getInstance().logout();
                                    EmaSdk.getInstance().login();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            //登录过期
                            mInitLoginListener.onCallBack(EmaCallBackConst.LOGINEXPIRED, "login expired");
                        }
                    }
                });
            }
        };

        mTimer.schedule(timerTask, 60 * 1000);
    }

    /**
     * 停止心跳
     */
    public void stopCheckToken() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

}
