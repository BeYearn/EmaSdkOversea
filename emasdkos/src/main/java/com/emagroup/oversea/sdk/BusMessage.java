package com.emagroup.oversea.sdk;

/**
 * Created by beyearn on 2017/5/26.
 */

public class BusMessage {
    private int code;
    private Object object;

    public BusMessage() {}

    public BusMessage(int code, Object o) {
        this.code = code;
        this.object = o;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
