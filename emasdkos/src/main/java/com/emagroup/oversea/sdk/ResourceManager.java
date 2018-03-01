package com.emagroup.oversea.sdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

/**
 * Created by beyearn on 2017/6/30.
 */

public class ResourceManager {

    private static ResourceManager mInstance;
    private final Context mContext;
    private final Resources mResources;
    private final String mPackageName;
    private final LayoutInflater mLayoutInflater;

    private ResourceManager(Context context) {
        mContext = context;
        mResources = mContext.getResources();
        mPackageName = mContext.getApplicationInfo().packageName;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public static ResourceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ResourceManager(context);
        }
        return mInstance;
    }


    /**
     * 获取某种资源ID
     * @param resourceName  ema_btn_cancel
     * @param type    id   style   ema_fragment_login
     */
    public int getIdentifier(String resourceName, String type){
        return mResources.getIdentifier(resourceName, type, mPackageName);
    }


    public int getViewId(String recourceName){
        return mResources.getIdentifier(recourceName,"id",mPackageName);
    }

    /**
     * 根据key获取metaData的string类型的数据
     *
     * @param context
     * @param key
     * @return
     */
    public static String getStringFromMetaData(Context context, String key) {
        ApplicationInfo ai;
        String value = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            value = bundle.getString(key);
        } catch (Exception e) {
            Log.e("getStringFromMetaData", "参数设置错误, 请检查！");
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 由布局文件名字获得实例化出来的view（暂时不考虑横竖屏）
     * @param resourceName  ema_login_succdialog
     */
    public int getLayoutId(String resourceName){
        return  mResources.getIdentifier(resourceName, "layout", mPackageName);
    }


    public static String getOpId(Context context){
        return getStringFromMetaData(context,"EMA_OP_ID").substring(2);
    }

    public static String getGameId(Context context){
        return getStringFromMetaData(context,"EMA_GAME_ID").substring(2);
    }
    public static String getEnvi(Context context){
        return getStringFromMetaData(context,"EMA_WHICH_ENVI");
    }
}
