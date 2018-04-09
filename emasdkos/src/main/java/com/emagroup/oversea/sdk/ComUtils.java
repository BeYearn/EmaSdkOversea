package com.emagroup.oversea.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.emagroup.oversea.sdk.EmaConst.REQUEST_CODE_ACCESSWIFISTATE_PERMISSION;
import static com.emagroup.oversea.sdk.EmaConst.REQUEST_CODE_READPHONESTATE_PERMISSION;

/**
 * Created by beyearn on 2018/2/26.
 */

public class ComUtils {

    public static String getTimestamp() {

        String timestamp = null;
        try {
            HashMap<String, String> param = new HashMap<>();
            param.put("timestamp", "1");
            String timestampStr = new HttpRequestor().doPost(Url.timeStampUrl(), param);
            JSONObject jsonObject = new JSONObject(timestampStr);
            JSONObject data = jsonObject.getJSONObject("data");
            timestamp = data.getString("timestamp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;

    }

    public static String getSign(Map<String, String> map) {
        Map<String, String> sortMapByKey = sortMapByKey(map);

        String rawSign = "";
        for (Map.Entry<String, String> entry : sortMapByKey.entrySet()) {
            rawSign += entry.getValue();
        }

        return getSHA256StrJava(rawSign + EmaSdk.getInstance().getSecret());

    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String str1, String str2) {
                        return str1.compareTo(str2);
                    }
                });

        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }


    //设备ID
    public static String getDEVICE_ID(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        String DEVICE_ID = tm.getDeviceId();
        String MacAddress = manager.getConnectionInfo().getMacAddress();
        String AndroidSerialNum = android.os.Build.SERIAL;

        if (TextUtils.isEmpty(DEVICE_ID)) {
            String oneIdNoMd5 = MacAddress + AndroidSerialNum;
            String oneId = getSHA256StrJava(oneIdNoMd5).substring(8, 24);
            return oneId;
        }
        Log.e("DEVICE_ID" + "MAC", DEVICE_ID + "......" + MacAddress + "..." + AndroidSerialNum);
        return DEVICE_ID;
    }


    public static void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkSelfPermission = activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READPHONESTATE_PERMISSION);
            }
            int wifiStatePermission = activity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
            if (wifiStatePermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_CODE_ACCESSWIFISTATE_PERMISSION);
            }
        } else {
        }
    }
}
