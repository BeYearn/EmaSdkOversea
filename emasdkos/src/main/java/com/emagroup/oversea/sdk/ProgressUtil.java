package com.emagroup.oversea.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by beyearn on 2018/3/1.
 */

public class ProgressUtil {

    private static ProgressUtil instance;
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private BroadcastReceiver getkeyOkReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case EmaConst.EMA_BC_PROGRESS_ACTION:

                    String progressState = intent.getStringExtra(EmaConst.EMA_BC_PROGRESS_STATE);
                    Log.e("dialogBCReciver", progressState);

                    if (null == progressDialog) {
                        progressDialog = new ProgressDialog(mActivity);
                        progressDialog.setMessage("请稍候...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                    }

                    if (EmaConst.EMA_BC_PROGRESS_START.equals(progressState)) {
                        progressDialog.show();
                    } else if (EmaConst.EMA_BC_PROGRESS_CLOSE.equals(progressState)) {
                        progressDialog.dismiss();
                    }
                    break;
            }
        }
    };


    private ProgressUtil(Activity activity) {
        this.mActivity = activity;
    }


    public static ProgressUtil getInstance(Activity activity) {
        if (instance == null) {
            instance = new ProgressUtil(activity);
        }
        return instance;
    }

    public void initBroadcastRevicer() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(EmaConst.EMA_BC_PROGRESS_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        mActivity.registerReceiver(getkeyOkReciver, filter);
    }

    public void unRegister() {
        mActivity.unregisterReceiver(getkeyOkReciver);
    }

    public void openProgressDialog() {
        Intent intent = new Intent(EmaConst.EMA_BC_PROGRESS_ACTION);
        intent.putExtra(EmaConst.EMA_BC_PROGRESS_STATE, EmaConst.EMA_BC_PROGRESS_START);
        mActivity.sendBroadcast(intent);
    }

    public void closeProgressDialog() {
        Intent intent = new Intent(EmaConst.EMA_BC_PROGRESS_ACTION);
        intent.putExtra(EmaConst.EMA_BC_PROGRESS_STATE, EmaConst.EMA_BC_PROGRESS_CLOSE);
        mActivity.sendBroadcast(intent);
    }

}
