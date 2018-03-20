package com.emagroup.oversea.sdk;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by beyearn on 2018/2/27.
 */

public class EmaUser {

    private static EmaUser mInstance;
    private UserLoginInfo mUserLoginInfo;
    public static final String LOGIN_INFO_SAVE_NAME = "userLoginInfo";
    private OrderInfo mOrderInfo;

    private EmaUser() {
    }

    public static EmaUser getInstance() {
        if (mInstance == null) {
            mInstance = new EmaUser();
        }
        return mInstance;
    }


    public UserLoginInfo getUserLoginInfo(Context context) {
        if (mUserLoginInfo == null) {
            return getUserInfo(context);
        } else {
            return mUserLoginInfo;
        }
    }

    public void clearLoginInfo(Context context) {
        mUserLoginInfo = null;
        delateUserInfo(context);
    }

    public OrderInfo getUserOrderInfo() {
        return mOrderInfo;
    }

    public void clearOrderInfo() {
        mOrderInfo = null;
    }


    public void setLoginInfo(Context context, String data) {
        try {
            JSONObject loginResult = new JSONObject(data);
            int code = loginResult.getInt("code");
            if (code == 0) {
                JSONObject loginData = loginResult.getJSONObject("data");
                String accessToken = loginData.getString("accessToken");
                String account = loginData.getString("account");
                String type = loginData.getString("type");
                String nickName = loginData.getString("nickName");

                mUserLoginInfo = new UserLoginInfo();

                mUserLoginInfo.setAccessToken(accessToken);
                mUserLoginInfo.setAccount(account);
                mUserLoginInfo.setType(type);
                mUserLoginInfo.setNickName(nickName);

                saveUserInfo(context, mUserLoginInfo);

                EmaCallbackUtil.getInstance().onInitLoginCallback(EmaCallBackConst.LOGINSUCCESS, "login success");

            } else {
                EmaCallbackUtil.getInstance().onInitLoginCallback(EmaCallBackConst.LOGINFALIED, "login failed");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setOrderInfo(String orderInfo, boolean consume_now) {
        if (mOrderInfo == null) {
            mOrderInfo = new OrderInfo();
        }
        try {
            JSONObject data = new JSONObject(orderInfo);
            String order_id = data.getString("order_id");
            String op_id = data.getString("op_id");
            String game_id = data.getString("game_id");
            String product_id = data.getString("product_id");
            String product_name = data.getString("product_name");
            String price = data.getString("price");
            String currency = data.getString("currency");
            String quantity = data.getString("quantity");

            mOrderInfo.setCurrency(currency);
            mOrderInfo.setGame_id(game_id);
            mOrderInfo.setOp_id(op_id);
            mOrderInfo.setOrder_id(order_id);
            mOrderInfo.setPrice(Double.parseDouble(price));
            mOrderInfo.setQuantity(quantity);
            mOrderInfo.setProduct_name(product_name);
            mOrderInfo.setProduct_id(product_id);

            mOrderInfo.setConsumeNow(consume_now);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void saveUserInfo(Context context, UserLoginInfo userLoginInfo) {

        try {
            FileOutputStream fos = context.openFileOutput(LOGIN_INFO_SAVE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(userLoginInfo);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delateUserInfo(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(LOGIN_INFO_SAVE_NAME, Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private UserLoginInfo getUserInfo(Context context) {
        try {
            FileInputStream fis = context.openFileInput(LOGIN_INFO_SAVE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object loginInfo = ois.readObject();
            if (loginInfo instanceof UserLoginInfo) {
                return (UserLoginInfo) loginInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
