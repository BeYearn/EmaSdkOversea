package com.emagroup.oversea.sdk;

import java.io.Serializable;

/**
 * Created by beyearn on 2018/2/27.
 */

public class UserLoginInfo implements Serializable {
    private String accessToken;
    private String account;
    private String type;  //账号类型 0: 官方 1：Facebook 2：Google 3：Twitter 4：Gamecenter  5: guest
    private String nickName;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
