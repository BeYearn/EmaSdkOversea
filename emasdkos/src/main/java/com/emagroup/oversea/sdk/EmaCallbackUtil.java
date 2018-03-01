package com.emagroup.oversea.sdk;

/**
 * Created by beyearn on 2017/7/3.
 */

public class EmaCallbackUtil {

    private static EmaCallbackUtil mInstance;
    private EmaSDKListener mPayListener;

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
}
