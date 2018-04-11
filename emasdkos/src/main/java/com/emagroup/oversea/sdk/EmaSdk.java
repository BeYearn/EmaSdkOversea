package com.emagroup.oversea.sdk;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public Activity getActivity() {
        return mActivity;
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

    private IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };


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
                    intent.putExtra("webUrl", (String) msg.obj);
                    mActivity.startActivity(intent);
                    break;
                case EmaConst.EMA_ACCOUNT_UPGRADE_URL_DONE:
                    Intent intent2 = new Intent(mActivity, EmaLoginActivity.class);
                    intent2.putExtra("webUrl", (String) msg.obj);
                    intent2.putExtra("showCloseView",true);
                    mActivity.startActivity(intent2);
                    break;
            }
        }
    };


    public void init(Activity activity, EmaSDKListener listener, String secret) {

        this.mActivity = activity;
        this.mInitLoginListener = listener;
        this.mSecret = secret;

        //初始化广播
        ProgressUtil.getInstance(mActivity).initBroadcastRevicer();

        //设置环境
        String envi = ResourceManager.getEnvi(activity.getApplicationContext());
        if (envi.equals("testing")) {
            Url.setServerUrl(Url.TESTING_SERVER_URL);
        } else {
            //默认就是正式
        }
        ProgressUtil.getInstance(mActivity).openProgressDialog();

        //请求权限
        ComUtils.requestPermission(activity);

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
                ProgressUtil.getInstance(mActivity).closeProgressDialog();
            }
        });

        //绑定googleplay购买结算服务
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        activity.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }


    public void login() {
        ProgressUtil.getInstance(mActivity).openProgressDialog();
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
                            ProgressUtil.getInstance(mActivity).closeProgressDialog();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                //2.无效或没有 则重新授权
                HashMap<String, String> urlParams = new HashMap<>();
                urlParams.put("client_id", getClientId());
                urlParams.put("redirect", "https://accounts.ema.games/client/gettoken");
                urlParams.put("account", ComUtils.getDEVICE_ID(mActivity.getApplicationContext())); //游客id  同deviceid
                urlParams.put("op_id", ResourceManager.getOpId(mActivity));
                urlParams.put("game_id", ResourceManager.getGameId(mActivity));
                urlParams.put("device_id", ComUtils.getDEVICE_ID(mActivity.getApplicationContext()));
                urlParams.put("timestamp", ComUtils.getTimestamp());
                urlParams.put("sign", ComUtils.getSign(urlParams));


                String getParams = ComUtils.buildGetParams(urlParams);
                String loginUrl = Url.indexUrl() + "?" + getParams;

                Message message = Message.obtain();
                message.what = EmaConst.EMA_LOGIN_URL_DONE;
                message.obj = loginUrl;
                mHandler.sendMessage(message);

                ProgressUtil.getInstance(mActivity).closeProgressDialog();
            }
        });
    }

    /**
     * 登出
     */
    public void logout() {
        EmaUser.getInstance().clearLoginInfo(mActivity.getApplicationContext());
        EmaCallbackUtil.getInstance().onInitLoginCallback(EmaCallBackConst.LOGOUTSUCCESS, "logout successful");
    }

    /**
     * 显示账户升级页面
     */
    public void accountUpgrade() {
        ThreadUtil.runInSubThread(new Runnable() {
            @Override
            public void run() {
                UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(mActivity.getApplicationContext());
                HashMap<String, String> urlParams = new HashMap<>();
                urlParams.put("client_id", getClientId());
                urlParams.put("op_id", ResourceManager.getOpId(mActivity));
                urlParams.put("game_id", ResourceManager.getGameId(mActivity));
                urlParams.put("account", userLoginInfo.getAccount()); //游客id  同deviceid
                urlParams.put("token", userLoginInfo.getAccessToken());
                urlParams.put("timestamp", ComUtils.getTimestamp());
                urlParams.put("sign", ComUtils.getSign(urlParams));

                String getParams = ComUtils.buildGetParams(urlParams);
                String upgradeUrl = Url.accountUpgrade() + "?" + getParams;

                Message message = Message.obtain();
                message.what = EmaConst.EMA_ACCOUNT_UPGRADE_URL_DONE;
                message.obj = upgradeUrl;
                mHandler.sendMessage(message);
            }
        });
    }

    //===========================下面的是支付的相关方法====================================================================


    /**
     * 获取在售商品列表
     * <p>
     * 耗时请求需要在子线程请求
     * {"productId":"com.emagroups.wol.40","type":"inapp","price":"US$0.99","price_amount_micros":990000,
     * "price_currency_code":"USD","title":"测试商品1 (Warriors of Light)","description":"第一个测试商品"}
     */
    public List<String> getProductList() {
        final ArrayList<String> productIdList = new ArrayList<>();

        HashMap<String, String> urlParams = new HashMap<>();
        urlParams.put("client_id", getClientId());
        urlParams.put("op_id", ResourceManager.getOpId(mActivity));
        urlParams.put("game_id", ResourceManager.getGameId(mActivity));
        urlParams.put("timestamp", ComUtils.getTimestamp());
        urlParams.put("sign", ComUtils.getSign(urlParams));
        try {
            String result = new HttpRequestor().doPost(Url.getProductsUrl(), urlParams);
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("code") == 0) {
                JSONArray productIdArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < productIdArray.length(); i++) {
                    productIdList.add(productIdArray.getString(i));
                }
                List<String> skuDetailList = getSkuDetail(productIdList);
                return skuDetailList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void pay(final Map<String, String> payParams, EmaSDKListener payListener) {

        boolean login = EmaUser.getInstance().isLogin();
        if (!login) {
            ToastHelper.toast(mActivity, "please login first !");
            return;
        }

        EmaCallbackUtil.getInstance().setPayListener(payListener);

        ProgressUtil.getInstance(mActivity).openProgressDialog();

        ThreadUtil.runInSubThread(new Runnable() {
            @Override
            public void run() {

                UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(mActivity.getApplicationContext());
                HashMap<String, String> params = new HashMap<>();
                params.put("client_id", getClientId());
                params.put("account", userLoginInfo.getAccount());
                params.put("op_id", ResourceManager.getOpId(mActivity));
                params.put("game_id", ResourceManager.getGameId(mActivity));

                params.put("server_id", payParams.get("server_id"));
                params.put("quantity", payParams.get("quantity"));
                params.put("product_id", payParams.get("product_id"));
                params.put("role_id", payParams.get("role_id"));
                params.put("custom_data", payParams.get("custom_data"));

                params.put("device_id", ComUtils.getDEVICE_ID(mActivity.getApplicationContext()));
                params.put("token", userLoginInfo.getAccessToken());
                params.put("timestamp", ComUtils.getTimestamp());
                params.put("sign", ComUtils.getSign(params));

                try {
                    String orderInfo = new HttpRequestor().doPost(Url.createOrderUrl(), params);
                    L.e("createOrder", orderInfo);

                    JSONObject jsonObject = new JSONObject(orderInfo);
                    if (jsonObject.getInt("code") == 0) {

                        boolean consume_now = Boolean.parseBoolean(payParams.get("consume_now"));
                        EmaUser.getInstance().setOrderInfo(jsonObject.getString("data"), consume_now);

                        Bundle buyIntentBundle = mService.getBuyIntent(3, mActivity.getPackageName()
                                , EmaUser.getInstance().getUserOrderInfo().getProduct_id(), "inapp", EmaUser.getInstance().getUserOrderInfo().getOrder_id());

                        int responseCode = buyIntentBundle.getInt("RESPONSE_CODE ");
                        L.e("buyIntentBundle", "responseCode:" + responseCode);

                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                        //在onActivityResult中获得响应
                        if (pendingIntent.getIntentSender() == null) {
                            Log.e("google pay", "the product is not consumed");
                        } else {
                            mActivity.startIntentSenderForResult(pendingIntent.getIntentSender(), 1001,
                                    new Intent(), Integer.valueOf(0),
                                    Integer.valueOf(0), Integer.valueOf(0));
                        }
                    } else {
                        EmaCallbackUtil.getInstance().onPayCallBack(EmaCallBackConst.PAYFALIED, "create order failed");
                    }
                    ProgressUtil.getInstance(mActivity).closeProgressDialog();
                } catch (Exception e) {
                    ProgressUtil.getInstance(mActivity).closeProgressDialog();
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 查询可供购买的商品详情
     * 请不要在主线程上调用该方法。 调用此方法会触发网络请求，进而阻塞主线程。 请创建单独的线程并从该线程内部调用 getSkuDetails 方法。
     */
    private List<String> getSkuDetail(List<String> productIdList) {

        ArrayList<String> skuList = new ArrayList<>(productIdList);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            Bundle skuDetailsInapp = mService.getSkuDetails(3, mActivity.getPackageName(), "inapp", querySkus);
            Bundle skuDetailsSubs = mService.getSkuDetails(3, mActivity.getPackageName(), "subs", querySkus);

            ArrayList<String> responseList = new ArrayList<>();
            int responseInapp = skuDetailsInapp.getInt("RESPONSE_CODE");
            int responseSubs = skuDetailsSubs.getInt("RESPONSE_CODE");
            if (responseInapp == 0) {
                responseList.addAll(skuDetailsInapp.getStringArrayList("DETAILS_LIST"));
            }
            if (responseSubs == 0) {
                responseList.addAll(skuDetailsSubs.getStringArrayList("DETAILS_LIST"));
            }
            return responseList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询单个商品的详情
     * 请不要在主线程上调用 getSkuDetails 方法。 调用此方法会触发网络请求，进而阻塞主线程。 请创建单独的线程并从该线程内部调用 getSkuDetails 方法。
     */
    private String getSkuDetail(String productId) {

        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("com.emagroups.wol.40");   //商品id
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            Bundle skuDetails = mService.getSkuDetails(3, mActivity.getPackageName(), "inapp", querySkus);

            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                JSONObject object = new JSONObject(responseList.get(0));
                String sku = object.getString("productId");
                String type = object.getString("type");
                String price = object.getString("price");
                String price_amount_micros = object.getString("price_amount_micros");
                String price_currency_code = object.getString("price_currency_code");
                String title = object.getString("title");
                String description = object.getString("description");

                return responseList.get(0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询已购买的商品
     * 此方法将返回当前归用户拥有但未消耗的商品，包括购买的商品和通过兑换促销代码获得的商品。
     */
    public List<String> getPurchases() {
        try {
            Bundle ownedItems = mService.getPurchases(3, mActivity.getPackageName(), "inapp", null);

            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");//用于检索用户拥有的下一组应用内商品的继续令牌的字符串

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                }
                return purchaseDataList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 方便测试用  专门用于消耗已有商品
     */
    public void consumeHad() {

        List<String> purchases = getPurchases();
        for (String data : purchases) {
            try {
                JSONObject dataObj = new JSONObject(data);
                String purchaseToken = dataObj.getString("purchaseToken");
                consumePurchase(purchaseToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 消耗购买
     */
    public void consumePurchase(final String purchaseToken) {
        ThreadUtil.runInSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    int response = mService.consumePurchase(3, mActivity.getPackageName(), purchaseToken);
                    L.e("consumePurchase", "response: " + response);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        ProgressUtil.getInstance(mActivity).openProgressDialog();

        switch (requestCode) {
            case 1001:
                if (intent == null) {
                    Log.e("google play", "onActivityResult's intent is null");
                    return;
                }
                int responseCode = intent.getIntExtra("RESPONSE_CODE", 0);
                final String purchaseData = intent.getStringExtra("INAPP_PURCHASE_DATA");
                final String dataSignature = intent.getStringExtra("INAPP_DATA_SIGNATURE");

                if (resultCode == Activity.RESULT_OK) {

                    ThreadUtil.runInSubThread(new Runnable() {
                        @Override
                        public void run() {
                            UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(mActivity.getApplicationContext());

                            try {
                                JSONObject jsonObject = new JSONObject(purchaseData);
                                String sdkOrderId = jsonObject.getString("developerPayload");
                                String productId = jsonObject.getString("productId");
                                String purchaseToken = jsonObject.getString("purchaseToken");


                                HashMap<String, String> params = new HashMap<>();
                                params.put("account", userLoginInfo.getAccount()); //游客id  同deviceid
                                params.put("op_id", ResourceManager.getOpId(mActivity));
                                params.put("game_id", ResourceManager.getGameId(mActivity));
                                params.put("product_id", productId);
                                params.put("purchase_data", purchaseData);
                                params.put("data_signnture", Base64.encodeToString(dataSignature.getBytes(), Base64.DEFAULT));//为了那个奇怪的+号
                                params.put("order_id", sdkOrderId);
                                params.put("token", userLoginInfo.getAccessToken());

                                //gpa通知回调
                                String s = new HttpRequestor().doPost(Url.payNotifyUrl(), params);
                                L.e("payNotify", "result: " + s);

                                JSONObject notifyResutl = new JSONObject(s);
                                if (notifyResutl.getInt("code") == 0) { //gpa购买成功
                                    EmaCallbackUtil.getInstance().onPayCallBack(EmaCallBackConst.PAYSUCCESS, "purchase successful");

                                    boolean consumeNow = EmaUser.getInstance().getUserOrderInfo().isConsumeNow();
                                    L.e("consume_now", consumeNow + "!!");
                                    if (consumeNow) {
                                        consumePurchase(purchaseToken);
                                    }

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ProgressUtil.getInstance(mActivity).closeProgressDialog();
                        }
                    });
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    EmaCallbackUtil.getInstance().onPayCallBack(EmaCallBackConst.PAYCANELI, "purchase canceled");

                    ThreadUtil.runInSubThread(new Runnable() {
                        @Override
                        public void run() {
                            UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(mActivity.getApplicationContext());

                            HashMap<String, String> params = new HashMap<>();
                            params.put("client_id", getClientId());
                            params.put("account", userLoginInfo.getAccount()); //游客id  同deviceid
                            params.put("op_id", ResourceManager.getOpId(mActivity));
                            params.put("game_id", ResourceManager.getGameId(mActivity));
                            params.put("order_id", EmaUser.getInstance().getUserOrderInfo().getOrder_id());
                            params.put("token", userLoginInfo.getAccessToken());
                            params.put("timestamp", ComUtils.getTimestamp());
                            params.put("sign", ComUtils.getSign(params));
                            try {
                                String result = new HttpRequestor().doPost(Url.cancleOrder(), params);
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("code");
                                if (code == 0) {
                                    //清空订单信息
                                    EmaUser.getInstance().clearOrderInfo();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ProgressUtil.getInstance(mActivity).closeProgressDialog();
                        }
                    });
                }
                break;
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case EmaConst.REQUEST_CODE_READPHONESTATE_PERMISSION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    mHandler.sendEmptyMessage(EmaCallBackConst.INITFALIED);
                    Toast.makeText(mActivity, "STORAGE_PERMISSION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case EmaConst.REQUEST_CODE_ACCESSWIFISTATE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    mHandler.sendEmptyMessage(EmaCallBackConst.INITFALIED);
                    Toast.makeText(mActivity, "ACCESS_WIFISTATE_PERMISSION Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void onDestory() {
        if (mService != null) {
            mActivity.unbindService(mServiceConn);
        }
    }


}
