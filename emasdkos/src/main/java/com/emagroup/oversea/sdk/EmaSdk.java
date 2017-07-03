package com.emagroup.oversea.sdk;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by beyearn on 2017/5/24.
 */

public class EmaSdk {

    private static Object object = new Object();
    private static EmaSdk instance;
    private Activity mActivity;
    private EmaSDKListener mInitLoginListener;

    private EmaSdk() {
    }

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

    public void init(Activity activity, EmaSDKListener listener){

        this.mActivity = activity;
        this.mInitLoginListener = listener;

        EmaCallbackUtil.getInstance().setmInitLoginListener(mInitLoginListener);

    }





    public void login(){

        Intent intent = new Intent(mActivity, EmaLoginActivity.class);
        mActivity.startActivity(intent);

    }



}
