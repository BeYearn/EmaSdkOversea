package com.emagroup.oversea.sdk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/8/23.
 */
public class EmaPayInfo implements Parcelable {

    private String productName;
    private String productNum;
    private String productId;

    private String gameTransCode; // 游戏需要的一个透传参数，可空

    public String getGameTransCode() {
        return gameTransCode;
    }

    public void setGameTransCode(String gameTransCode) {
        this.gameTransCode = gameTransCode;
    }

    public EmaPayInfo() {
    }

    //登录后才能拿到
    private String uid;

    //订单号，发起支付才能得到
    private String orderId;
    private String orderShortId;  // 短orderId 专为uc而用（因为uc要求这个长度不能超过30）

    //订单金额
    private int price;
    // 描述
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

    public String getOrderShortId() {
        return orderShortId;
    }

    public void setOrderShortId(String orderShortId) {
        this.orderShortId = orderShortId;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(productNum);
        dest.writeString(productId);
        dest.writeString(orderId);
        dest.writeString(orderShortId);
        dest.writeString(uid);
        dest.writeFloat(price);
        dest.writeString(gameTransCode);
    }

    public EmaPayInfo(Parcel source) {
        //先读取mId，再读取mDate
        productName = source.readString();
        productNum = source.readString();
        productId = source.readString();
        orderId = source.readString();
        orderShortId=source.readString();
        uid = source.readString();
        price = source.readInt();
        gameTransCode = source.readString();
    }

    //实例化静态内部对象CREATOR实现接口Parcelable.Creator
    public static final Creator<EmaPayInfo> CREATOR = new Creator<EmaPayInfo>() {

        @Override
        public EmaPayInfo[] newArray(int size) {
            return new EmaPayInfo[size];
        }

        //将Parcel对象反序列化为ParcelableDate
        @Override
        public EmaPayInfo createFromParcel(Parcel source) {
            return new EmaPayInfo(source);
        }
    };

}
