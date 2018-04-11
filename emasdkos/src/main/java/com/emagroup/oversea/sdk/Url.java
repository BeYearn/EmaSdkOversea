package com.emagroup.oversea.sdk;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Url {

    public static final String STAGING_SERVER_URL="https://staging-platform.lemonade-game.com";
    public static final String PRODUCTION_SERVER_URL="https://platform.lemonade-game.com";



    public static final String TESTING_SERVER_URL="http://accounts-test.ema.games";


    private static String serverUrl="https://accounts.ema.games";

    public static void setServerUrl(String url){
        serverUrl=url;
    }

    public static String getServerUrl() {
        return serverUrl;
    }


    public static String timeStampUrl(){
        //return serverUrl+"/client/get-timestamp";
        return "https://api.ema.games/server-info";
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

    public static String accountUpgrade(){
        return serverUrl+"/server/upgrade";
    }

    public static String getProductsUrl(){
        return serverUrl+"/sdk/default/get-products";
    }

    public static String createOrderUrl(){
        return serverUrl+"/sdk/default/create-order";
    }

    public static String cancleOrder() {
        return serverUrl+"/sdk/default/cancel-order";
    }

    public static String payNotifyUrl() {
        return serverUrl+"/sdk/default/google-notify";
        //return "http://accounts-test.ema.games/sdk/default/google-notify";
    }
}
