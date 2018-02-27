package com.emagroup.oversea.sdk;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Url {

    public static final String TESTING_SERVER_URL="https://testing-platform.lemonade-game.com:8443";
    public static final String STAGING_SERVER_URL="https://staging-platform.lemonade-game.com";
    public static final String PRODUCTION_SERVER_URL="https://platform.lemonade-game.com";


    private static String serverUrl="https://accounts.ema.games";

    public static void setServerUrl(String url){
        serverUrl=url;
    }

    public static String getServerUrl() {
        return serverUrl;
    }


    public static String timeStampUrl(){
        return serverUrl+"/client/get-timestamp";
    }

    public static String initUrl(){
        return serverUrl+"/client/sdk-init";
    }


    /**
     * 验证token
     * @return
     */
    public static String checkTokenUrl(){
        return serverUrl+"/client/check-token";
    }


    /**
     * 授权登录
     * @return
     */
    public static String indexUrl(){
        return serverUrl+"/client/index";
    }

}
