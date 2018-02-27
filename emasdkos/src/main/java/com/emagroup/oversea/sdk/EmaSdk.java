package com.emagroup.oversea.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by beyearn on 2017/5/24.
 */

public class EmaSdk {

    private static Object object = new Object();
    private static EmaSdk instance;
    private Activity mActivity;
    private EmaSDKListener mInitLoginListener;
    private String mSecret;
    private String mClientId;

    public String getSecret() {
        return mSecret;
    }

    public String getClientId() {
        return mClientId;
    }

    private EmaSdk() {
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EmaCallBackConst.INITSUCCESS:
                    EmaCallbackUtil.getInstance().onInitLoginCallback(EmaCallBackConst.INITSUCCESS, "ema sdk init successful");
                    break;
                case EmaCallBackConst.INITFALIED:
                    EmaCallbackUtil.getInstance().onInitLoginCallback(EmaCallBackConst.INITFALIED, "ema sdk init failed");
                    break;
                case EmaConst.EMA_LOGIN_URL_DONE:
                    Intent intent = new Intent(mActivity, EmaLoginActivity.class);
                    intent.putExtra("loginUrl", (String) msg.obj);
                    mActivity.startActivity(intent);
                    break;
            }
        }
    };

    public static EmaSdk getInstance() {
        if (null == instance) {
            synchronized (object) {
                if (null == instance) {
                    instance = new EmaSdk();
                }
            }
        }
        return instance;
    }

    public void init(Activity activity, EmaSDKListener listener, String secret) {

        this.mActivity = activity;
        this.mInitLoginListener = listener;
        this.mSecret = secret;

        EmaCallbackUtil.getInstance().setmInitLoginListener(mInitLoginListener);

        ThreadUtil.runInSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("op_id", ResourceManager.getOpId(mActivity));
                    params.put("game_id", ResourceManager.getGameId(mActivity));
                    params.put("timestamp", ComUtils.getTimestamp());
                    params.put("sign", ComUtils.getSign(params));

                    String result = new HttpRequestor().doPost(Url.initUrl(), params);
                    L.e("init", result);
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    mClientId = data.getString("client_id");

                    mHandler.sendEmptyMessage(EmaCallBackConst.INITSUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(EmaCallBackConst.INITFALIED);
                }
            }
        });


    }


    public void login() {
        ThreadUtil.runInSubThread(new Runnable() {
            @Override
            public void run() {
                //1.先验证token
                UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(mActivity.getApplicationContext());
                if (null != userLoginInfo) {
                    HashMap<String, String> urlParams = new HashMap<>();
                    urlParams.put("client_id", getClientId());
                    urlParams.put("account", userLoginInfo.getAccount());
                    urlParams.put("op_id", ResourceManager.getOpId(mActivity));
                    urlParams.put("game_id", ResourceManager.getGameId(mActivity));
                    urlParams.put("device_id", ComUtils.getDEVICE_ID(mActivity.getApplicationContext()));
                    urlParams.put("token", userLoginInfo.getAccessToken());
                    urlParams.put("type", userLoginInfo.getType());
                    urlParams.put("timestamp", ComUtils.getTimestamp());
                    urlParams.put("sign", ComUtils.getSign(urlParams));

                    try {
                        String checkResult = new HttpRequestor().doPost(Url.checkTokenUrl(), urlParams);
                        JSONObject jsonObject = new JSONObject(checkResult);
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            //token有效 登录成功
                            EmaCallbackUtil.getInstance().onInitLoginCallback(EmaCallBackConst.LOGINSUCCESS, "login success");
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                //2.无效或没有重新授权
                HashMap<String, String> urlParams = new HashMap<>();
                urlParams.put("client_id", getClientId());
                urlParams.put("redirect", "https://accounts.ema.games/client/gettoken");
                urlParams.put("account", ComUtils.getDEVICE_ID(mActivity.getApplicationContext())); //游客id  同deviceid
                urlParams.put("op_id", ResourceManager.getOpId(mActivity));
                urlParams.put("game_id", ResourceManager.getGameId(mActivity));
                urlParams.put("device_id", ComUtils.getDEVICE_ID(mActivity.getApplicationContext()));
                urlParams.put("timestamp", ComUtils.getTimestamp());
                urlParams.put("sign", ComUtils.getSign(urlParams));


                StringBuilder paramsBuilder = new StringBuilder();
                if (urlParams != null) {
                    Iterator iterator = urlParams.keySet().iterator();
                    String key = null;
                    String value = null;
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        if (urlParams.get(key) != null) {
                            value = (String) urlParams.get(key);
                        } else {
                            value = "";
                        }
                        paramsBuilder.append(key).append("=").append(value);
                        if (iterator.hasNext()) {
                            paramsBuilder.append("&");
                        }
                    }
                }
                String loginUrl = Url.indexUrl() + "?" + paramsBuilder.toString();

                Message message = Message.obtain();
                message.what = EmaConst.EMA_LOGIN_URL_DONE;
                message.obj = loginUrl;
                mHandler.sendMessage(message);
            }
        });
    }


}
